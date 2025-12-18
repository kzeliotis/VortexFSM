package dc.gtest.vortex.api;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;

import dc.gtest.vortex.R;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.data.HistoryData;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.StartupLoadTracker;
import dc.gtest.vortex.support.UiThread;

import static dc.gtest.vortex.api.MyApi.API_GET_HISTORY_ASSIGNMENTS;
import static dc.gtest.vortex.api.MyApi.API_GET_SUB_ASSIGNMENTS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.HISTORY_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_no_history;
import static dc.gtest.vortex.support.MyLocalization.localized_no_subassignments;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_HISTORY_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_HISTORY_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_SUBASSIGNMENTS_DATA;

public class GetHistoryExecutor {

    // =========================================================
    // Optional global semaphore (null = unlimited)
    // =========================================================
    private static Semaphore SEMAPHORE;

    public static void setSemaphore(@Nullable Semaphore semaphore) {
        SEMAPHORE = semaphore;
    }

    // =========================================================
    // Instance fields
    // =========================================================
    private final WeakReference<Activity> activityRef;
    private ProgressBar mProgressBar;

    private final String assignmentId;
    private final boolean hideProgress;
    private final boolean subAssignments;
    private final String projectInstallationId;
    private final int priority;
    private final boolean useStartupTracker;

    // =========================================================
    // Constructor
    // =========================================================
    public GetHistoryExecutor(
            @Nullable Activity activity,
            String assignmentId,
            boolean hideProgress,
            boolean subAssignments,
            String projectInstallationId,
            int priority,
            boolean useStartupTracker
    ) {
        this.activityRef = new WeakReference<>(activity);
        this.assignmentId = assignmentId;
        this.hideProgress = hideProgress;
        this.subAssignments = subAssignments;
        this.projectInstallationId = projectInstallationId;
        this.priority = priority;
        this.useStartupTracker = useStartupTracker;
    }

    // =========================================================
    // Public API
    // =========================================================
    public void execute() {

        if(!useStartupTracker){
            // UI thread → onPreExecute()
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


        // Background execution
        PriorityApiExecutors.IO.execute(new PriorityTask(priority) {
            @Override
            public void run() {
                try {
                    if (SEMAPHORE != null) {
                        SEMAPHORE.acquire(); // blocks background thread only
                    }

                    doApiCall();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                } finally {
                    if (SEMAPHORE != null) {
                        SEMAPHORE.release();
                    }
                }
            }
        });
    }

    // =========================================================
    // Background logic (network + persistence)
    // =========================================================
    private void doApiCall() {

        int responseCode = -1;
        String responseMessage = "";
        String responseBody = "";
        String apiUrl = "";

        try {
            Context ctx = getSafeContext();
            if (ctx == null) return;

            String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");

            if (subAssignments) {
                apiUrl = baseHostUrl + API_GET_SUB_ASSIGNMENTS + assignmentId;
            } else {
                apiUrl = baseHostUrl + API_GET_HISTORY_ASSIGNMENTS + assignmentId;
                if (!"0".equals(projectInstallationId)) {
                    apiUrl += "&ProjectInstallationId=" + projectInstallationId;
                }
            }

            Bundle bundle = MyApi.get(apiUrl, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLogs.showFullLog(
                "myLogs: " + this.getClass().getSimpleName(),
                apiUrl,
                "no body for get request",
                responseCode,
                responseMessage,
                responseBody
        );

        // Save response in background (same as AsyncTask)
        if (responseCode >= 200 && responseCode < 300 &&
                responseBody != null &&
                !responseBody.equals("") &&
                !responseBody.equals("null") &&
                !responseBody.equals("{}")) {

            if (subAssignments) {
                MyPrefs.setStringWithFileName(PREF_FILE_SUBASSIGNMENTS_DATA, assignmentId, responseBody);
            } else {
                if (!"0".equals(projectInstallationId)) {
                    MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATION_HISTORY_DATA, projectInstallationId, responseBody);
                } else {
                    MyPrefs.setStringWithFileName(PREF_FILE_HISTORY_DATA, assignmentId, responseBody);
                }
            }
        }

        String finalBody = responseBody;

        // UI thread → onPostExecute()
        UiThread.run(() -> onResult(finalBody));
    }

    // =========================================================
    // UI result handling
    // =========================================================
    private void onResult(@Nullable String responseBody) {

        if (hideProgress && mProgressBar != null && !useStartupTracker) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (useStartupTracker){
            StartupLoadTracker.jobDone();
        }

        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        RecyclerView rvHistoryList = activity.findViewById(R.id.rvHistoryList);
        if (rvHistoryList != null && rvHistoryList.getAdapter() != null) {

            // Keep same behavior: generate on UI thread (you can move heavy parsing to background later)
            HistoryData.generate(responseBody);

            if (HISTORY_LIST.size() == 0) {
                Toast.makeText(
                        MyApplication.getContext(),
                        subAssignments ? localized_no_subassignments : localized_no_history,
                        Toast.LENGTH_LONG
                ).show();
            }

            rvHistoryList.getAdapter().notifyDataSetChanged();
        }
    }

    // =========================================================
    // Safe context resolution
    // =========================================================
    private @Nullable Context getSafeContext() {
        Activity a = activityRef.get();
        return (a != null) ? a.getApplicationContext() : MyApplication.getContext();
    }
}
