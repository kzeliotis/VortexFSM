package dc.gtest.vortex.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AssignmentIndicatorRvAdapter;
import dc.gtest.vortex.adapters.AssignmentTypesRvAdapter;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MySliderMenu;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENT_INDICATORS_LIST;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_types;

public class AssignmentIndicatorsActivity extends BaseDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (MyUtils.isNetworkAvailable()) {
//            GetUserPartnersResources getUserPartnersResources = new GetUserPartnersResources();
//            getUserPartnersResources.execute();
//        }

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_assignment_indicators, flBaseContainer, true);

        RecyclerView rvAssignmentIndicators = findViewById(R.id.rvAssignmentIndicators);

        List<String> rIds = Arrays.asList(NEW_ASSIGNMENT.getAssignmentIndicatorIds().split(", "));
        AssignmentIndicatorRvAdapter assignmentIndicatorRvAdapter = new AssignmentIndicatorRvAdapter(ASSIGNMENT_INDICATORS_LIST, AssignmentIndicatorsActivity.this, rIds);
        rvAssignmentIndicators.setAdapter(assignmentIndicatorRvAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.itemCheck) {
            finish();
        }

        return true;
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
