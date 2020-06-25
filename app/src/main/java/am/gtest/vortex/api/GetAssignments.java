package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.AssignmentsActivity;
import am.gtest.vortex.adapters.AssignmentsRvAdapter;
import am.gtest.vortex.application.MyApplication;
import am.gtest.vortex.data.AssignmentsData;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_GET_ASSIGNMENTS;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyGlobals.ASSIGNMENTS_LIST;
import static am.gtest.vortex.support.MyLocalization.localized_no_assignments;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENTS;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_IS_LOGGED_IN;
import static am.gtest.vortex.support.MyPrefs.PREF_PASSWORD;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class GetAssignments extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private int responseCode;

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final boolean downloadAllData;

    public GetAssignments(Context ctx, boolean downloadAllData) {
        this.ctx = ctx;
        this.downloadAllData = downloadAllData;

        Log.e(LOG_TAG, "============================ downloadAllData: " + downloadAllData + "; from: " + ctx.getClass().getSimpleName());
    }

    @Override
    protected void onPreExecute() {
        mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String responseMessage = "";
        String responseBody = "";

        String userName = MyPrefs.getString(PREF_USER_NAME, "");
        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String PassWD = MyPrefs.getStringWithFileName(PREF_PASSWORD,  "1", "");
        String apiUrl = baseHostUrl + API_GET_ASSIGNMENTS + userName; //+ "&password=" + PassWD;

        try {
            Bundle bundle = MyApi.get(apiUrl);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
//            if(e.toString().contains("SocketTimeoutException")) {
//                Intent intent = new Intent(ctx, AssignmentsActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra(KEY_DOWNLOAD_ALL_DATA, false);
//                ctx.startActivity(intent);
//            }
            e.printStackTrace();
        }

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for get request", responseCode, responseMessage, responseBody);

        return responseBody;
    }

    @Override
    protected void onPostExecute(String responseBody) {

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseCode == 200 && responseBody != null ) {
            MyPrefs.setString(PREF_DATA_ASSIGNMENTS, responseBody);
            AssignmentsData.generate(ctx);
            boolean LoggedIn = MyPrefs.getBoolean(PREF_KEY_IS_LOGGED_IN, false);

            if (ASSIGNMENTS_LIST.size() == 0 && LoggedIn) {
                Toast.makeText(MyApplication.getContext(), localized_no_assignments, Toast.LENGTH_LONG).show();
            }

            if (downloadAllData) {
                // get and save products data
                for (int i = 0; i < ASSIGNMENTS_LIST.size(); i++) {
                    GetProducts getProducts = new GetProducts(ctx, ASSIGNMENTS_LIST.get(i).getAssignmentId(), false, "0");
                    getProducts.execute();
                }

                // get and save history data
                // create project ids array to avoid multiple calls to the same history API
                List<String> AssignmentIds = new ArrayList<>();
                for (int i = 0; i < ASSIGNMENTS_LIST.size(); i++) {
                    if (!AssignmentIds.contains(ASSIGNMENTS_LIST.get(i).getAssignmentId())) {
                        AssignmentIds.add(ASSIGNMENTS_LIST.get(i).getAssignmentId());
                    }
                }
                for (int i = 0; i < AssignmentIds.size(); i++) {

                    boolean hideProgress = false;

                    if (i == AssignmentIds.size() - 1) {
                        hideProgress = true;
                    }
                    String AssId = AssignmentIds.get(i);
                    if (!AssId.contains("-")) {
                        GetHistory getHistory = new GetHistory(ctx, AssignmentIds.get(i), hideProgress, false, "0");
                        getHistory.execute();
                    }

                }

                Log.e(LOG_TAG, "================= ASSIGNMENTS_LIST.size(): " + ASSIGNMENTS_LIST.size() + "; projectIds.size(): " + AssignmentIds.size());
            }
        }

        RecyclerView rvAssignments = ((AppCompatActivity)ctx).findViewById(R.id.rvAssignments);

        if (rvAssignments != null) {

            AssignmentsRvAdapter assignmentsRvAdapter = (AssignmentsRvAdapter) rvAssignments.getAdapter();

            if (assignmentsRvAdapter != null) {
                assignmentsRvAdapter.notifyDataSetChanged();
                assignmentsRvAdapter.getFilter().filter(AssignmentsActivity.searchedText);
            }
        }
    }
}
