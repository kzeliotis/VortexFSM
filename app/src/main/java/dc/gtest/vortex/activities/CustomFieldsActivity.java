package dc.gtest.vortex.activities;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.CustomFieldsRvAdapter;
import dc.gtest.vortex.api.GetCustomFields;
import dc.gtest.vortex.api.SendCustomFields;
import dc.gtest.vortex.data.CustomFieldsData;
import dc.gtest.vortex.models.CustomFieldDetailModel;
import dc.gtest.vortex.models.CustomFieldModel;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.KEY_REFRESH_CUSTOM_FIELDS;
import static dc.gtest.vortex.support.MyGlobals.KEY_VORTEX_TABLE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static dc.gtest.vortex.support.MyLocalization.localized_company_custom_fields;
import static dc.gtest.vortex.support.MyLocalization.localized_custom_fields;
import static dc.gtest.vortex.support.MyLocalization.localized_det_custom_fields;
import static dc.gtest.vortex.support.MyLocalization.localized_no;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_try_later_2_lines;
import static dc.gtest.vortex.support.MyLocalization.localized_select_value;
import static dc.gtest.vortex.support.MyLocalization.localized_send_data_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_to_send_data;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyLocalization.localized_yes;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CUSTOM_FIELDS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;
import static dc.gtest.vortex.support.MyGlobals.CUSTOM_FIELDS_LIST;

public class CustomFieldsActivity extends BaseDrawerActivity implements View.OnClickListener {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private CustomFieldsRvAdapter customFieldsRvAdapter;
    private SearchView searchView;
    private TextView tvObjectDescription;
    private Button btnSendChanges;
    private String vortexTable;
    private String vortexTableId;
    private Boolean refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_custom_fields, flBaseContainer, true);

        RecyclerView rvCustomFields = findViewById(R.id.rvCustomFields);

        tvObjectDescription = findViewById(R.id.tvObjectDescription);
        btnSendChanges = findViewById(R.id.btnSendChanges);
        vortexTable = getIntent().getStringExtra(KEY_VORTEX_TABLE);
        CUSTOM_FIELDS_LIST.clear();

        refresh = getIntent().getBooleanExtra(KEY_REFRESH_CUSTOM_FIELDS, false);

        customFieldsRvAdapter = new CustomFieldsRvAdapter(CUSTOM_FIELDS_LIST, CustomFieldsActivity.this, localized_select_value, vortexTable);
        rvCustomFields.setAdapter(customFieldsRvAdapter);

        btnSendChanges.setOnClickListener(this);

        switch(vortexTable){
            case "ProjectInstallations":
                vortexTableId = SELECTED_INSTALLATION.getProjectInstallationId();
                break;

            case "Company":
                vortexTableId = "1";
                break;

            case "Det":
                vortexTableId = SELECTED_ASSIGNMENT.getAssignmentId();
                break;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }

        updateUiTexts();

        String customFields = "";

        switch(vortexTable){
            case "ProjectInstallations":
                customFields = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW, SELECTED_INSTALLATION.getProjectInstallationId(), "");
                break;
            case "Company":
                customFields = MyPrefs.getStringWithFileName(PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW, "1", "");
                break;
            case "Det":
                customFields = MyPrefs.getStringWithFileName(PREF_FILE_DET_CUSTOM_FIELDS_DATA_FOR_SHOW, SELECTED_ASSIGNMENT.getAssignmentId(), "");
                break;
        }


        if (!customFields.isEmpty() && !refresh) {
            CustomFieldsData.generate(false, vortexTable, vortexTableId);
            customFieldsRvAdapter.notifyDataSetChanged();
        } else {
            if (MyUtils.isNetworkAvailable()) {
                    GetCustomFields getCustomFields = new GetCustomFields(this,  refresh, vortexTable);
                    getCustomFields.execute(vortexTableId);
            } else {
                Toast.makeText(this, localized_no_internet_try_later_2_lines, Toast.LENGTH_LONG).show();
            }
            refresh = false;
        }
    }

    @Override
    public void onClick(View v) {

        //Intent intent;
        if (v.getId() == R.id.btnSendChanges) {
            new AlertDialog.Builder(this)
                    .setMessage(localized_to_send_data)
                    .setNegativeButton(localized_no, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton(localized_yes, (dialog, which) -> {
                        dialog.dismiss();
                        String prefKey = UUID.randomUUID().toString() + "_" + vortexTableId + "_" + vortexTable;

                        List<CustomFieldModel> cfList = new ArrayList<>();

                        cfList.addAll(CUSTOM_FIELDS_LIST);

                        for (int i = 0; i < cfList.size(); i++) {
                            cfList.get(i).setAssignmentId(SELECTED_ASSIGNMENT.getAssignmentId());
                            List<CustomFieldDetailModel> emptyDetails = new ArrayList<>();
                            cfList.get(i).setCustomFieldDetails(emptyDetails);
//                                for (int d = 0; d < cfList.get(i).getCustomFieldDetails().size(); d++){
//                                    cfList.get(i).getCustomFieldDetails().get(d).setCustomFieldDetailsString("");
//                                }
                        }

                        MyPrefs.setStringWithFileName(MyPrefs.PREF_FILE_CUSTOM_FIELDS_FOR_SYNC, prefKey, cfList.toString());

                        if (MyUtils.isNetworkAvailable()) {
                            SendCustomFields sendCustomFields = new SendCustomFields(CustomFieldsActivity.this, prefKey, true, vortexTable);
                            sendCustomFields.execute(prefKey);
                        } else {
                            Toast.makeText(CustomFieldsActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
                            finish();
                        }

                    })
                    .show();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//
//        MenuItem itemSearch = menu.findItem(R.id.action_search);
//        searchView = (SearchView) itemSearch.getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                installationZonesRvAdapter.getFilter().filter(newText);
//                return true;
//            }
//        });
//
//        return true;
//    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_custom_fields);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        switch(vortexTable){
            case "ProjectInstallations":
                tvObjectDescription.setText(SELECTED_INSTALLATION.getProjectInstallationFullDescription());
                break;
            case "Company":
                tvObjectDescription.setText(localized_company_custom_fields);
                break;
            case "Det":
                tvObjectDescription.setText(localized_det_custom_fields + "-" + SELECTED_ASSIGNMENT.getAssignmentId());
                break;
        }

        btnSendChanges.setText(localized_send_data_caps);
    }
}
