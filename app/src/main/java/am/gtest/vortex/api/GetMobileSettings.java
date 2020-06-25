package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Browser;

import org.json.JSONArray;
import org.json.JSONObject;

import am.gtest.vortex.BuildConfig;
import am.gtest.vortex.R;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.PermGetLocation;

import static am.gtest.vortex.api.MyApi.API_GET_MOBILE_SETTINGS;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyGlobals.PERMISSIONS_FINE_LOCATION;
import static am.gtest.vortex.support.MyLocalization.localized_new_update_available;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_GPS_PRIORITY;
import static am.gtest.vortex.support.MyPrefs.PREF_LOCATION_REFRESH_INTERVAL;
import static am.gtest.vortex.support.MyPrefs.PREF_MANDATORY_SIGNATURE;
import static am.gtest.vortex.support.MyPrefs.PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT;
import static am.gtest.vortex.support.MyPrefs.PREF_SHOW_GET_ASSIGNMENT_COST;
import static am.gtest.vortex.support.MyPrefs.PREF_SHOW_INSTALLATIONS_BUTTON;
import static am.gtest.vortex.support.MyPrefs.PREF_SHOW_MANDATORY_TASKS_COMMENTS;
import static am.gtest.vortex.support.MyPrefs.PREF_SHOW_START_WORK;


public class GetMobileSettings extends AsyncTask<String, Void, String > {


    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private PermGetLocation permGetLocation;

    public GetMobileSettings(Context ctx,PermGetLocation permGetLocation) {
        this.ctx = ctx;
        this.permGetLocation = permGetLocation;
    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_MOBILE_SETTINGS;

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
       // MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no_body_for_get_request", responseCode, responseMessage, responseBody);

        if (responseCode == 200 && responseBody != null && !responseBody.isEmpty()) {

            try {
                JSONArray jArrayDataFromApi = new JSONArray(responseBody);

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    String MandatorySignature = MyJsonParser.getStringValue(oneObject, "MobileMandatorySignature", "");
                    if (MandatorySignature.equals("1")) {
                        MyPrefs.setBoolean(PREF_MANDATORY_SIGNATURE, true);
                    } else if (MandatorySignature.equals("0")) {
                        MyPrefs.setBoolean(PREF_MANDATORY_SIGNATURE, false);
                    }

                    String ShowStartWork = MyJsonParser.getStringValue(oneObject, "ShowStartWork", "");
                    if (ShowStartWork.equals("1")) {
                        MyPrefs.setBoolean(PREF_SHOW_START_WORK, true);
                    } else if (ShowStartWork.equals("0")) {
                        MyPrefs.setBoolean(PREF_SHOW_START_WORK, false);
                    }

                    int MobileShowGetAssignmentCostButton = MyJsonParser.getIntValue(oneObject, "MobileShowGetAssignmentCostButton", 0);
                    if (MobileShowGetAssignmentCostButton == 1){
                        MyPrefs.setBoolean(PREF_SHOW_GET_ASSIGNMENT_COST, true);
                    } else {
                        MyPrefs.setBoolean(PREF_SHOW_GET_ASSIGNMENT_COST, false);
                    }


                    String MobileShowMandatoryTaskComments = MyJsonParser.getStringValue(oneObject, "MobileShowMandatoryTaskComments", "");
                    if (MobileShowMandatoryTaskComments.equals("1")) {
                        MyPrefs.setBoolean(PREF_SHOW_MANDATORY_TASKS_COMMENTS, true);
                    } else if (MobileShowMandatoryTaskComments.equals("0")) {
                        MyPrefs.setBoolean(PREF_SHOW_MANDATORY_TASKS_COMMENTS, false);
                    }


                    int MobileSendZoneMeasurementsOnCheckOut = MyJsonParser.getIntValue(oneObject,  "MobileSendZoneMeasurementsOnCheckOut", 0);
                    if (MobileSendZoneMeasurementsOnCheckOut == 1) {
                        MyPrefs.setBoolean(PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT, true);
                    } else {
                        MyPrefs.setBoolean(PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT, false);
                    }

                    int MobileShowInstallationsButton = MyJsonParser.getIntValue(oneObject, "MobileShowInstallationsButton", 0);
                    if (MobileShowInstallationsButton == 1){
                        MyPrefs.setBoolean(PREF_SHOW_INSTALLATIONS_BUTTON, true);
                    } else {
                        MyPrefs.setBoolean(PREF_SHOW_INSTALLATIONS_BUTTON, false);
                    }


                    Integer MobileRefreshLocationEvery = MyJsonParser.getIntValue(oneObject,  "MobileRefreshLocationEvery", 30);
                    MyPrefs.setInt(PREF_LOCATION_REFRESH_INTERVAL, MobileRefreshLocationEvery);

                    Integer MobileGPSPriority = MyJsonParser.getIntValue(oneObject,  "MobileGPSPriority", 3);
                    MyPrefs.setInt(PREF_GPS_PRIORITY, MobileGPSPriority);

                    String CurrentApkVersion = MyJsonParser.getStringValue(oneObject, "MobileApkVersionNumber", "");
                    String CurrentApkVersionURL = MyJsonParser.getStringValue(oneObject, "MobileApkVersionURL", "");
                    if (!CurrentApkVersion.isEmpty() && !CurrentApkVersionURL.isEmpty()){
                        String BuildVersion = BuildConfig.VERSION_NAME;
                        int BuildVersionInt = Integer.parseInt(BuildVersion.replace(".", ""));
                        int CurrentVersionInt = Integer.parseInt(CurrentApkVersion.replace(".", ""));
                        if(BuildVersionInt < CurrentVersionInt){
                            Activity activity = (Activity) ctx;
                            new AlertDialog.Builder(activity)
                                    .setMessage(localized_new_update_available)
                                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                                        dialog.dismiss();
                                        Uri uri = Uri.parse(CurrentApkVersionURL + "vortex_" + CurrentApkVersion + ".apk");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                        Bundle b = new Bundle();
                                        b.putBoolean("new_window", true); //sets new window
                                        intent.putExtras(b);
                                        //intent.putExtra(Browser.EXTRA_APPLICATION_ID, "com.android.chrome");
                                        ctx.startActivity(intent);
                                        //ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CurrentApkVersionURL + "vortex_" + CurrentApkVersion + ".apk")));
                                    })
                                    .setNegativeButton(R.string.no,null)
                                    .show();

                        }
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }



            }

        permGetLocation.myRequestPermission(PERMISSIONS_FINE_LOCATION);

    }


    }


