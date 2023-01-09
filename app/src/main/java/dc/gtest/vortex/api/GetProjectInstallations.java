package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.InstallationRvAdapter;
import dc.gtest.vortex.data.ProjectInstallationsData;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_PROJECT_INSTALLATIONS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_INSTALLATIONS_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATIONS_DATA_FOR_SHOW;

public class GetProjectInstallations extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final InstallationRvAdapter installationRvAdapter;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private final boolean refresh;


    public GetProjectInstallations(Context ctx, InstallationRvAdapter installationRvAdapter, boolean refresh) {
        this.ctx = ctx;
        this.installationRvAdapter = installationRvAdapter;
        this.refresh = refresh;
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
        String projectId = params[0];

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_PROJECT_INSTALLATIONS + projectId;

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
        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "", responseCode, responseMessage, responseBody);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseCode == 200 && responseBody != null ) {

            MyPrefs.setString(PREF_DATA_INSTALLATIONS_LIST, responseBody);

            String AssignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
            MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATIONS_DATA_FOR_SHOW, AssignmentId, responseBody);

            ProjectInstallationsData.generate(refresh);

            if (installationRvAdapter != null) {
                installationRvAdapter.notifyDataSetChanged();
            }
        }
    }


}
