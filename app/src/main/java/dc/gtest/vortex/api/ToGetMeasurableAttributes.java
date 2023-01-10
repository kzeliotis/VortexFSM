package dc.gtest.vortex.api;

import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_MEASURABLE_ATTRIBUTES;

public class ToGetMeasurableAttributes extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private int responseCode;

    @Override
    protected String doInBackground(String... params) {

        String responseMessage = "";
        String responseBody = "";

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + "/vortex.svc/GetMeasurableAttributes";

        try {
            Bundle bundle = MyApi.get(apiUrl);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseBody;

//        try {
//            URL url = new URL(connectToApiUrl);
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setReadTimeout(30000);
//            httpURLConnection.setConnectTimeout(30000);
//            httpURLConnection.setRequestMethod("GET");
//            httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, ""));
//            httpURLConnection.setDoInput(true);
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
