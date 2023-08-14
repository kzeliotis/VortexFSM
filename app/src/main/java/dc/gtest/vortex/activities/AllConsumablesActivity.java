package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
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
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AllConsumablesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private AllConsumablesRvAdapter allConsumablesRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private Button btnSendConsumables;
    private Button btnEditConsumables;

    private String assignmentId = "";

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
        boolean selectFromPicking = getIntent().getBooleanExtra(CONST_SELECT_FROM_PICKING, false);
        if(selectFromPicking){btnEditConsumables.setVisibility(View.GONE);}

        AllConsumablesData.generate(assignmentId, WarehouseProducts, selectFromPicking);
        if(WarehouseProducts) {
            allConsumablesRvAdapter = new AllConsumablesRvAdapter(ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED, this, WarehouseProducts, false);
        } else if (selectFromPicking) {
            allConsumablesRvAdapter = new AllConsumablesRvAdapter(PICKING_PRODUCTS_LIST_FILTERED, this, WarehouseProducts, true);
        }else{
            allConsumablesRvAdapter = new AllConsumablesRvAdapter(ALL_CONSUMABLES_LIST_FILTERED, this, WarehouseProducts, false);
        }
        rvAllConsumables.setAdapter(allConsumablesRvAdapter);


        String RelatedConsumables = "";
        if(WarehouseProducts){
            RelatedConsumables = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_WAREHOUSE_CONSUMABLES_FOR_SHOW, MyPrefs.getString(MyPrefs.PREF_WAREHOUSEID, "0"), "");
        }else{
            RelatedConsumables = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW, assignmentId, "");
        }

        if (RelatedConsumables.isEmpty() || MyUtils.isNetworkAvailable()) {
            GetAllConsumables getAllConsumables = new GetAllConsumables(allConsumablesRvAdapter, assignmentId, WarehouseProducts, selectFromPicking);
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
                allConsumablesRvAdapter.getFilter().filter(newText);
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