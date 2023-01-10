package dc.gtest.vortex.api;

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

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_MEASUREMENTS_FOR_SYNC;

public class ToSendNewMeasurement extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final boolean finishActivity;

    private String prefKey;
    private int responseCode;

    public ToSendNewMeasurement(Context ctx, boolean finishActivity) {
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
        String newMeasurementsJsonString = MyPrefs.getStringWithFileName(PREF_FILE_ADDED_MEASUREMENTS_FOR_SYNC, prefKey, "");

        try {
            JSONObject jsonObject = new JSONObject(newMeasurementsJsonString);

            Log.e(LOG_TAG, "newMeasurementsJsonString: \n" + jsonObject.toString(2));

        } catch (Exception e) {
            e.printStackTrace();
        }

        String responseMessage = "";
        String responseBody = "";

        prefKey = params[0];
        String postBody = newMeasurementsJsonString; //MyPrefs.getStringWithFileName(PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC, prefKey, "");

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + "/Vortex.svc/InsertMeasurements";

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


        // TODO be sure this is not application/json
//
//        try {
//            URL url = new URL(connectToApiUrl);
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setReadTimeout(30000);
//            httpURLConnection.setConnectTimeout(30000);
//            httpURLConnection.setRequestMethod("POST");
////            httpURLConnection.setRequestProperty("Content-type","application/json; charset=UTF-8");
//            httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, ""));
//            httpURLConnection.setDoOutput(true);
//            httpURLConnection.setDoInput(true);
//
//            OutputStream outputStream = httpURLConnection.getOutputStream();
//
//            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
//
//            bufferedWriter.write(newMeasurementsJsonString);
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

            MyPrefs.removeStringWithFileName(PREF_FILE_ADDED_MEASUREMENTS_FOR_SYNC, prefKey);

            Toast.makeText(ctx, localized_data_sent_2_rows, Toast.LENGTH_LONG).show();

            // TODO improve refresh data from MeasurementsListActivity
//            if (MyUtils.isNetworkAvailable()) {
//                GetAssignments getAssignments = new GetAssignments(ctx);
//                getAssignments.execute();
//            }

            if (finishActivity) {
                ((Activity)ctx).finish();
            }
        } else {
            MyDialogs.showOK(ctx, localized_failed_to_send_data_saved_for_sync + "\n\n" + this.getClass().getSimpleName());
        }
    }
}
