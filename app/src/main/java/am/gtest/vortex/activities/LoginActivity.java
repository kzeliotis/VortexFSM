package am.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import am.gtest.vortex.R;
import am.gtest.vortex.api.GetCustomFields;
import am.gtest.vortex.api.GetDefaultTechActions;
import am.gtest.vortex.api.GetMobileSettings;
import am.gtest.vortex.api.GetProductTypes;
import am.gtest.vortex.api.GetAllAttributes;
import am.gtest.vortex.api.GetAllConsumables;
import am.gtest.vortex.api.GetShowUsePtOvernightButtons;
import am.gtest.vortex.api.GetShowZoneProductsButton;
import am.gtest.vortex.api.GetCoordsSendingInterval;
import am.gtest.vortex.api.GetUserPartnersResources;
import am.gtest.vortex.api.GetManuals;
import am.gtest.vortex.api.ToGetMeasurableAttributes;
import am.gtest.vortex.api.GetAllProducts;
import am.gtest.vortex.api.GetServices;
import am.gtest.vortex.api.GetStatuses;
import am.gtest.vortex.api.SendLogin;
import am.gtest.vortex.application.MyApplication;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.CONST_EN;
import static am.gtest.vortex.support.MyGlobals.CONST_GR;
import static am.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static am.gtest.vortex.support.MyGlobals.globalExternalFileDir;
import static am.gtest.vortex.support.MyLocalization.localized_cancel;
import static am.gtest.vortex.support.MyLocalization.localized_fill_all_fields;
import static am.gtest.vortex.support.MyLocalization.localized_fill_host_server;
import static am.gtest.vortex.support.MyLocalization.localized_no_internet_connection;
import static am.gtest.vortex.support.MyLocalization.localized_save;
import static am.gtest.vortex.support.MyLocalization.localized_to_exit;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_DEVICE_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FIRST_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_IS_LOGGED_IN;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static am.gtest.vortex.support.MyPrefs.PREF_PASSWORD;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;
import static android.view.Gravity.TOP;

