package am.gtest.vortex.api;

import android.os.AsyncTask;
import android.os.Bundle;

import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_SEND_USER_COORDINATES;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static am.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

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
       // MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "No body for GET request.", responseCode, responseMessage, responseBody);
    }
}
