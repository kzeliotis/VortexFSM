package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.Toast;

import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_SET_ASSIGNMENT_ATTACHMENT;
import static dc.gtest.vortex.api.MyApi.API_SET_VORTEX_ATTACHMENT;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyLocalization.localized_file_uploaded;
import static dc.gtest.vortex.support.MyLocalization.localized_file_will_be_sent;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ATTACHMENT_FOR_SYNC;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SendAttachment  extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private String apiUrl;
    private String postBody;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    private String prefKey;

    public SendAttachment(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
//        mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
//        if (mProgressBar != null) {
//            mProgressBar.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected String doInBackground(String... params) {
        prefKey = params[0];
        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + (prefKey.startsWith("-") ? API_SET_VORTEX_ATTACHMENT :API_SET_ASSIGNMENT_ATTACHMENT);
        postBody = MyPrefs.getStringWithFileName(PREF_FILE_ATTACHMENT_FOR_SYNC, prefKey, "");

        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String PostBodyLog = "";
        if (postBody.length() > 60){
            PostBodyLog = postBody.substring(0, 60) + " " + prefKey;
        } else {
            PostBodyLog = postBody + " " + prefKey;
        }

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, PostBodyLog, responseCode, responseMessage, responseBody);

        return null;
    }

    @Override
    protected void onPostExecute(String dataFromApi) {
        //MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, postBody, responseCode, responseMessage, responseBody);

//        if (mProgressBar != null) {
//            mProgressBar.setVisibility(View.GONE);
//        }

        if (responseCode == 200) {

            String resultNotes = "";
            try {
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                resultNotes = jsonObject.getAsJsonObject("r").get("resultnotes").getAsString();
            } catch (Exception e) {
            }


            if(responseBody.equals("1") || resultNotes.equals("Success")){
                MyPrefs.removeStringWithFileName(PREF_FILE_ATTACHMENT_FOR_SYNC, prefKey);
                MyLogs.writeFile_FullLog("myLogs: PrepareFile", apiUrl, "Removestring from PREF_FILE_ATTACHMENT", responseCode, prefKey, responseBody);

                Toast toast = Toast.makeText(ctx, localized_file_uploaded, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        } else {
            MyDialogs.showOK(ctx, localized_file_will_be_sent);
        }
    }


}
