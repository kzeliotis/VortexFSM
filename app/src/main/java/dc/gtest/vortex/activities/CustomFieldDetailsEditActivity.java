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
import dc.gtest.vortex.adapters.CustomFieldDetailsEditRvAdapter;
import dc.gtest.vortex.api.SendCustomFields;
import dc.gtest.vortex.models.CustomFieldDetailColumnModel;
import dc.gtest.vortex.models.CustomFieldDetailModel;
import dc.gtest.vortex.models.CustomFieldModel;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CUSTOM_FIELDS_LIST;
import static dc.gtest.vortex.support.MyGlobals.KEY_VORTEX_TABLE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD_DETAIL;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static dc.gtest.vortex.support.MyLocalization.localized_edit;
import static dc.gtest.vortex.support.MyLocalization.localized_new_record;
import static dc.gtest.vortex.support.MyLocalization.localized_no;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_select_value;
import static dc.gtest.vortex.support.MyLocalization.localized_send_data_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_to_send_data;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyLocalization.localized_yes;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class CustomFieldDetailsEditActivity extends BaseDrawerActivity implements View.OnClickListener {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private CustomFieldDetailsEditRvAdapter customFieldDetailsEditRvAdapter;
    private SearchView searchView;
    private TextView tvActionDescription;
    private Button btnSendChanges;
    private String vortexTable;
    private String vortexTableId;
    private Boolean closeForm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_custom_field_details_edit, flBaseContainer, true);

        RecyclerView rvCustomFieldDetails = findViewById(R.id.rvCustomFieldDetails);

        tvActionDescription = findViewById(R.id.tvActionDescription);
        btnSendChanges = findViewById(R.id.btnSendChanges);
        vortexTable = getIntent().getStringExtra(KEY_VORTEX_TABLE);
        List<CustomFieldDetailColumnModel> cf_Columns = SELECTED_CUSTOM_FIELD_DETAIL.getCustomFieldsDetailColumns();
        if (cf_Columns == null){
            cf_Columns = new ArrayList<>();
        }

        //refresh = getIntent().getBooleanExtra(KEY_REFRESH_CUSTOM_FIELDS, false);

        customFieldDetailsEditRvAdapter = new CustomFieldDetailsEditRvAdapter(cf_Columns, CustomFieldDetailsEditActivity.this, localized_select_value, vortexTable);
        rvCustomFieldDetails.setAdapter(customFieldDetailsEditRvAdapter);

        btnSendChanges.setOnClickListener(this);

        switch(vortexTable){
            case "ProjectInstallations":
                vortexTableId = SELECTED_INSTALLATION.getProjectInstallationId();
                break;

            case "Company":
                vortexTableId = "1";
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

        customFieldDetailsEditRvAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {

        //Intent intent;
        switch (v.getId()) {

            case R.id.btnSendChanges:

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

                            String gamo_details = "";

                            for (int i = 0; i < cfList.size(); i++){

                                cfList.get(i).setAssignmentId(SELECTED_ASSIGNMENT.getAssignmentId());
                                List<CustomFieldDetailModel> emptyDetails = new ArrayList<>();
                                cfList.get(i).setCustomFieldDetails(emptyDetails);

                                if(cfList.get(i).getCustomFieldId().equals(SELECTED_CUSTOM_FIELD_DETAIL.getCustomFieldId())){
                                    List<CustomFieldDetailModel> editedDetails = new ArrayList<>();
                                    SELECTED_CUSTOM_FIELD_DETAIL.setIsEdited(true);
                                    gamo_details = SELECTED_CUSTOM_FIELD_DETAIL.getCustomFieldDetailsString();
                                    SELECTED_CUSTOM_FIELD_DETAIL.setCustomFieldDetailsString("");
                                    editedDetails.add(SELECTED_CUSTOM_FIELD_DETAIL);
                                    cfList.get(i).setCustomFieldDetails(editedDetails);
                                }
                            }


                            MyPrefs.setStringWithFileName(MyPrefs.PREF_FILE_CUSTOM_FIELDS_FOR_SYNC, prefKey, cfList.toString());

                            SELECTED_CUSTOM_FIELD_DETAIL.setCustomFieldDetailsString(gamo_details);

                            if (MyUtils.isNetworkAvailable()) {
                                SendCustomFields sendCustomFields = new SendCustomFields(CustomFieldDetailsEditActivity.this, prefKey, true, vortexTable);
                                sendCustomFields.execute(prefKey);
                            } else {
                                Toast.makeText(CustomFieldDetailsEditActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
                                finish();
                            }

                        })
                        .show();

                break;

        }
    }


    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(SELECTED_CUSTOM_FIELD.getCustomFieldDescription());
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        if(SELECTED_CUSTOM_FIELD_DETAIL.getDetailTableId().equals("0")){
            tvActionDescription.setText(localized_new_record);
        }else{
            tvActionDescription.setText(localized_edit);
        }

        btnSendChanges.setText(localized_send_data_caps);
    }
}
