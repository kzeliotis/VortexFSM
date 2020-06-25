package am.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.ZoneCustomFieldsRvAdapter;
import am.gtest.vortex.api.SendProjectZone;
import am.gtest.vortex.models.CustomFieldModel;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static am.gtest.vortex.support.MyGlobals.NEW_INSTALLATION_ZONE;
import static am.gtest.vortex.support.MyLocalization.localized_code;
import static am.gtest.vortex.support.MyLocalization.localized_custom_fields;
import static am.gtest.vortex.support.MyLocalization.localized_edit_installation_zone;
import static am.gtest.vortex.support.MyLocalization.localized_fill_zone_descirption;
import static am.gtest.vortex.support.MyLocalization.localized_new_installation_zone;
import static am.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static am.gtest.vortex.support.MyLocalization.localized_notes;
import static am.gtest.vortex.support.MyLocalization.localized_select_value;
import static am.gtest.vortex.support.MyLocalization.localized_send_data_caps;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyLocalization.localized_zone_description;

public class InstallationZoneEditActivity extends BaseDrawerActivity implements View.OnClickListener {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private RecyclerView rvZoneCustomFields;
    private TextView tvProjectZoneCode;
    private EditText etProjectZoneCode;
    private TextView tvZoneDescription;
    private EditText etZoneDescription;
    private TextView tvZoneNotes;
    private EditText etZoneNotes;
    private TextView tvCustomFields;
    private Button btnSendZone;
    private ZoneCustomFieldsRvAdapter zoneCustomFieldsRvAdapter;
    private boolean IsNewZone = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_installation_zone_edit, flBaseContainer, true);

        rvZoneCustomFields = findViewById(R.id.rvZoneCustomFields);
        tvProjectZoneCode = findViewById(R.id.tvProjectZoneCode);
        etProjectZoneCode = findViewById(R.id.etProjectZoneCode);
        tvZoneDescription = findViewById(R.id.tvZoneDescription);
        etZoneDescription = findViewById(R.id.etZoneDescription);
        tvZoneNotes = findViewById(R.id.tvZoneNotes);
        etZoneNotes = findViewById(R.id.etZoneNotes);
        tvCustomFields = findViewById(R.id.tvCustomFields);
        btnSendZone = findViewById(R.id.btnSendZone);

        List<CustomFieldModel> customFieldsList = new ArrayList<>();

        if(NEW_INSTALLATION_ZONE.getZoneId().equals("-1")){
            IsNewZone = true;
            try {
                List<CustomFieldModel> cfList = new ArrayList<>();
                CustomFieldModel cfModel;
                String cfListString =  MyPrefs.getStringWithFileName(MyPrefs.PREF_FILE_NEW_ZONE_CUSTOM_FIELDS_DATA_FOR_SHOW, "0", "");
                JSONArray customFields = new JSONArray(cfListString);
                for (int dv = 0; dv < customFields.length(); dv++) {
                    JSONObject cfObject = customFields.getJSONObject(dv);
                    if(MyJsonParser.getStringValue(cfObject, "CustomFieldId", "").equals("-1")){continue;}
                    cfModel = new CustomFieldModel();
                    cfModel.setCustomFieldId(MyJsonParser.getStringValue(cfObject, "CustomFieldId", "0"));
                    cfModel.setCustomFieldDescription(MyJsonParser.getStringValue(cfObject, "CustomFieldDescription", ""));
                    cfModel.setCustomFieldValue(MyJsonParser.getStringValue(cfObject, "CustomFieldValue", ""));
                    cfModel.setHasValues(MyJsonParser.getBooleanValue(cfObject, "HasValues", false));
                    cfModel.setCustomFieldDataType(MyJsonParser.getStringValue(cfObject, "CustomFieldDataType", ""));
                    cfModel.setCustomFieldValueId(MyJsonParser.getStringValue(cfObject, "VortexTableCustomFieldId", "0"));
                    cfModel.setEditable(MyJsonParser.getBooleanValue(cfObject, "Editable", true));
                    cfModel.setObjectTable(MyJsonParser.getStringValue(cfObject, "VortexTable", ""));
                    cfModel.setObjectTableIdField(MyJsonParser.getStringValue(cfObject, "VortexTableIdField", ""));
                    cfModel.setObjectTableId(MyJsonParser.getStringValue(cfObject, "VortexTableId", "0"));

                    cfList.add(cfModel);
                }
                customFieldsList =  cfList;
                NEW_INSTALLATION_ZONE.setZoneCustomFields(cfList);

            } catch (Exception e){

            }
        }else{
            customFieldsList = NEW_INSTALLATION_ZONE.getZoneCustomFields();
        }


        zoneCustomFieldsRvAdapter = new ZoneCustomFieldsRvAdapter(customFieldsList, InstallationZoneEditActivity.this, localized_select_value);
        rvZoneCustomFields.setAdapter(zoneCustomFieldsRvAdapter);

        btnSendZone.setOnClickListener(this);

    }



    @Override
    protected void onResume() {
        super.onResume();

        updateUiTexts();

    }

    private void updateUiTexts() {

        String title = "";

        if(IsNewZone){title = localized_new_installation_zone;}else{title = localized_edit_installation_zone;}

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(MyPrefs.PREF_USER_NAME, ""));
        }

        tvProjectZoneCode.setText(localized_code);
        etProjectZoneCode.setText(NEW_INSTALLATION_ZONE.getZoneCode());
        tvZoneDescription.setText(localized_zone_description);
        etZoneDescription.setText(NEW_INSTALLATION_ZONE.getZoneName());
        tvZoneNotes.setText(localized_notes);
        etZoneNotes.setText(NEW_INSTALLATION_ZONE.getZoneNotes());
        tvCustomFields.setText(localized_custom_fields);
        btnSendZone.setText(localized_send_data_caps);

    }



    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

//    @Override
//    public boolean onLongClick(View v) {
//        return true;
//    }


    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.btnSendZone:

                MyUtils.hideKeypad(this, etZoneNotes);
                etZoneNotes.clearFocus();

                String zoneDescription = etZoneDescription.getText().toString();

                if(zoneDescription.trim().length() == 0){
                    Toast.makeText(this, localized_fill_zone_descirption, Toast.LENGTH_SHORT).show();
                    return;
                }

                NEW_INSTALLATION_ZONE.setZoneCode(etProjectZoneCode.getText().toString());
                NEW_INSTALLATION_ZONE.setZoneName(zoneDescription);
                NEW_INSTALLATION_ZONE.setZoneNotes(etZoneNotes.getText().toString());
                NEW_INSTALLATION_ZONE.setProjectInstallationId(SELECTED_INSTALLATION.getProjectInstallationId());
                NEW_INSTALLATION_ZONE.setProjectId(SELECTED_INSTALLATION.getProjectId());
                NEW_INSTALLATION_ZONE.setCustomFieldsString("");

                String prefKey = UUID.randomUUID().toString();
                MyPrefs.setStringWithFileName(MyPrefs.PREF_FILE_NEW_INSTALLATION_ZONES_FOR_SYNC, prefKey, NEW_INSTALLATION_ZONE.toString());

                if (MyUtils.isNetworkAvailable()) {
                    SendProjectZone sendProjectZone = new SendProjectZone(InstallationZoneEditActivity.this, prefKey, true);
                    sendProjectZone.execute(prefKey);
                } else {
                    Toast.makeText(InstallationZoneEditActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
                    finish();
                }

                break;

        }
    }



 }
