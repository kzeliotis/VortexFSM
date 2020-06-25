package am.gtest.vortex.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.AllConsumablesRvAdapter;
import am.gtest.vortex.api.GetAllConsumables;
import am.gtest.vortex.api.SendConsumables;
import am.gtest.vortex.data.AllConsumablesData;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST;
import static am.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.CONST_FINISH_ACTIVITY;
import static am.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST;
import static am.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static am.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static am.gtest.vortex.support.MyLocalization.localized_select_consumable;
import static am.gtest.vortex.support.MyLocalization.localized_select_set_save_consumable;
import static am.gtest.vortex.support.MyLocalization.localized_send_consumable_caps;
import static am.gtest.vortex.support.MyLocalization.localized_sure_to_leave;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_CONSUMABLES_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AllConsumablesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private AllConsumablesRvAdapter allConsumablesRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private Button btnSendConsumables;

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

        AllConsumablesData.generate(assignmentId);
        allConsumablesRvAdapter = new AllConsumablesRvAdapter(ALL_CONSUMABLES_LIST_FILTERED, this);
        rvAllConsumables.setAdapter(allConsumablesRvAdapter);

        String RelatedConsumables = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW, assignmentId, "");

        if (RelatedConsumables.isEmpty() || MyUtils.isNetworkAvailable()) {
            GetAllConsumables getAllConsumables = new GetAllConsumables(allConsumablesRvAdapter, assignmentId);
            getAllConsumables.execute();
        }

        //if (ALL_CONSUMABLES_LIST.size() == 0) {
        //if (RelatedConsumables.isEmpty()) {

        //}
        //}

        btnSendConsumables.setOnClickListener(v -> {

            if (CONSUMABLES_TOADD_LIST.size() > 0) {

                MyPrefs.setStringWithFileName(PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC, assignmentId, CONSUMABLES_TOADD_LIST.toString());
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
                        finish();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
    }
}