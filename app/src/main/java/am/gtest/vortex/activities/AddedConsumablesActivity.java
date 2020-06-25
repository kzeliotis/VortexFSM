package am.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.AddedConsumablesRvAdapter;
import am.gtest.vortex.data.AddedConsumablesData;
import am.gtest.vortex.support.MyCanEdit;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;

import static am.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST;
import static am.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static am.gtest.vortex.support.MyLocalization.localized_add_new_consumable_caps;
import static am.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static am.gtest.vortex.support.MyLocalization.localized_consumables;
import static am.gtest.vortex.support.MyLocalization.localized_suggested_used;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AddedConsumablesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private AddedConsumablesRvAdapter addedConsumablesRvAdapter;
    private SearchView searchView;

    private TextView tvAssignmentId;
    private TextView tvTableHead;
    private Button btnAddNewConsumable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_added_consumables, flBaseContainer, true);

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        tvTableHead = findViewById(R.id.tvTableHead);
        RecyclerView rvAddedConsumables = findViewById(R.id.rvAddedConsumables);
        btnAddNewConsumable = findViewById(R.id.btnAddNewConsumable);

        ADDED_CONSUMABLES_LIST.clear();
        ADDED_CONSUMABLES_LIST_FILTERED.clear();

        addedConsumablesRvAdapter = new AddedConsumablesRvAdapter(ADDED_CONSUMABLES_LIST, ADDED_CONSUMABLES_LIST_FILTERED);
        rvAddedConsumables.setAdapter(addedConsumablesRvAdapter);

        btnAddNewConsumable.setOnClickListener(v -> {
            if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {
                Intent intent = new Intent(AddedConsumablesActivity.this, AllConsumablesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        AddedConsumablesData.generate(SELECTED_ASSIGNMENT.getAssignmentId());
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
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String assignmentIdText = localized_assignment_id + ": " + MyPrefs.getString(PREF_ASSIGNMENT_ID, "");
        tvAssignmentId.setText(assignmentIdText);

        tvTableHead.setText(localized_suggested_used);
        btnAddNewConsumable.setText(localized_add_new_consumable_caps);
    }
}
