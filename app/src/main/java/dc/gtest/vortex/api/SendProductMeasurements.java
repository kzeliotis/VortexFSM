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
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_SEND_ZONE_PRODUCTS_MEASUREMENTS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.PRODUCT_MEASUREMENTS_LIST;
//import static am.gtest.vortex.support.MyGlobals.ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC;
import static dc.gtest.vortex.support.MyGlobals.resendZoneMeasurements;
import static dc.gtest.vortex.support.MyLocalization.localized_data_synchronized;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_FOR_SYNC;

public class SendProductMeasurements extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final String prefKey;
    private final String assignmentId;

    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    private final boolean finishActivity;

    public SendProductMeasurements(Context ctx, String prefKey, boolean finishActivity, String assignmentId) {
        this.ctx = ctx;
        this.prefKey = prefKey;
        this.finishActivity = finishActivity;
        this.assignmentId = assignmentId;

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

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_SEND_ZONE_PRODUCTS_MEASUREMENTS;

        if (assignmentId.equals("0")) {
            postBody = MyPrefs.getStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, prefKey, "");
        } else {
            postBody = MyPrefs.getStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC, prefKey, "");
//            if (ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.containsKey(assignmentId)){
//                if (ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).containsKey(prefKey)){
//                    postBody = ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).get(prefKey).toString();
//                }
//            }
        }

        postBody = postBody.replace("}]", "}\n]");

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false);

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
        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseCode == 200) {

            if (assignmentId.equals("0")) {
                MyPrefs.removeStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, prefKey);

                // according to instruction the changed data must be shown even after data is successfully sent
//            MyPrefs.removeStringWithFileName(ctx, PREF_FILE_ZONE_PRODUCTS_FOR_SHOW, prefKey);

                PRODUCT_MEASUREMENTS_LIST.clear();
            } else {
                MyPrefs.removeStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC, prefKey);
//                if (ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.containsKey(assignmentId)){
//                    if (ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).containsKey(prefKey)){
//                        ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).remove(prefKey);
//                    }
//                    if (ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).isEmpty()){
//                        ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.remove(assignmentId);
//                    }
//                }

            }

            Toast.makeText(ctx, localized_data_synchronized, Toast.LENGTH_LONG).show();

            if (finishActivity) {
                ((Activity)ctx).finish();
            }
        } else {

            if (!assignmentId.equals("0")) {

                String Zone_M =  MyPrefs.getStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC, prefKey, "");
                MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, prefKey, Zone_M);
                MyPrefs.removeStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC, prefKey);

//                if (ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.containsKey(assignmentId)){
//                    if (ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).containsKey(prefKey)){
//                        String Zone_M = ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).get(prefKey).toString();
//                        MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, prefKey, Zone_M);
//                        ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).remove(prefKey);
//                    }
//                    if (ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).isEmpty()){
//                        ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.remove(assignmentId);
//                    }
//                }

            }

            if(resendZoneMeasurements){
                MyPrefs.removeStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, prefKey);
                PRODUCT_MEASUREMENTS_LIST.clear();
                Toast.makeText(ctx, localized_failed_to_send_data, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ctx, localized_failed_to_send_data_saved_for_sync + "\n\n" + this.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            }

        }

        if(resendZoneMeasurements){
            resendZoneMeasurements = false;
        }

    }
}
