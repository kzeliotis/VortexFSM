package dc.gtest.vortex.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.InstallationRvAdapter;
import dc.gtest.vortex.api.GetProjectInstallations;
import dc.gtest.vortex.data.ProjectInstallationsData;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_LIST;
import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.KEY_REFRESH_INSTALLATIONS;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_installations;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_try_later_2_lines;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_INSTALLATIONS_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATIONS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_PROJECT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class ProjectInstallationActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private InstallationRvAdapter installationRvAdapter;
    private SearchView searchView;
    private TextView tvProjectDescription;
    private Boolean refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_installation, flBaseContainer, true);

        tvProjectDescription = findViewById(R.id.tvProjectDescription);

        refresh = getIntent().getBooleanExtra(KEY_REFRESH_INSTALLATIONS, false);

        RecyclerView rvInstallations = findViewById(R.id.rvInstallations);

        INSTALLATION_LIST.clear();
        INSTALLATION_LIST_FILTERED.clear();
        MyPrefs.setString(PREF_DATA_INSTALLATIONS_LIST, "");

//        ZonesData.generate(this);
        installationRvAdapter = new InstallationRvAdapter(INSTALLATION_LIST_FILTERED, ProjectInstallationActivity.this);
        rvInstallations.setAdapter(installationRvAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }

        updateUiTexts();

        String projectId = MyPrefs.getString(PREF_PROJECT_ID, "");
        String AssignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
        String Installations = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATIONS_DATA_FOR_SHOW, AssignmentId, "");



        if (!Installations.isEmpty() && !refresh) {
            ProjectInstallationsData.generate(false);
            installationRvAdapter.notifyDataSetChanged();
        } else {
            if (MyUtils.isNetworkAvailable()) {
                if (!projectId.isEmpty()) {
                    GetProjectInstallations getProjectInstallations = new GetProjectInstallations(this, installationRvAdapter, refresh);
                    getProjectInstallations.execute(projectId);
                } else {
                    Toast.makeText(this, localized_no_internet_try_later_2_lines, Toast.LENGTH_LONG).show();
                }
            }
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
                installationRvAdapter.getFilter().filter(newText);
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
            getSupportActionBar().setTitle(localized_installations);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String assignmentIdText = localized_assignment_id + ": " + MyPrefs.getString(PREF_ASSIGNMENT_ID, "");
        tvProjectDescription.setText(assignmentIdText);

    }

}
