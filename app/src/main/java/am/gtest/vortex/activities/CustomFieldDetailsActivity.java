package am.gtest.vortex.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.CustomFieldDetailsRvAdapter;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST;
import static am.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST_FILTERED;

public class CustomFieldDetailsActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private CustomFieldDetailsRvAdapter customFieldDetailsRvAdapter;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_custom_field_details, flBaseContainer, true);

        RecyclerView rvCustomFieldDetails = findViewById(R.id.rvCustomFieldDetails);


        CUSTOM_FIELD_DETAILS_LIST = SELECTED_CUSTOM_FIELD.getCustomFieldDetails();
        CUSTOM_FIELD_DETAILS_LIST_FILTERED = CUSTOM_FIELD_DETAILS_LIST;


        customFieldDetailsRvAdapter = new CustomFieldDetailsRvAdapter(CUSTOM_FIELD_DETAILS_LIST_FILTERED, this, SELECTED_CUSTOM_FIELD.getObjectTable());

        rvCustomFieldDetails.setAdapter(customFieldDetailsRvAdapter);

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
                customFieldDetailsRvAdapter.getFilter().filter(newText);
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
            getSupportActionBar().setTitle(SELECTED_CUSTOM_FIELD.getCustomFieldDescription());
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(MyPrefs.PREF_USER_NAME, ""));
        }

    }
}
