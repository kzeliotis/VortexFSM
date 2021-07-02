package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import am.gtest.vortex.R;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.api.MyApi.API_DELETE_PRODUCT;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static am.gtest.vortex.support.MyLocalization.localized_delete_failed;
import static am.gtest.vortex.support.MyLocalization.localized_no_permission_delete;
import static am.gtest.vortex.support.MyLocalization.localized_product_deleted;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

public class DeleteProduct extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;
    private final String AssignmentId;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private int responseCode;

    public DeleteProduct(Context ctx, String AssignmentId) {
        this.ctx = ctx;
        this.AssignmentId = AssignmentId;
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

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + API_DELETE_PRODUCT + projectProductId + "&AssignmentId=" + AssignmentId + "&WarehouseId=" + MyPrefs.getString(MyPrefs.PREF_WAREHOUSEID, "0");

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

            MyDialogs.showOK(ctx, localized_product_deleted);

            if (MyUtils.isNetworkAvailable()) {
                GetProducts getProducts = new GetProducts(ctx, SELECTED_ASSIGNMENT.getAssignmentId(), true, "0");
                getProducts.execute();
            }
        } else if (responseBody != null && responseBody.equals("2")) {

            MyDialogs.showOK(ctx, localized_no_permission_delete);

        } else {
            MyDialogs.showOK(ctx, localized_delete_failed);
        }
    }
}
