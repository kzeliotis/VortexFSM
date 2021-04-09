package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.AssignmentsActivity;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_UPDATE_ASSIGNMENT;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static am.gtest.vortex.support.MyLocalization.localized_error_synchronizing;
import static am.gtest.vortex.support.MyLocalization.localized_successfully_checked_in;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_CHECK_IN_DATA_TO_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IS_SCANNED;

public class SendCheckIn extends AsyncTask<String, Void, String > {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final String assignmentId;
    private final boolean showProgressAndToast;

    private int responseCode;

    public SendCheckIn(Context ctx, String assignmentId, boolean showProgressAndToast) {
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

        String postBody = MyPrefs.getStringWithFileName(PREF_FILE_CHECK_IN_DATA_TO_SYNC, assignmentId, "");

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false);

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

            MyPrefs.removeStringWithFileName(PREF_FILE_CHECK_IN_DATA_TO_SYNC, assignmentId);

            if (showProgressAndToast) {
                Toast.makeText(ctx, localized_successfully_checked_in, Toast.LENGTH_LONG).show();

                if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_SCANNED, assignmentId, false)){
                    Activity activity = (Activity) ctx;
                    activity.finishAffinity();
                    Intent intent = new Intent(activity, AssignmentsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(KEY_DOWNLOAD_ALL_DATA, false);
                    ctx.startActivity(intent);
                }

            }

        } else {
            if (showProgressAndToast) {
                MyDialogs.showOK(ctx, localized_error_synchronizing+ "\n\n" + this.getClass().getSimpleName());
            }
        }
    }
}
