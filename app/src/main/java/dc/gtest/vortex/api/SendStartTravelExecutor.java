package dc.gtest.vortex.api;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.UiThread;

import static dc.gtest.vortex.api.MyApi.API_UPDATE_ASSIGNMENT;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static dc.gtest.vortex.support.MyLocalization.localized_error_synchronizing;
import static dc.gtest.vortex.support.MyLocalization.localized_workorder_not_available;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_TRAVEL_STARTED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_START_TRAVEL_DATA_TO_SYNC;

public class SendStartTravelExecutor {

    // =========================================================
    // Instance fields
    // =========================================================
    private final WeakReference<Activity> activityRef;
    private ProgressBar mProgressBar;

    private final String assignmentId;
    private final boolean showProgressAndToast;
    private final int priority;

    // =========================================================
    // Constructor
    // =========================================================
    public SendStartTravelExecutor(
            @Nullable Activity activity,
            String assignmentId,
            boolean showProgressAndToast,
            int priority
    ) {
        this.activityRef = new WeakReference<>(activity);
        this.assignmentId = assignmentId;
        this.showProgressAndToast = showProgressAndToast;
        this.priority = priority;
    }

    // =========================================================
    // Public API
    // =========================================================
    public void execute() {

        // -----------------------------
        // UI thread → onPreExecute()
        // -----------------------------
        if (showProgressAndToast) {
            UiThread.run(() -> {
                Activity activity = activityRef.get();
                if (activity != null && !activity.isFinishing()) {
                    mProgressBar = activity.findViewById(R.id.progressBar);
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        // -----------------------------
        // Background execution
        // -----------------------------
        PriorityApiExecutors.IO.execute(
                new PriorityTask(priority) {

                    @Override
                    public void run() {
                        doApiCall();
                    }
                }
        );
    }

    // =========================================================
    // Background logic
    // =========================================================
    private void doApiCall() {

        int responseCode = -1;
        String responseMessage = "";
        String responseBody = "";

        String apiUrl =
                MyPrefs.getString(PREF_BASE_HOST_URL, "") +
                        API_UPDATE_ASSIGNMENT;

        String postBody =
                MyPrefs.getStringWithFileName(
                        PREF_FILE_START_TRAVEL_DATA_TO_SYNC,
                        assignmentId,
                        ""
                );

        try {
            Context ctx = getSafeContext();
            if (ctx == null) return;

            Bundle bundle =
                    MyApi.post(apiUrl, postBody, false, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLogs.showFullLog(
                "SendStartTravelExecutor",
                apiUrl,
                postBody,
                responseCode,
                responseMessage,
                responseBody
        );

        int finalCode = responseCode;
        String finalBody = responseBody;

        // -----------------------------
        // UI thread → onPostExecute()
        // -----------------------------
        UiThread.run(() -> onResult(finalCode, finalBody));
    }

    // =========================================================
    // UI result handling
    // =========================================================
    private void onResult(int responseCode,
                          @Nullable String responseBody) {

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        Context ctx = getSafeContext();
        if (ctx == null) return;

        if ("1".equals(responseBody)) {

            MyPrefs.removeStringWithFileName(
                    PREF_FILE_START_TRAVEL_DATA_TO_SYNC,
                    assignmentId
            );

            if (showProgressAndToast) {
                Toast.makeText(
                        ctx,
                        localized_data_sent_2_rows,
                        Toast.LENGTH_LONG
                ).show();
            }

        } else if ("2".equals(responseBody)) {

            MyPrefs.removeStringWithFileName(
                    PREF_FILE_START_TRAVEL_DATA_TO_SYNC,
                    assignmentId
            );

            MyPrefs.setBooleanWithFileName(
                    PREF_FILE_IS_TRAVEL_STARTED,
                    assignmentId,
                    false
            );

            if (showProgressAndToast) {
                Toast.makeText(
                        ctx,
                        localized_workorder_not_available,
                        Toast.LENGTH_LONG
                ).show();
            }

        } else {

            if (showProgressAndToast && activityRef.get() != null) {
                MyDialogs.showOK(
                        activityRef.get(),
                        localized_error_synchronizing +
                                "\n\nSendStartTravelExecutor"
                );
            }
        }
    }

    // =========================================================
    // Safe context resolution
    // =========================================================
    private @Nullable Context getSafeContext() {
        Activity a = activityRef.get();
        return (a != null)
                ? a.getApplicationContext()
                : MyApplication.getContext();
    }
}
