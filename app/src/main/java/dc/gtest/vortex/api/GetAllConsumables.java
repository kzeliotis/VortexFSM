package dc.gtest.vortex.api;

import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.adapters.AllConsumablesRvAdapter;
import dc.gtest.vortex.data.AllConsumablesData;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_ALL_CONSUMABLES;
import static dc.gtest.vortex.api.MyApi.API_GET_PICKING_LIST;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ALL_CONSUMABLES;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PICKING_LIST_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_WAREHOUSE_CONSUMABLES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_WAREHOUSEID;

public class GetAllConsumables extends AsyncTask<String, Void, String > {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final AllConsumablesRvAdapter allConsumablesRvAdapter;
    private final String AssignmentId;
    private final boolean warehouseProducts;
    private final boolean pickingList;
    private final String projectWarehouseId;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public GetAllConsumables(AllConsumablesRvAdapter allConsumablesRvAdapter, String AssignmentId, boolean warehouseProducts,
                             boolean PickingList, String ProjectWarehouseId) {
        this.allConsumablesRvAdapter = allConsumablesRvAdapter;
        this.AssignmentId = AssignmentId;
        this.warehouseProducts = warehouseProducts;
        this.pickingList = PickingList;
        this.projectWarehouseId = ProjectWarehouseId;
    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String warehouseID = "0";
        if(warehouseProducts){
            warehouseID = MyPrefs.getString(PREF_WAREHOUSEID, "0");
            if(projectWarehouseId.length() > 0 && !projectWarehouseId.equals("0")){
                warehouseID = projectWarehouseId;
            }
        }
        if(pickingList){
            apiUrl = baseHostUrl + API_GET_PICKING_LIST + AssignmentId;
        } else {
            apiUrl = baseHostUrl + API_GET_ALL_CONSUMABLES + AssignmentId + "&WarehouseId=" + warehouseID;
        }

        try {
            Bundle bundle = MyApi.get(apiUrl, null);

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
        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no_body_for_get_request", responseCode, responseMessage, ""); //response body too long

        if ( responseCode == 200 && responseBody != null ) {
            MyPrefs.setString(PREF_DATA_ALL_CONSUMABLES, responseBody);
            String warehouseID = "0";
            if(warehouseProducts) {
                warehouseID = MyPrefs.getString(PREF_WAREHOUSEID, "0");
                if(projectWarehouseId.length() > 0 && !projectWarehouseId.equals("0")){
                    warehouseID = projectWarehouseId;
                }
                MyPrefs.setStringWithFileName(PREF_FILE_RELATED_WAREHOUSE_CONSUMABLES_FOR_SHOW, warehouseID, responseBody);
            } else if (pickingList) {
                MyPrefs.setStringWithFileName(PREF_FILE_PICKING_LIST_FOR_SHOW, AssignmentId, responseBody);
            } else {
                MyPrefs.setStringWithFileName(PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW, AssignmentId, responseBody);
            }

            AllConsumablesData.generate(AssignmentId, warehouseProducts, pickingList, warehouseID);

            if (allConsumablesRvAdapter != null) {
                allConsumablesRvAdapter.notifyDataSetChanged();
            }
        }
    }
}
