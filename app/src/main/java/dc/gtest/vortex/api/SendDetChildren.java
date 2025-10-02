package dc.gtest.vortex.api;

import static dc.gtest.vortex.api.MyApi.API_SEND_DET_CHILDREN;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyLocalization.localized_data_synchronized;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data;
import static dc.gtest.vortex.support.MyLocalization.localized_failed_to_send_data_saved_for_sync;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CHILDREN_FOR_SYNC;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.ApiResultModel;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

public class SendDetChildren extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final String prefKey;
    //private final String assignmentId;

    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    //private final boolean finishActivity;

    public SendDetChildren(Context ctx, String prefKey) {
        this.ctx = ctx;
        this.prefKey = prefKey;
        //this.finishActivity = finishActivity;
        //this.assignmentId = assignmentId;

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
        apiUrl = baseHostUrl + API_SEND_DET_CHILDREN;

        postBody = MyPrefs.getStringWithFileName(PREF_FILE_DET_CHILDREN_FOR_SYNC, prefKey, "");


        postBody = postBody.replace("}]", "}\n]");

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false, ctx);

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

            Gson gson = new Gson();
            ApiResultModel apiResult = gson.fromJson(responseBody, ApiResultModel.class);

            if(apiResult != null){
                if(apiResult.getR().getResult().equals("Success")) {
                    MyPrefs.removeStringWithFileName(PREF_FILE_DET_CHILDREN_FOR_SYNC, prefKey);
                    Toast.makeText(ctx, localized_data_synchronized, Toast.LENGTH_LONG).show();
                } else {
                    String resultnotes = apiResult.getR().getResultNotes();
                    Toast.makeText(ctx, localized_failed_to_send_data_saved_for_sync + "\n\n" + resultnotes + "\n\n" + this.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(ctx, localized_failed_to_send_data_saved_for_sync + "\n\n" + this.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            }

//            if (finishActivity) {
//                ((Activity)ctx).finish();
//            }
        } else {
            Toast.makeText(ctx, localized_failed_to_send_data_saved_for_sync + "\n\n" + this.getClass().getSimpleName(), Toast.LENGTH_LONG).show();

        }
    }



}
