package dc.gtest.vortex.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AssignmentTypesRvAdapter;
import dc.gtest.vortex.adapters.StatusesRvAdapter;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MySliderMenu;

import static dc.gtest.vortex.support.MyGlobals.ALL_STATUSES_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_types;
import static dc.gtest.vortex.support.MyLocalization.localized_status;

public class StatusesActivity extends BaseDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (MyUtils.isNetworkAvailable()) {
//            GetUserPartnersResources getUserPartnersResources = new GetUserPartnersResources();
//            getUserPartnersResources.execute();
//        }

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_assignment_statuses, flBaseContainer, true);

        RecyclerView rvStatuses = findViewById(R.id.rvAssignmentStatuses);

        StatusesRvAdapter statusesRvAdapter = new StatusesRvAdapter(ALL_STATUSES_LIST, StatusesActivity.this);
        rvStatuses.setAdapter(statusesRvAdapter);
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_status);
            //getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }
    }

}