package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.HistoryRvAdapter;
import dc.gtest.vortex.api.GetHistory;
import dc.gtest.vortex.data.HistoryData;
import dc.gtest.vortex.models.NewAssignmentModel;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.HISTORY_LIST;
import static dc.gtest.vortex.support.MyGlobals.KEY_GROUPED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.KEY_PROJECT_INSTALLATION_ID;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PROJECT;
import static dc.gtest.vortex.support.MyGlobals.globalGetHistoryParameter;
import static dc.gtest.vortex.support.MyLocalization.localized_add_subassignment;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_history;
import static dc.gtest.vortex.support.MyLocalization.localized_subAssignments;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_HISTORY_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_HISTORY_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_SUBASSIGNMENTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class HistoryActivity extends BaseDrawerActivity implements View.OnClickListener {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private HistoryRvAdapter historyRvAdapter;
    private String assignmentId = "";
    private String projectInstallationId = "";
    private Boolean subAssignments = false;
    private Button btnAddSubAssignment;
    private boolean added_grouped_assignments = false;

    private TextView tvAssignmentId;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_history, flBaseContainer, true);

        btnAddSubAssignment = findViewById(R.id.btnAddSubAssignment);
        assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");
        String sub = getIntent().getStringExtra("SubAssignments");
        projectInstallationId = getIntent().getStringExtra(KEY_PROJECT_INSTALLATION_ID);
        if (projectInstallationId == null) {projectInstallationId =  "0";}

        if (sub != null){
            subAssignments = true;
            btnAddSubAssignment.setText(localized_add_subassignment);
            btnAddSubAssignment.setOnClickListener(this);
        } else {
            btnAddSubAssignment.setVisibility(View.GONE);
        }

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        RecyclerView rvHistoryList = findViewById(R.id.rvHistoryList);

        HISTORY_LIST.clear();
        String historyData;
        if (subAssignments){
            historyData  = MyPrefs.getStringWithFileName(PREF_FILE_SUBASSIGNMENTS_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), "");
        }else{
            if (projectInstallationId.equals("0")){
                historyData  = MyPrefs.getStringWithFileName(PREF_FILE_HISTORY_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), "");  //getProjectId ??
            } else {
                historyData  = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_HISTORY_DATA, projectInstallationId, "");
            }

        }


        //Log.e(LOG_TAG, "=========================== historyData:\n" + historyData);

        HistoryData.generate(historyData);

//        if (HISTORY_LIST.size() == 0) {
//            Toast.makeText(MyApplication.getContext(), localized_no_history, Toast.LENGTH_LONG).show();
//        }

        historyRvAdapter = new HistoryRvAdapter(HISTORY_LIST, subAssignments, this);
        rvHistoryList.setAdapter(historyRvAdapter);

        assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
        if (globalGetHistoryParameter.length() > 0 ){
            assignmentId = globalGetHistoryParameter;
            globalGetHistoryParameter = "";
        }

        if (MyUtils.isNetworkAvailable()) {
            GetHistory getHistory = new GetHistory(this, assignmentId, true, subAssignments, projectInstallationId);
            getHistory.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }

        if(added_grouped_assignments){
            if (MyUtils.isNetworkAvailable()) {
                GetHistory getHistory = new GetHistory(this, assignmentId, true, subAssignments, projectInstallationId);
                getHistory.execute();
            }
//            HISTORY_LIST.clear();
//            String historyData;
//            historyData  = MyPrefs.getStringWithFileName(PREF_FILE_SUBASSIGNMENTS_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), "");
//            HistoryData.generate(historyData);
//            historyRvAdapter.notifyDataSetChanged();
        }

        updateUiTexts();
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
        historyRvAdapter.notifyDataSetChanged();
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            if(subAssignments){
                getSupportActionBar().setTitle(localized_subAssignments);
            }else{
                getSupportActionBar().setTitle(localized_history);
            }

            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String assignmentIdText = localized_assignment_id + ": " + assignmentId;
        tvAssignmentId.setText(assignmentIdText);
        if (assignmentId.contains(";")){
            String ProjectId = assignmentId.split(";")[0];
            if (ProjectId.equals("0")){
                tvAssignmentId.setText(SELECTED_PRODUCT.getProductDescription());
            } else {
                tvAssignmentId.setText(SELECTED_PROJECT.getProjectDescription());
            }
        }

        if (!projectInstallationId.equals("0")){
            tvAssignmentId.setText(SELECTED_INSTALLATION.getProjectInstallationFullDescription());
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
                historyRvAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnAddSubAssignment:
                added_grouped_assignments = true;
                NEW_ASSIGNMENT = new NewAssignmentModel();
                NEW_ASSIGNMENT.setAssignmentId(SELECTED_ASSIGNMENT.getAssignmentId());
                NEW_ASSIGNMENT.setAssignmentSourceProcedure("NEW GROUPED WORK ORDER FROM MOBILE");
                Intent intent = new Intent(HistoryActivity.this, UserPartnerResourcesActivity.class);
                intent.putExtra(CONST_IS_FOR_NEW_ASSIGNMENT, true);
                intent.putExtra(KEY_GROUPED_ASSIGNMENT, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            break;

        }

    }
}
