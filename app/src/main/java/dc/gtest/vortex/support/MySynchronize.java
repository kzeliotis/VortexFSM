package dc.gtest.vortex.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Map;

import dc.gtest.vortex.api.SendAttachment;
import dc.gtest.vortex.api.SendCheckIn;
import dc.gtest.vortex.api.SendCheckOut;
import dc.gtest.vortex.api.SendCustomFields;
import dc.gtest.vortex.api.SendDetChildren;
import dc.gtest.vortex.api.SendLogin;
import dc.gtest.vortex.api.SendNewAssignment;
import dc.gtest.vortex.api.SendNewCustomer;
import dc.gtest.vortex.api.SendNewProduct;
import dc.gtest.vortex.api.SendProductMeasurements;
import dc.gtest.vortex.api.SendConsumables;
import dc.gtest.vortex.api.SendImage;
import dc.gtest.vortex.api.SendMandatoryTasks;
import dc.gtest.vortex.api.SendProjectZone;
import dc.gtest.vortex.api.SendStartTravel;
import dc.gtest.vortex.api.SendUsePTOvernight;
import dc.gtest.vortex.api.SendNewAttribute;
import dc.gtest.vortex.api.SendZonesWithNoMeasurement;
import dc.gtest.vortex.api.SetProductsToInstallation;
import dc.gtest.vortex.api.ToSendNewMeasurement;
import dc.gtest.vortex.api.ToSendServices;
import dc.gtest.vortex.api.SendUpdatedAttribute;

import static dc.gtest.vortex.support.MyGlobals.CONST_DO_NOT_FINISH_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.CONST_DO_NOT_SHOW_PROGRESS_AND_TOAST;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_try_later_2_lines;
import static dc.gtest.vortex.support.MyPrefs.PREF_AZURE_CONNECTION_STRING;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_MEASUREMENTS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ATTACHMENT_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CHECK_IN_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CHECK_OUT_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CUSTOM_FIELDS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CHILDREN_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CHILDREN_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IMAGE_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_MANDATORY_TASKS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_CUSTOMER_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_INSTALLATION_ZONES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_PRODUCTS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RETURN_TO_BASE_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_START_TRAVEL_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_USED_SERVICES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_USE_PT_OVERNIGHT_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_ONLY_WIFI;
import static dc.gtest.vortex.support.MyPrefs.PREF_PASSWORD;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;
import static android.content.Context.MODE_PRIVATE;

public class MySynchronize {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;

    public MySynchronize(Context ctx) {
        this.ctx = ctx;
    }

