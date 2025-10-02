package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.data.AssignmentIndicatorsData;
import dc.gtest.vortex.data.AssignmentTypesData;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_ASSIGNMENT_INDICATORS;
import static dc.gtest.vortex.api.MyApi.API_GET_ASSIGNMENT_TYPES;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENT_INDICATORS;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENT_TYPES;

public class GetAssignmentIndicators extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    //private final Context ctx;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public GetAssignmentIndicators() {

    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_ASSIGNMENT_INDICATORS;

        try {
            Bundle bundle = MyApi.get(apiUrl, null);

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
//        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, MyGlobals.CONST_NO_BODY_FOR_GET_REQUEST, responseCode, responseMessage, responseBody);

        if (responseCode == 200 && responseBody != null && !responseBody.isEmpty()) {
            MyPrefs.setString(PREF_DATA_ASSIGNMENT_INDICATORS, responseBody);
            AssignmentIndicatorsData.generate(responseBody);
        }
    }

}
