package dc.gtest.vortex.api;

import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_SEND_USER_COORDINATES;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEEP_GPS_LOG;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class SendCoords extends AsyncTask<String,Void,String> {

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    @Override
    protected String doInBackground(String... params) {
        String userName = MyPrefs.getString(PREF_USER_NAME, "");
        String lat = MyPrefs.getString(PREF_CURRENT_LAT, "");
        String lng = MyPrefs.getString(PREF_CURRENT_LNG, "");

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_SEND_USER_COORDINATES + "?username=" + userName + "&lat=" + lat + "&lon=" + lng;

        try {
            Bundle bundle = MyApi.get(apiUrl, null);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
            MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, e.toString(), responseCode, responseMessage, responseBody);
        }

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {
       if(MyPrefs.getBoolean(PREF_KEEP_GPS_LOG,  false)){
           MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "No body for GET request.", responseCode, responseMessage, responseBody);
       }

    }
}
