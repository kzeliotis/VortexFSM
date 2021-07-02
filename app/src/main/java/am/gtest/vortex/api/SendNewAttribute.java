package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import am.gtest.vortex.R;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_SEND_NEW_ATTRIBUTE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static am.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static am.gtest.vortex.support.MyLocalization.localized_no_permission_write;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC;

public class SendNewAttribute extends AsyncTask<String, Void, String > {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final boolean finishActivity;

    private String prefKey;

    private int responseCode;

    public SendNewAttribute(Context ctx, boolean finishActivity) {
        this.ctx = ctx;
        this.finishActivity = finishActivity;
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
        String responseMessage = "";
        String responseBody = "";

        prefKey = params[0];
        String postBody = MyPrefs.getStringWithFileName(PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC, prefKey, "");

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + API_SEND_NEW_ATTRIBUTE;

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, true);

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

            MyPrefs.removeStringWithFileName(PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC, prefKey);

            Toast.makeText(ctx, localized_data_sent_2_rows, Toast.LENGTH_LONG).show();

            // no need as this API is called from a page which is going to be closed.
//            if (MyUtils.isNetworkAvailable()) {
//                GetProducts getProducts = new GetProducts(ctx);
//                getProducts.execute();
//            }

            if (finishActivity) {
                ((Activity) ctx).finish();
            }
        } else if (responseBody != null && responseBody.equals("2") ) {
            MyPrefs.removeStringWithFileName(PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC, prefKey);
            MyDialogs.showOK(ctx, localized_no_permission_write);
        } else {
            MyDialogs.showOK(ctx, localized_failed_to_send_data_saved_for_sync+ "\n\n" + this.getClass().getSimpleName());
        }
    }
}
