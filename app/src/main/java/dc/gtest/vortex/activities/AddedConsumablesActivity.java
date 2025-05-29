package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AddedConsumablesRvAdapter;
import dc.gtest.vortex.data.AddedConsumablesData;
import dc.gtest.vortex.data.ConsumablesToAddData;
import dc.gtest.vortex.support.MyCanEdit;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;

import static dc.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.CONST_EDIT_CONSUMABLES;
import static dc.gtest.vortex.support.MyGlobals.CONST_SELECT_FROM_PICKING;
import static dc.gtest.vortex.support.MyGlobals.CONST_SITE_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.support.MyGlobals.CONST_WAREHOUSE_PRODUCTS;
import static dc.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST;
import static dc.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_add_new_consumable_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_from_product_list;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_from_site_warehouse;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_from_technicians_warehouse;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_from_warehouse;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_product_from;
import static dc.gtest.vortex.support.MyLocalization.localized_consumables;
import static dc.gtest.vortex.support.MyLocalization.localized_consumables_to_send;
import static dc.gtest.vortex.support.MyLocalization.localized_select_from_picking;
import static dc.gtest.vortex.support.MyLocalization.localized_suggested_used;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_ADD_CONSUMABLE_FROM_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_ADD_CONSUMABLE_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_ADD_CONSUMABLE_FROM_WAREHOUSE;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

import java.util.ArrayList;
import java.util.List;

public class AddedConsumablesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private AddedConsumablesRvAdapter addedConsumablesRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private TextView tvTableHead;
    private Button btnAddNewConsumable;
    private Button btnAddFromPicking;
    private boolean edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_added_consumables, flBaseContainer, true);

        edit = getIntent().getBooleanExtra(CONST_EDIT_CONSUMABLES, false);
        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        tvTableHead = findViewById(R.id.tvTableHead);
        RecyclerView rvAddedConsumables = findViewById(R.id.rvAddedConsumables);
        btnAddNewConsumable = findViewById(R.id.btnAddNewConsumable);
        btnAddFromPicking = findViewById(R.id.btnAddFromPicking);
        boolean consumablesFromList = MyPrefs.getBoolean(PREF_ADD_CONSUMABLE_FROM_LIST, true);
        boolean consumablesFromWareHouse = MyPrefs.getBoolean(PREF_ADD_CONSUMABLE_FROM_WAREHOUSE, true);
        boolean consumablesFromPicking = MyPrefs.getBoolean(PREF_ADD_CONSUMABLE_FROM_PICKING, true);

        if(edit) {
            CONSUMABLES_TOADD_LIST.clear();
            CONSUMABLES_TOADD_LIST_FILTERED.clear();
            addedConsumablesRvAdapter = new AddedConsumablesRvAdapter(this,CONSUMABLES_TOADD_LIST, CONSUMABLES_TOADD_LIST_FILTERED, edit);
            btnAddNewConsumable.setVisibility(View.GONE);
            btnAddFromPicking.setVisibility(View.GONE);
        } else {
            ADDED_CONSUMABLES_LIST.clear();
            ADDED_CONSUMABLES_LIST_FILTERED.clear();
            addedConsumablesRvAdapter = new AddedConsumablesRvAdapter(this,ADDED_CONSUMABLES_LIST, ADDED_CONSUMABLES_LIST_FILTERED, edit);
            if(SELECTED_ASSIGNMENT.getPickingList().length() > 0 && consumablesFromPicking){
                btnAddFromPicking.setVisibility(View.VISIBLE);
            } else {
                btnAddFromPicking.setVisibility(View.GONE);
            }

            if (!consumablesFromList && !consumablesFromWareHouse){
                btnAddNewConsumable.setVisibility(View.GONE);
            }
        }
        rvAddedConsumables.setAdapter(addedConsumablesRvAdapter);


        btnAddNewConsumable.setOnClickListener(v -> {

            if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {
                if (consumablesFromList && !consumablesFromWareHouse){
                    Intent intent = new Intent(AddedConsumablesActivity.this, AllConsumablesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(CONST_WAREHOUSE_PRODUCTS, false);
                    startActivity(intent);
                }else{
                    List<String> optionsList = new ArrayList<>();

                    if(consumablesFromList) {optionsList.add(localized_choose_from_product_list);}
                    if(consumablesFromWareHouse) {optionsList.add(localized_choose_from_technicians_warehouse);}
                    if(consumablesFromWareHouse && !SELECTED_ASSIGNMENT.getProjectWarehouseId().equals("0"))
                                                {optionsList.add(localized_choose_from_site_warehouse);}

                    String[] options = optionsList.toArray(new String[0]);

                    new AlertDialog.Builder(this)
                            .setTitle(localized_choose_product_from) // optional title
                            .setItems(options, (dialog, which) -> {
                                Intent intent = new Intent(AddedConsumablesActivity.this, AllConsumablesActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

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
                }
            }



//            if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {
//                if (consumablesFromList && consumablesFromWareHouse){
//
//                    new AlertDialog.Builder(this)
//                            .setMessage(localized_choose_from_warehouse)
//                            .setPositiveButton(R.string.yes, (dialog, which) -> {
//                                dialog.dismiss();
//                                Intent intent = new Intent(AddedConsumablesActivity.this, AllConsumablesActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.putExtra(CONST_WAREHOUSE_PRODUCTS, true);
//                                startActivity(intent);
//                            })
//                            .setNegativeButton(R.string.no, (dialog, which) -> {
//                                dialog.dismiss();
//                                Intent intent = new Intent(AddedConsumablesActivity.this, AllConsumablesActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.putExtra(CONST_WAREHOUSE_PRODUCTS, false);
//                                startActivity(intent);
//                            })
//                            .show();
//                }
//            } else if (consumablesFromList && !consumablesFromWareHouse){
//                Intent intent = new Intent(AddedConsumablesActivity.this, AllConsumablesActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra(CONST_WAREHOUSE_PRODUCTS, false);
//                startActivity(intent);
//            } else if (!consumablesFromList && consumablesFromWareHouse){
//                Intent intent = new Intent(AddedConsumablesActivity.this, AllConsumablesActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra(CONST_WAREHOUSE_PRODUCTS, true);
//                startActivity(intent);
//            }


        });

        btnAddFromPicking.setOnClickListener(v -> {
            Intent intent = new Intent(AddedConsumablesActivity.this, AllConsumablesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(CONST_WAREHOUSE_PRODUCTS, false);
            intent.putExtra(CONST_SELECT_FROM_PICKING, true);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(edit){
            ConsumablesToAddData.generate(SELECTED_ASSIGNMENT.getAssignmentId());
        }else{
            AddedConsumablesData.generate(SELECTED_ASSIGNMENT.getAssignmentId());
        }
        addedConsumablesRvAdapter.notifyDataSetChanged();


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
                addedConsumablesRvAdapter.getFilter().filter(newText);
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
            getSupportActionBar().setTitle(localized_consumables);
            if(edit){
                getSupportActionBar().setTitle(localized_consumables_to_send);
            }
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String assignmentIdText = localized_assignment_id + ": " + MyPrefs.getString(PREF_ASSIGNMENT_ID, "");
        tvAssignmentId.setText(assignmentIdText);

        tvTableHead.setText(localized_suggested_used);
        btnAddNewConsumable.setText(localized_add_new_consumable_caps);
        btnAddFromPicking.setText(localized_select_from_picking);
    }
}
