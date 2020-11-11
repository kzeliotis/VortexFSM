package am.gtest.vortex.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.AllProductsRvAdapter;
import am.gtest.vortex.api.GetAllProducts;
import am.gtest.vortex.data.AllProductsData;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.ALL_PRODUCTS_LIST;
import static am.gtest.vortex.support.MyGlobals.ALL_PRODUCTS_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_PRODUCTS_LIST;
import static am.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.CONST_WAREHOUSE_PRODUCTS;
import static am.gtest.vortex.support.MyGlobals.KEY_PROJECT_INSTALLATION_ID;
import static am.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static am.gtest.vortex.support.MyLocalization.localized_select_product;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AllProductsActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private AllProductsRvAdapter allProductsRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private String projectInstallationId;

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


        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);
        boolean WarehouseProducts = getIntent().getBooleanExtra(CONST_WAREHOUSE_PRODUCTS, false);

        AllProductsData.generate(WarehouseProducts);
        if (WarehouseProducts) {
            allProductsRvAdapter = new AllProductsRvAdapter(ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED, this, isForNewAssignment, WarehouseProducts, projectInstallationId);
        } else {
            allProductsRvAdapter = new AllProductsRvAdapter(ALL_PRODUCTS_LIST_FILTERED, this, isForNewAssignment, WarehouseProducts, projectInstallationId);
        }

        rvAllProducts.setAdapter(allProductsRvAdapter);

        if (WarehouseProducts){
            if (ALL_WAREHOUSE_PRODUCTS_LIST.size() == 0 || MyUtils.isNetworkAvailable()) {
                GetAllProducts getAllProducts = new GetAllProducts(allProductsRvAdapter, true);
                getAllProducts.execute();
            }
        } else {
            if (ALL_PRODUCTS_LIST.size() == 0) {
                GetAllProducts getAllProducts = new GetAllProducts(allProductsRvAdapter, false);
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
