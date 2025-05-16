package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.data.ServicesData;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_SERVICES;
import static dc.gtest.vortex.api.MyApi.API_GET_SERVICES_FOR_ASSIGNMENT;
import static dc.gtest.vortex.api.MyApi.API_GET_SERVICES_FROM_PICKING;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_SERVICES;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ASSIGNMENT_SERVICES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PICKING_SERVICES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_SERVICES_FOR_SHOW;

public class GetServices extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;
    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private final String AssignmentId;
    private final String ProjectProductId;
    private final String ProductId;
    private final String CustomerId;
    private final Boolean isForNewAssigment;
    private final boolean fromPicking;

    public GetServices(String AssignmentId, String ProjectProductId, String ProductId, String CustomerId, Boolean isForNewAssignment, Context ctx, Boolean FromPicking) {
        this.AssignmentId = AssignmentId;
        this.ProjectProductId = ProjectProductId;
        this.ProductId = ProductId;
        this.CustomerId = CustomerId;
        this.isForNewAssigment = isForNewAssignment;
        this.fromPicking = FromPicking;
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        if (isForNewAssigment) {
            apiUrl = baseHostUrl + API_GET_SERVICES_FOR_ASSIGNMENT + CustomerId + "&ProjectProductId=" + ProjectProductId + "&ProductId=" + ProductId;
        }else if(fromPicking){
            apiUrl = baseHostUrl+ API_GET_SERVICES_FROM_PICKING + AssignmentId;
        }else{
            apiUrl = baseHostUrl+ API_GET_SERVICES + AssignmentId + "&ProjectProductId=" + ProjectProductId + "&ProductId=" + ProductId + "&CustomerId=" + CustomerId;
        }

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

            if (AssignmentId != "0") {
                if(fromPicking){
                    MyPrefs.setStringWithFileName(PREF_FILE_PICKING_SERVICES_FOR_SHOW, AssignmentId, responseBody);
                }else{
                    MyPrefs.setStringWithFileName(PREF_FILE_RELATED_SERVICES_FOR_SHOW, AssignmentId, responseBody);
                }
            }else if (isForNewAssigment){
                MyPrefs.setStringWithFileName(PREF_FILE_NEW_ASSIGNMENT_SERVICES_FOR_SHOW, AssignmentId, responseBody);
                ServicesData.generate(responseBody, AssignmentId, true);
            }else{
                MyPrefs.setString(PREF_DATA_SERVICES, responseBody);
                ServicesData.generate(responseBody, AssignmentId, false);
            }

            RecyclerView rvServices = ((Activity) ctx).findViewById(R.id.rvServices);
            if (rvServices != null && rvServices.getAdapter() != null) {
                rvServices.getAdapter().notifyDataSetChanged();
            }


        }
    }
}
