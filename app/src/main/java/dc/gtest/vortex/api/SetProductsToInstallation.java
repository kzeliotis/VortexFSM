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
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.api.MyApi.API_DELETE_ZONE;
import static dc.gtest.vortex.api.MyApi.API_SEND_NEW_PRODUCT;
import static dc.gtest.vortex.api.MyApi.API_SET_PRODUCTS_TO_INSTALLATION;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static dc.gtest.vortex.support.MyLocalization.localized_delete_failed;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static dc.gtest.vortex.support.MyLocalization.localized_no_permission_write;
import static dc.gtest.vortex.support.MyLocalization.localized_zone_deleted;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_PRODUCTS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_WAREHOUSEID;

public class SetProductsToInstallation extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private int responseCode;
    private final String projectInstallationId;


    public SetProductsToInstallation(Context ctx, String ProjectInstallationId) {
        this.ctx = ctx;
        this.projectInstallationId = ProjectInstallationId;
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

        //String projectZoneId = params[0];
        String ppIds = MyPrefs.getStringWithFileName(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SYNC, projectInstallationId, "");
        String projectZoneId = "0";
        if (ppIds.contains("|")){
            int index = ppIds.indexOf("|");
            projectZoneId = ppIds.substring(index+1);
            ppIds = ppIds.substring(0, index);
        }

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + API_SET_PRODUCTS_TO_INSTALLATION + projectInstallationId + "&ProjectProductIDs=" + ppIds + "&ProjectZoneId=" + projectZoneId ;

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

            MyPrefs.removeStringWithFileName(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SYNC, projectInstallationId);

//            if (MyUtils.isNetworkAvailable()) {
//                GetZones getZones = new GetZones(ctx, null, true, projectInstallationId);
//                getZones.execute("0");
//            }

        } else {

            MyDialogs.showOK(ctx, localized_failed_to_send_data_saved_for_sync+ "\n\n" + this.getClass().getSimpleName());
        }
    }
}

