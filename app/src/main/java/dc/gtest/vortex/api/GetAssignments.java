package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AssignmentsActivity;
import dc.gtest.vortex.adapters.AssignmentsRvAdapter;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.data.AssignmentsData;
import dc.gtest.vortex.models.AssignmentModel;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_ASSIGNMENTS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.CONST_ASSIGNMENT_ATTACHMENTS_FOLDER;
import static dc.gtest.vortex.support.MyGlobals.CONST_ASSIGNMENT_PHOTOS_FOLDER;
import static dc.gtest.vortex.support.MyLocalization.localized_no_assignments;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_DOWNLOAD_ALL_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_DOWNLOAD_ALL_DATA_ZONES;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ATTACHMENT_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IMAGE_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_IS_LOGGED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_PASSWORD;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;
import static android.content.Context.MODE_PRIVATE;

public class GetAssignments extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private int responseCode;
    private String responseMessage;

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final boolean downloadAllData;

    public GetAssignments(Context ctx, boolean downloadAllData) {
        this.ctx = ctx;
        this.downloadAllData = downloadAllData;

        Log.e(LOG_TAG, "============================ downloadAllData: " + downloadAllData + "; from: " + ctx.getClass().getSimpleName());
    }

    @Override
    protected void onPreExecute() {
        mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        responseMessage = "";
        String responseBody = "";

        String userName = MyPrefs.getString(PREF_USER_NAME, "");
        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String PassWD = MyPrefs.getStringWithFileName(PREF_PASSWORD,  "1", "");
        String apiUrl = baseHostUrl + API_GET_ASSIGNMENTS + userName; //+ "&password=" + PassWD;

        try {
            Bundle bundle = MyApi.get(apiUrl, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
//            if(e.toString().contains("SocketTimeoutException")) {
//                Intent intent = new Intent(ctx, AssignmentsActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra(KEY_DOWNLOAD_ALL_DATA, false);
//                ctx.startActivity(intent);
//            }
            e.printStackTrace();
        }

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for get request", responseCode, responseMessage, responseBody);

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseCode == -1){
            new AlertDialog.Builder(ctx).setMessage(responseMessage).show();
        }
        else if (responseCode == 200 && responseBody != null ) {
            MyPrefs.setString(PREF_DATA_ASSIGNMENTS, responseBody);
            AssignmentsData.generate(ctx);
            boolean LoggedIn = MyPrefs.getBoolean(PREF_KEY_IS_LOGGED_IN, false);

            List<String> AssignmentIds = new ArrayList<>();
            if (ASSIGNMENTS_LIST.size() == 0 && LoggedIn) {
                Toast.makeText(MyApplication.getContext(), localized_no_assignments, Toast.LENGTH_LONG).show();
            } else {

                for (int i = 0; i < ASSIGNMENTS_LIST.size(); i++) {
                    if (!AssignmentIds.contains(ASSIGNMENTS_LIST.get(i).getAssignmentId())) {
                        AssignmentIds.add(ASSIGNMENTS_LIST.get(i).getAssignmentId());
                    }
                }
                emptyUnusedFiles(ctx, AssignmentIds);
            }

            if (downloadAllData) {
                // get and save products data

                if(MyPrefs.getBoolean(PREF_DOWNLOAD_ALL_DATA, true)){
                    for (int i = 0; i < ASSIGNMENTS_LIST.size(); i++) {
                        GetProducts getProducts = new GetProducts(ctx, ASSIGNMENTS_LIST.get(i).getAssignmentId(), false, "0", false, "");
                        getProducts.execute();

                        GetAllConsumables getAllConsumables = new GetAllConsumables(null, ASSIGNMENTS_LIST.get(i).getAssignmentId(), false, true, "");
                        getAllConsumables.execute();

                    }
                }


                if (MyPrefs.getBoolean(PREF_DOWNLOAD_ALL_DATA_ZONES, true)) {
                    for (AssignmentModel assignment : ASSIGNMENTS_LIST) {
                        String projectId = assignment.getProjectId();
                        GetZones getZones = new GetZones(ctx, null, true, "0", assignment.getAssignmentId());
                        getZones.execute(projectId);
                    }
                }


                // get and save history data
                // create project ids array to avoid multiple calls to the same history API


                if(MyPrefs.getBoolean(PREF_DOWNLOAD_ALL_DATA, true)){
                    for (int i = 0; i < AssignmentIds.size(); i++) {

                        boolean hideProgress = false;

                        if (i == AssignmentIds.size() - 1) {
                            hideProgress = true;
                        }
                        String AssId = AssignmentIds.get(i);
                        if (!AssId.contains("-")) {
                            GetHistory getHistory = new GetHistory(ctx, AssignmentIds.get(i), hideProgress, false, "0");
                            getHistory.execute();
                        }

                    }
                }


                Log.e(LOG_TAG, "================= ASSIGNMENTS_LIST.size(): " + ASSIGNMENTS_LIST.size() + "; projectIds.size(): " + AssignmentIds.size());
            }
        }

        RecyclerView rvAssignments = ((AppCompatActivity)ctx).findViewById(R.id.rvAssignments);

        if (rvAssignments != null) {

            AssignmentsRvAdapter assignmentsRvAdapter = (AssignmentsRvAdapter) rvAssignments.getAdapter();

            if (assignmentsRvAdapter != null) {
                assignmentsRvAdapter.notifyDataSetChanged();
                assignmentsRvAdapter.getFilter().filter(AssignmentsActivity.searchedText);
            }
        }
    }


    private void emptyUnusedFiles(Context ctx, List<String> AssignmentIds){

        if (AssignmentIds.size() == 0) {return;}

        try{
            File[] folders = new File(ctx.getExternalFilesDir(null).toString()).listFiles(File::isDirectory);

            for (File folder : folders){
                String folderName = folder.getName();
                if (folderName.contains(CONST_ASSIGNMENT_PHOTOS_FOLDER) || folderName.contains(CONST_ASSIGNMENT_ATTACHMENTS_FOLDER)){
                    boolean IsPhoto = folderName.contains(CONST_ASSIGNMENT_PHOTOS_FOLDER);
                    String assignmentId = folderName.split("_")[0];
                    if (!AssignmentIds.contains(assignmentId)){
                        long diff = new Date().getTime() - folder.lastModified();
                        int x = 4; //4 days
                        if (diff > x * 24 * 60 * 60 * 1000) {
                            Map<String, ?> filesToBeSynced = new HashMap<>();
                            if (IsPhoto){
                                filesToBeSynced = ctx.getSharedPreferences(PREF_FILE_IMAGE_FOR_SYNC, MODE_PRIVATE).getAll();
                            } else {
                                filesToBeSynced = ctx.getSharedPreferences(PREF_FILE_ATTACHMENT_FOR_SYNC, MODE_PRIVATE).getAll();
                            }

                            boolean permissionToDelete = true;
                            for (Map.Entry<String, ?> entry : filesToBeSynced.entrySet()) {
                                String prefKey = entry.getKey();
                                if (prefKey.split("_")[0].equals(assignmentId)){
                                    permissionToDelete = false;
                                }
                            }

                            if (permissionToDelete){
                                try{
                                    File[] files = folder.listFiles();
                                    if(files!=null) { //some JVMs return null for empty dirs
                                        for(File f: files) {
                                            if(f.isDirectory()) {
                                                //deleteFolder(f);
                                            } else {
                                                f.delete();
                                            }
                                        }
                                    }
                                    folder.delete();
                                } catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }


    }

}
