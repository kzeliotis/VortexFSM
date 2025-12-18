package dc.gtest.vortex.api;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Semaphore;

import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.ZonesRvAdapter;
import dc.gtest.vortex.data.InstallationZonesData;
import dc.gtest.vortex.data.ZonesData;
import dc.gtest.vortex.models.AssignmentProjectZonesModel;
import dc.gtest.vortex.models.ZoneModel;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.StartupLoadTracker;
import dc.gtest.vortex.support.UiThread;

import static dc.gtest.vortex.api.MyApi.API_GET_ZONES_FULL;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_INSTALLATION_ZONES_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ZONES_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class GetZonesExecutor {

    // =========================================================
    // Optional global semaphore
    // =========================================================
    private static Semaphore SEMAPHORE;

    public static void setSemaphore(@Nullable Semaphore semaphore) {
        SEMAPHORE = semaphore;
    }

    // =========================================================
    // Instance fields
    // =========================================================
    private final WeakReference<Activity> activityRef;
    private ProgressBar mProgressBar;
    private final boolean refresh;
    private final int priority;
    private final String serializedData;
    private final boolean useStartupTracker;

    // =========================================================
    // Constructor
    // =========================================================
    public GetZonesExecutor(
            @Nullable Activity activity,
            boolean refresh,
            int priority,
            String serializedData,
            boolean useStartupTracker
    ) {
        this.activityRef = new WeakReference<>(activity);
        this.refresh = refresh;
        this.priority = priority;
        this.serializedData = serializedData;
        this.useStartupTracker = useStartupTracker;
    }

    // =========================================================
    // Public API
    // =========================================================
    public void execute() {

        // -----------------------------
        // UI thread → onPreExecute()
        // -----------------------------
        if(!useStartupTracker){
            UiThread.run(() -> {
                Activity activity = activityRef.get();
                if (activity != null && !activity.isFinishing()) {
                    mProgressBar = activity.findViewById(R.id.progressBar);
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


        // -----------------------------
        // Background execution
        // -----------------------------
        PriorityApiExecutors.IO.execute(
                new PriorityTask(priority) {

                    @Override
                    public void run() {

                        try {
                            if (SEMAPHORE != null) {
                                SEMAPHORE.acquire();
                            }

                            doApiCall(serializedData);

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();

                        } finally {
                            if (SEMAPHORE != null) {
                                SEMAPHORE.release();
                            }
                        }
                    }
                }
        );
    }

    // =========================================================
    // Background logic
    // =========================================================
    private void doApiCall(String postBody) {

        int responseCode = -1;
        String responseMessage = null;
        String responseBody = null;
        String apiUrl;

        try {
            Context ctx = getSafeContext();
            if (ctx == null) return;

             apiUrl =
                    MyPrefs.getString(PREF_BASE_HOST_URL, "") +
                            API_GET_ZONES_FULL;


            Bundle bundle = MyApi.post(apiUrl, postBody, false, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

            if (responseCode != 200 || responseBody == null || responseBody.equals("[]")) {
                MyLogs.showFullLog(
                        "GetZonesExecutor",
                        apiUrl,
                        "",
                        responseCode,
                        responseMessage,
                        responseBody
                );
            } else {

                Gson gson = new Gson();

                JsonReader reader =
                        new JsonReader(new StringReader(responseBody));

                TypeAdapter<ZoneModel> zoneAdapter =
                        gson.getAdapter(ZoneModel.class);

                reader.beginArray();

                while (reader.hasNext()) {

                    reader.beginObject();

                    String assignmentId = null;
                    String projectId = null;
                    String zonesData = "";

                    while (reader.hasNext()) {

                        String name = reader.nextName();

                        if (name.equals("AssignmentId")) {
                            assignmentId = reader.nextString();
                        }
                        else if (name.equals("ProjectId")) {
                            projectId = reader.nextString();
                        }
                        else if (name.equals("ProjectZoneData")) {

                            reader.beginArray();

                            while (reader.hasNext()) {

                                ZoneModel z = zoneAdapter.read(reader);

                                String projectZoneId = z.getZoneId();
                                String zoneProducts = z.getZoneProducts();

                                if (zoneProducts != null
                                        && !zoneProducts.isEmpty()
                                        && !zoneProducts.equals("[]")) {

                                    String zpPrefKey =
                                            projectId + "_"
                                                    + projectZoneId + "_"
                                                    + assignmentId;

                                    MyPrefs.setStringWithFileName(
                                            PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW,
                                            zpPrefKey,
                                            zoneProducts
                                    );

                                    z.setZoneProducts("");
                                }

                                if(zonesData.isEmpty()){
                                    zonesData = gson.toJson(z);
                                }else{
                                    zonesData += ", \n" + gson.toJson(z);
                                }


                            }

                            reader.endArray();

                            String zoneJson = "[" + zonesData + "]";

                            MyPrefs.setStringWithFileName(
                                    PREF_FILE_ZONES_DATA_FOR_SHOW,
                                    assignmentId,
                                    zoneJson
                            );
                        }
                        else {
                            reader.skipValue();
                        }
                    }

                    reader.endObject();
                }

                reader.endArray();
                reader.close();


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        int finalCode = responseCode;
        String finalBody = responseBody;

        // -----------------------------
        // UI thread → onPostExecute()
        // -----------------------------
        UiThread.run(() -> onResult(finalCode, finalBody));
    }

    // =========================================================
    // UI result handling
    // =========================================================
    private void onResult(int responseCode,
                          String responseBody) {

        if (mProgressBar != null && !useStartupTracker) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (useStartupTracker){
            StartupLoadTracker.jobDone();
        }

//        if (responseCode != 200 || responseBody == null || responseBody.equals("[]")) {
//            return;
//        }
//
//        Gson gson = new Gson();
//
//        Type listType = new TypeToken<List<AssignmentProjectZonesModel>>() {}.getType();
//        Type zoneType = new TypeToken<List<ZoneModel>>() {}.getType();
//
//        List<AssignmentProjectZonesModel> ZoneDataList = gson.fromJson(responseBody, listType);
//
//        for (AssignmentProjectZonesModel az : ZoneDataList){
//
//            String assignmentId = String.valueOf(az.getAssignmentId());
//            String projectId = String.valueOf(az.getProjectId());
//            String zonesData = az.getProjectZoneData();
//
//            if(zonesData == null||zonesData.isEmpty()||zonesData.equals("[]")){
//                continue;
//            }
//
//            List<ZoneModel> zonesList = gson.fromJson(zonesData, zoneType);
//
//            for(ZoneModel z : zonesList){
//
//                String projectZoneId = z.getZoneId();
//
//                String zoneProducts = z.getZoneProducts();
//
//                if(zoneProducts != null && !zoneProducts.isEmpty() && !zoneProducts.equals("[]")){
//                    String zp_prefKey = projectId + "_" + projectZoneId + "_" + assignmentId;
//                    MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW, zp_prefKey, zoneProducts);
//                    z.setZoneProducts("");
//                }
//
//                String zonesString = z.toString();
//                MyPrefs.setStringWithFileName(PREF_FILE_ZONES_DATA_FOR_SHOW, assignmentId, zonesString);
//
//            }
//
//        }



    }



    // =========================================================
    // Safe context resolution
    // =========================================================
    private @Nullable Context getSafeContext() {
        Activity a = activityRef.get();
        return (a != null)
                ? a.getApplicationContext()
                : MyApplication.getContext();
    }
}
