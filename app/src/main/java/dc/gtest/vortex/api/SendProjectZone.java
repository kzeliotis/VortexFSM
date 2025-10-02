package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import dc.gtest.vortex.R;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.api.MyApi.API_SEND_INSTALLATION_ZONE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_INSTALLATION_ZONES_FOR_SYNC;

public class SendProjectZone extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private final ProgressBar mProgressBar;

    private final String prefKey;
    private final boolean showProgressBar;

    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody = null;
    private String no_internet_message;

    public SendProjectZone(Context ctx, String prefKey, boolean showProgressBar) {
        this.ctx = ctx;
        this.prefKey = prefKey;
        this.showProgressBar = showProgressBar;
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

        apiUrl = baseHostUrl + API_SEND_INSTALLATION_ZONE;
        postBody = MyPrefs.getStringWithFileName(PREF_FILE_NEW_INSTALLATION_ZONES_FOR_SYNC, prefKey, "");

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false, ctx);

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

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);


        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseBody != null && responseBody.equals("1")) {

            String zoneString = MyPrefs.getStringWithFileName(PREF_FILE_NEW_INSTALLATION_ZONES_FOR_SYNC, prefKey, "");
            String projectInstallationId = "0";

            try {
                JSONArray zoneArray = new JSONArray("[" + zoneString.replace("\n","") + "]");
                JSONObject zoneObject = zoneArray.getJSONObject(0);
                projectInstallationId = MyJsonParser.getStringValue(zoneObject, "ProjectInstallationId", "");
            }catch (Exception ex){
                ex.printStackTrace();
            }

            MyPrefs.removeStringWithFileName(PREF_FILE_NEW_INSTALLATION_ZONES_FOR_SYNC, prefKey);

            if (MyUtils.isNetworkAvailable()) {
                GetZones getZones = new GetZones(ctx, null, true, projectInstallationId);
                getZones.execute("0");
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

