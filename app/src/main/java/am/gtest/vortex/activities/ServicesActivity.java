package am.gtest.vortex.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.ServicesRvAdapter;
import am.gtest.vortex.api.GetServices;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.KEY_CUSTOMERID;
import static am.gtest.vortex.support.MyGlobals.KEY_PRODUCTID;
import static am.gtest.vortex.support.MyGlobals.KEY_PROJECT_PRODUCT_ID;
import static am.gtest.vortex.support.MyGlobals.SERVICES_LIST_FILTERED;
import static am.gtest.vortex.support.MyLocalization.localized_services;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class ServicesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private ServicesRvAdapter servicesRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String ProjectProductId = getIntent().getStringExtra(KEY_PROJECT_PRODUCT_ID);
        String ProductId = getIntent().getStringExtra(KEY_PRODUCTID);
        String CustomerId = getIntent().getStringExtra(KEY_CUSTOMERID);
        if (SERVICES_LIST_FILTERED.size() == 0){
            if (MyUtils.isNetworkAvailable()) {
                GetServices getServices = new GetServices("0", ProjectProductId, ProductId, CustomerId);
                getServices.execute();
            }
        }

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_services, flBaseContainer, true);

        RecyclerView rvServices = findViewById(R.id.rvServices);

        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);
        servicesRvAdapter = new ServicesRvAdapter(SERVICES_LIST_FILTERED, ServicesActivity.this, isForNewAssignment);
        rvServices.setAdapter(servicesRvAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUiTexts();
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
                servicesRvAdapter.getFilter().filter(newText);
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
            getSupportActionBar().setTitle(localized_services);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }
    }
}
