package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.ProductsActivity;
import am.gtest.vortex.adapters.AttributesRvAdapter;
import am.gtest.vortex.adapters.ProductsRvAdapter;
import am.gtest.vortex.application.MyApplication;
import am.gtest.vortex.data.ProductsData;
import am.gtest.vortex.support.MyLogs;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.api.MyApi.API_GET_ASSIGNMENT_PRODUCTS;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static am.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static am.gtest.vortex.support.MyGlobals.ATTRIBUTES_LIST;
import static am.gtest.vortex.support.MyGlobals.PRODUCTS_LIST;
import static am.gtest.vortex.support.MyGlobals.globalSelectedProductId;
import static am.gtest.vortex.support.MyLocalization.localized_no_product;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_PRODUCTS_DATA;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_DATA;

public class GetProducts extends AsyncTask<String, Void, String > {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private int responseCode;

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private String assignmentId;
    private String projectInstallationId;
    private final boolean hideProgress;

    public GetProducts(Context ctx, String assignmentId, boolean hideProgress, String projectInstallationId) {
        this.ctx = ctx;
        this.assignmentId = assignmentId;
        this.projectInstallationId = projectInstallationId;
        this.hideProgress = hideProgress;
    }

    @Override
    protected void onPreExecute() {

        if (ctx != null) {
            mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);

//            Log.e(LOG_TAG, "================================== onPreExecute assignmentId: " + assignmentId);
            }
        }

    }

    @Override
    protected String doInBackground(String... params) {
        String responseMessage = "";
        String responseBody = "";

        String baseHostUrl = MyPrefs.getString(PREF_BASE_HOST_URL, "");
        String apiUrl = baseHostUrl + API_GET_ASSIGNMENT_PRODUCTS + assignmentId;

        if (!projectInstallationId.equals("0")){
            apiUrl += "&ProjectInstallationId=" + projectInstallationId;
        }

        try {
            Bundle bundle = MyApi.get(apiUrl);

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

        if (hideProgress && mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);

//            Log.e(LOG_TAG, "================================== onPostExecute assignmentId: " + assignmentId);
        }

        // for log
//        for (int i = 0; i < PRODUCTS_LIST.size(); i++) {
//            if (PRODUCTS_LIST.get(i).getProjectProductId().equalsIgnoreCase(globalSelectedProductId)) {
//                Log.e(LOG_TAG, "===========================PRODUCTS_LIST.get(i).getProductAttributes():\n" + PRODUCTS_LIST.get(i).getProductAttributes());
//                break;
//            }
//        }

        if (responseCode >= 200 && responseCode < 300 &&
                responseBody != null &&
                !responseBody.equals("") &&
                !responseBody.equals("null") &&
//                !responseBody.equals("[]") && // execute code to save empty list
                !responseBody.equals("{}")
                ) {

            if(!projectInstallationId.equals("0")){
                MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATION_PRODUCTS_DATA, projectInstallationId, responseBody);
            } else {
                MyPrefs.setStringWithFileName(PREF_FILE_PRODUCTS_DATA, assignmentId, responseBody);
            }

        }

        RecyclerView rvAssignmentProducts = null;
        if (ctx != null) {
            rvAssignmentProducts = ((AppCompatActivity)ctx).findViewById(R.id.rvAssignmentProducts);
        }


        if (rvAssignmentProducts != null) {

            ProductsRvAdapter productsRvAdapter = (ProductsRvAdapter) rvAssignmentProducts.getAdapter();

            if (productsRvAdapter != null) {
                if (responseBody != null) {
                    ProductsData.generate(responseBody);

                    if (PRODUCTS_LIST.size() == 0) {
                        Toast toast = Toast.makeText(MyApplication.getContext(), localized_no_product, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                    productsRvAdapter.notifyDataSetChanged();
                    productsRvAdapter.getFilter().filter(ProductsActivity.searchText);
                }
            }
        }

        if (!projectInstallationId.equals(0)) {



        }

        RecyclerView rvAttributes = null;
        if (ctx != null){
            rvAttributes = ((AppCompatActivity)ctx).findViewById(R.id.rvAttributes);
        }


        if (rvAttributes != null) {

            AttributesRvAdapter attributesRvAdapter = (AttributesRvAdapter) rvAttributes.getAdapter();

            if (attributesRvAdapter != null) {

                if (responseBody != null) {
                    ProductsData.generate(responseBody);

                    for (int i = 0; i < PRODUCTS_LIST.size(); i++) {
                        if (PRODUCTS_LIST.get(i).getProjectProductId().equalsIgnoreCase(globalSelectedProductId)) {
                            ATTRIBUTES_LIST.clear();
                            ATTRIBUTES_LIST.addAll(PRODUCTS_LIST.get(i).getProductAttributes());
                            break;
                        }
                    }

                    attributesRvAdapter.notifyDataSetChanged();
                }
            }
        }

//        Log.e(LOG_TAG, "===========================ATTRIBUTES_LIST:\n" + ATTRIBUTES_LIST.toString());
    }
}
