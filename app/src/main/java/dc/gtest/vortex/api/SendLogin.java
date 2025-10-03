package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AssignmentsActivity;
import dc.gtest.vortex.activities.LoginActivity;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.support.CalendarView;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySynchronize;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.CALENDAR_EVENTS;
import static dc.gtest.vortex.support.MyGlobals.KEY_AFTER_LOGIN;
import static dc.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static dc.gtest.vortex.support.MyLocalization.localized_login_failed;
import static dc.gtest.vortex.support.MyLocalization.localized_user_is_inactive;
import static dc.gtest.vortex.support.MyLocalization.localized_wrong_credentials;
import static dc.gtest.vortex.support.MyPrefs.PREF_AES_KEY;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_DEV_LOGIN;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_IS_LOGGED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_USERID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

import static dc.gtest.vortex.support.MyPrefs.PREF_WAREHOUSEID;

import androidx.appcompat.app.AppCompatActivity;

public class SendLogin extends AsyncTask<String, Void, String > {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;
    private final boolean reSync;
    private final boolean downloadAllData;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private String username;
    private String password;
    private String postBody;
    private String apiUrl;
    private String responseMessage;
    private String responseBody;

    private int responseCode;

    public SendLogin(Context ctx, boolean ReSync, boolean downloadAllData) {
        this.ctx = ctx;
        this.reSync = ReSync;
        this.downloadAllData = downloadAllData;
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

        username = params[0];
        password = params[1];
        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        //String apiUrl = baseHostUrl+ "/Vortex.svc/AuthenticateUserWithWarehouseId" + "?username=" + username + "&password=" + password;
        apiUrl = baseHostUrl+ "/Vortex.svc/GetUserAuthentication";
        String AES_KEY = MyPrefs.getString(PREF_AES_KEY, "");
        String encrypted = AES_KEY.length()>0 ? "1" : "0";

        postBody =
                "{\n" +
                        "  \"username\": \"" + username + "\",\n" +
                        "  \"password\": \"" + password + "\",\n" +
                        "  \"Encrypted\": \"" + encrypted + "\"\n" +
                        "}";


        //you are paparas
        try {
            Bundle bundle = MyApi.post(apiUrl, postBody, false, ctx);

            responseCode = bundle.getInt(MY_API_RESPONSE_CODE);
            responseMessage = bundle.getString(MY_API_RESPONSE_MESSAGE);
            responseBody = bundle.getString(MY_API_RESPONSE_BODY);

        } catch (Exception e) {
            MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "Login attempt user " + username, responseCode, e.getMessage(), responseBody);
            e.printStackTrace();
        }

        //MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for get request", responseCode, responseMessage, responseBody);

