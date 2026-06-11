package dc.gtest.vortex.api;

import static dc.gtest.vortex.api.MyApi.API_SEND_DIAGNOSTICS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyLocalization.localized_diagnostics_sent;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.ApiResultModel;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

public class SendDiagnostics extends AsyncTask<File, Void, Bundle> {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;
    private final String diagnosticsInfo;
    private String apiUrl;

    public SendDiagnostics(Context ctx, String DiagnosticsInfo) {
        this.ctx = ctx;
        this.diagnosticsInfo = DiagnosticsInfo;
    }

    @Override
    protected void onPreExecute() {
        mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Bundle doInBackground(File... params) {
        File zipFile = params[0];

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_SEND_DIAGNOSTICS;

        Bundle bundle = new Bundle();
        try {
            bundle = MyApi.postFile(apiUrl, zipFile, diagnosticsInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bundle;
    }

    @Override
    protected void onPostExecute(Bundle bundle) {
        int responseCode       = bundle.getInt(MY_API_RESPONSE_CODE);
        String responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
        String responseBody    = bundle.getString(MY_API_RESPONSE_BODY);

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(),
                apiUrl, "zip file upload", responseCode, responseMessage, responseBody);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseCode == 200) {
            Gson gson = new Gson();
            ApiResultModel apiResult = gson.fromJson(responseBody, ApiResultModel.class);

            if (apiResult != null && apiResult.getR().getResult().equals("Success")) {
                Toast.makeText(ctx,
                        localized_diagnostics_sent,
                        Toast.LENGTH_LONG).show();
            } else {
                String notes = apiResult != null ? apiResult.getR().getResultnotes() : "Unknown error";
                Toast.makeText(ctx,
                        localized_failed_to_send_data + "\n\r" + notes,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ctx,
                    localized_failed_to_send_data,
                    Toast.LENGTH_LONG).show();
        }
    }
}