    public void mySynchronize(boolean login) {

        String result = "Success";

        if (MyUtils.isNetworkAvailable()) {

            if (MyPrefs.getBoolean(PREF_ONLY_WIFI, false)) {
                ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

                if (connManager != null) {
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                    if (mWifi.isConnected()) {
                        synchronizeSavedImages(ctx);
                    }  else {
                        result = "Failed - NoWifi";
                    }
                }
            } else {
                synchronizeSavedImages(ctx);
                synchronizeSavedAttachments(ctx);
            }

            synchronizeSavedData(login);

        } else {
            MyDialogs.showOK(ctx, localized_no_internet_try_later_2_lines);
            result = "Failed";
        }

        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), "", "", 0, "", result);
    }

    public static void synchronizeSavedImages(Context ctx) {

        Map<String, ?> imagesToBeSynchronized = ctx.getSharedPreferences(PREF_FILE_IMAGE_FOR_SYNC, MODE_PRIVATE).getAll();

        for (Map.Entry<String, ?> entry : imagesToBeSynchronized.entrySet()) {
            String prefKey = entry.getKey();

            SendImage sendImage = new SendImage(ctx);
            sendImage.execute(prefKey);
        }
    }

    public static void synchronizeSavedAttachments(Context ctx) {

        Map<String, ?> filesToBeSynchronized = ctx.getSharedPreferences(PREF_FILE_ATTACHMENT_FOR_SYNC, MODE_PRIVATE).getAll();
        String conStr = MyPrefs.getString(PREF_AZURE_CONNECTION_STRING, "");

        for (Map.Entry<String, ?> entry : filesToBeSynchronized.entrySet()) {
            String prefKey = entry.getKey();

            if(conStr.length()>0){
                UploadToAzure uploadToAzure = new UploadToAzure(ctx);
                uploadToAzure.execute(prefKey);
            } else {
                SendAttachment sendAttachment = new SendAttachment(ctx);
                sendAttachment.execute(prefKey);
            }

        }
    }

    private void synchronizeSavedData(boolean login) {

        if (login){
            String password = MyPrefs.getStringWithFileName(PREF_PASSWORD, "1", "");
            String username = MyPrefs.getString(PREF_USER_NAME, "");

            SendLogin sendLogin = new SendLogin(ctx, true, true);
            sendLogin.execute(username, password);
        }

        Map<String, ?> startTravelDataForSync = ctx.getSharedPreferences(PREF_FILE_START_TRAVEL_DATA_TO_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : startTravelDataForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendStartTravel sendStartTravel = new SendStartTravel(ctx, prefKey, CONST_DO_NOT_SHOW_PROGRESS_AND_TOAST);
            sendStartTravel.execute();
        }

        Map<String, ?> checkInDataForSync = ctx.getSharedPreferences(PREF_FILE_CHECK_IN_DATA_TO_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : checkInDataForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendCheckIn sendCheckIn = new SendCheckIn(ctx, prefKey, CONST_DO_NOT_SHOW_PROGRESS_AND_TOAST);
            sendCheckIn.execute();
        }

        Map<String, ?> checkOutDataForSync = ctx.getSharedPreferences(PREF_FILE_CHECK_OUT_DATA_TO_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : checkOutDataForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendCheckOut sendCheckOut = new SendCheckOut(ctx, prefKey, CONST_DO_NOT_SHOW_PROGRESS_AND_TOAST, false);

            if (!sendCheckOut.isCheckingOut()) {
                sendCheckOut.execute();
            }
        }

        Map<String, ?> rtnToBaseDataForSync = ctx.getSharedPreferences(PREF_FILE_RETURN_TO_BASE_DATA_TO_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : rtnToBaseDataForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendCheckOut sendCheckOut = new SendCheckOut(ctx, prefKey, CONST_DO_NOT_SHOW_PROGRESS_AND_TOAST, true);

            if (!sendCheckOut.isCheckingOut()) {
                sendCheckOut.execute();
            }
        }

        Map<String, ?> newProductDataForSync = ctx.getSharedPreferences(PREF_FILE_NEW_PRODUCTS_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : newProductDataForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendNewProduct sendNewProduct = new SendNewProduct(ctx, prefKey, CONST_DO_NOT_SHOW_PROGRESS_AND_TOAST);
            sendNewProduct.execute();
        }

        Map<String, ?> updatedAttributesDataForSync = ctx.getSharedPreferences(PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : updatedAttributesDataForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendUpdatedAttribute sendUpdatedAttribute = new SendUpdatedAttribute(ctx, "", "");
            sendUpdatedAttribute.execute(prefKey);
        }

        Map<String, ?> attributesForSync = ctx.getSharedPreferences(PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : attributesForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendNewAttribute sendNewAttribute = new SendNewAttribute(ctx, CONST_DO_NOT_FINISH_ACTIVITY);
            sendNewAttribute.execute(prefKey);
        }

        Map<String, ?> servicesForSync = ctx.getSharedPreferences(PREF_FILE_USED_SERVICES_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : servicesForSync.entrySet()) {
            String prefKey = entry.getKey();

            ToSendServices toSendServices = new ToSendServices(ctx, CONST_DO_NOT_FINISH_ACTIVITY);
            toSendServices.execute(prefKey);
        }

        Map<String, ?> measurementsForSync = ctx.getSharedPreferences(PREF_FILE_ADDED_MEASUREMENTS_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : measurementsForSync.entrySet()) {
            String prefKey = entry.getKey();

            ToSendNewMeasurement toSendNewMeasurement = new ToSendNewMeasurement(ctx, CONST_DO_NOT_FINISH_ACTIVITY);
            toSendNewMeasurement.execute(prefKey);
        }

        Map<String, ?> mandatoryTaskForSync = ctx.getSharedPreferences(PREF_FILE_MANDATORY_TASKS_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : mandatoryTaskForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendMandatoryTasks sendMandatoryTasks = new SendMandatoryTasks(ctx);
            sendMandatoryTasks.execute(prefKey, "");
        }

        Map<String, ?> consumablesForSync = ctx.getSharedPreferences(PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : consumablesForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendConsumables sendConsumables = new SendConsumables(ctx, prefKey, CONST_DO_NOT_FINISH_ACTIVITY);
            sendConsumables.execute();
        }

        Map<String, ?> zoneProductsForSync = ctx.getSharedPreferences(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : zoneProductsForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendProductMeasurements sendProductMeasurements = new SendProductMeasurements(ctx, prefKey, CONST_DO_NOT_FINISH_ACTIVITY, "0");
            sendProductMeasurements.execute();
        }

//        for (Map.Entry<String, ?> entry : ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.entrySet()) {
//            String assignmentId = entry.getKey();
//            for (Map.Entry<String, ?> entry2 : ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.get(assignmentId).entrySet()) {
//                String prefKey = entry2.getKey();
//
//                SendProductMeasurements sendProductMeasurements = new SendProductMeasurements(ctx, prefKey, CONST_DO_NOT_FINISH_ACTIVITY, assignmentId);
//                sendProductMeasurements.execute();
//            }
//        }

        Map<String, ?> usePtOvernightForSync = ctx.getSharedPreferences(PREF_FILE_USE_PT_OVERNIGHT_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : usePtOvernightForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendUsePTOvernight sendUsePTOvernight = new SendUsePTOvernight();
            sendUsePTOvernight.execute(prefKey);
        }

        Map<String, ?> newCustomersForSync = ctx.getSharedPreferences(PREF_FILE_NEW_CUSTOMER_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : newCustomersForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendNewCustomer sendNewCustomer = new SendNewCustomer(ctx, prefKey, false, false, null);
            sendNewCustomer.execute(prefKey);
        }

        Map<String, ?> newAssignmentForSync = ctx.getSharedPreferences(PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : newAssignmentForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendNewAssignment sendNewAssignment = new SendNewAssignment(ctx, prefKey, false);
            sendNewAssignment.execute(prefKey);
        }

        Map<String, ?> newZonesForSync = ctx.getSharedPreferences(PREF_FILE_NEW_INSTALLATION_ZONES_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : newZonesForSync.entrySet()) {
            String prefKey = entry.getKey();

            SendProjectZone sendProjectZone = new SendProjectZone(ctx, prefKey, false);
            sendProjectZone.execute(prefKey);
        }


        Map<String, ?> customFieldsforSync = ctx.getSharedPreferences(PREF_FILE_CUSTOM_FIELDS_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : customFieldsforSync.entrySet()) {
            String prefKey = entry.getKey();
            String vortexTable = prefKey.split("_")[2];

            SendCustomFields sendCustomFields = new SendCustomFields(ctx, prefKey, true, vortexTable);
            sendCustomFields.execute(prefKey);
        }
//        GetAssignments getAssignments = new GetAssignments(ctx, true);
//        getAssignments.execute();

        Map<String, ?> zonesWithNoMeasurements = ctx.getSharedPreferences(PREF_FILE_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : zonesWithNoMeasurements.entrySet()) {
            String prefKey = entry.getKey();

            SendZonesWithNoMeasurement sendZonesWithNoMeasurement = new SendZonesWithNoMeasurement(ctx, prefKey);
            sendZonesWithNoMeasurement.execute();
        }

        Map<String, ?> productsToInstallation = ctx.getSharedPreferences(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SYNC, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : productsToInstallation.entrySet()) {
            String prefKey = entry.getKey();

            SetProductsToInstallation setProductsToInstallation = new SetProductsToInstallation(ctx, prefKey);
            setProductsToInstallation.execute();
        }

//        Map<String, ?> detChildren = ctx.getSharedPreferences(PREF_FILE_DET_CHILDREN_FOR_SYNC, MODE_PRIVATE).getAll();
//        for (Map.Entry<String, ?> entry : detChildren.entrySet()) {
//            String prefKey = entry.getKey();
//
//            SendDetChildren sendDetChildren = new SendDetChildren(ctx, prefKey);
//            sendDetChildren.execute();
//        }


    }
}
