package dc.gtest.vortex.api;

import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.data.ServicesData;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_SERVICES;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_SERVICES;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_SERVICES_FOR_SHOW;

public class GetServices extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private final String AssignmentId;
    private final String ProjectProductId;
    private final String ProductId;
    private final String CustomerId;

    public GetServices(String AssignmentId, String ProjectProductId, String ProductId, String CustomerId) {
        this.AssignmentId = AssignmentId;
        this.ProjectProductId = ProjectProductId;
        this.ProductId = ProductId;
        this.CustomerId = CustomerId;
    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl+ API_GET_SERVICES + AssignmentId + "&ProjectProductId=" + ProjectProductId + "&ProductId=" + ProductId + "&CustomerId=" + CustomerId;

        try {
            Bundle bundle = MyApi.get(apiUrl);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for get request", responseCode, responseMessage, responseBody);

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {

//        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for get request", responseCode, responseMessage, responseBody);

        if ( responseBody != null && !responseBody.equals("")) {

            if (AssignmentId != "0"){
                MyPrefs.setStringWithFileName(PREF_FILE_RELATED_SERVICES_FOR_SHOW, AssignmentId, responseBody);
            }else{
                MyPrefs.setString(PREF_DATA_SERVICES, responseBody);
                ServicesData.generate(responseBody, AssignmentId);
            }

        }
    }
}
