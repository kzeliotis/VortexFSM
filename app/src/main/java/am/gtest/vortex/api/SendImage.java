package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import am.gtest.vortex.R;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_SET_ASSIGNMENT_PHOTO;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyLocalization.localized_image_uploaded;
import static am.gtest.vortex.support.MyLocalization.localized_no_internet_image_will_be_sent;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IMAGE_FOR_SYNC;

public class SendImage extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    private String prefKey;

    public SendImage(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
//        mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
//        if (mProgressBar != null) {
//            mProgressBar.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected String doInBackground(String... params) {
        prefKey = params[0];
        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_SET_ASSIGNMENT_PHOTO;
        postBody = MyPrefs.getStringWithFileName(PREF_FILE_IMAGE_FOR_SYNC, prefKey, "");

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String dataFromApi) {
//        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);

//        if (mProgressBar != null) {
//            mProgressBar.setVisibility(View.GONE);
//        }

        if (responseCode == 200) {
            MyPrefs.removeStringWithFileName(PREF_FILE_IMAGE_FOR_SYNC, prefKey);

            Toast toast = Toast.makeText(ctx, localized_image_uploaded, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            MyDialogs.showOK(ctx, localized_no_internet_image_will_be_sent);
        }
    }
}
