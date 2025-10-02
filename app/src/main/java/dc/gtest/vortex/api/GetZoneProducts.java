package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.ZoneProductsRvAdapter;
import dc.gtest.vortex.data.ZoneProductsData;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_ZONE_PRODUCTS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

public class GetZoneProducts extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final ZoneProductsRvAdapter zoneProductsRvAdapter;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private String ZoneId;

    public GetZoneProducts(Context ctx, ZoneProductsRvAdapter zoneProductsRvAdapter) {
        this.ctx = ctx;
        this.zoneProductsRvAdapter = zoneProductsRvAdapter;
    }

    @Override
    protected void onPreExecute() {
        try {
            mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        ZoneId = params[0];

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_ZONE_PRODUCTS + ZoneId;

        try {
            Bundle bundle = MyApi.get(apiUrl, ctx);

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
//        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, MyGlobals.NO_BODY_FOR_GET_REQUEST, responseCode, responseMessage, responseBody);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseCode == 200 && responseBody != null ) {

            ZoneProductsData.generate(responseBody, ZoneId);

            if (zoneProductsRvAdapter != null) {
                zoneProductsRvAdapter.notifyDataSetChanged();
            }
        }
    }
}
