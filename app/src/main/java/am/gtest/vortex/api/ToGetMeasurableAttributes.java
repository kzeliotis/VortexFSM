package am.gtest.vortex.api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_DEVICE_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_MEASURABLE_ATTRIBUTES;

public class ToGetMeasurableAttributes extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String connectToApiUrl = baseHostUrl+ "/vortex.svc/GetMeasurableAttributes";

        try {
            URL url = new URL(connectToApiUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(30000);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, ""));
            httpURLConnection.setDoInput(true);

            int responseCode = httpURLConnection.getResponseCode();
            InputStream inputStream;

            Log.e(LOG_TAG, "responseCode: " + responseCode + "; responseMessage: " + httpURLConnection.getResponseMessage());

            if (responseCode >= 400) {
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
    protected void onPostExecute(String dataFromApi) {

        //Log.e(LOG_TAG, "dataFromApi: \n" + dataFromApi);

        if ( dataFromApi != null ) {

            MyPrefs.setString(PREF_MEASURABLE_ATTRIBUTES, dataFromApi);

//            try {
//                JSONArray jArrayDataFromApi = new JSONArray(dataFromApi);
//                Log.e(LOG_TAG, "jArrayDataFromApi: \n" + jArrayDataFromApi.toString(2));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }
}
