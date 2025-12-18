package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AttributesRvAdapter;
import dc.gtest.vortex.api.GetProducts;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.support.MyCanEdit;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MySynchronize;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.CONST_PARENT_ATTRIBUTES_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.KEY_ID_SEARCH;
import static dc.gtest.vortex.support.MyGlobals.KEY_PARENT_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.KEY_PRODUCT_DESCRIPTION;
import static dc.gtest.vortex.support.MyGlobals.KEY_WAREHOUSE_ID;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static dc.gtest.vortex.support.MyGlobals.attributeValueforScan;
import static dc.gtest.vortex.support.MyGlobals.globalSelectedProductId;
import static dc.gtest.vortex.support.MyLocalization.localized_add_new_attributes_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_attributes;
import static dc.gtest.vortex.support.MyLocalization.localized_installation_date;
import static dc.gtest.vortex.support.MyLocalization.localized_no_attribute;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AttributesActivity extends BaseDrawerActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private AttributesRvAdapter attributesRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private TextView tvProductDescription;
    private TextView tvAttrProdInstallationDateTitle;
    private TextView tvAttrProdInstallationDate;
    private Button btnAddNewAttributes;
    private boolean searchSerial;

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
        searchSerial = getIntent().getBooleanExtra(KEY_ID_SEARCH, false);

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

        attributesRvAdapter = new AttributesRvAdapter(ATTRIBUTES_LIST, AttributesActivity.this, searchSerial);
        rvAttributes.setAdapter(attributesRvAdapter);

        btnAddNewAttributes.setOnClickListener(v -> {
            if (searchSerial || MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {
                Intent intent = new Intent(AttributesActivity.this, AllAttributesActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, CONST_PARENT_ATTRIBUTES_ACTIVITY);
                intent.putExtra(KEY_PRODUCT_DESCRIPTION, SELECTED_PRODUCT.getProductDescription());
                intent.putExtra(KEY_WAREHOUSE_ID, "0");
                intent.putExtra(KEY_ID_SEARCH,  searchSerial);
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
            String _assignmentId = searchSerial ? "0" : SELECTED_ASSIGNMENT.getAssignmentId();
            String idValue = searchSerial ? SELECTED_PRODUCT.getIdentityValue() : "";
            GetProducts getProducts = new GetProducts(this, _assignmentId, true, "0",
                    false, idValue, false);
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
        if(searchSerial){assignmentIdText = "";}
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
            new MySynchronize(this).mySynchronize(true);
        }

        return true;
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 49374:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() != null) {
                        String ScannedCode = result.getContents();
                        // assignmentsRvAdapter.getFilter().filter(ScannedCode);
                        //Toast.makeText(this,  ScannedCode, Toast.LENGTH_LONG).show();
                        attributeValueforScan.setText(ScannedCode);
                    }
                }
                break;
        }
    }
}
