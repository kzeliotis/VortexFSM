package dc.gtest.vortex.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.ServicesRvAdapter;
import dc.gtest.vortex.api.GetServices;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.KEY_CUSTOMERID;
import static dc.gtest.vortex.support.MyGlobals.KEY_PRODUCTID;
import static dc.gtest.vortex.support.MyGlobals.KEY_PROJECT_PRODUCT_ID;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_FOR_NEW_ASSIGNMENT_LIST;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyLocalization.localized_services;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;
import static dc.gtest.vortex.support.MyPrefs.getInt;

public class ServicesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private ServicesRvAdapter servicesRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String ProjectProductId = getIntent().getStringExtra(KEY_PROJECT_PRODUCT_ID);
        String ProductId = getIntent().getStringExtra(KEY_PRODUCTID);
        String CustomerId = getIntent().getStringExtra(KEY_CUSTOMERID);
        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);

        if (isForNewAssignment){
            SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED.clear();
            if (MyUtils.isNetworkAvailable()) {
                GetServices getServices = new GetServices("0", ProjectProductId, ProductId, CustomerId, isForNewAssignment, this);
                getServices.execute();
            }
        } else {
            if (SERVICES_LIST_FILTERED.size() == 0){
                if (MyUtils.isNetworkAvailable()) {
                    GetServices getServices = new GetServices("0", ProjectProductId, ProductId, CustomerId, isForNewAssignment, this);
                    getServices.execute();
                }
            }
        }


        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_services, flBaseContainer, true);

        RecyclerView rvServices = findViewById(R.id.rvServices);

        if(isForNewAssignment){
            servicesRvAdapter = new ServicesRvAdapter(SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED, ServicesActivity.this, isForNewAssignment);
        }else{
            servicesRvAdapter = new ServicesRvAdapter(SERVICES_LIST_FILTERED, ServicesActivity.this, isForNewAssignment);
        }

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
