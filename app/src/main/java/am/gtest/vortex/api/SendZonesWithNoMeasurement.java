package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import am.gtest.vortex.R;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.api.MyApi.API_SEND_UPDATE_ATTRIBUTE_VALUES;
import static am.gtest.vortex.api.MyApi.API_SEND_ZONES_WITH_NO_MEASUREMENTS;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.ZONES_WITH_NO_MEASUREMENTS_MAP;
import static am.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static am.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static am.gtest.vortex.support.MyLocalization.localized_no_permission_write;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_WITH_NO_MEASUREMENTS;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC;


public class SendZonesWithNoMeasurement extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private String prefKey;
    private final String assignmentId;

    private int responseCode;

    public SendZonesWithNoMeasurement(Context ctx, String AssignmentId) {
        this.ctx = ctx;
        this.assignmentId = AssignmentId;
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

        prefKey = assignmentId;

        String Zoneids = ""; // MyPrefs.getStringWithFileName(PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC, prefKey, "");
        Map<String, List<String>> ZONES_WITH_NO_MEASUREMENTS = new HashMap<>();
        if (MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC, assignmentId, "").length() > 0) {
            ZONES_WITH_NO_MEASUREMENTS = new Gson().fromJson(MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC, assignmentId, ""), new TypeToken<HashMap<String, List<String>>>(){}.getType());
        }
        List<String> zoneIds_without = new ArrayList<>();
        if (ZONES_WITH_NO_MEASUREMENTS != null && ZONES_WITH_NO_MEASUREMENTS.containsKey(assignmentId)) {
            zoneIds_without = ZONES_WITH_NO_MEASUREMENTS.get(assignmentId);
        }

        StringBuilder sb = new StringBuilder();
        for (String s : zoneIds_without)
        {
            sb.append(s);
            sb.append(", ");
        }

        Zoneids = sb.toString();
        Zoneids = Zoneids.substring(0, Zoneids.length()-2);

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + API_SEND_ZONES_WITH_NO_MEASUREMENTS + assignmentId + "&ZoneIds=" + Zoneids;

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

            MyPrefs.removeStringWithFileName(PREF_FILE_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC, prefKey);

            Toast.makeText(ctx, localized_data_sent_2_rows, Toast.LENGTH_LONG).show();

        } else {
            MyDialogs.showOK(ctx, localized_failed_to_send_data_saved_for_sync + "\n\n" + this.getClass().getSimpleName());
        }
    }
}