package dc.gtest.vortex.activities;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENT_TYPES_LIST;
import static dc.gtest.vortex.support.MyGlobals.KEY_CUSTOMERID;
import static dc.gtest.vortex.support.MyGlobals.MASTER_PROJECTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.MASTER_PROJECTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_types;
import static dc.gtest.vortex.support.MyLocalization.localized_master_projects;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AssignmentTypesRvAdapter;
import dc.gtest.vortex.adapters.MasterProjectsRvAdapter;
import dc.gtest.vortex.api.GetMasterProjects;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

public class MasterProjectsActivity extends BaseDrawerActivity {

    private SearchView searchView;

    private MasterProjectsRvAdapter masterProjectsRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String CustomerId = getIntent().getStringExtra(KEY_CUSTOMERID);
        if (MyUtils.isNetworkAvailable()) {
            if(MyPrefs.getBoolean(MyPrefs.PREF_SHOW_ALL_MASTER_PROJECTS, false))
            {
                CustomerId = "";
            }

            GetMasterProjects getMasterProjects = new GetMasterProjects(this);
            getMasterProjects.execute(CustomerId);
        }

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_master_projects, flBaseContainer, true);

        RecyclerView rvMasterProjects = findViewById(R.id.rvMasterProjects);

        masterProjectsRvAdapter = new MasterProjectsRvAdapter(MASTER_PROJECTS_LIST_FILTERED, MasterProjectsActivity.this);
        rvMasterProjects.setAdapter(masterProjectsRvAdapter);
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_master_projects);
            //getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }
    }

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
                masterProjectsRvAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

}
