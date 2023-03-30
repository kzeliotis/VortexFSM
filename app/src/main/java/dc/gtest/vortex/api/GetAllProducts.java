package dc.gtest.vortex.api;

import android.os.AsyncTask;
import android.os.Bundle;

import dc.gtest.vortex.adapters.AllProductsRvAdapter;
import dc.gtest.vortex.data.AllProductsData;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.api.MyApi.API_GET_ALL_PRODUCTS;
import static dc.gtest.vortex.api.MyApi.API_GET_PICKING_LIST;
import static dc.gtest.vortex.api.MyApi.API_GET_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ALL_PRODUCTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_WAREHOUSEID;

public class GetAllProducts extends AsyncTask<String, Void, String > {

    private final AllProductsRvAdapter allProductsRvAdapter;
    private final boolean warehouseProducts;

    private String apiUrl;
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public GetAllProducts(AllProductsRvAdapter allProductsRvAdapter, boolean warehouseProducts) {
        this.allProductsRvAdapter = allProductsRvAdapter;
        this.warehouseProducts = warehouseProducts;
    }

    @Override
    protected String doInBackground(String... params) {

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        if(warehouseProducts) {
            String warehouseID = MyPrefs.getString(PREF_WAREHOUSEID, "0");
            apiUrl = baseHostUrl + API_GET_WAREHOUSE_PRODUCTS + warehouseID;
        }else {
            apiUrl = baseHostUrl + API_GET_ALL_PRODUCTS;
        }

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
//        MyLogs.showFullLog("myLogs: " + this.getClass().getSimpleName(), apiUrl, "no body for GET request", responseCode, responseMessage, responseBody);

        if (responseCode == 200 && responseBody != null) {

            if (warehouseProducts) {
                MyPrefs.setString(PREF_DATA_WAREHOUSE_PRODUCTS, responseBody);
            }else{
                MyPrefs.setString(PREF_DATA_ALL_PRODUCTS, responseBody);
            }

            AllProductsData.generate(warehouseProducts);

            if (allProductsRvAdapter != null) {
                allProductsRvAdapter.notifyDataSetChanged();
            }
        }
    }
}
