package dc.gtest.vortex.api;

import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_SHOW_USE_PT_OVERNIGHT_BUTTONS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_USE_PT_OVERNIGHT_BUTTONS;

public class GetShowUsePtOvernightButtons extends AsyncTask<String, Void, String > {

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_SHOW_USE_PT_OVERNIGHT_BUTTONS;

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
        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "No body for GET request.", responseCode, responseMessage, responseBody);

        if (responseCode == 200 && responseBody != null) {
            if (responseBody.equals("1")) {
                MyPrefs.setBoolean(PREF_SHOW_USE_PT_OVERNIGHT_BUTTONS, true);
            } else if (responseBody.equals("0")) {
                MyPrefs.setBoolean(PREF_SHOW_USE_PT_OVERNIGHT_BUTTONS, false);
            }
        }
    }
}
