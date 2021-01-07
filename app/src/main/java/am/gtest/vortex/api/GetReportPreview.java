package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import am.gtest.vortex.application.MyApplication;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_GET_REPORT_PREVIEW;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyLocalization.localized_no_manual;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

public class GetReportPreview extends AsyncTask<String, Void, String > {

private int responseCode;
private final String assignmentId;
@SuppressLint("StaticFieldLeak")
private final Context ctx;

public GetReportPreview(Context ctx, String AssignmentId) {
        this.ctx = ctx;
        this.assignmentId = AssignmentId;
        }

@Override
protected String doInBackground(String... params) {
        String responseMessage = "";
        String responseBody = null;

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl+ API_GET_REPORT_PREVIEW + assignmentId;

        try {
        Bundle bundle = MyApi.get(apiUrl);

        responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
        responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
        responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
        e.printStackTrace();
        }

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for get request", responseCode, responseMessage, responseBody);

        return responseBody;
        }

@Override
protected void onPostExecute(String responseBody) {


    if ( responseBody != null ) {

        try
        {
            responseBody = responseBody.replaceAll("\\\\", "");
            responseBody = responseBody.replace("\"", "");
            Uri uri = Uri.parse(responseBody);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle b = new Bundle();
            b.putBoolean("new_window", true); //sets new window
            intent.putExtras(b);
            ctx.startActivity(intent);
        } catch (Exception ex) {
            Toast toast = Toast.makeText(MyApplication.getContext(), "Invalid URL \r\n " + responseBody, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


    }
}
}
