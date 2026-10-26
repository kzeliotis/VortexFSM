package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dc.gtest.vortex.data.MandatoryTasksData;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_SEND_MANDATORY_TASKS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyLocalization.localized_error_synchronizing;
import static dc.gtest.vortex.support.MyLocalization.localized_resend_mandatory_steps_successful;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CHECK_OUT_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_MANDATORY_TASKS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_SEND_REPORT_VALUE_FOR_SYNC;

public class SendMandatoryTasks extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    // assignments whose steps are being sent now (not resend), so the send from SendCheckOut.onPostExecute does not duplicate one already running
    private static final Set<String> SENDING = ConcurrentHashMap.newKeySet();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    private String assignmentId;
    private boolean Resend;
    private boolean skipped;
    private boolean marked;

    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public SendMandatoryTasks(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {

        assignmentId = params[0];

        String NoEmail = params[1];

        if (NoEmail.equals("true")){
            Resend = true;
        }

        if (!Resend) {
            // the steps must not reach the server before the checkout: they stay in the queue, SendCheckOut sends them when it succeeds
            if (!MyPrefs.getStringWithFileName(PREF_FILE_CHECK_OUT_DATA_TO_SYNC, assignmentId, "").isEmpty()) {
                skipped = true;
                MyLogs.appendFile_FullLog(LOG_TAG, "AssignmentId " + assignmentId + ": not sent, checkout not synchronized yet", "MandatoryTasksQueue");
                return null;
            }

            // already sent by another task while this one was waiting
            if (MyPrefs.getStringWithFileName(PREF_FILE_MANDATORY_TASKS_FOR_SYNC, assignmentId, "").isEmpty()) {
                skipped = true;
                return null;
            }

            if (!SENDING.add(assignmentId)) {
                skipped = true;
                MyLogs.appendFile_FullLog(LOG_TAG, "AssignmentId " + assignmentId + ": not sent, already sending", "MandatoryTasksQueue");
                return null;
            }
            marked = true;
        }

        String sendReport = MyPrefs.getStringWithFileName(PREF_FILE_SEND_REPORT_VALUE_FOR_SYNC, assignmentId, "1");

        String mandatoryTasks = MyPrefs.getStringWithFileName(PREF_FILE_MANDATORY_TASKS_FOR_SYNC, assignmentId, "");

        postBody = MandatoryTasksData.buildRequestBody(assignmentId, NoEmail, sendReport, mandatoryTasks);

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");


        apiUrl = baseHostUrl + API_SEND_MANDATORY_TASKS;

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {

        if (skipped) {
            return;
        }

        try {
            MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);

            if (responseBody != null && responseBody.equals("1") ) {
                MyPrefs.removeStringWithFileName(PREF_FILE_MANDATORY_TASKS_FOR_SYNC, assignmentId);
                MyPrefs.removeStringWithFileName(PREF_FILE_SEND_REPORT_VALUE_FOR_SYNC, assignmentId);
                if (Resend){
                    Toast.makeText(ctx, localized_resend_mandatory_steps_successful, Toast.LENGTH_LONG).show();
                }

            } else {
                if (Resend){
                    Toast.makeText(ctx, localized_error_synchronizing, Toast.LENGTH_LONG).show();
                }
            }
        } finally {
            if (marked) {
                SENDING.remove(assignmentId);
            }
        }
    }

    @Override
    protected void onCancelled(String responseBody) {
        if (marked) {
            SENDING.remove(assignmentId);
        }
    }
}
