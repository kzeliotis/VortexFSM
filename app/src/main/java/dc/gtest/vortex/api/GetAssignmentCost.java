package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_ASSIGNMENT_COST;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

public class GetAssignmentCost extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private final String assignmentId;


    public GetAssignmentCost(Context ctx, String AssignmentId) {
        this.ctx = ctx;
        this.assignmentId = AssignmentId;
            }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_ASSIGNMENT_COST + assignmentId;

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
        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no_body_for_get_request", responseCode, responseMessage, responseBody);

        if (responseCode == 200 && responseBody != null && !responseBody.isEmpty()) {

            responseBody = responseBody.replace("\\u000d", "\r");
            responseBody = responseBody.replace("\\u000a", "\n");

            StringBuilder sb = new StringBuilder(responseBody);
            sb.deleteCharAt(0);
            sb.deleteCharAt(responseBody.length()-2);

            responseBody = sb.toString();

            try {
              Activity activity = (Activity) ctx;
              new AlertDialog.Builder(activity)
                      .setMessage(responseBody)
                      .setPositiveButton("OK", (dialog, which) -> {
                          dialog.dismiss();
                      })
                      .show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }



}
