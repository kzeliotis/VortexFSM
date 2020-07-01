package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import am.gtest.vortex.R;
import am.gtest.vortex.data.CustomFieldsData;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_GET_VORTEX_TABLE_CUSTOM_FIELDS;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_COMPANY_CUSTOM_FIELDS_LIST;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW;

public class GetCustomFields extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    //private final InstallationRvAdapter installationRvAdapter;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private String vortexTable;
    private String vortexTableId;
    private boolean refresh;


    public GetCustomFields(Context ctx, boolean refresh, String vortexTable) {
        this.ctx = ctx;
        //this.installationRvAdapter = installationRvAdapter;
        this.refresh = refresh;
        this.vortexTable = vortexTable;

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
        vortexTableId = params[0];

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");

        apiUrl = baseHostUrl + API_GET_VORTEX_TABLE_CUSTOM_FIELDS + vortexTable + "&VortexTableId=" + vortexTableId;


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

            switch(vortexTable){
                case "ProjectInstallations":
                    //MyPrefs.setString(PREF_DATA_INSTALLATION_CUSTOM_FIELDS_LIST, responseBody);
                    MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW, vortexTableId, responseBody);
                    break;

                case "Company":
                    //MyPrefs.setString(PREF_DATA_COMPANY_CUSTOM_FIELDS_LIST, responseBody);
                    MyPrefs.setStringWithFileName(PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW, vortexTableId, responseBody);
                    break;
            }


            CustomFieldsData.generate(refresh, vortexTable, vortexTableId);

            RecyclerView rvCustomFields = ((Activity)ctx).findViewById(R.id.rvCustomFields);

            if(rvCustomFields != null && rvCustomFields.getAdapter() != null){
                rvCustomFields.getAdapter().notifyDataSetChanged();
            }

            RecyclerView rvCustomFieldDetails = ((Activity) ctx).findViewById(R.id.rvCustomFieldDetails);
            if (rvCustomFieldDetails != null && rvCustomFieldDetails.getAdapter() != null) {

                rvCustomFieldDetails.getAdapter().notifyDataSetChanged();
            }

        }
    }
}
