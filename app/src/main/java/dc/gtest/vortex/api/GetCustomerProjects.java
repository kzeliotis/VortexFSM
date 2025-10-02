package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_CUSTOMER_PROJECTS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;

public class GetCustomerProjects extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public GetCustomerProjects(Context ctx) {
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

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_CUSTOMER_PROJECTS + NEW_ASSIGNMENT.getCustomerId();

        try {
            Bundle bundle = MyApi.get(apiUrl, ctx);

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
        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for get request", responseCode, responseMessage, responseBody);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

//        if (COMPANIES_LIST.size() > 0) {
//            Intent intent = new Intent(ctx, CompaniesDrawerActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            ctx.startActivity(intent);
//        } else {
//            Toast toast = Toast.makeText(ctx, localized_no_company_is_available, Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//        }
    }
}
