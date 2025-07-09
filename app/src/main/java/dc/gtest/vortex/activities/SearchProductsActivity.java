package dc.gtest.vortex.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.MySpinnerAdapter;
import dc.gtest.vortex.adapters.SearchProductsRvAdapter;
import dc.gtest.vortex.data.ProductsData;
import dc.gtest.vortex.models.ProductModel;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;

import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCT_TYPES_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PROJECT;
import static dc.gtest.vortex.support.MyGlobals.globalGetHistoryParameter;
import static dc.gtest.vortex.support.MyLocalization.localized_all_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_no_product;
import static dc.gtest.vortex.support.MyLocalization.localized_products;
import static dc.gtest.vortex.support.MyLocalization.localized_project_history;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyLocalization.localized_zone;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchProductsActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private SearchProductsRvAdapter productsRvAdapter;
    private Button btnBottom;

    public static String searchText = "";
    private TextView tvZoneSpinnerTitle;
    private Spinner spZones;
    private boolean doSpinnerItemSelected = true;
    private boolean doSearchTexChanged = true;
    public static String selectedZone = "";
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_search_products, flBaseContainer, true);

        TextView tvProject = findViewById(R.id.tvProject);
        RecyclerView rvSearchProducts = findViewById(R.id.rvSearchProducts);
        spZones = findViewById(R.id.spZones);
        tvZoneSpinnerTitle = findViewById(R.id.tvZoneSpinnerTitle);

        tvProject.setText(SELECTED_PROJECT.getProjectDescription());

        ProductsData.generate(SELECTED_PROJECT.getProjectProducts());

        if (PRODUCTS_LIST.size() == 0) {
            Toast toast = Toast.makeText(this, localized_no_product, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);
        productsRvAdapter = new SearchProductsRvAdapter(PRODUCTS_LIST, SearchProductsActivity.this, isForNewAssignment);
        rvSearchProducts.setAdapter(productsRvAdapter);

        btnBottom = findViewById(R.id.btnBottom);
        if (btnBottom != null) {
            btnBottom.setVisibility(View.VISIBLE);
            btnBottom.setText(localized_project_history);

            btnBottom.setOnClickListener(v -> {
                globalGetHistoryParameter = SELECTED_PROJECT.getProjectId() + ";0";
                Intent intent = new Intent(SearchProductsActivity.this, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            });
        }

        setAdapterOnSpinner(this, spZones);
        spZones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (doSpinnerItemSelected) {
                    if (position == 0) {
                        selectedZone = "";
                    } else {
                        selectedZone = "SPINNER:" + spZones.getSelectedItem().toString().toLowerCase();
                    }

                    productsRvAdapter.getFilter().filter(selectedZone);
                } else {
                    doSpinnerItemSelected = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        clearFilters();

    }

    @Override
    protected void onResume() {
        super.onResume();
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
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                productsRvAdapter.getFilter().filter(newText);
//                return true;
//            }
        });

        return true;
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        setAdapterOnSpinner(this, spZones);
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_products);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String filterByZone = localized_zone + ":";
        tvZoneSpinnerTitle.setText(filterByZone);

    }


    public static void setAdapterOnSpinner(Context ctx, Spinner spZones) {
        Set<String> distinctZones = new HashSet<>();

        for (ProductModel product : PRODUCTS_LIST) {
            String zoneDescription = product.getProjectZoneDescription();
            if (zoneDescription != null && !zoneDescription.trim().isEmpty()) {
                distinctZones.add(zoneDescription);
            }
        }

        // Convert to sorted list
        List<String> sortedZones = new ArrayList<>(distinctZones);
        Collections.sort(sortedZones);

        String[] zonesArray = new String[sortedZones.size() + 1];
        zonesArray[0] = "-";

        for (int i = 0; i < sortedZones.size(); i++) {
            zonesArray[i + 1] = sortedZones.get(i);
        }

        spZones.setAdapter(new MySpinnerAdapter(ctx, zonesArray));
    }

    private void clearFilters() {


        searchText = "";
        selectedZone = "";

        doSearchTexChanged = false;
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
        doSearchTexChanged = true;

        if (spZones.getSelectedItemPosition() != 0) {
            doSpinnerItemSelected = false;
        }
        spZones.setSelection(0);
    }


}
