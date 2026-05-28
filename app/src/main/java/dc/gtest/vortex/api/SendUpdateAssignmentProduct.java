package dc.gtest.vortex.api;

import static dc.gtest.vortex.api.MyApi.API_SEND_UPDATE_ASSIGNMENT_PRODUCT;
import static dc.gtest.vortex.api.MyApi.API_SEND_UPDATE_ATTRIBUTE_VALUES;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_data_sent_2_rows;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static dc.gtest.vortex.support.MyLocalization.localized_no_permission_write;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_USERID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AssignmentsActivity;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.models.ApiResultModel;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

public class SendUpdateAssignmentProduct extends AsyncTask<String, Void, String > {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private String prefKey;
    private final String assignmentId;
    private final String projectProductId;

    private int responseCode;

    public SendUpdateAssignmentProduct(Context ctx, String AssignmentId, String ProjectProductId) {
        this.ctx = ctx;
        this.assignmentId = AssignmentId;
        this.projectProductId = ProjectProductId;
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
        String responseBody = null;


        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + API_SEND_UPDATE_ASSIGNMENT_PRODUCT + assignmentId + "&ProjectProductId=" +
                projectProductId + "&UserId=" + MyPrefs.getString(PREF_USERID, "0");

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

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (responseBody != null&& responseBody.startsWith("{")){
            Gson gson = new Gson();
            ApiResultModel apiResult = gson.fromJson(responseBody, ApiResultModel.class);
            String result = "";
            String resultnotes = "";
            if (apiResult != null){
                result = apiResult.getR().getResult();
                resultnotes = apiResult.getR().getResultnotes();
            }

            if(result.equals("Success")){
                Toast.makeText(MyApplication.getContext(), localized_data_sent_2_rows, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent((Activity) ctx, AssignmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_DOWNLOAD_ALL_DATA, false);
                ((Activity) ctx).startActivity(intent);
                ((Activity) ctx).finish();   // NOT finishAffinity
            }else{
                Toast.makeText(MyApplication.getContext(), resultnotes, Toast.LENGTH_SHORT).show();
            }

        } else {
            MyDialogs.showOK(ctx, localized_failed_to_send_data_saved_for_sync+ "\n\n" + this.getClass().getSimpleName());
        }
    }
}
