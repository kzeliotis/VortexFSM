package dc.gtest.vortex.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.FrameLayout;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AssignmentTypesRvAdapter;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENT_TYPES_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_types;
import static dc.gtest.vortex.support.MyLocalization.localized_resources;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AssignmentTypesActivity extends BaseDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (MyUtils.isNetworkAvailable()) {
//            GetUserPartnersResources getUserPartnersResources = new GetUserPartnersResources();
//            getUserPartnersResources.execute();
//        }

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_assignment_types, flBaseContainer, true);

        RecyclerView rvAssignmentTypes = findViewById(R.id.rvAssignmentTypes);

        AssignmentTypesRvAdapter assignmentTypesRvAdapter = new AssignmentTypesRvAdapter(ASSIGNMENT_TYPES_LIST, AssignmentTypesActivity.this);
        rvAssignmentTypes.setAdapter(assignmentTypesRvAdapter);
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_assignment_types);
            //getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }
    }

}
