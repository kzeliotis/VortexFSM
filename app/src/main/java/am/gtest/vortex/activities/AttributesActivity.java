package am.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.AttributesRvAdapter;
import am.gtest.vortex.api.GetProducts;
import am.gtest.vortex.application.MyApplication;
import am.gtest.vortex.support.MyCanEdit;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MySynchronize;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.ATTRIBUTES_LIST;
import static am.gtest.vortex.support.MyGlobals.CONST_PARENT_ATTRIBUTES_ACTIVITY;
import static am.gtest.vortex.support.MyGlobals.KEY_PARENT_ACTIVITY;
import static am.gtest.vortex.support.MyGlobals.KEY_PRODUCT_DESCRIPTION;
import static am.gtest.vortex.support.MyGlobals.KEY_WAREHOUSE_ID;
import static am.gtest.vortex.support.MyGlobals.PRODUCTS_LIST;
import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static am.gtest.vortex.support.MyGlobals.globalSelectedProductId;
import static am.gtest.vortex.support.MyLocalization.localized_add_new_attributes_caps;
import static am.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static am.gtest.vortex.support.MyLocalization.localized_attributes;
import static am.gtest.vortex.support.MyLocalization.localized_installation_date;
import static am.gtest.vortex.support.MyLocalization.localized_no_attribute;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AttributesActivity extends BaseDrawerActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private AttributesRvAdapter attributesRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private TextView tvProductDescription;
    private TextView tvAttrProdInstallationDateTitle;
    private TextView tvAttrProdInstallationDate;
    private Button btnAddNewAttributes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_attributes, flBaseContainer, true);

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvAttrProdInstallationDateTitle = findViewById(R.id.tvAttrProdInstallationDateTitle);
        tvAttrProdInstallationDate = findViewById(R.id.tvAttrProdInstallationDate);
        RecyclerView rvAttributes = findViewById(R.id.rvAttributes);
        btnAddNewAttributes = findViewById(R.id.btnAddNewAttributes);

        for (int i = 0; i < PRODUCTS_LIST.size(); i++) {
            if (PRODUCTS_LIST.get(i).getProjectProductId().equalsIgnoreCase(globalSelectedProductId)) {
                ATTRIBUTES_LIST = PRODUCTS_LIST.get(i).getProductAttributes();
                break;
            }
        }

        if (ATTRIBUTES_LIST.size() == 0) {
            Toast toast = Toast.makeText(MyApplication.getContext(), localized_no_attribute, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        Log.e(LOG_TAG, "======================== ATTRIBUTES_LIST:\n" + ATTRIBUTES_LIST.toString());

        attributesRvAdapter = new AttributesRvAdapter(ATTRIBUTES_LIST, AttributesActivity.this);
        rvAttributes.setAdapter(attributesRvAdapter);

        btnAddNewAttributes.setOnClickListener(v -> {
            if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {
                Intent intent = new Intent(AttributesActivity.this, AllAttributesActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, CONST_PARENT_ATTRIBUTES_ACTIVITY);
                intent.putExtra(KEY_PRODUCT_DESCRIPTION, SELECTED_PRODUCT.getProductDescription());
                intent.putExtra(KEY_WAREHOUSE_ID, "0");
                startActivity(intent);
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

        if (MyUtils.isNetworkAvailable()) {
            GetProducts getProducts = new GetProducts(this, SELECTED_ASSIGNMENT.getAssignmentId(), true, "0");
            getProducts.execute();
        }
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_attributes);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String assignmentIdText = localized_assignment_id + ": " + SELECTED_ASSIGNMENT.getAssignmentId();
        tvAssignmentId.setText(assignmentIdText);

        tvProductDescription.setText(SELECTED_PRODUCT.getProductDescription());
        tvAttrProdInstallationDateTitle.setText(localized_installation_date);
        tvAttrProdInstallationDate.setText(SELECTED_PRODUCT.getInstallationDate());
        btnAddNewAttributes.setText(localized_add_new_attributes_caps);
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
                attributesRvAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO improve synchronize
        if (id == R.id.itemSynchronize) {
            new MySynchronize(this).mySynchronize();
        }

        return true;
    }
}
