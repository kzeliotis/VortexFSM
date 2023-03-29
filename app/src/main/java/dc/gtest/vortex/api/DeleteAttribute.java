package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.api.MyApi.API_DELETE_ATTRIBUTE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_attribute_deleted;
import static dc.gtest.vortex.support.MyLocalization.localized_delete_failed;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

public class DeleteAttribute extends AsyncTask<String, Void, String > {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private int responseCode;

    public DeleteAttribute(Context ctx) {
        this.ctx = ctx;
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

        String projectProductId = params[0];
        String attributeId = params[1];
        String ValueId = params [2];

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + API_DELETE_ATTRIBUTE + projectProductId + "&AttributeId=" + attributeId + "&ValueId=" + ValueId;

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

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseBody != null && responseBody.equals("1")) {

            Toast.makeText(ctx, localized_attribute_deleted, Toast.LENGTH_LONG).show();

            if (MyUtils.isNetworkAvailable()) {
                GetProducts getProducts = new GetProducts(ctx, SELECTED_ASSIGNMENT.getAssignmentId(), true, "0", false);
                getProducts.execute();
            }

        } else {
            MyDialogs.showOK(ctx, localized_delete_failed);
        }
    }
}
