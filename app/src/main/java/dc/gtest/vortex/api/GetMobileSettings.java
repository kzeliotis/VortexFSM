package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import dc.gtest.vortex.BuildConfig;
import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.PermGetLocation;

import static dc.gtest.vortex.api.MyApi.API_GET_MOBILE_SETTINGS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.PERMISSIONS_FINE_LOCATION;
import static dc.gtest.vortex.support.MyLocalization.localized_new_update_available;
import static dc.gtest.vortex.support.MyPrefs.PREF_ADD_CONSUMABLE_FROM_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_ADD_CONSUMABLE_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_ADD_CONSUMABLE_FROM_WAREHOUSE;
import static dc.gtest.vortex.support.MyPrefs.PREF_ADD_SERVICES_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_ALLOW_CHECKIN_OUT_SUBASSIGNMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_ALLOW_MULTIPLE_CHECK_INS;
import static dc.gtest.vortex.support.MyPrefs.PREF_API_CONNECTION_TIMEOUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_API_READ_TIMEOUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_AZURE_CONNECTION_STRING;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DOWNLOAD_ALL_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_ENABLE_LOCATION_SERVICE;
import static dc.gtest.vortex.support.MyPrefs.PREF_GPS_PRIORITY;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEEP_GPS_LOG;
import static dc.gtest.vortex.support.MyPrefs.PREF_LOCATION_REFRESH_INTERVAL;
import static dc.gtest.vortex.support.MyPrefs.PREF_MANDATORY_CONSUMABLES_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_MANDATORY_SERVICES_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_MANDATORY_SIGNATURE;
import static dc.gtest.vortex.support.MyPrefs.PREF_PROCESS_ASSIGNMENT_ON_SCAN;
import static dc.gtest.vortex.support.MyPrefs.PREF_QTY_LIMIT_CONSUMABLE_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_QTY_LIMIT_SERVICES_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_SCROLLABLE_PROBLEM_DESCRIPTION;
import static dc.gtest.vortex.support.MyPrefs.PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_ALL_MASTER_PROJECTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_CHARGE_FIELD;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_DET_CHILDREN;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_DET_CHILDREN_START_STOP;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_GET_ASSIGNMENT_COST;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_INSTALLATIONS_BUTTON;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_MANDATORY_TASKS_COMMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_PAYMENT_FILED;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_PRODUCTS_TREE;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_SEND_REPORT_CHECKBOX;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_START_WORK;
import static dc.gtest.vortex.support.MyPrefs.PREF_USERID;
import static dc.gtest.vortex.support.MyPrefs.PREF_WARRANTY_EXTENSION_ON_PRODUCT_INSTALLATION;


public class GetMobileSettings extends AsyncTask<String, Void, String > {


    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;
    private final PermGetLocation permGetLocation;

