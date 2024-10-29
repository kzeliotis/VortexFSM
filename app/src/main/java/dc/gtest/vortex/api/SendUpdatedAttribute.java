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
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.api.MyApi.API_SEND_UPDATE_ATTRIBUTE_VALUES;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static dc.gtest.vortex.support.MyLocalization.localized_no_permission_write;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC;

public class SendUpdatedAttribute extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private String prefKey;
    private final String PpjId;
    private final String ValueId;

    private int responseCode;

    public SendUpdatedAttribute(Context ctx, String PpjId, String ValueId) {
        this.ctx = ctx;
        this.PpjId = PpjId;
        this.ValueId = ValueId;
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
        String responseBody = null;

        prefKey = params[0];

        String urlSuffix = MyPrefs.getStringWithFileName(PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC, prefKey, "");

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + API_SEND_UPDATE_ATTRIBUTE_VALUES + urlSuffix;

        try {
            Bundle bundle = MyApi.get(apiUrl);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
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

        if (responseBody != null && responseBody.equals("1")) {

            MyPrefs.removeStringWithFileName(PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC, prefKey);

            Toast.makeText(ctx, localized_data_sent_2_rows, Toast.LENGTH_LONG).show();

            if (MyUtils.isNetworkAvailable()) {
                GetProducts getProducts = new GetProducts(ctx, SELECTED_ASSIGNMENT.getAssignmentId(), true, "0", false, "");
                getProducts.execute();
            }
        } else if (responseBody != null && responseBody.equals("2")) {
            MyPrefs.removeStringWithFileName(PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC, prefKey);
            String newV = MyPrefs.getStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_new_" + PpjId, ValueId, "");
            String oldV = MyPrefs.getStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_old_" + PpjId, ValueId, "");

            MyPrefs.setStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_old_" + PpjId, ValueId, "");
            MyPrefs.setStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_new_" + PpjId, ValueId, oldV);

            MyDialogs.showOK(ctx, localized_no_permission_write);

        } else {
            MyDialogs.showOK(ctx, localized_failed_to_send_data_saved_for_sync+ "\n\n" + this.getClass().getSimpleName());
        }
    }
}
