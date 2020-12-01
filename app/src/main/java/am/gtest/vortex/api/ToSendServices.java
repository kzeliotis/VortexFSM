package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_DEVICE_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_USED_SERVICES_FOR_SYNC;

public class ToSendServices extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final boolean finishActivity;

    private String prefKey;
    private int responseCode;

    public ToSendServices(Context ctx, boolean finishActivity) {
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
        prefKey = params[0];

        String servicesJsonString = MyPrefs.getStringWithFileName(PREF_FILE_USED_SERVICES_FOR_SYNC, prefKey, "");

        Log.e(LOG_TAG, "servicesJsonString: \n" + servicesJsonString);

        try {
            JSONObject servicesJson = new JSONObject(servicesJsonString);
            Log.e(LOG_TAG, "servicesJson: \n" + servicesJson.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String responseMessage = "";
        String responseBody = "";

        prefKey = params[0];
        String postBody = servicesJsonString;

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + "/Vortex.svc/InsertServices";

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

//        Log.e(LOG_TAG, "connectToApiUrl: \n" + connectToApiUrl);

//        try {
//            URL url = new URL(connectToApiUrl);
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setReadTimeout(30000);
//            httpURLConnection.setConnectTimeout(30000);
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.setRequestProperty("Content-type","application/json; charset=UTF-8");
//            httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, ""));
//            httpURLConnection.setDoOutput(true);
//            httpURLConnection.setDoInput(true);
//
//            OutputStream outputStream = httpURLConnection.getOutputStream();
//
//            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
//
//            bufferedWriter.write(servicesJsonString);
//            bufferedWriter.flush();
//            bufferedWriter.close();
//            outputStream.close();
//
//            int responseCode = httpURLConnection.getResponseCode();
//            InputStream inputStream;
//
//            Log.e(LOG_TAG, "responseCode: " + responseCode + "; responseMessage: " + httpURLConnection.getResponseMessage());
//
//            if (responseCode >= 400) {
//                inputStream = httpURLConnection.getErrorStream();
//            } else {
//                inputStream = httpURLConnection.getInputStream();
//            }
//
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line).append("\n");
//            }
//
//            bufferedReader.close();
//            inputStream.close();
//            httpURLConnection.disconnect();
//
//            return stringBuilder.toString().trim();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
    }

    @Override
    protected void onPostExecute(String dataFromDB) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        Log.e(LOG_TAG, "dataFromDB: \n" + dataFromDB);

        if (dataFromDB != null && dataFromDB.equals("1") ) {

            MyPrefs.removeStringWithFileName(PREF_FILE_USED_SERVICES_FOR_SYNC, prefKey);

            Toast.makeText(ctx, localized_data_sent_2_rows, Toast.LENGTH_LONG).show();

//            // TODO improve refresh data from ServicesListActivity
//            if (MyUtils.isNetworkAvailable()) {
//                GetAssignments getAssignments = new GetAssignments(ctx);
//                getAssignments.execute();
//            }

            if (finishActivity) {
                ((Activity)ctx).finish();
            }
        } else {
            MyDialogs.showOK(ctx, localized_failed_to_send_data_saved_for_sync+ "\n\n" + this.getClass().getSimpleName());
        }
    }
}
