package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.UserPartnerResourcesRvAdapter;
import dc.gtest.vortex.api.GetUserPartnersResources;
import dc.gtest.vortex.api.SendNewAssignment;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.CONST_SINGLE_SELECTION;
import static dc.gtest.vortex.support.MyGlobals.KEY_ASSIGNMENT_DATE;
import static dc.gtest.vortex.support.MyGlobals.KEY_CUSTOMERID;
import static dc.gtest.vortex.support.MyGlobals.KEY_GROUPED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.USER_PARTNER_RESOURCE_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_customer;
import static dc.gtest.vortex.support.MyLocalization.localized_create_grouped_assignment;
import static dc.gtest.vortex.support.MyLocalization.localized_existing_customer;
import static dc.gtest.vortex.support.MyLocalization.localized_new_customer;
import static dc.gtest.vortex.support.MyLocalization.localized_no;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_resources;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyLocalization.localized_yes;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class UserPartnerResourcesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private boolean groupAssignment = false;
    private SearchView searchView;

    private UserPartnerResourcesRvAdapter userPartnerResourcesRvAdapter;
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
        groupAssignment = getIntent().getBooleanExtra(KEY_GROUPED_ASSIGNMENT, false);


        if (singleSelection) {
            for (int i = 0; i < USER_PARTNER_RESOURCE_LIST.size(); i++) {
               USER_PARTNER_RESOURCE_LIST.get(i).setChecked(false);
            }
        }

        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);

        String assDate = getIntent().getStringExtra(KEY_ASSIGNMENT_DATE);
        List<String> rIds = Arrays.asList(NEW_ASSIGNMENT.getResourceIds().split(", "));
        userPartnerResourcesRvAdapter = new UserPartnerResourcesRvAdapter(USER_PARTNER_RESOURCE_LIST, UserPartnerResourcesActivity.this, isForNewAssignment, singleSelection, assDate, rIds);
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
                userPartnerResourcesRvAdapter.getFilter().filter(newText);
                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.itemCheck) {
            if (groupAssignment && NEW_ASSIGNMENT.getResourceIds().length() > 0){
                new AlertDialog.Builder(UserPartnerResourcesActivity.this)
                        .setMessage(localized_create_grouped_assignment)
                        .setNegativeButton(localized_no, (dialog, which) -> {
                            dialog.dismiss();
                            finish();

                        })
                        .setPositiveButton(localized_yes, (dialog, which) -> {
                            dialog.dismiss();

                            String prefKey = UUID.randomUUID().toString();
                            MyPrefs.setStringWithFileName(PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC, prefKey, NEW_ASSIGNMENT.toString());

                            if (MyUtils.isNetworkAvailable()) {
                                SendNewAssignment sendNewAssignment = new SendNewAssignment(UserPartnerResourcesActivity.this, prefKey, true);
                                sendNewAssignment.execute(prefKey);
                            } else {
                                Toast.makeText(UserPartnerResourcesActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
                                finish();
                            }

                        })
                        .show();
            } else {
                finish();
            }

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
