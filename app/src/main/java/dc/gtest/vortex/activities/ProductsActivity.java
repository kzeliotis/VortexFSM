package dc.gtest.vortex.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.MySpinnerAdapter;
import dc.gtest.vortex.adapters.ProductsRvAdapter;
import dc.gtest.vortex.api.GetProducts;
import dc.gtest.vortex.api.GetProductTypes;
import dc.gtest.vortex.api.GetZones;
import dc.gtest.vortex.api.SendNewAssignment;
import dc.gtest.vortex.api.SetProductsToInstallation;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.data.InstallationZonesData;
import dc.gtest.vortex.data.ProductTypesData;
import dc.gtest.vortex.data.ProductsData;
import dc.gtest.vortex.data.ZonesData;
import dc.gtest.vortex.models.NewAssignmentModel;
import dc.gtest.vortex.models.ProductModel;
import dc.gtest.vortex.models.ProjectZoneModel;
import dc.gtest.vortex.models.ZoneModel;
import dc.gtest.vortex.support.MyCanEdit;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.CONST_SITE_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.support.MyGlobals.CONST_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST;
import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_ZONES_LIST;
import static dc.gtest.vortex.support.MyGlobals.KEY_GROUPED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.KEY_ID_SCANNED_SERIAL;
import static dc.gtest.vortex.support.MyGlobals.KEY_ID_SEARCH;
import static dc.gtest.vortex.support.MyGlobals.KEY_PROJECT_INSTALLATION_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_SELECT_PRODUCTS_TO_ADD;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.NEW_ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCT_TYPES_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static dc.gtest.vortex.support.MyGlobals.ZONES_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_add_new_product;
import static dc.gtest.vortex.support.MyLocalization.localized_add_select_products;
import static dc.gtest.vortex.support.MyLocalization.localized_all_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_from_product_list;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_from_site_warehouse;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_from_technicians_warehouse;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_from_warehouse;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_product_from;
import static dc.gtest.vortex.support.MyLocalization.localized_create_workorder;
import static dc.gtest.vortex.support.MyLocalization.localized_create_workorder_on_asset;
import static dc.gtest.vortex.support.MyLocalization.localized_filter_by_type;
import static dc.gtest.vortex.support.MyLocalization.localized_new_assignment;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_no_product;
import static dc.gtest.vortex.support.MyLocalization.localized_products;
import static dc.gtest.vortex.support.MyLocalization.localized_select;
import static dc.gtest.vortex.support.MyLocalization.localized_select_existing;
import static dc.gtest.vortex.support.MyLocalization.localized_sure_to_leave;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_PRODUCT_TYPES;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ID_SEARCH_PRODUCTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_PRODUCTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NO_INSTALLATION_PRODUCTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class ProductsActivity extends BaseDrawerActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private TextView tvAssignmentId;
    private TextView tvTypeSpinnerTitle;
    private Spinner spProductTypes;
    private Spinner spZones;
    private Button btnAddNewProduct;
    private Button btnAddExistingProduct;
    private CardView crdZones;

    private ProductsRvAdapter productsRvAdapter;

    public static String searchText = "";
    public static String selectedType = "";
    public static String projectInstallationId = "0";
    public static String selectZoneId = "";
    public static String scannedSerial = "";
    public static boolean IsInstallation = false;
    public static boolean selectProducts = false;
    public static boolean searchSerial = false;

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

        selectProducts = getIntent().getBooleanExtra(KEY_SELECT_PRODUCTS_TO_ADD, false);
        searchSerial = getIntent().getBooleanExtra(KEY_ID_SEARCH, false);
        scannedSerial = getIntent().getStringExtra(KEY_ID_SCANNED_SERIAL);

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
        spZones = findViewById(R.id.spZones);
        btnAddNewProduct = findViewById(R.id.btnAddNewProduct);
        btnAddExistingProduct = findViewById(R.id.btnAddExistingProduct);
        crdZones = findViewById(R.id.crdvZones);
        if(!selectProducts){
            crdZones.setVisibility(View.GONE);
        }

        if(searchSerial){
            LinearLayout llsp = findViewById(R.id.llTypeSpinner);
            llsp.setVisibility(View.GONE);
        }

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

                    productsRvAdapter.getFilter().filter(selectedType);
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

        if(selectProducts){
            setupZonesSpinner(this);
            spZones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(position == 0) {
                        selectZoneId = "0";
                    } else {
                        selectZoneId = "0";
                        String selection = spZones.getSelectedItem().toString();
                        if(selection.contains("|")){
                            int index = selection.indexOf("|");
                            selectZoneId = selection.substring(0, index).replace("ID: ", "").trim();
                        }

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }


        clearFilters();

        if(IsInstallation && !selectProducts){
            btnAddExistingProduct.setVisibility(View.VISIBLE);

            btnAddExistingProduct.setOnClickListener(v -> {
                Intent intent = new Intent(ProductsActivity.this, ProductsActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_PROJECT_INSTALLATION_ID, SELECTED_INSTALLATION.getProjectInstallationId());
                intent.putExtra(KEY_SELECT_PRODUCTS_TO_ADD, true);
                startActivity(intent);
            });

        } else {
            btnAddExistingProduct.setVisibility(View.GONE);
        }



        btnAddNewProduct.setOnClickListener(v -> {
            if(!searchSerial){
                if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {
                    if(selectProducts){
                        String selectedIds = MyPrefs.getStringWithFileName(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW, projectInstallationId, "");
                        if (selectedIds.length() > 0){
                            new AlertDialog.Builder(this)
                                    .setMessage(localized_add_select_products)
                                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                                        dialog.dismiss();
                                        MyPrefs.setStringWithFileName(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SYNC, projectInstallationId, selectedIds + "|" + selectZoneId);
                                        MyPrefs.removeStringWithFileName(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW, projectInstallationId);
                                        SetProductsToInstallation setProductsToInstallation = new SetProductsToInstallation(this, projectInstallationId);
                                        setProductsToInstallation.execute();
                                        selectProducts = false;
                                        finish();
                                    })
                                    .setNegativeButton(R.string.no, null)
                                    .show();
                        }

                    }else{

                        NEW_ATTRIBUTES_LIST.clear();

                        List<String> optionsList = new ArrayList<>();

                        optionsList.add(localized_choose_from_product_list);
                        optionsList.add(localized_choose_from_technicians_warehouse);
                        if(!SELECTED_ASSIGNMENT.getProjectWarehouseId().equals("0")) {optionsList.add(localized_choose_from_site_warehouse);}

                        String[] options = optionsList.toArray(new String[0]);

                        new AlertDialog.Builder(this)
                                .setTitle(localized_choose_product_from) // optional title
                                .setItems(options, (dialog, which) -> {
                                    Intent intent = new Intent(ProductsActivity.this, AllProductsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra(KEY_PROJECT_INSTALLATION_ID, projectInstallationId);

                                    String selectedOption = options[which];

                                    if (selectedOption.equals(localized_choose_from_product_list)) {
                                        intent.putExtra(CONST_WAREHOUSE_PRODUCTS, false);
                                    } else if (selectedOption.equals(localized_choose_from_technicians_warehouse)) {
                                        intent.putExtra(CONST_WAREHOUSE_PRODUCTS, true);
                                    } else if (selectedOption.equals(localized_choose_from_site_warehouse)) {
                                        intent.putExtra(CONST_SITE_WAREHOUSE_PRODUCTS, true);
                                    }

                                    startActivity(intent);
                                })
                                .show();

//                        new AlertDialog.Builder(this)
//                                .setMessage(localized_choose_from_warehouse)
//                                .setPositiveButton(R.string.yes, (dialog, which) -> {
//                                    dialog.dismiss();
//                                    Intent intent = new Intent(ProductsActivity.this, AllProductsActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    intent.putExtra(CONST_WAREHOUSE_PRODUCTS, true);
//                                    intent.putExtra(KEY_PROJECT_INSTALLATION_ID, projectInstallationId);
//                                    startActivity(intent);
//                                })
//                                .setNegativeButton(R.string.no, (dialog, which) -> {
//                                    dialog.dismiss();
//                                    Intent intent = new Intent(ProductsActivity.this, AllProductsActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    intent.putExtra(CONST_WAREHOUSE_PRODUCTS, false);
//                                    intent.putExtra(KEY_PROJECT_INSTALLATION_ID, projectInstallationId);
//                                    startActivity(intent);
//                                })
//                                .show();
                    }
                }
            } else {
                //Create workorder with default values
                if(PRODUCTS_LIST.size() == 1){
                    new AlertDialog.Builder(this)
                            .setMessage(localized_create_workorder_on_asset)
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                dialog.dismiss();

                                ProductModel pm = PRODUCTS_LIST.get(0);

                                NEW_ASSIGNMENT = new NewAssignmentModel();
                                NEW_ASSIGNMENT.setAssignmentSourceProcedure("SCANNED ASSET WORK ORDER");
                                NEW_ASSIGNMENT.setProjectProductId(pm.getProjectProductId());

                                String prefKey = UUID.randomUUID().toString();
                                MyPrefs.setStringWithFileName(PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC, prefKey, NEW_ASSIGNMENT.toString());

                                if (MyUtils.isNetworkAvailable()) {
                                    SendNewAssignment sendNewAssignment = new SendNewAssignment(ProductsActivity.this, prefKey, true);
                                    sendNewAssignment.execute(prefKey);
                                } else {
                                    Toast.makeText(ProductsActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();

                }

            }



        });

        PRODUCTS_LIST.clear();

        productsRvAdapter = new ProductsRvAdapter(PRODUCTS_LIST, ProductsActivity.this, selectProducts ? Integer.parseInt(projectInstallationId) : 0, searchSerial);
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
        String projectInstallationId = "0";
        if(IsInstallation && !selectProducts) {
            projectInstallationId = SELECTED_INSTALLATION.getProjectInstallationId();
            productsData = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_PRODUCTS_DATA, projectInstallationId, "");
        } else if (selectProducts) {
            productsData = MyPrefs.getStringWithFileName(PREF_FILE_NO_INSTALLATION_PRODUCTS_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), "");
        } else if (searchSerial){
            productsData = MyPrefs.getStringWithFileName(PREF_FILE_ID_SEARCH_PRODUCTS_DATA, "0", "");
        } else {
            productsData = MyPrefs.getStringWithFileName(PREF_FILE_PRODUCTS_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), "");
        }


        Log.e(LOG_TAG, "==================== productsData: " + productsData);

        ProductsData.generate(productsData);
        if (PRODUCTS_LIST.size() == 0 && !searchSerial) {
            Toast.makeText(MyApplication.getContext(), localized_no_product, Toast.LENGTH_LONG).show();
        }
        productsRvAdapter.notifyDataSetChanged();

        if (MyUtils.isNetworkAvailable()) {
            String _assignmentId = searchSerial ? "0" : SELECTED_ASSIGNMENT.getAssignmentId();
            String idValue = searchSerial ? scannedSerial : "";
            GetProducts getProducts = new GetProducts(this, SELECTED_ASSIGNMENT.getAssignmentId(), true, projectInstallationId, selectProducts, idValue);
            getProducts.execute();

        }

        updateUiTexts();
    }

    @Override
    public void onBackPressed() {

        if(selectProducts){
            MyPrefs.removeStringWithFileName(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW, projectInstallationId);
            selectProducts = false;
        }

        if(searchSerial){
            MyPrefs.removeStringWithFileName(PREF_FILE_ID_SEARCH_PRODUCTS_DATA, "0");
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_scanner, menu);

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
                    if(newText.isEmpty()){
                        searchView.setIconified(true);
                    }
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

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemScanner){
            IntentIntegrator integrator = new IntentIntegrator(ProductsActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.setOrientationLocked(true);
            integrator.initiateScan();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 49374) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    String ScannedCode = result.getContents();
                    //ScannedCode = ScannedCode + "|";
                    //searchView = (SearchView) mSearchMenu.getActionView();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchView.onActionViewExpanded();
                            searchView.setIconified(false);
                            searchView.setQuery(ScannedCode, false);
                            searchView.clearFocus();
                        }
                    }, 200);
                }
            }
        }
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


        if(IsInstallation) {
            tvAssignmentId.setText(SELECTED_INSTALLATION.getProjectInstallationFullDescription());
        } else if (searchSerial){
            String foundSerialInfo = "";
            if(!PRODUCTS_LIST.isEmpty()){
                ProductModel pm = PRODUCTS_LIST.get(0);
                foundSerialInfo = pm.getNotes();
            }
            tvAssignmentId.setText(foundSerialInfo);
        } else {
            String assignmentIdText = localized_assignment_id + ": " + SELECTED_ASSIGNMENT.getAssignmentId();
            tvAssignmentId.setText(assignmentIdText);
        }


        String filterByType = localized_filter_by_type + ":";
        tvTypeSpinnerTitle.setText(filterByType);

        if(selectProducts) {
            btnAddNewProduct.setText(localized_select);
        } else if (searchSerial) {
            btnAddNewProduct.setText(localized_create_workorder);
        }else{
            btnAddNewProduct.setText(localized_add_new_product);
        }
        btnAddExistingProduct.setText(localized_select_existing);
    }

    private void setupProductTypesSpinner() {
        String productTypes = MyPrefs.getString(PREF_DATA_PRODUCT_TYPES, "");

        if (productTypes.isEmpty() || productTypes.equals("[]")) {
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

    private void setupZonesSpinner(Context ctx) {

        String pjZones = MyPrefs.getStringWithFileName(PREF_FILE_ZONES_DATA_FOR_SHOW,  SELECTED_ASSIGNMENT.getAssignmentId(), "");
        if(pjZones.isEmpty()){
            try{
                String result = "";
                GetZones getZones = new GetZones(this, null, false, "0");
                result = getZones.execute(SELECTED_ASSIGNMENT.getProjectId()).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ZonesData.generate(false);

        String InstZones = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW,  SELECTED_INSTALLATION.getProjectInstallationId(), "");
        if(InstZones.isEmpty()){
            try{
                String result = "";
                GetZones getZones = new GetZones(this, null, false, SELECTED_INSTALLATION.getProjectInstallationId());
                result = getZones.execute(SELECTED_ASSIGNMENT.getProjectId()).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        InstallationZonesData.generate(false);

        String[] ZonesArray = new String[INSTALLATION_ZONES_LIST.size() + ZONES_LIST.size() + 1];

        ZonesArray[0] = "-";
        int i = 1;
        for (ZoneModel zm : ZONES_LIST){
            if (zm.getProjectInstallationId().isEmpty() || zm.getProjectInstallationId().equals("0")){
                String zoneid = zm.getZoneId();
                String zoneDescription = zm.getZoneName();
                ZonesArray[i] = "ID: " + zoneid + "| " +zoneDescription;
                i+=1;
            }
        }

        for (ProjectZoneModel zm : INSTALLATION_ZONES_LIST){
            String zoneid = zm.getZoneId();
            String zoneDescription = zm.getZoneName();
            ZonesArray[i] = "ID: " + zoneid + "| " + zoneDescription;
            i+=1;
        }

        spZones.setAdapter(new MySpinnerAdapter(ctx, ZonesArray));
        spZones.setSelection(0);

    }



}
