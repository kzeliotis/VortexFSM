package dc.gtest.vortex.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.ZonesRvAdapter;
import dc.gtest.vortex.api.GetZones;
import dc.gtest.vortex.data.ZonesData;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.KEY_REFRESH_ZONES;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.ZONES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ZONES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.ZONES_WITH_NO_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_try_later_2_lines;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyLocalization.localized_zones;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_WITH_NO_MEASUREMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_PROJECT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ZONES_LIST;

public class ZonesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private ZonesRvAdapter zonesRvAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_zones, flBaseContainer, true);

        RecyclerView rvZones = findViewById(R.id.rvZones);

        if (MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_NO_MEASUREMENTS, SELECTED_ASSIGNMENT.getAssignmentId(), "").length() > 0) {
            ZONES_WITH_NO_MEASUREMENTS_MAP = new Gson().fromJson(MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_NO_MEASUREMENTS, SELECTED_ASSIGNMENT.getAssignmentId(), ""), new TypeToken<HashMap<String, List<String>>>(){}.getType());
        }

        ZONES_LIST.clear();
        ZONES_LIST_FILTERED.clear();
        MyPrefs.setString(PREF_DATA_ZONES_LIST, "");

//        ZonesData.generate(this);
        zonesRvAdapter = new ZonesRvAdapter(ZONES_LIST_FILTERED, ZonesActivity.this);
        rvZones.setAdapter(zonesRvAdapter);
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
        String Zones = MyPrefs.getStringWithFileName(PREF_FILE_ZONES_DATA_FOR_SHOW, AssignmentId, "");
        boolean refresh = getIntent().getBooleanExtra(KEY_REFRESH_ZONES, false);


        if (!Zones.isEmpty() && !refresh) {
            ZonesData.generate(false);
            zonesRvAdapter.notifyDataSetChanged();
        } else {
            if (MyUtils.isNetworkAvailable()) {
                if (!projectId.isEmpty()) {
                    GetZones getZones = new GetZones(this, zonesRvAdapter, refresh, "0");
                    getZones.execute(projectId);
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
                zonesRvAdapter.getFilter().filter(newText);
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
            getSupportActionBar().setTitle(localized_zones);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }
    }
}
