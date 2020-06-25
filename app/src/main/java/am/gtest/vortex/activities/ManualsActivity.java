package am.gtest.vortex.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.ManualsRvAdapter;
import am.gtest.vortex.api.GetManuals;
import am.gtest.vortex.data.ManualsData;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;

import static am.gtest.vortex.support.MyGlobals.MANUALS_LIST;
import static am.gtest.vortex.support.MyLocalization.localized_manuals;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class ManualsActivity extends BaseDrawerActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private ManualsRvAdapter manualsRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_manuals, flBaseContainer, true);

        RecyclerView rvManuals = findViewById(R.id.rvManuals);

        ManualsData.generate();
        manualsRvAdapter = new ManualsRvAdapter(MANUALS_LIST, ManualsActivity.this);
        rvManuals.setAdapter(manualsRvAdapter);

        GetManuals getManuals = new GetManuals(this, true);
        getManuals.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUiTexts();
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_manuals);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) itemSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                manualsRvAdapter.getFilter().filter(newText);
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
}
