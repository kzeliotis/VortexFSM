package dc.gtest.vortex.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Arrays;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.UserPartnerResourcesRvAdapter;
import dc.gtest.vortex.api.GetUserPartnersResources;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.CONST_SINGLE_SELECTION;
import static dc.gtest.vortex.support.MyGlobals.KEY_ASSIGNMENT_DATE;
import static dc.gtest.vortex.support.MyGlobals.KEY_CUSTOMERID;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.USER_PARTNER_RESOURCE_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_resources;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class UserPartnerResourcesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String CustomerId = getIntent().getStringExtra(KEY_CUSTOMERID);
        if(CustomerId == null){CustomerId = "0";}
        String AssignmentId = "0";

        if(!SELECTED_ASSIGNMENT.getAssignmentId().isEmpty() && CustomerId.equals("0")){
            AssignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
        }

        String assignmentDate = getIntent().getStringExtra(KEY_ASSIGNMENT_DATE);

        if (MyUtils.isNetworkAvailable()) {
            GetUserPartnersResources getUserPartnersResources = new GetUserPartnersResources(AssignmentId, CustomerId);
            try {
              String result_ = getUserPartnersResources.execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_user_partner_resources, flBaseContainer, true);

        RecyclerView rvUserPartnerResources = findViewById(R.id.rvUserPartnerResources);
        boolean singleSelection = getIntent().getBooleanExtra(CONST_SINGLE_SELECTION, false);


        if (singleSelection) {
            for (int i = 0; i < USER_PARTNER_RESOURCE_LIST.size(); i++) {
               USER_PARTNER_RESOURCE_LIST.get(i).setChecked(false);
            }
        }

        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);
        String assDate = getIntent().getStringExtra(KEY_ASSIGNMENT_DATE);
        List<String> rIds = Arrays.asList(NEW_ASSIGNMENT.getResourceIds().split(", "));
        UserPartnerResourcesRvAdapter userPartnerResourcesRvAdapter = new UserPartnerResourcesRvAdapter(USER_PARTNER_RESOURCE_LIST, UserPartnerResourcesActivity.this, isForNewAssignment, singleSelection, assDate, rIds);
        rvUserPartnerResources.setAdapter(userPartnerResourcesRvAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUiTexts();
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
            getSupportActionBar().setTitle(localized_resources);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }
    }


}