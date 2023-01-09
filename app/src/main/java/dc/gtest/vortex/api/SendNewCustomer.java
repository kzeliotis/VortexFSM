package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_SEND_SET_CUSTOMER;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_CUSTOMER_FOR_SYNC;

public class SendNewCustomer extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private final ProgressBar mProgressBar;

    private final String prefKey;
    private final boolean showProgressBar;
    private final boolean isForNewAssignment;
    private final String customerName;

    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody = null;

    public SendNewCustomer(Context ctx, String prefKey, boolean showProgressBar, boolean isForNewAssignment, String customerName) {
        this.ctx = ctx;
        this.prefKey = prefKey;
        this.showProgressBar = showProgressBar;
        this.isForNewAssignment = isForNewAssignment;
        this.customerName = customerName;
        mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
    }

    @Override
    protected void onPreExecute() {
        if (showProgressBar && mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(String... params) {


        postBody = MyPrefs.getStringWithFileName(PREF_FILE_NEW_CUSTOMER_FOR_SYNC, prefKey, "");

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_SEND_SET_CUSTOMER;

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

        if (responseBody != null ) {
            try {
                JSONObject jObject = new JSONObject(responseBody);

                if (jObject.has("id")) {
                    int newCustomerId = jObject.optInt("id", -1);
                    String newCustomerProjectId = jObject.optString("id_child", "");
                    JSONObject result = new JSONObject(jObject.optString("r", ""));
                    String ResultNotes = result.optString("resultnotes", "");

                    if (newCustomerId > 0) {
                        MyPrefs.removeStringWithFileName(PREF_FILE_NEW_CUSTOMER_FOR_SYNC, prefKey);

                        if (isForNewAssignment) {
                            NEW_ASSIGNMENT.setCustomerId(String.valueOf(newCustomerId));
                            NEW_ASSIGNMENT.setCustomerName(customerName);
                            NEW_ASSIGNMENT.setProjectId(newCustomerProjectId);
                        } else if (showProgressBar) {
                            Toast.makeText(ctx, localized_data_sent_2_rows, Toast.LENGTH_LONG).show();
                        }

                        ((Activity) ctx).finish();

                    } else if (ResultNotes.equals("Authorization Failure") || ResultNotes.equals("Insufficient Rights")) {
                        MyPrefs.removeStringWithFileName(PREF_FILE_NEW_CUSTOMER_FOR_SYNC, prefKey);
                        Toast.makeText(ctx, ResultNotes, Toast.LENGTH_LONG).show();

                    }  else {
                        if (showProgressBar) {
                            Toast.makeText(ctx, localized_no_internet_data_saved + "\n\n" + this.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
                            ((Activity) ctx).finish();
                        }
                    }
                } else {
                    if (showProgressBar) {
                        Toast.makeText(ctx, localized_no_internet_data_saved + "\n\n" + this.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
                        ((Activity) ctx).finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                if (showProgressBar) {
                    Toast.makeText(ctx, localized_no_internet_data_saved + "\n\n" + this.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
                    ((Activity) ctx).finish();
                }
            }

        } else {
            if (showProgressBar) {
                Toast.makeText(ctx, localized_no_internet_data_saved + "\n\n" + this.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
                ((Activity) ctx).finish();
            }
        }
    }
}
