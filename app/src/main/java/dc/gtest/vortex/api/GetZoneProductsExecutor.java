package dc.gtest.vortex.api;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;

import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.ZoneProductsRvAdapter;
import dc.gtest.vortex.data.ZoneProductsData;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.UiThread;

import static dc.gtest.vortex.api.MyApi.API_GET_ZONE_PRODUCTS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

public class GetZoneProductsExecutor {

    // =========================================================
    // Optional global semaphore (null = unlimited concurrency)
    // =========================================================
    private static Semaphore SEMAPHORE;

    public static void setSemaphore(@Nullable Semaphore semaphore) {
        SEMAPHORE = semaphore;
    }

    // =========================================================
    // Instance fields
    // =========================================================
    private final WeakReference<Activity> activityRef;
    private final ZoneProductsRvAdapter zoneProductsRvAdapter;

    private ProgressBar mProgressBar;

    private final String assignmentId;
    private final String projectId;
    private final int priority;

    // =========================================================
    // Constructor
    // =========================================================
    public GetZoneProductsExecutor(
            @Nullable Activity activity,
            @Nullable ZoneProductsRvAdapter adapter,
            String assignmentId,
            String projectId,
            int priority
    ) {
        this.activityRef = new WeakReference<>(activity);
        this.zoneProductsRvAdapter = adapter;
        this.assignmentId = assignmentId;
        this.projectId = projectId;
        this.priority = priority;
    }

    // =========================================================
    // Public API
    // =========================================================
    public void execute(String zoneId) {

        // -----------------------------
        // UI thread: mimic onPreExecute
        // -----------------------------
        UiThread.run(() -> {
            Activity activity = activityRef.get();
            if (activity != null && !activity.isFinishing()) {
                mProgressBar = activity.findViewById(R.id.progressBar);
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // -----------------------------
        // Background execution
        // -----------------------------
        PriorityApiExecutors.IO.execute(
                new PriorityTask(priority) {

                    @Override
                    public void run() {

                        try {
                            // Optional concurrency limit
                            if (SEMAPHORE != null) {
                                SEMAPHORE.acquire();
                            }

                            doApiCall(zoneId);

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();

                        } finally {
                            if (SEMAPHORE != null) {
                                SEMAPHORE.release();
                            }
                        }
                    }
                }
        );
    }

    // =========================================================
    // Background logic
    // =========================================================
    private void doApiCall(String zoneId) {

        int responseCode = -1;
        String responseBody = null;
        String apiUrl;

        try {
            Context ctx = getSafeContext();
            if (ctx == null) return;

            apiUrl =
                    MyPrefs.getString(PREF_BASE_HOST_URL, "") +
                            API_GET_ZONE_PRODUCTS +
                            zoneId;

            Bundle bundle = MyApi.get(apiUrl, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

            MyLogs.showFullLog(
                    "GetZoneProductsExecutor",
                    apiUrl,
                    "GET",
                    responseCode,
                    null,
                    responseBody
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        int finalCode = responseCode;
        String finalBody = responseBody;

        // -----------------------------
        // UI thread: mimic onPostExecute
        // -----------------------------
        UiThread.run(() -> onResult(finalCode, finalBody, zoneId));
    }

    // =========================================================
    // UI result handling
    // =========================================================
    private void onResult(int responseCode,
                          String responseBody,
                          String zoneId) {

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseCode == 200 && responseBody != null) {

            ZoneProductsData.generate(
                    responseBody,
                    zoneId,
                    assignmentId,
                    projectId
            );

            if (zoneProductsRvAdapter != null) {
                zoneProductsRvAdapter.notifyDataSetChanged();
            }
        }
    }

    // =========================================================
    // Context resolution (safe)
    // =========================================================
    private @Nullable Context getSafeContext() {
        Activity a = activityRef.get();
        return (a != null)
                ? a.getApplicationContext()
                : MyApplication.getContext();
    }
}