        return responseBody;
    }


    @Override
    protected void onPostExecute(String responseBody) {

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "Login user " + username, responseCode, responseMessage, responseBody);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }


        if (responseCode == 200 && responseBody != null && !responseBody.isEmpty() && !responseBody.equals("[]")) {

            MyPrefs.setBoolean(PREF_DEV_LOGIN, password.contains("|consult1ng"));

            try {
                JSONArray jArrayDataFromApi = new JSONArray(responseBody);

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    String IsActive = MyJsonParser.getStringValue(oneObject, "IsActive", "");
                    if (IsActive.equals("false")) {
                        //MyDialogs.showOK(ctx, localized_user_is_inactive);

                        if (reSync) {
                            Toast.makeText(MyApplication.getContext(), localized_user_is_inactive, Toast.LENGTH_LONG).show();
                            MyPrefs.setString(PREF_DATA_ASSIGNMENTS, "");
                            MyPrefs.setBoolean(PREF_KEY_IS_LOGGED_IN, false);
                            ((Activity) ctx).finishAffinity();

                            Intent intent = new Intent(ctx, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ctx.startActivity(intent);
                        } else {
                            MyDialogs.showOK(ctx, localized_user_is_inactive);
                            EditText etEmail = ((Activity) ctx).findViewById(R.id.etLoginUserLogin);
                            EditText etPass = ((Activity) ctx).findViewById(R.id.etLoginUserPassword);

                            etEmail.setText("");
                            etPass.setText("");

                            etEmail.setBackgroundResource(R.drawable.rounded_edittext_white);
                            etPass.setBackgroundResource(R.drawable.rounded_edittext_white);

                            MyPrefs.setBoolean(PREF_KEY_IS_LOGGED_IN, false);
                        }

                    } else if (reSync) {

                        new MySynchronize(ctx).mySynchronize(false);

                        GetAssignments getAssignments = new GetAssignments(ctx, downloadAllData);
                        getAssignments.execute();


                    } else {

                        MyPrefs.setBoolean(PREF_KEY_IS_LOGGED_IN, true);
                        MyPrefs.setString(PREF_USER_NAME, username);

                        String UserId = MyJsonParser.getStringValue(oneObject, "UserId", "0");
                        String WarehouseId = MyJsonParser.getStringValue(oneObject, "WarehouseId", "0");

                        MyPrefs.setString(PREF_WAREHOUSEID, WarehouseId);
                        MyPrefs.setString(PREF_USERID, UserId);

                        Intent intent = new Intent(ctx, AssignmentsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(KEY_DOWNLOAD_ALL_DATA, true);
                        intent.putExtra(KEY_AFTER_LOGIN, true);
                        ctx.startActivity(intent);
                        ((Activity) ctx).finish();

                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            if (reSync) {

                Toast.makeText(MyApplication.getContext(), localized_wrong_credentials, Toast.LENGTH_LONG).show();
                MyPrefs.setString(PREF_DATA_ASSIGNMENTS, "");
                MyPrefs.setBoolean(PREF_KEY_IS_LOGGED_IN, false);
                ((Activity) ctx).finishAffinity();

                Intent intent = new Intent(ctx, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ctx.startActivity(intent);

            } else {
                MyDialogs.showOK(ctx, localized_login_failed);

                EditText etEmail = ((Activity) ctx).findViewById(R.id.etLoginUserLogin);
                EditText etPass = ((Activity) ctx).findViewById(R.id.etLoginUserPassword);

                etEmail.setText("");
                etPass.setText("");

                etEmail.setBackgroundResource(R.drawable.rounded_edittext_white);
                etPass.setBackgroundResource(R.drawable.rounded_edittext_white);

                MyPrefs.setBoolean(PREF_KEY_IS_LOGGED_IN, false);
            }


        }



//        if ( responseBody != null && responseBody.contains(";") ) {
//            MyPrefs.setBoolean(PREF_KEY_IS_LOGGED_IN, true);
//            MyPrefs.setString(PREF_USER_NAME, username);
//            String WarehouseId = responseBody.split(";")[1];
//            WarehouseId = WarehouseId.replace("\"", "");
//            //WarehouseId = WarehouseId.substring(0, WarehouseId.length() - 1);
//
//
//           String UserId = "0";
//
//            if(responseBody.split(";").length > 2){
//                UserId = responseBody.split(";")[2];
//                UserId = UserId.replace("\"", "");
//            }
//
//            MyPrefs.setString(PREF_WAREHOUSEID, WarehouseId);
//            MyPrefs.setString(PREF_USERID, UserId);
//
//            Intent intent = new Intent(ctx, AssignmentsActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra(KEY_DOWNLOAD_ALL_DATA, true);
//            ctx.startActivity(intent);
//            ((Activity)ctx).finish();
//        } else {
//
//            MyDialogs.showOK(ctx, localized_login_failed);
//
//            EditText etEmail = ((Activity) ctx).findViewById(R.id.etLoginUserLogin);
//            EditText etPass = ((Activity) ctx).findViewById(R.id.etLoginUserPassword);
//
//            etEmail.setText("");
//            etPass.setText("");
//
//            etEmail.setBackgroundResource(R.drawable.rounded_edittext_white);
//            etPass.setBackgroundResource(R.drawable.rounded_edittext_white);
//
//            MyPrefs.setBoolean(PREF_KEY_IS_LOGGED_IN, false);
//        }
    }
}
