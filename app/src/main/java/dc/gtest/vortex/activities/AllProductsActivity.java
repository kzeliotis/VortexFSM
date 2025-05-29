package dc.gtest.vortex.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AllProductsRvAdapter;
import dc.gtest.vortex.api.GetAllProducts;
import dc.gtest.vortex.data.AllProductsData;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.ALL_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.CONST_SITE_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.support.MyGlobals.CONST_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.support.MyGlobals.KEY_PRODUCT_COMPONENT_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_PROJECT_INSTALLATION_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_REPLACE_PROJECT_PRODUCT_ID;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_select_product;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AllProductsActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private AllProductsRvAdapter allProductsRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private String projectInstallationId;
    private String projectWarehouseId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_all_products, flBaseContainer, true);

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        RecyclerView rvAllProducts = findViewById(R.id.rvAllProducts);

        projectInstallationId = getIntent().getStringExtra(KEY_PROJECT_INSTALLATION_ID);
        if (projectInstallationId == null){
            projectInstallationId = "0";
        }

        String replaceProjectProductId = getIntent().getStringExtra(KEY_REPLACE_PROJECT_PRODUCT_ID);
        if (replaceProjectProductId == null){
            replaceProjectProductId = "0";
        }

        String replaceProductComponentId = getIntent().getStringExtra(KEY_PRODUCT_COMPONENT_ID);
        if (replaceProductComponentId == null){
            replaceProductComponentId = "0";
        }

        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);
        boolean WarehouseProducts = getIntent().getBooleanExtra(CONST_WAREHOUSE_PRODUCTS, false);
        String selectedWarehouseId = WarehouseProducts ? MyPrefs.getString(MyPrefs.PREF_WAREHOUSEID, "0") : "0";
        boolean siteWarehouse = getIntent().getBooleanExtra(CONST_SITE_WAREHOUSE_PRODUCTS, false);
        if (siteWarehouse){
            projectWarehouseId = SELECTED_ASSIGNMENT.getProjectWarehouseId();
            selectedWarehouseId = projectWarehouseId;
            WarehouseProducts = true;
        }

        AllProductsData.generate(WarehouseProducts, selectedWarehouseId);
        if (WarehouseProducts) {
            allProductsRvAdapter = new AllProductsRvAdapter(ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED, this, isForNewAssignment,
                    WarehouseProducts, projectInstallationId, replaceProjectProductId, replaceProductComponentId, projectWarehouseId);
        } else {
            allProductsRvAdapter = new AllProductsRvAdapter(ALL_PRODUCTS_LIST_FILTERED, this, isForNewAssignment,
                    WarehouseProducts, projectInstallationId, replaceProjectProductId, replaceProductComponentId, projectWarehouseId);
        }

        rvAllProducts.setAdapter(allProductsRvAdapter);

        if (WarehouseProducts){
            if (ALL_WAREHOUSE_PRODUCTS_LIST.size() == 0 || MyUtils.isNetworkAvailable()) {
                GetAllProducts getAllProducts = new GetAllProducts(allProductsRvAdapter, true, projectWarehouseId);
                getAllProducts.execute();
            }
        } else {
            if (ALL_PRODUCTS_LIST.size() == 0) {
                GetAllProducts getAllProducts = new GetAllProducts(allProductsRvAdapter, false, "0");
                getAllProducts.execute();
            }
        }
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
                allProductsRvAdapter.getFilter().filter(newText);
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
            getSupportActionBar().setTitle(localized_select_product);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String assignmentIdText = localized_assignment_id + ": " + MyPrefs.getString(PREF_ASSIGNMENT_ID, "");
        tvAssignmentId.setText(assignmentIdText);
    }
}