public class LoginActivity extends AppCompatActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private EditText etLoginUserLoginEmail;
    private EditText etLoginUserPassword;
    private Button mLoginButton;

    private MenuItem languageMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_coordinator);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FrameLayout flCoordinatorLayout = findViewById(R.id.flBaseCoordinatorLayout);
        getLayoutInflater().inflate(R.layout.content_login, flCoordinatorLayout, true);

        MyLocalization.setupLanguage(this);

        etLoginUserLoginEmail = findViewById(R.id.etLoginUserLogin);
        etLoginUserPassword = findViewById(R.id.etLoginUserPassword);
        mLoginButton = findViewById(R.id.btnLogin);
        TextView tvDeviceId = findViewById(R.id.tvDeviceId);

        String deviceIdText = "Device ID: " + MyPrefs.getDeviceId(PREF_DEVICE_ID, "");
        tvDeviceId.setText(deviceIdText);

        etLoginUserLoginEmail.clearFocus();
        etLoginUserPassword.clearFocus();

        globalExternalFileDir = this.getExternalFilesDir(null).toString();

        String userName = MyPrefs.getString(PREF_USER_NAME, null);
        boolean accessGranted = MyPrefs.getBoolean(PREF_KEY_IS_LOGGED_IN, false);

        if (accessGranted && userName != null){
            if (MyUtils.isNetworkAvailable()) {
                getGeneralDataFromServer();
            }

            Intent intent = new Intent(LoginActivity.this, AssignmentsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_DOWNLOAD_ALL_DATA, true);
            startActivity(intent);
            finish();
        }

        mLoginButton.setOnClickListener(v -> {
            mLoginButton.requestFocusFromTouch();
            MyUtils.hideKeypad(etLoginUserPassword);

            if (MyPrefs.getString(PREF_BASE_HOST_URL, "").isEmpty()) {
                Toast toast = Toast.makeText(LoginActivity.this, localized_fill_host_server, Toast.LENGTH_SHORT);
                toast.setGravity(TOP, 0, 0);
                toast.show();
            } else {
                String etLoginUserLoginEmailText = etLoginUserLoginEmail.getText().toString();
                String etLoginUserPasswordText = etLoginUserPassword.getText().toString();

                if (etLoginUserLoginEmailText.equals("") || etLoginUserPasswordText.equals("")) {
                    MyDialogs.showOK(LoginActivity.this, localized_fill_all_fields);
                } else {
                    if (MyUtils.isNetworkAvailable()) {
                        MyPrefs.setStringWithFileName(PREF_PASSWORD, "1", etLoginUserPasswordText);
                        SendLogin sendLogin = new SendLogin(LoginActivity.this, false, false);
                        sendLogin.execute(etLoginUserLoginEmailText, etLoginUserPasswordText);

                        getGeneralDataFromServer();
                    } else {
                        Toast.makeText(MyApplication.getContext(), localized_no_internet_connection, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        languageIconChange();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_activity_menu, menu);

        languageMenu = menu.findItem(R.id.languageMenu);

        languageIconChange();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_lang_en:
                MyPrefs.setString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);
                languageMenu.setIcon(R.drawable.gb);
                MyLocalization.setupLanguage(this);
                return true;

            case R.id.action_lang_gr:
                MyPrefs.setString(PREF_KEY_SELECTED_LANGUAGE, CONST_GR);
                languageMenu.setIcon(R.drawable.gr);
                MyLocalization.setupLanguage(this);
                return true;

            case R.id.item_host_server:
                EditText etHostServerUrl = new EditText(this);

                etHostServerUrl.setPadding(48, 16, 48, 32);
                etHostServerUrl.setText(MyPrefs.getString(PREF_BASE_HOST_URL, getString(R.string.default_server)));

                new AlertDialog.Builder(this)
                        .setMessage(localized_fill_host_server)
                        .setView(etHostServerUrl)
                        .setPositiveButton(localized_save, (dialog, which) -> {

                            MyUtils.hideKeypad(etHostServerUrl);
                            dialog.dismiss();

                            if (!etHostServerUrl.getText().toString().isEmpty()) {
                                MyPrefs.setString(PREF_FIRST_HOST_URL, etHostServerUrl.getText().toString().trim());
                                MyPrefs.setString(PREF_BASE_HOST_URL, etHostServerUrl.getText().toString().trim());
                            } else {
                                Toast toast = Toast.makeText(LoginActivity.this, localized_fill_host_server, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 0);
                                toast.show();
                            }
                        })
                        .setNegativeButton(localized_cancel, (dialog, which) -> {
                            MyUtils.hideKeypad(etHostServerUrl);
                            dialog.dismiss();
                        })
                        .show();

                return true;
        }

        return true;
    }

    private void languageIconChange() {
        if (languageMenu != null) {

            String language = MyPrefs.getString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);

            switch (language) {
                case CONST_GR: languageMenu.setIcon(R.drawable.gr);
                    break;
                default: languageMenu.setIcon(R.drawable.gb);
                    break;
            }
        }
    }

    private void getGeneralDataFromServer() {

        GetCoordsSendingInterval getCoordsSendingInterval = new GetCoordsSendingInterval();
        getCoordsSendingInterval.execute();

        GetStatuses getStatuses = new GetStatuses(this);
        getStatuses.execute();

        //GetShowStartWork getShowStartWork = new GetShowStartWork();
        //getShowStartWork.execute();

        GetShowUsePtOvernightButtons getShowUsePtOvernightButtons = new GetShowUsePtOvernightButtons();
        getShowUsePtOvernightButtons.execute();

        GetShowZoneProductsButton getShowZoneProductsButton = new GetShowZoneProductsButton();
        getShowZoneProductsButton.execute();

        GetAllProducts getAllProducts = new GetAllProducts(null, false);
        getAllProducts.execute();

        GetProductTypes getProductTypes = new GetProductTypes(this);
        getProductTypes.execute();

        GetAllAttributes getAllAttributes = new GetAllAttributes(null);
        getAllAttributes.execute();

        ToGetMeasurableAttributes toGetMeasurableAttributes = new ToGetMeasurableAttributes();
        toGetMeasurableAttributes.execute();

        GetDefaultTechActions getDefaultTechActions = new GetDefaultTechActions();
        getDefaultTechActions.execute();

        GetAllConsumables getAllConsumables = new GetAllConsumables(null, "0");
        getAllConsumables.execute();

        GetServices getServices = new GetServices("0", "0","0", "0");
        getServices.execute();

        GetUserPartnersResources getUserPartnersResources = new GetUserPartnersResources("0","0");
        getUserPartnersResources.execute();

        GetManuals getManuals = new GetManuals(this, false);
        getManuals.execute();

        GetCustomFields getCustomFields = new GetCustomFields(this, true, "Company");
        getCustomFields.execute("1");
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(localized_to_exit)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    finishAffinity();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
