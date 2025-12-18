package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AllConsumablesRvAdapter;
import dc.gtest.vortex.api.GetAllConsumables;
import dc.gtest.vortex.api.SendConsumables;
import dc.gtest.vortex.data.AllConsumablesData;
import dc.gtest.vortex.models.AddedConsumableModel;
import dc.gtest.vortex.models.AllConsumableModel;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.CONST_EDIT_CONSUMABLES;
import static dc.gtest.vortex.support.MyGlobals.CONST_FINISH_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.CONST_SELECT_FROM_PICKING;
import static dc.gtest.vortex.support.MyGlobals.CONST_SITE_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.support.MyGlobals.CONST_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST;
import static dc.gtest.vortex.support.MyGlobals.PICKING_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_FROM_PICKING_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_add_items_question;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_edit_selection;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_quantity;
import static dc.gtest.vortex.support.MyLocalization.localized_select_consumable;
import static dc.gtest.vortex.support.MyLocalization.localized_select_set_save_consumable;
import static dc.gtest.vortex.support.MyLocalization.localized_send_consumable_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_sure_to_leave;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_CONSUMABLES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CONSUMABLES_FROM_PICKING_SENT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_WAREHOUSE_CONSUMABLES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_MANDATORY_CONSUMABLES_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_QTY_LIMIT_CONSUMABLE_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class AllConsumablesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private AllConsumablesRvAdapter allConsumablesRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private Button btnSendConsumables;
    private Button btnEditConsumables;

    private String assignmentId = "";
    private String _projectWarehouseId = "0";
    private boolean code_scanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_all_consumables, flBaseContainer, true);

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        RecyclerView rvAllConsumables = findViewById(R.id.rvAllConsumables);
        btnSendConsumables = findViewById(R.id.btnSendConsumables);
        btnEditConsumables = findViewById(R.id.btnEditConsumables);
        boolean WarehouseProducts = getIntent().getBooleanExtra(CONST_WAREHOUSE_PRODUCTS, false);
        String selectedWarehouseId = MyPrefs.getString(MyPrefs.PREF_WAREHOUSEID, "0");
        boolean siteWarehouseProducts = getIntent().getBooleanExtra(CONST_SITE_WAREHOUSE_PRODUCTS, false);
        if(siteWarehouseProducts){
            _projectWarehouseId = SELECTED_ASSIGNMENT.getProjectWarehouseId();
            selectedWarehouseId = _projectWarehouseId;
            WarehouseProducts = true;
        }
        boolean selectFromPicking = getIntent().getBooleanExtra(CONST_SELECT_FROM_PICKING, false);
        if(selectFromPicking){btnEditConsumables.setVisibility(View.GONE);}

        AllConsumablesData.generate(assignmentId, WarehouseProducts, selectFromPicking, selectedWarehouseId);
        if(WarehouseProducts) {
            allConsumablesRvAdapter = new AllConsumablesRvAdapter(ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED, this, WarehouseProducts,
                    false, _projectWarehouseId);
        } else if (selectFromPicking) {
            List<AllConsumableModel> pickList = new ArrayList<AllConsumableModel>();
            pickList.addAll(PICKING_PRODUCTS_LIST_FILTERED);

            if(MyPrefs.getBoolean(PREF_QTY_LIMIT_CONSUMABLE_FROM_PICKING, false)){
                for (AllConsumableModel itm : pickList){
                    int detPickingId = itm.getDetPickingId();
                    double addedStock = 0.0;
                    for (AddedConsumableModel am : ADDED_CONSUMABLES_LIST) {
                        if(detPickingId == am.getDetPickingId()){
                            addedStock += Double.parseDouble(am.getUsed().replace(",", "."));
                        }
                    }
                    String stock = itm.getStock().replace(",", ".");
                    double stock_d = Double.parseDouble(stock);
                    stock_d -= addedStock;
                    itm.setStock(String.valueOf(stock_d));
                }
            }

            allConsumablesRvAdapter = new AllConsumablesRvAdapter(pickList, this, WarehouseProducts, true, _projectWarehouseId);
        }else{
            allConsumablesRvAdapter = new AllConsumablesRvAdapter(ALL_CONSUMABLES_LIST_FILTERED, this, WarehouseProducts, false, _projectWarehouseId);
        }
        rvAllConsumables.setAdapter(allConsumablesRvAdapter);


        String RelatedConsumables = "";
        if(WarehouseProducts){
            RelatedConsumables = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_WAREHOUSE_CONSUMABLES_FOR_SHOW, MyPrefs.getString(MyPrefs.PREF_WAREHOUSEID, "0"), "");
        }else{
            RelatedConsumables = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW, assignmentId, "");
        }

        if (RelatedConsumables.isEmpty() || MyUtils.isNetworkAvailable()) {
            GetAllConsumables getAllConsumables = new GetAllConsumables(allConsumablesRvAdapter, assignmentId, WarehouseProducts,
                                            selectFromPicking, siteWarehouseProducts ? _projectWarehouseId : "0", false);
            getAllConsumables.execute();
        }


        btnSendConsumables.setOnClickListener(v -> {


            if(selectFromPicking){
                if(SELECTED_FROM_PICKING_LIST.size() > 0){
                    String addedItems = "";
                    for (AddedConsumableModel a : SELECTED_FROM_PICKING_LIST){
                        if(addedItems.length() > 0){
                            addedItems += "\r\n" + a.getName() + " - " + localized_quantity + ": " + a.getUsed();
                        } else {
                            addedItems = "\r\n" + a.getName() + " - " + localized_quantity + ": " + a.getUsed();
                        }
                    }
                    new AlertDialog.Builder(this)
                            .setMessage(localized_add_items_question + addedItems)
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                dialog.dismiss();
                                CONSUMABLES_TOADD_LIST.addAll(SELECTED_FROM_PICKING_LIST);
                                ADDED_CONSUMABLES_LIST.addAll(SELECTED_FROM_PICKING_LIST);
                                SELECTED_FROM_PICKING_LIST.clear();
                                if (CONSUMABLES_TOADD_LIST.size() > 0 && MyPrefs.getBoolean(PREF_MANDATORY_CONSUMABLES_FROM_PICKING, false)) {
                                    MyPrefs.setBooleanWithFileName(PREF_FILE_CONSUMABLES_FROM_PICKING_SENT, SELECTED_ASSIGNMENT.getAssignmentId(), true);
                                }
                                SendSelected();
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    MyDialogs.showOK(AllConsumablesActivity.this, localized_select_set_save_consumable);
                }
            } else {
                SendSelected();
            }
        });

        btnEditConsumables.setOnClickListener(v -> {

            if (CONSUMABLES_TOADD_LIST.size() > 0) {
                Intent intent = new Intent(AllConsumablesActivity.this, AddedConsumablesActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(CONST_EDIT_CONSUMABLES, true);
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
    }


    public void SendSelected(){
        if (CONSUMABLES_TOADD_LIST.size() > 0) {

            MyPrefs.setStringWithFileName(PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC, assignmentId, CONSUMABLES_TOADD_LIST.toString());
            CONSUMABLES_TOADD_LIST.clear();
            MyPrefs.setStringWithFileName(PREF_FILE_ADDED_CONSUMABLES_FOR_SHOW, assignmentId, ADDED_CONSUMABLES_LIST.toString());

            if (MyUtils.isNetworkAvailable()) {
                SendConsumables sendConsumables = new SendConsumables(AllConsumablesActivity.this, assignmentId, CONST_FINISH_ACTIVITY);
                sendConsumables.execute();
            } else {
                MyDialogs.showOK(AllConsumablesActivity.this, localized_no_internet_data_saved);
            }
        } else {
            MyDialogs.showOK(AllConsumablesActivity.this, localized_select_set_save_consumable);
        }
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

                if(code_scanned){
                    newText = "Barcode Scan:" + newText;
                    code_scanned = false;
                }

                allConsumablesRvAdapter.getFilter().filter(newText);

                if(newText.isEmpty()){
                    searchView.setIconified(true);
                }
                return true;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemScanner){
            IntentIntegrator integrator = new IntentIntegrator(AllConsumablesActivity.this);
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
                            code_scanned = true;
                            searchView.onActionViewExpanded();
                            searchView.setIconified(false);
                            searchView.setQuery( ScannedCode, false);
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
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_select_consumable);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String assignmentIdText = localized_assignment_id + ": " + MyPrefs.getString(PREF_ASSIGNMENT_ID, "");
        tvAssignmentId.setText(assignmentIdText);

        btnSendConsumables.setText(localized_send_consumable_caps);
        btnEditConsumables.setText(localized_edit_selection);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(localized_sure_to_leave)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        CONSUMABLES_TOADD_LIST.clear();
                        SELECTED_FROM_PICKING_LIST.clear();
                        finish();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
    }
}