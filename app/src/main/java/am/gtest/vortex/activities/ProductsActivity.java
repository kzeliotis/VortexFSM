package am.gtest.vortex.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.MySpinnerAdapter;
import am.gtest.vortex.adapters.ProductsRvAdapter;
import am.gtest.vortex.api.GetProducts;
import am.gtest.vortex.api.GetProductTypes;
import am.gtest.vortex.application.MyApplication;
import am.gtest.vortex.data.ProductTypesData;
import am.gtest.vortex.data.ProductsData;
import am.gtest.vortex.support.MyCanEdit;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MySynchronize;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.CONST_WAREHOUSE_PRODUCTS;
import static am.gtest.vortex.support.MyGlobals.KEY_PROJECT_INSTALLATION_ID;
import static am.gtest.vortex.support.MyGlobals.NEW_ATTRIBUTES_LIST;
import static am.gtest.vortex.support.MyGlobals.PRODUCTS_LIST;
import static am.gtest.vortex.support.MyGlobals.PRODUCT_TYPES_LIST;
import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static am.gtest.vortex.support.MyLocalization.localized_add_new_product;
import static am.gtest.vortex.support.MyLocalization.localized_all_caps;
import static am.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static am.gtest.vortex.support.MyLocalization.localized_choose_from_warehouse;
import static am.gtest.vortex.support.MyLocalization.localized_filter_by_type;
import static am.gtest.vortex.support.MyLocalization.localized_no_product;
import static am.gtest.vortex.support.MyLocalization.localized_products;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_PRODUCT_TYPES;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_PRODUCTS_DATA;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_DATA;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class ProductsActivity extends BaseDrawerActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private TextView tvAssignmentId;
    private TextView tvTypeSpinnerTitle;
    private Spinner spProductTypes;
    private Button btnAddNewProduct;

    private ProductsRvAdapter productsRvAdapter;

    public static String searchText = "";
    public static String selectedType = "";
    public static String projectInstallationId = "0";
    public static boolean IsInstallation = false;

    private SearchView searchView;

    private boolean doSpinnerItemSelected = true;
    private boolean doSearchTexChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_assignment_products, flBaseContainer, true);

        // clear previous data
        searchText = "";
        selectedType = "";

        projectInstallationId = getIntent().getStringExtra(KEY_PROJECT_INSTALLATION_ID);
        if (projectInstallationId != null){
            IsInstallation = true;
        } else {
            IsInstallation = false;
            projectInstallationId = "0";
        }

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        tvTypeSpinnerTitle = findViewById(R.id.tvTypeSpinnerTitle);
        spProductTypes = findViewById(R.id.spProductTypes);
        btnAddNewProduct = findViewById(R.id.btnAddNewProduct);
        RecyclerView rvAssignmentProducts = findViewById(R.id.rvAssignmentProducts);

        setupProductTypesSpinner();
        spProductTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.e(LOG_TAG, "---------------------------- view.getId(): " + view.getId());
                Log.e(LOG_TAG, "---------------------------- R.id.spProductTypes: " + R.id.spProductTypes);
                Log.e(LOG_TAG, "---------------------------- position: " + position);
                Log.e(LOG_TAG, "---------------------------- start doSpinnerItemSelected: " + doSpinnerItemSelected);

                if (doSpinnerItemSelected) {
                    if (position == 0) {
                        selectedType = "";
                    } else {
                        selectedType = spProductTypes.getSelectedItem().toString().toLowerCase();
                        Log.e(LOG_TAG, "---------------------------- selectedType: " + selectedType);
                    }

                    productsRvAdapter.getFilter().filter(searchText);
                } else {
                    doSpinnerItemSelected = true;
                }

                Log.e(LOG_TAG, "---------------------------- end doSpinnerItemSelected: " + doSpinnerItemSelected);
                Log.e(LOG_TAG, "---------------------------- selectedType: " + selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        clearFilters();

        btnAddNewProduct.setOnClickListener(v -> {
            if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {

                NEW_ATTRIBUTES_LIST.clear();

                new AlertDialog.Builder(this)
                        .setMessage(localized_choose_from_warehouse)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            dialog.dismiss();
                            Intent intent = new Intent(ProductsActivity.this, AllProductsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(CONST_WAREHOUSE_PRODUCTS, true);
                            intent.putExtra(KEY_PROJECT_INSTALLATION_ID, projectInstallationId);
                            startActivity(intent);
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> {
                            dialog.dismiss();
                            Intent intent = new Intent(ProductsActivity.this, AllProductsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(CONST_WAREHOUSE_PRODUCTS, false);
                            intent.putExtra(KEY_PROJECT_INSTALLATION_ID, projectInstallationId);
                            startActivity(intent);
                        })
                        .show();


            }
        });

        PRODUCTS_LIST.clear();

        productsRvAdapter = new ProductsRvAdapter(PRODUCTS_LIST, ProductsActivity.this);
        rvAssignmentProducts.setAdapter(productsRvAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        searchText = "";
//        if (searchView != null) {
//            searchView.setQuery("", false);
//            searchView.setIconified(true);
//        }

        // do this at onResume to refresh view with new added not synchronized data
        String productsData;
        if(IsInstallation){
            productsData = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_PRODUCTS_DATA, projectInstallationId, "");
        } else {
            productsData = MyPrefs.getStringWithFileName(PREF_FILE_PRODUCTS_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), "");
        }


        Log.e(LOG_TAG, "==================== productsData: " + productsData);

        ProductsData.generate(productsData);
        if (PRODUCTS_LIST.size() == 0) {
            Toast.makeText(MyApplication.getContext(), localized_no_product, Toast.LENGTH_LONG).show();
        }
        productsRvAdapter.notifyDataSetChanged();

        if (MyUtils.isNetworkAvailable()) {
            GetProducts getProducts = new GetProducts(this, SELECTED_ASSIGNMENT.getAssignmentId(), true, projectInstallationId);
            getProducts.execute();
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
                if (doSearchTexChanged) {
                    searchText = newText;
                    productsRvAdapter.getFilter().filter(newText);
                } else {
                    doSearchTexChanged = true;
                }
                return true;
            }
        });

        return true;
    }

    private void clearFilters() {

        Log.e(LOG_TAG, " -------------- inside clearFilters.");

        searchText = "";
        selectedType = "";

        doSearchTexChanged = false;
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
        doSearchTexChanged = true;

        if (spProductTypes.getSelectedItemPosition() != 0) {
            doSpinnerItemSelected = false;
        }
        spProductTypes.setSelection(0);
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        setAdapterOnSpinner(this, spProductTypes);
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_products);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        if(IsInstallation){
            tvAssignmentId.setText(SELECTED_INSTALLATION.getProjectInstallationFullDescription());
        } else {
            String assignmentIdText = localized_assignment_id + ": " + SELECTED_ASSIGNMENT.getAssignmentId();
            tvAssignmentId.setText(assignmentIdText);
        }


        String filterByType = localized_filter_by_type + ":";
        tvTypeSpinnerTitle.setText(filterByType);

        btnAddNewProduct.setText(localized_add_new_product);
    }

    private void setupProductTypesSpinner() {
        String productTypes = MyPrefs.getString(PREF_DATA_PRODUCT_TYPES, "");

        if (productTypes.isEmpty()) {
            if (MyUtils.isNetworkAvailable()) {
                GetProductTypes getProductTypes = new GetProductTypes(this);
                getProductTypes.execute();
            }
        } else {
            ProductTypesData.generate(productTypes);
            setAdapterOnSpinner(this, spProductTypes);
        }
    }

    public static void setAdapterOnSpinner(Context ctx, Spinner spProductTypes) {

        String[] productTypesArray = new String[PRODUCT_TYPES_LIST.size() + 1];
        productTypesArray[0] = localized_all_caps;

        for (int i = 0; i < PRODUCT_TYPES_LIST.size(); i++) {
            productTypesArray[i+1] = PRODUCT_TYPES_LIST.get(i).getTypeDescription();
        }

        spProductTypes.setAdapter(new MySpinnerAdapter(ctx, productTypesArray));
    }
}
