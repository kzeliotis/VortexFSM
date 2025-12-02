package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.InstallationZonesRvAdapter;
import dc.gtest.vortex.api.GetZones;
import dc.gtest.vortex.data.InstallationZonesData;
import dc.gtest.vortex.models.ProjectZoneModel;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.KEY_REFRESH_ZONES;
import static dc.gtest.vortex.support.MyGlobals.NEW_INSTALLATION_ZONE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static dc.gtest.vortex.support.MyLocalization.localized_add_new_zone;
import static dc.gtest.vortex.support.MyLocalization.localized_installation_zones;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_try_later_2_lines;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_PROJECT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;
import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_ZONES_LIST;
import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_ZONES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_INSTALLATION_ZONES_LIST;

public class InstallationZonesActivity extends BaseDrawerActivity implements View.OnClickListener {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private InstallationZonesRvAdapter installationZonesRvAdapter;
    private SearchView searchView;
    private TextView tvInstallationDescription;
    private Button btnAddInstallationZone;
    private Boolean refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_installation_zones, flBaseContainer, true);

        RecyclerView rvInstallationZones = findViewById(R.id.rvInstallationZones);

        tvInstallationDescription = findViewById(R.id.tvInstallationDescription);
        btnAddInstallationZone = findViewById(R.id.btnAddNewInstallationZone);

        INSTALLATION_ZONES_LIST.clear();
        INSTALLATION_ZONES_LIST_FILTERED.clear();
        MyPrefs.setString(PREF_DATA_INSTALLATION_ZONES_LIST, "");

        refresh = getIntent().getBooleanExtra(KEY_REFRESH_ZONES, false);

//      ZonesData.generate(this);
        installationZonesRvAdapter = new InstallationZonesRvAdapter(INSTALLATION_ZONES_LIST_FILTERED, InstallationZonesActivity.this);
        rvInstallationZones.setAdapter(installationZonesRvAdapter);

        btnAddInstallationZone.setOnClickListener(this);

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
        String projectInstallationId = SELECTED_INSTALLATION.getProjectInstallationId();
        String Zones = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW, projectInstallationId, "");

        if (!Zones.isEmpty() && !refresh) {
            InstallationZonesData.generate(false);
            installationZonesRvAdapter.notifyDataSetChanged();
        } else {
            if (MyUtils.isNetworkAvailable()) {
                if (!projectId.isEmpty()) {
                    GetZones getZones = new GetZones(this, null, refresh, projectInstallationId, "");
                    getZones.execute(projectId);
                } else {
                    Toast.makeText(this, localized_no_internet_try_later_2_lines, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {

            case R.id.btnAddNewInstallationZone:
                NEW_INSTALLATION_ZONE = new ProjectZoneModel();
                NEW_INSTALLATION_ZONE.setZoneId("-1");
                intent = new Intent(InstallationZonesActivity.this, InstallationZoneEditActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
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
                installationZonesRvAdapter.getFilter().filter(newText);
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
            getSupportActionBar().setTitle(localized_installation_zones);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        tvInstallationDescription.setText(SELECTED_INSTALLATION.getProjectInstallationFullDescription());
        btnAddInstallationZone.setText(localized_add_new_zone);
    }
}
