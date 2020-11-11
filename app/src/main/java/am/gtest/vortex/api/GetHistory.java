package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import am.gtest.vortex.R;
import am.gtest.vortex.application.MyApplication;
import am.gtest.vortex.data.HistoryData;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_GET_HISTORY_ASSIGNMENTS;
import static am.gtest.vortex.api.MyApi.API_GET_SUB_ASSIGNMENTS;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyGlobals.HISTORY_LIST;
import static am.gtest.vortex.support.MyLocalization.localized_no_history;
import static am.gtest.vortex.support.MyLocalization.localized_no_subassignments;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_HISTORY_DATA;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_HISTORY_DATA;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_SUBASSIGNMENTS_DATA;

public class GetHistory extends AsyncTask<String, Void, String > {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private int responseCode;

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;


    private final String AssignmentId;
    private final boolean hideProgress;
    private final boolean subAssignments;
    private final String projectInstallationId;


    public GetHistory(Context ctx, String AssignmentId, boolean hideProgress, boolean subAssignments, String projectInstallationId) {
        this.ctx = ctx;
        this.AssignmentId = AssignmentId;
        this.hideProgress = hideProgress;
        this.subAssignments = subAssignments;
        this.projectInstallationId = projectInstallationId;

    }

    @Override
    protected void onPreExecute() {
        mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);

//            Log.e(LOG_TAG, "================================== onPreExecute projectId: " + projectId);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String responseMessage = "";
        String responseBody = "";

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl;
        if(subAssignments){
            apiUrl = baseHostUrl + API_GET_SUB_ASSIGNMENTS + AssignmentId;
        } else {
            apiUrl = baseHostUrl + API_GET_HISTORY_ASSIGNMENTS + AssignmentId;
            if (!projectInstallationId.equals("0")){
                apiUrl += "&ProjectInstallationId=" + projectInstallationId;
            }
        }

        try {
            Bundle bundle = MyApi.get(apiUrl);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for get request", responseCode, responseMessage, responseBody);

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {

        if (hideProgress && mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);

//            Log.e(LOG_TAG, "================================== onPostExecute projectId: " + projectId);
        }

        if (responseCode >= 200 && responseCode < 300 &&
                responseBody != null &&
                !responseBody.equals("") &&
                !responseBody.equals("null") &&
//                !responseBody.equals("[]") &&  // execute code to save empty list
                !responseBody.equals("{}")
                ) {

            if(subAssignments){
                MyPrefs.setStringWithFileName(PREF_FILE_SUBASSIGNMENTS_DATA, AssignmentId, responseBody);
            }else{
                if(!projectInstallationId.equals("0")){
                    MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATION_HISTORY_DATA, projectInstallationId, responseBody);
                } else {
                    MyPrefs.setStringWithFileName(PREF_FILE_HISTORY_DATA, AssignmentId, responseBody);
                }
            }

        }

        RecyclerView rvHistoryList = ((Activity) ctx).findViewById(R.id.rvHistoryList);
        if (rvHistoryList != null && rvHistoryList.getAdapter() != null) {

            HistoryData.generate(responseBody);

            if (HISTORY_LIST.size() == 0) {
                if (subAssignments){
                    Toast.makeText(MyApplication.getContext(), localized_no_subassignments, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MyApplication.getContext(), localized_no_history, Toast.LENGTH_LONG).show();
                }

            }

            rvHistoryList.getAdapter().notifyDataSetChanged();
        }
    }
}
