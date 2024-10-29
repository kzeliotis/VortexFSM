package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amrdeveloper.treeview.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.ProductsActivity;
import dc.gtest.vortex.adapters.AttributesRvAdapter;
import dc.gtest.vortex.adapters.ProductTreeRvAdapter;
import dc.gtest.vortex.adapters.ProductsRvAdapter;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.data.ProductsData;
import dc.gtest.vortex.models.ProductModel;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.api.MyApi.API_GET_ASSIGNMENT_PRODUCTS;
import static dc.gtest.vortex.api.MyApi.API_GET_PROJECT_PRODUCT_BY_IDENTITY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_BODY;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_CODE;
import static dc.gtest.vortex.api.MyApi.MY_API_RESPONSE_MESSAGE;
import static dc.gtest.vortex.support.MyGlobals.ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_TREE_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_TREE_LIST_SAVED_STATE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.globalSelectedProductId;
import static dc.gtest.vortex.support.MyLocalization.localized_no_product;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ID_SEARCH_PRODUCTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_PRODUCTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NO_INSTALLATION_PRODUCTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_DATA;

public class GetProducts extends AsyncTask<String, Void, String > {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private int responseCode;

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private final String assignmentId;
    private final String projectInstallationId;
    private final boolean hideProgress;
    private final boolean selectProductsForInstallation;
    private final String idValue;

    public GetProducts(Context ctx, String assignmentId, boolean hideProgress, String projectInstallationId, boolean SelectProductForInstallation, String idValue) {
        this.ctx = ctx;
        this.assignmentId = assignmentId;
        this.projectInstallationId = projectInstallationId;
        this.hideProgress = hideProgress;
        this.selectProductsForInstallation = SelectProductForInstallation;
        this.idValue = idValue;
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

        if (!projectInstallationId.equals("0") && !selectProductsForInstallation){
            apiUrl += "&ProjectInstallationId=" + projectInstallationId;
        } else if (selectProductsForInstallation){
            apiUrl += "&ProjectInstallationId=-1";
        }

        if(!idValue.isEmpty()){
            apiUrl = baseHostUrl + API_GET_PROJECT_PRODUCT_BY_IDENTITY + idValue;
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

            if(!projectInstallationId.equals("0") && !selectProductsForInstallation) {
                MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATION_PRODUCTS_DATA, projectInstallationId, responseBody);
            } else if (selectProductsForInstallation) {
                MyPrefs.setStringWithFileName(PREF_FILE_NO_INSTALLATION_PRODUCTS_DATA, assignmentId, responseBody);
            } else if (idValue.isEmpty()){
                MyPrefs.setStringWithFileName(PREF_FILE_ID_SEARCH_PRODUCTS_DATA, "0", responseBody);
            } else {
                MyPrefs.setStringWithFileName(PREF_FILE_PRODUCTS_DATA, assignmentId, responseBody);
            }

        }

        RecyclerView rvAssignmentProducts = null;
        if (ctx != null) {
            rvAssignmentProducts = ((AppCompatActivity)ctx).findViewById(R.id.rvAssignmentProducts);
        }


        if (rvAssignmentProducts != null) {

            RecyclerView.Adapter adapter = rvAssignmentProducts.getAdapter();

            if (adapter instanceof ProductsRvAdapter){
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
            } else if (adapter instanceof ProductTreeRvAdapter) {
                ProductTreeRvAdapter productsRvAdapter = (ProductTreeRvAdapter) rvAssignmentProducts.getAdapter();

                if (productsRvAdapter != null) {
                    if (responseBody != null) {
                        ProductsData.generate(responseBody);

                        if (PRODUCTS_LIST.size() == 0) {
                            Toast toast = Toast.makeText(MyApplication.getContext(), localized_no_product, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                        //restore expand/collapse state else focus assignment product on view
                        if(PRODUCTS_TREE_LIST_SAVED_STATE.size()>0){
                            for (TreeNode tn : PRODUCTS_TREE_LIST){
                                TreeNode selectedNode = PRODUCTS_TREE_LIST_SAVED_STATE.stream()
                                        .filter(obj -> ((ProductModel)obj.getValue()).getProjectProductId().equals(((ProductModel)tn.getValue()).getProjectProductId()))
                                        .findFirst()
                                        .orElse(null);
                                if(selectedNode != null){
                                    tn.setExpanded(selectedNode.isExpanded());
                                    tn.setSelected(selectedNode.isSelected());
                                }
                            }
                        } else {
                            TreeNode assignmentProductNode = PRODUCTS_TREE_LIST.stream()
                                    .filter(obj -> ((ProductModel)obj.getValue()).getProjectProductId().equals(SELECTED_ASSIGNMENT.getprojectProductId()))
                                    .findFirst()
                                    .orElse(null);
                            if (assignmentProductNode != null){
                                List<String> parents = new ArrayList<>();
                                parents = MyUtils.GetNodeAndParents(assignmentProductNode, parents);
                                for(TreeNode treenode : PRODUCTS_TREE_LIST){
                                    if(parents.contains(((ProductModel) treenode.getValue()).getProjectProductId())){
                                        treenode.setSelected(true);
                                        treenode.setExpanded(true);
                                    } else if (treenode == assignmentProductNode){
                                        treenode.setSelected(true);
                                    }
                                }

                                List<TreeNode> visibleNodes = PRODUCTS_TREE_LIST.stream().filter(TreeNode::isSelected).collect(Collectors.toList());
                                int index = visibleNodes.indexOf(assignmentProductNode);
                                rvAssignmentProducts.smoothScrollToPosition(index);

                            }
                        }

                        productsRvAdapter.notifyDataSetChanged();
                        productsRvAdapter.getFilter().filter(ProductsActivity.searchText);
                    }
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