    public GetMobileSettings(Context ctx,PermGetLocation permGetLocation) {
        this.ctx = ctx;
        this.permGetLocation = permGetLocation;
    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        apiUrl = baseHostUrl + API_GET_MOBILE_SETTINGS + MyPrefs.getString(PREF_USERID, "0");

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
                    MyPrefs.setBoolean(PREF_SHOW_GET_ASSIGNMENT_COST, MobileShowGetAssignmentCostButton == 1);


                    String MobileShowMandatoryTaskComments = MyJsonParser.getStringValue(oneObject, "MobileShowMandatoryTaskComments", "");
                    if (MobileShowMandatoryTaskComments.equals("1")) {
                        MyPrefs.setBoolean(PREF_SHOW_MANDATORY_TASKS_COMMENTS, true);
                    } else if (MobileShowMandatoryTaskComments.equals("0")) {
                        MyPrefs.setBoolean(PREF_SHOW_MANDATORY_TASKS_COMMENTS, false);
                    }


                    int MobileSendZoneMeasurementsOnCheckOut = MyJsonParser.getIntValue(oneObject,  "MobileSendZoneMeasurementsOnCheckOut", 0);
                    MyPrefs.setBoolean(PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT, MobileSendZoneMeasurementsOnCheckOut == 1);

                    int MobileShowInstallationsButton = MyJsonParser.getIntValue(oneObject, "MobileShowInstallationsButton", 0);
                    MyPrefs.setBoolean(PREF_SHOW_INSTALLATIONS_BUTTON, MobileShowInstallationsButton == 1);

                    int MobileProcessAssignmentOnScan = MyJsonParser.getIntValue(oneObject,  "MobileProcessAssignmentOnScan", 0);
                    MyPrefs.setBoolean(PREF_PROCESS_ASSIGNMENT_ON_SCAN, MobileProcessAssignmentOnScan == 1);

                    int MobileShowSendReportCheckbox = MyJsonParser.getIntValue(oneObject,  "MobileShowSendReportCheckbox", 0);
                    MyPrefs.setBoolean(PREF_SHOW_SEND_REPORT_CHECKBOX, MobileShowSendReportCheckbox == 1);

                    int MobileKeepGPSLog = MyJsonParser.getIntValue(oneObject,  "MobileKeepGPSLog", 0);
                    MyPrefs.setBoolean(PREF_KEEP_GPS_LOG, MobileKeepGPSLog == 1);

                    int MobileEnableLocationService = MyJsonParser.getIntValue(oneObject,  "MobileEnableLocationService", 0);
                    MyPrefs.setBoolean(PREF_ENABLE_LOCATION_SERVICE, MobileEnableLocationService == 1);

                    int MobileDownloadAllDataOnSync = MyJsonParser.getIntValue(oneObject,  "MobileDownloadAllDataOnSync", 1);
                    MyPrefs.setBoolean(PREF_DOWNLOAD_ALL_DATA, MobileDownloadAllDataOnSync == 1);

                    int MobileScrollableProblemDescription = MyJsonParser.getIntValue(oneObject,  "MobileScrollableProblemDescription", 0);
                    MyPrefs.setBoolean(PREF_SCROLLABLE_PROBLEM_DESCRIPTION, MobileScrollableProblemDescription == 1);

                    String AzureBlobConnectionString = MyJsonParser.getStringValue(oneObject,  "AzureBlobConnectionString", "");
                    MyPrefs.setString(PREF_AZURE_CONNECTION_STRING, AzureBlobConnectionString);

                    int MobileRefreshLocationEvery = MyJsonParser.getIntValue(oneObject,  "MobileRefreshLocationEvery", 30);
                    MyPrefs.setInt(PREF_LOCATION_REFRESH_INTERVAL, MobileRefreshLocationEvery);

                    int MobileGPSPriority = MyJsonParser.getIntValue(oneObject,  "MobileGPSPriority", 3);
                    MyPrefs.setInt(PREF_GPS_PRIORITY, MobileGPSPriority);

                    int MobileApiConnectionTimeout = MyJsonParser.getIntValue(oneObject,  "MobileApiConnectionTimeout", 15);
                    MyPrefs.setInt(PREF_API_CONNECTION_TIMEOUT, MobileApiConnectionTimeout);

                    int MobileApiReadTimeout = MyJsonParser.getIntValue(oneObject,  "MobileApiReadTimeout", 120);
                    MyPrefs.setInt(PREF_API_READ_TIMEOUT, MobileApiReadTimeout);

                    int AllowParallelCheckInsFromMobile = MyJsonParser.getIntValue(oneObject,  "AllowParallelCheckInsFromMobile", 1);
                    MyPrefs.setBoolean(PREF_ALLOW_MULTIPLE_CHECK_INS, AllowParallelCheckInsFromMobile == 1);

                    int ShowChargeFieldInMobile = MyJsonParser.getIntValue(oneObject,  "ShowChargeFieldInMobile", 1);
                    MyPrefs.setBoolean(PREF_SHOW_CHARGE_FIELD, ShowChargeFieldInMobile == 1);

                    int ShowCollectionFieldInMobile = MyJsonParser.getIntValue(oneObject,  "ShowCollectionFieldInMobile", 1);

                    MyPrefs.setBoolean(PREF_SHOW_PAYMENT_FILED, ShowCollectionFieldInMobile == 1);

                    int AddConsumablesFromListMobile = MyJsonParser.getIntValue(oneObject,  "AddConsumablesFromListMobile", 1);

                    MyPrefs.setBoolean(PREF_ADD_CONSUMABLE_FROM_LIST, AddConsumablesFromListMobile == 1);

                    int AddConsumableFromWarehouseMobile = MyJsonParser.getIntValue(oneObject,  "AddConsumableFromWarehouseMobile", 1);

                    MyPrefs.setBoolean(PREF_ADD_CONSUMABLE_FROM_WAREHOUSE, AddConsumableFromWarehouseMobile == 1);

                    int AddConsumableFromPickingMobile = MyJsonParser.getIntValue(oneObject,  "AddConsumableFromPickingMobile", 1);

                    MyPrefs.setBoolean(PREF_ADD_CONSUMABLE_FROM_PICKING, AddConsumableFromPickingMobile == 1);

                    int QtyLimitOnConsumablesFromPicking = MyJsonParser.getIntValue(oneObject,  "QtyLimitOnConsumablesFromPicking", 0);

                    MyPrefs.setBoolean(PREF_QTY_LIMIT_CONSUMABLE_FROM_PICKING, QtyLimitOnConsumablesFromPicking == 1);

                    int MandatoryComsumableFromPicking = MyJsonParser.getIntValue(oneObject,  "MandatoryComsumableFromPicking", 0);

                    MyPrefs.setBoolean(PREF_MANDATORY_CONSUMABLES_FROM_PICKING, MandatoryComsumableFromPicking == 1);

                    int ShowAllMasterProjectsOnNewAssignment = MyJsonParser.getIntValue(oneObject, "ShowAllMasterProjectsOnNewAssignment", 0);

                    MyPrefs.setBoolean(PREF_SHOW_ALL_MASTER_PROJECTS, ShowAllMasterProjectsOnNewAssignment == 1);

                    int MobileAllowCheckInOutSubAssignments = MyJsonParser.getIntValue(oneObject, "MobileAllowCheckInOutSubAssignments", 0);

                    MyPrefs.setBoolean(PREF_ALLOW_CHECKIN_OUT_SUBASSIGNMENTS, MobileAllowCheckInOutSubAssignments == 1);

                    int MobileEnableDetChildren = MyJsonParser.getIntValue(oneObject, "MobileEnableDetChildren", 0);

                    MyPrefs.setBoolean(PREF_SHOW_DET_CHILDREN, MobileEnableDetChildren == 1);

                    int MobileEnableDetChildrenStartStop = MyJsonParser.getIntValue(oneObject, "MobileEnableDetChildrenStartStop", 0);

                    MyPrefs.setBoolean(PREF_SHOW_DET_CHILDREN_START_STOP, MobileEnableDetChildrenStartStop == 1);

                    int MobileShowProductsTree = MyJsonParser.getIntValue(oneObject, "MobileShowProductsTree", 0);

                    MyPrefs.setBoolean(PREF_SHOW_PRODUCTS_TREE, MobileShowProductsTree == 1);

                    int AddServicesFromPickingMobile = MyJsonParser.getIntValue(oneObject,  "AddServicesFromPickingMobile", 1);

                    MyPrefs.setBoolean(PREF_ADD_SERVICES_FROM_PICKING, AddServicesFromPickingMobile == 1);

                    int QtyLimitOnServicesFromPicking = MyJsonParser.getIntValue(oneObject,  "QtyLimitOnServicesFromPicking", 0);

                    MyPrefs.setBoolean(PREF_QTY_LIMIT_SERVICES_FROM_PICKING, QtyLimitOnServicesFromPicking == 1);

                    int MandatoryServicesFromPicking = MyJsonParser.getIntValue(oneObject,  "MandatoryServicesFromPicking", 0);

                    MyPrefs.setBoolean(PREF_MANDATORY_SERVICES_FROM_PICKING, MandatoryServicesFromPicking == 1);

                    int MobileWarrantyExtensionOnProductInstallation = MyJsonParser.getIntValue(oneObject, "MobileWarrantyExtensionOnProductInstallation", 0);

                    MyPrefs.setBoolean(PREF_WARRANTY_EXTENSION_ON_PRODUCT_INSTALLATION, MobileWarrantyExtensionOnProductInstallation == 1);

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
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
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

        if(!(permGetLocation == null)){
            permGetLocation.myRequestPermission(PERMISSIONS_FINE_LOCATION);
        }


    }


    }


