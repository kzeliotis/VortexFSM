package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_UPDATE_ASSIGNMENT;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static dc.gtest.vortex.support.MyLocalization.localized_error_synchronizing;
import static dc.gtest.vortex.support.MyLocalization.localized_workorder_not_available;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_TRAVEL_STARTED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_START_TRAVEL_DATA_TO_SYNC;

public class SendStartTravel extends AsyncTask<String, Void, String > {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final String assignmentId;
    private final boolean showProgressAndToast;

    private int responseCode;

    public SendStartTravel(Context ctx, String assignmentId, boolean showProgressAndToast) {
        this.ctx = ctx;
        this.assignmentId = assignmentId;
        this.showProgressAndToast = showProgressAndToast;
    }

    @Override
    protected void onPreExecute() {
        if (showProgressAndToast) {
            mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String responseMessage = "";
        String responseBody = "";

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + API_UPDATE_ASSIGNMENT;

        String postBody = MyPrefs.getStringWithFileName(PREF_FILE_START_TRAVEL_DATA_TO_SYNC, assignmentId, "");

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseBody != null && responseBody.equals("1") ) {

            MyPrefs.removeStringWithFileName(PREF_FILE_START_TRAVEL_DATA_TO_SYNC, assignmentId);

            if (showProgressAndToast) {
                Toast.makeText(ctx, localized_data_sent_2_rows, Toast.LENGTH_LONG).show();
            }

        } else if (responseBody != null && responseBody.equals("2") ) {

            MyPrefs.removeStringWithFileName(PREF_FILE_START_TRAVEL_DATA_TO_SYNC, assignmentId);

            MyPrefs.setBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false);

            Toast.makeText(ctx, localized_workorder_not_available, Toast.LENGTH_LONG).show();

        } else {
            if (showProgressAndToast) {
                MyDialogs.showOK(ctx, localized_error_synchronizing+ "\n\n" + this.getClass().getSimpleName());
            }
        }
    }
}
