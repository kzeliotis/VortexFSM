package dc.gtest.vortex.api;

import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_SEND_USE_PT_OVERNIGHT;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_USE_PT_OVERNIGHT_FOR_SYNC;

public class SendUsePTOvernight extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private String prefKey;

    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    @Override
    protected String doInBackground(String... params) {

        prefKey = params[0];

        postBody = MyPrefs.getStringWithFileName(PREF_FILE_USE_PT_OVERNIGHT_FOR_SYNC, prefKey, "");

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_SEND_USE_PT_OVERNIGHT;

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false);

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

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);

        if (responseBody != null && responseBody.equals("1") ) {
            MyPrefs.removeStringWithFileName(PREF_FILE_USE_PT_OVERNIGHT_FOR_SYNC, prefKey);
        }
    }
}
