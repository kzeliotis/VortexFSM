package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.ZonesRvAdapter;
import am.gtest.vortex.data.InstallationZonesData;
import am.gtest.vortex.data.ZonesData;
import am.gtest.vortex.support.MyGlobals;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_GET_ZONES;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_INSTALLATION_ZONES_LIST;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_ZONES_LIST;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_DATA_FOR_SHOW;
import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;


public class GetZones extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final ZonesRvAdapter zonesRvAdapter;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private boolean refresh;
    private String projectInstallationId;



    public GetZones(Context ctx, ZonesRvAdapter zonesRvAdapter, boolean refresh, String ProjectInstallationId) {
        this.ctx = ctx;
        this.zonesRvAdapter = zonesRvAdapter;
        this.projectInstallationId = ProjectInstallationId;
        this.refresh = refresh;

    }

    @Override
    protected void onPreExecute() {

        mProgressBar = null;

        if(ctx != null){
            mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected String doInBackground(String... params) {
        String projectId = params[0];

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_ZONES + projectId;

        if(!projectInstallationId.equals("0")){
            apiUrl += "&ProjectInstallationId=" + projectInstallationId;
        }

        try {
            Bundle bundle = MyApi.get(apiUrl);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {
       MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "", responseCode, responseMessage, responseBody);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseCode == 200 && responseBody != null ) {

            if(projectInstallationId.equals("0")){
                MyPrefs.setString(PREF_DATA_ZONES_LIST, responseBody);

                String AssignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
                MyPrefs.setStringWithFileName(PREF_FILE_ZONES_DATA_FOR_SHOW, AssignmentId, responseBody);

                ZonesData.generate(refresh);

                if (zonesRvAdapter != null) {
                    zonesRvAdapter.notifyDataSetChanged();
                }


            }else{

                MyPrefs.setString(PREF_DATA_INSTALLATION_ZONES_LIST, responseBody);
                MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW, SELECTED_INSTALLATION.getProjectInstallationId(), responseBody);

                InstallationZonesData.generate(refresh);


                if(ctx != null){

                    String className =((Activity) ctx).getClass().getSimpleName();

                    if(className.equals("InstallationZoneEditActivity")){
                        ((Activity) ctx).finish();
                    } else {
                        RecyclerView  rvInstallationZones = ((Activity) ctx).findViewById(R.id.rvInstallationZones);
                        if (rvInstallationZones != null){
                            rvInstallationZones.getAdapter().notifyDataSetChanged();
                        }
                    }
                }
            }


        }
    }
}
