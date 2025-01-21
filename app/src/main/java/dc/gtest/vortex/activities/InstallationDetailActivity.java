package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;

import static dc.gtest.vortex.support.MyGlobals.KEY_PROJECT_INSTALLATION_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_REFRESH_CUSTOM_FIELDS;
import static dc.gtest.vortex.support.MyGlobals.KEY_REFRESH_ZONES;
import static dc.gtest.vortex.support.MyGlobals.KEY_VORTEX_TABLE;
import static dc.gtest.vortex.support.MyLocalization.localized_custom_fields_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_history;
import static dc.gtest.vortex.support.MyLocalization.localized_installation;
import static dc.gtest.vortex.support.MyLocalization.localized_installation_type;
import static dc.gtest.vortex.support.MyLocalization.localized_installation_zones_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_notes;
import static dc.gtest.vortex.support.MyLocalization.localized_products;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_PRODUCTS_TREE;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;

public class InstallationDetailActivity extends BaseDrawerActivity implements View.OnClickListener, View.OnLongClickListener {

    private TextView tvInstallationDetailsDescription;
    private TextView tvInstallationType;
    private TextView tvInstallationDetailNotes;

    private Button btnInstallationProducts;
    private Button btnInstallationHistory;
    private Button btnInstallationZones;
    private Button btnInstallationCustomFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_installation_details, flBaseContainer, true);

        tvInstallationDetailsDescription = findViewById(R.id.tvInstallationDetailsDescription);
        tvInstallationType = findViewById(R.id.tvInstallationType);
        tvInstallationDetailNotes = findViewById(R.id.tvInstallationDetailNotes);

        btnInstallationProducts = findViewById(R.id.btnInstallationProducts);
        btnInstallationHistory = findViewById(R.id.btnInstallationHistory);
        btnInstallationZones = findViewById(R.id.btnInstallationZones);
        btnInstallationCustomFields = findViewById(R.id.btnInstallationCustomFields);

        btnInstallationProducts.setOnClickListener(this);
        btnInstallationHistory.setOnClickListener(this);
        btnInstallationZones.setOnClickListener(this);
        btnInstallationZones.setOnLongClickListener(this);
        btnInstallationCustomFields.setOnClickListener(this);
        btnInstallationCustomFields.setOnLongClickListener(this);

    }



    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.btnInstallationProducts:
                if (MyPrefs.getBoolean(PREF_SHOW_PRODUCTS_TREE, false)){
                    intent = new Intent(InstallationDetailActivity.this, ProductTreeActivity.class);
                }else{
                    intent = new Intent(InstallationDetailActivity.this, ProductsActivity.class);
                }
                intent = new Intent(InstallationDetailActivity.this, ProductsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_PROJECT_INSTALLATION_ID, SELECTED_INSTALLATION.getProjectInstallationId());
                startActivity(intent);
                break;

            case R.id.btnInstallationHistory:
                intent = new Intent(InstallationDetailActivity.this, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_PROJECT_INSTALLATION_ID, SELECTED_INSTALLATION.getProjectInstallationId());
                startActivity(intent);

                break;

            case R.id.btnInstallationZones:
                intent = new Intent(InstallationDetailActivity.this, InstallationZonesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btnInstallationCustomFields:
                intent = new Intent(InstallationDetailActivity.this, CustomFieldsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_VORTEX_TABLE, "ProjectInstallations");
                startActivity(intent);
                break;
        }

    }

    public boolean onLongClick(View v) {

        Intent intent;

        switch (v.getId()) {
            case R.id.btnInstallationZones:
                intent = new Intent(InstallationDetailActivity.this, InstallationZonesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_REFRESH_ZONES, true);
                startActivity(intent);
                break;

            case R.id.btnInstallationCustomFields:
                intent = new Intent(InstallationDetailActivity.this, CustomFieldsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_VORTEX_TABLE, "ProjectInstallations");
                intent.putExtra(KEY_REFRESH_CUSTOM_FIELDS, true);
                startActivity(intent);
                break;
        }
        return true;
    }


            @Override
    protected void onResume() {
        super.onResume();
        updateUiTexts();
    }


    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }


    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_installation);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String installationDescription = SELECTED_INSTALLATION.getProjectInstallationDescription();
        String installationCode = SELECTED_INSTALLATION.getProjectInstallationCode();
        if (installationCode.length() > 0) { installationCode += " - "; }
        String installationNotes = SELECTED_INSTALLATION.getProjectInstallationNotes();
        String installationType = SELECTED_INSTALLATION.getProjectInstallationTypeDescription();


        tvInstallationDetailsDescription.setText(localized_installation + ": " + installationCode + installationDescription);
        tvInstallationType.setText(localized_installation_type + ": " + installationType);
        tvInstallationDetailNotes.setText(localized_notes + ": " + installationNotes);

        btnInstallationProducts.setText(localized_products);
        btnInstallationHistory.setText(localized_history);
        btnInstallationZones.setText(localized_installation_zones_caps);
        btnInstallationCustomFields.setText(localized_custom_fields_caps);

    }



}
