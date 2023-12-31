package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_ATTACHMENT_URL;
import static dc.gtest.vortex.api.MyApi.API_GET_MANUAL_FILE;
import static dc.gtest.vortex.api.MyApi.API_GET_REPORT_PREVIEW;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

public class GetReportPreview extends AsyncTask<String, Void, String > {

private int responseCode;
private final String assignmentId;
private final String blobAttachmentId;
private final String fileName;
private final String attachmentId;
private final String objectType;
@SuppressLint("StaticFieldLeak")
private final Context ctx;

public GetReportPreview(Context ctx, String AssignmentId, String BlobAttachmentId, String fileName, String AttachmentId, String ObjectType ) {
        this.ctx = ctx;
        this.assignmentId = AssignmentId;
        this.blobAttachmentId = BlobAttachmentId;
        this.fileName = fileName;
        this.attachmentId = AttachmentId;
        this.objectType = ObjectType;
        }

@Override
protected String doInBackground(String... params) {
        String responseMessage = "";
        String responseBody = null;

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = "";

        if (blobAttachmentId.length() > 0 && objectType.length() == 0) {
            apiUrl = baseHostUrl + API_GET_MANUAL_FILE + blobAttachmentId + "&fileName=" + fileName;
        } else if (blobAttachmentId.length() > 0 && objectType.length() > 0){
            apiUrl = baseHostUrl + API_GET_ATTACHMENT_URL + assignmentId + "&AttachmentId=" + attachmentId + "&BlobAttachmentId=" + blobAttachmentId + "&ObjectType=" + objectType;
        } else {
            apiUrl= baseHostUrl+ API_GET_REPORT_PREVIEW + assignmentId;
        }


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
            if (responseBody.contains("<div id=\"content\">")){
                responseBody = responseBody.split("<div id=\"content\">")[1];
            }
            Toast toast = Toast.makeText(MyApplication.getContext(), "Invalid URL \r\n " + responseBody, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


    }
}
}
