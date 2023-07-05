package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import dc.gtest.vortex.BuildConfig;
import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.PermGetLocation;

import static dc.gtest.vortex.api.MyApi.API_GET_AES_KEY;
import static dc.gtest.vortex.api.MyApi.API_GET_MOBILE_SETTINGS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;

import static dc.gtest.vortex.support.MyGlobals.PERMISSIONS_FINE_LOCATION;
import static dc.gtest.vortex.support.MyLocalization.localized_new_update_available;
import static dc.gtest.vortex.support.MyPrefs.PREF_API_CONNECTION_TIMEOUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_AZURE_CONNECTION_STRING;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DOWNLOAD_ALL_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_ENABLE_LOCATION_SERVICE;
import static dc.gtest.vortex.support.MyPrefs.PREF_GPS_PRIORITY;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEEP_GPS_LOG;
import static dc.gtest.vortex.support.MyPrefs.PREF_LOCATION_REFRESH_INTERVAL;
import static dc.gtest.vortex.support.MyPrefs.PREF_MANDATORY_SIGNATURE;
import static dc.gtest.vortex.support.MyPrefs.PREF_PROCESS_ASSIGNMENT_ON_SCAN;
import static dc.gtest.vortex.support.MyPrefs.PREF_SCROLLABLE_PROBLEM_DESCRIPTION;
import static dc.gtest.vortex.support.MyPrefs.PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_GET_ASSIGNMENT_COST;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_INSTALLATIONS_BUTTON;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_MANDATORY_TASKS_COMMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_SEND_REPORT_CHECKBOX;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_START_WORK;

public class GetAESKey extends AsyncTask<String, Void, String > {


    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public GetAESKey(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {

        String username = params[0];
        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_AES_KEY + username;

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
        // MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no_body_for_get_request", responseCode, responseMessage, responseBody);

    }
}
