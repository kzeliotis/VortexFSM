package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.support.MyLogs;

import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;

public class GetUserGoogleId extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    private static final String CENTRAL_WCF_URL = "https://cloud.vortexsuite.com:9876/Vortex.svc";

    public GetUserGoogleId(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String idToken = params[0];
        String googleId = "";

        try {
            String url = CENTRAL_WCF_URL + "/GetUserGoogleId";
            String body = "{\"idToken\": \"" + idToken + "\"}";

            Bundle bundle = MyApi.post(url, body, false, ctx);

            int responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            String responseBody = bundle.getString(MY_API_RESPONSE_BODY);

            if (responseCode == 200 && responseBody != null && !responseBody.isEmpty()) {
                // Response is a plain JSON string e.g. "103456789012345678901"
                googleId = responseBody.replace("\"", "").trim();
            }else{
                googleId = responseBody;
            }

        } catch (Exception e) {
            MyLogs.showFullLog("myLogs: GetUserGoogleId", CENTRAL_WCF_URL, "", 0, e.getMessage(), "");
            e.printStackTrace();
        }

        return googleId;
    }
}