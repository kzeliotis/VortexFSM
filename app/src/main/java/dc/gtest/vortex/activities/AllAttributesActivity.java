package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.UUID;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AllAttributesRvAdapter;
import dc.gtest.vortex.api.GetAllAttributes;
import dc.gtest.vortex.api.SendNewAttribute;
import dc.gtest.vortex.api.SendNewProduct;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.data.AllAttributesData;
import dc.gtest.vortex.models.ProductModel;
import dc.gtest.vortex.support.MyDateTime;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.CONST_FINISH_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.CONST_PARENT_ALL_PRODUCTS_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.CONST_PARENT_ATTRIBUTES_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.CONST_SHOW_PROGRESS_AND_TOAST;
import static dc.gtest.vortex.support.MyGlobals.KEY_PARENT_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.KEY_PRODUCT_DESCRIPTION;
import static dc.gtest.vortex.support.MyGlobals.KEY_PRODUCT_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_PROJECT_INSTALLATION_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_WAREHOUSE_ID;
import static dc.gtest.vortex.support.MyGlobals.NEW_ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static dc.gtest.vortex.support.MyGlobals.attributeValueforScan;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_please_select_product;
import static dc.gtest.vortex.support.MyLocalization.localized_select_attribute;
import static dc.gtest.vortex.support.MyLocalization.localized_select_set_save_attribute;
import static dc.gtest.vortex.support.MyLocalization.localized_send_data_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_PRODUCTS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AllAttributesActivity extends BaseDrawerActivity {
    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private AllAttributesRvAdapter allAttributesRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private Button btnSendNewAttributes;

    public static String savedAttributes = "";
    private String newProductName = "";
    private String productId = "";
    private String parentActivity;
    private String warehouseID;
    private String projectInstallationId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_all_attributes, flBaseContainer, true);

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        TextView tvSelectedProduct = findViewById(R.id.tvSelectedProduct);
        RecyclerView rvAllAttributes = findViewById(R.id.rvAllAttributes);
        btnSendNewAttributes = findViewById(R.id.btnSendNewAttributes);

        savedAttributes = "";
        newProductName = getIntent().getStringExtra(KEY_PRODUCT_DESCRIPTION);
        parentActivity = getIntent().getStringExtra(KEY_PARENT_ACTIVITY);
        warehouseID = getIntent().getStringExtra(KEY_WAREHOUSE_ID);
        productId = getIntent().getStringExtra(KEY_PRODUCT_ID);
        projectInstallationId = getIntent().getStringExtra(KEY_PROJECT_INSTALLATION_ID);
        if (projectInstallationId == null) {projectInstallationId = "0";}

        tvSelectedProduct.setText(newProductName);

        AllAttributesData.generate();
        allAttributesRvAdapter = new AllAttributesRvAdapter(ALL_ATTRIBUTES_LIST_FILTERED, this);
        rvAllAttributes.setAdapter(allAttributesRvAdapter);

        if (ALL_ATTRIBUTES_LIST.size() == 0) {
            GetAllAttributes getAllAttributes = new GetAllAttributes(allAttributesRvAdapter);
            getAllAttributes.execute();
        }

        btnSendNewAttributes.setOnClickListener(v -> {

            switch (parentActivity) {
                case CONST_PARENT_ATTRIBUTES_ACTIVITY:
                    if (savedAttributes != null && !savedAttributes.equals("")) {

                        String newAttributesJsonString =
                                "{\n" +
                                        "  \"assignmentId\": \"" + MyPrefs.getString(PREF_ASSIGNMENT_ID, "") + "\",\n" +
                                        "  \"oldProductId\": \"" + SELECTED_PRODUCT.getProjectProductId() + "\",\n" +
                                        "  \"WarehouseId\": \"" + warehouseID + "\",\n" +
                                        "  \"ProjectProductId\": \"0\",\n" +
                                        "  \"UserId\": \"" + MyPrefs.getString(MyPrefs.PREF_USERID, "0") + "\",\n" +
                                        "  \"Attributes\": {\n" +
                                        "    " + savedAttributes +
                                        "  }\n" +
                                        "}";

                        String prefKey = UUID.randomUUID().toString();
                        MyPrefs.setStringWithFileName(PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC, prefKey, newAttributesJsonString);

                        if (MyUtils.isNetworkAvailable()) {
                            SendNewAttribute sendNewAttribute = new SendNewAttribute(AllAttributesActivity.this, CONST_FINISH_ACTIVITY);
                            sendNewAttribute.execute(prefKey);
                        } else {
                            MyDialogs.showOK(AllAttributesActivity.this, localized_no_internet_data_saved);
                        }
                    } else {
                        MyDialogs.showOK(AllAttributesActivity.this, localized_select_set_save_attribute);
                    }
                    break;
                case CONST_PARENT_ALL_PRODUCTS_ACTIVITY:
                    if (newProductName != null && !newProductName.equals("")) {

                        newProductName = newProductName.replace('"', '\"');

                        String newProductJsonString =
                                "{\n" +
                                        "  \"assignmentId\": \"" + MyPrefs.getString(PREF_ASSIGNMENT_ID, "") + "\",\n" +
//                                        "  \"newProductName\": \"" + newProductName + "\",\n" +
                                        "  \"WarehouseId\": \"" + warehouseID + "\",\n" +
                                        "  \"ProjectProductId\": \"0\",\n" +
                                        "  \"ProductId\": \"" + productId + "\",\n" +
                                        "  \"ProjectInstallationId\": \"" + projectInstallationId + "\",\n" +
                                        "  \"UserId\": \"" + MyPrefs.getString(MyPrefs.PREF_USERID, "0") + "\",\n" +
                                        "  \"Attributes\": {\n" +
                                        "    " + savedAttributes + "\n" +
                                        "  }\n" +
                                        "}";

                        String prefKey = UUID.randomUUID().toString();
                        MyPrefs.setStringWithFileName(PREF_FILE_NEW_PRODUCTS_FOR_SYNC, prefKey, newProductJsonString);

                        ProductModel productModel = new ProductModel();
                        productModel.setInstallationDate(MyDateTime.get_MM_dd_yyyy_HH_mm_from_now());
                        productModel.setProductDescription(newProductName);
                        productModel.setProductAttributes(NEW_ATTRIBUTES_LIST);
                        productModel.setNotSynchronized(true);

                        String productsData = MyPrefs.getStringWithFileName(PREF_FILE_PRODUCTS_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), "");

                        if (productsData.length() > 0) {
                            productsData = productsData.substring(0, productsData.length() - 1) + "," + productModel + "]";
                        } else {
                            productsData = "[" + productModel + "]";
                        }

                        MyPrefs.setStringWithFileName(PREF_FILE_PRODUCTS_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), productsData);

                        Log.e(LOG_TAG, "==================== productsData: " + productsData);

                        if (MyUtils.isNetworkAvailable()) {
                            SendNewProduct sendNewProduct = new SendNewProduct(AllAttributesActivity.this, prefKey, CONST_SHOW_PROGRESS_AND_TOAST);
                            sendNewProduct.execute();
                        } else {
                            Toast.makeText(MyApplication.getContext(), localized_no_internet_data_saved, Toast.LENGTH_SHORT).show();
                        }

                        finish();

                    } else {
                        MyDialogs.showOK(AllAttributesActivity.this, localized_please_select_product);
                    }
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }

        updateUiTexts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) itemSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                allAttributesRvAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_select_attribute);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String assignmentIdText = localized_assignment_id + ": " + MyPrefs.getString(PREF_ASSIGNMENT_ID, "");
        tvAssignmentId.setText(assignmentIdText);

        btnSendNewAttributes.setText(localized_send_data_caps);
    }


    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 49374) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    String ScannedCode = result.getContents();
                    // assignmentsRvAdapter.getFilter().filter(ScannedCode);
                    //Toast.makeText(this,  ScannedCode, Toast.LENGTH_LONG).show();
                    attributeValueforScan.setText(ScannedCode);
                }
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            new AlertDialog.Builder(this)
//                    .setMessage(localized_sure_to_leave)
//                    .setPositiveButton(R.string.yes, (dialog, which) -> {
//                        dialog.dismiss();
//                        finish();
//                    })
//                    .setNegativeButton(R.string.no, null)
//                    .show();
//        }
//    }
}
