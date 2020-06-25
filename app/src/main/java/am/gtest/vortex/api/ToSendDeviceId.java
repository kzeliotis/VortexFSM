package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.LoginActivity;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_VERIFY_IMEI_EXTERNAL_URL;
import static am.gtest.vortex.api.MyApi.API_VERIFY_IMEI_INTERNAL_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_DEVICE_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_REGISTERED_APK;

public class ToSendDeviceId extends AsyncTask<String,Void,String> {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    private String deviceID;
    private String connectToApiUrl;

    private int responseCodeMainUrl = 0;
    private int responseCodeAlternativeUrl = 0;

    public ToSendDeviceId(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        deviceID = params[0];
        connectToApiUrl = API_VERIFY_IMEI_EXTERNAL_URL + deviceID;

        Log.e(LOG_TAG, "------------------ connectToApiUrl: " + connectToApiUrl);

        try {
            URL url = new URL(connectToApiUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(30000);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, ""));
            httpURLConnection.setDoInput(true);

            responseCodeMainUrl = httpURLConnection.getResponseCode();
            InputStream inputStream;

            Log.e(LOG_TAG, "responseCode: " + responseCodeMainUrl + "; responseMessage: " + httpURLConnection.getResponseMessage());

            if (responseCodeMainUrl == 401) {
                return alternativeUrl();
            } else
                if (responseCodeMainUrl >= 400) {
                inputStream = httpURLConnection.getErrorStream();
            } else {
                inputStream = httpURLConnection.getInputStream();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return stringBuilder.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String dataFromDB) {
        Log.e(LOG_TAG, "dataFromDB: \n" + dataFromDB);

        Toast toast = Toast.makeText(ctx,
                "Main URL:   " + responseCodeMainUrl + "\nAlt. URL:   " + responseCodeAlternativeUrl,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        if (dataFromDB != null && dataFromDB.equals("1")) {
            MyPrefs.setBoolean(PREF_REGISTERED_APK, true);

            Intent intent = new Intent(ctx, LoginActivity.class);
            ctx.startActivity(intent);
            ((Activity) ctx).finish();
        } else {
            MyDialogs.showOK(ctx, ctx.getString(R.string.app_is_not_registered));
        }
    }

    private String alternativeUrl() {
        connectToApiUrl = API_VERIFY_IMEI_INTERNAL_URL + deviceID;

        try {
            URL url = new URL(connectToApiUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, ""));
            httpURLConnection.setDoInput(true);

            responseCodeAlternativeUrl = httpURLConnection.getResponseCode();
            InputStream inputStream;

            Log.e(LOG_TAG, "responseCode: " + responseCodeAlternativeUrl + "; responseMessage: " + httpURLConnection.getResponseMessage());

            if (responseCodeAlternativeUrl >= 400) {
                inputStream = httpURLConnection.getErrorStream();
            } else {
                inputStream = httpURLConnection.getInputStream();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return stringBuilder.toString().trim();

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return null;
    }
}
