package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import am.gtest.vortex.R;
import am.gtest.vortex.application.MyApplication;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.api.MyApi.API_SEND_CUSTOM_FIELDS;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static am.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_CUSTOM_FIELDS_FOR_SYNC;

public class SendCustomFields extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private final ProgressBar mProgressBar;

    private String prefKey;
    private boolean showProgressBar;

    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody = null;
    private String vortexTable;

    public SendCustomFields(Context ctx, String prefKey, boolean showProgressBar, String vortexTable) {
        this.ctx = ctx;
        this.prefKey = prefKey;
        this.showProgressBar = showProgressBar;
        this.vortexTable = vortexTable;
        mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);

    }

    @Override
    protected void onPreExecute() {
        if (showProgressBar && mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(String... params) {


        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");

        apiUrl = baseHostUrl + API_SEND_CUSTOM_FIELDS;
        postBody = MyPrefs.getStringWithFileName(PREF_FILE_CUSTOM_FIELDS_FOR_SYNC, prefKey, "");

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false);

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

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseBody != null && responseBody.equals("1")) {

            String vortexTableId = prefKey.split("_")[1];

            MyPrefs.removeStringWithFileName(PREF_FILE_CUSTOM_FIELDS_FOR_SYNC, prefKey);

            if (MyUtils.isNetworkAvailable()) {
                GetCustomFields getCustomFields = new GetCustomFields(ctx, true, vortexTable);
                getCustomFields.execute(vortexTableId);
            }

            if (showProgressBar) {
                Toast.makeText(MyApplication.getContext(), localized_data_sent_2_rows, Toast.LENGTH_SHORT).show();
                ((AppCompatActivity)ctx).finish();
            }
        } else {
            if (showProgressBar) {
                MyDialogs.showOK(ctx, localized_failed_to_send_data_saved_for_sync + "\n\n" + this.getClass().getSimpleName());
            }
        }
    }
}
