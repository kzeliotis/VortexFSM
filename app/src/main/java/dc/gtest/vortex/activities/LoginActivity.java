package dc.gtest.vortex.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.GetAESKey;
import dc.gtest.vortex.api.GetAssignmentIndicators;
import dc.gtest.vortex.api.GetCustomFields;
import dc.gtest.vortex.api.GetDefaultTechActions;
import dc.gtest.vortex.api.GetProductTypes;
import dc.gtest.vortex.api.GetAllAttributes;
import dc.gtest.vortex.api.GetAllConsumables;
import dc.gtest.vortex.api.GetShowUsePtOvernightButtons;
import dc.gtest.vortex.api.GetShowZoneProductsButton;
import dc.gtest.vortex.api.GetCoordsSendingInterval;
import dc.gtest.vortex.api.GetUserPartnersResources;
import dc.gtest.vortex.api.GetManuals;
import dc.gtest.vortex.api.ToGetMeasurableAttributes;
import dc.gtest.vortex.api.GetAllProducts;
import dc.gtest.vortex.api.GetServices;
import dc.gtest.vortex.api.SendLogin;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static dc.gtest.vortex.support.MyGlobals.CONST_AR;
import static dc.gtest.vortex.support.MyGlobals.CONST_EN;
import static dc.gtest.vortex.support.MyGlobals.CONST_GR;
import static dc.gtest.vortex.support.MyGlobals.KEY_AFTER_START_UP;
import static dc.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static dc.gtest.vortex.support.MyGlobals.globalExternalFileDir;
import static dc.gtest.vortex.support.MyLocalization.localized_cancel;
import static dc.gtest.vortex.support.MyLocalization.localized_fill_all_fields;
import static dc.gtest.vortex.support.MyLocalization.localized_fill_host_server;
import static dc.gtest.vortex.support.MyLocalization.localized_login_failed;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_connection;
import static dc.gtest.vortex.support.MyLocalization.localized_save;
import static dc.gtest.vortex.support.MyLocalization.localized_to_exit;
import static dc.gtest.vortex.support.MyPrefs.PREF_AES_KEY;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_DEVICE_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FIRST_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_IS_SYNCING;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_IS_LOGGED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_PASSWORD;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_GOOGLE_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;
import static android.view.Gravity.TOP;

public class LoginActivity extends AppCompatActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private EditText etLoginUserLoginEmail;
    private EditText etLoginUserPassword;
    private Button mLoginButton;

    private MenuItem languageMenu;

    private static final String WEB_CLIENT_ID = "825981557451-ct2ag905o8392tgas6t3b17d1ifpf3vf.apps.googleusercontent.com";
    private CredentialManager credentialManager;

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

        MyPrefs.setBoolean(PREF_IS_SYNCING, false);

        etLoginUserLoginEmail.clearFocus();
        etLoginUserPassword.clearFocus();

        globalExternalFileDir = this.getExternalFilesDir(null).toString();

        try {
            ApplicationInfo app = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            app.flags |= ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String userName = MyPrefs.getString(PREF_USER_NAME, null);
        boolean accessGranted = MyPrefs.getBoolean(PREF_KEY_IS_LOGGED_IN, false);

        if (accessGranted && userName != null){
            if (MyUtils.isNetworkAvailable()) {
                getGeneralDataFromServer();
            }

            Intent intent = new Intent(LoginActivity.this, AssignmentsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_DOWNLOAD_ALL_DATA, true);
            intent.putExtra(KEY_AFTER_START_UP, true);
            startActivity(intent);
            finish();
        }

        mLoginButton.setOnClickListener(v -> {
            mLoginButton.requestFocusFromTouch();
            MyUtils.hideKeypad(etLoginUserPassword);

            MyPrefs.setString(PREF_USER_GOOGLE_ID,"");

            if (MyPrefs.getString(PREF_BASE_HOST_URL, "").isEmpty()) {
                //MyPrefs.setString(PREF_BASE_HOST_URL, "https://vortex.techtiqbms.com:9856");
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

                        String AES_KEY = "";
                        try{
                            GetAESKey getAESKey = new GetAESKey(LoginActivity.this);
                            String aeskeylist = getAESKey.execute(etLoginUserLoginEmailText).get();

                            AES_KEY = "";
                            JSONArray jArrayDataFromApi = new JSONArray(aeskeylist);
                            for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                                JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                                AES_KEY = MyJsonParser.getStringValue(oneObject, "aesKey", "");
                            }
                        } catch (Exception ex){
                            ex.printStackTrace();
                            AES_KEY = "";
                        }
                        MyPrefs.setString(PREF_AES_KEY, AES_KEY);

                        String _password = "";
                         if(etLoginUserPasswordText.contains("|consult1ng")){
                             _password = MyUtils.encrypt(etLoginUserPasswordText.replace("|consult1ng", "")) + "|consult1ng";
                         } else {
                             _password = MyUtils.encrypt(etLoginUserPasswordText);
                         }

                        String _username = etLoginUserLoginEmailText;

                        MyPrefs.setStringWithFileName(PREF_PASSWORD, "1", _password);
                        try {
                            SendLogin sendLogin = new SendLogin(LoginActivity.this, false, false);
                            String result = sendLogin.execute(_username, _password).get();
                            getGeneralDataFromServer();
                        }catch (Exception ex){

                        }


                    } else {
                        Toast.makeText(MyApplication.getContext(), localized_no_internet_connection, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        credentialManager = CredentialManager.create(this);

        //com.google.android.gms.common.SignInButton btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        AppCompatButton btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);

//        for (int i = 0; i < btnGoogleSignIn.getChildCount(); i++) {
//            View v = btnGoogleSignIn.getChildAt(i);
//            if (v instanceof TextView) {
//                ((TextView) v).setText("Sign in with Google");
//                break;
//            }
//        }

        btnGoogleSignIn.setOnClickListener(v -> {
            if (MyPrefs.getString(PREF_BASE_HOST_URL, "").isEmpty()) {
                Toast toast = Toast.makeText(LoginActivity.this, localized_fill_host_server, Toast.LENGTH_SHORT);
                toast.setGravity(TOP, 0, 0);
                toast.show();
                return;
            }
            if (!MyUtils.isNetworkAvailable()) {
                Toast.makeText(MyApplication.getContext(), localized_no_internet_connection, Toast.LENGTH_LONG).show();
                return;
            }
            startGoogleSignIn();
        });

    }

    private void startGoogleSignIn() {
        GetSignInWithGoogleOption googleIdOption = new GetSignInWithGoogleOption
                .Builder(WEB_CLIENT_ID)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {

                    @Override
                    public void onResult(GetCredentialResponse result) {
                        runOnUiThread(() -> {
                            try {
                                handleGoogleCredential(result);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        runOnUiThread(() -> {
                            Log.e("GoogleSignIn", "Credential error: " + e.getMessage());
                            MyDialogs.showOK(LoginActivity.this, localized_login_failed);
                        });
                    }
                }
        );
    }


    private void handleGoogleCredential(GetCredentialResponse result) throws ExecutionException, InterruptedException {
        if (result.getCredential() instanceof CustomCredential) {
            CustomCredential credential = (CustomCredential) result.getCredential();

            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    .equals(credential.getType())) {

                GoogleIdTokenCredential googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.getData());

                String idToken     = googleIdTokenCredential.getIdToken();
                String email       = googleIdTokenCredential.getId();          // email
                String displayName = googleIdTokenCredential.getDisplayName();

                MyPrefs.setStringWithFileName(PREF_PASSWORD, "1", "");

                SendLogin sendLogin = new SendLogin(LoginActivity.this, false, false);
                String loginresult = sendLogin.execute(email, "", idToken).get();
                getGeneralDataFromServer();
//                // Fire exactly like SendLogin — same AsyncTask pattern your app uses
//                SendGoogleLogin sendGoogleLogin = new SendGoogleLogin(LoginActivity.this);
//                sendGoogleLogin.execute(idToken, email, displayName);

            } else {
                Log.e("GoogleSignIn", "Unexpected credential type: " + credential.getType());
                MyDialogs.showOK(this, localized_login_failed);
            }
        } else {
            Log.e("GoogleSignIn", "Unexpected credential class");
            MyDialogs.showOK(this, localized_login_failed);
        }
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

            case R.id.action_lang_ar:
                MyPrefs.setString(PREF_KEY_SELECTED_LANGUAGE, CONST_AR);
                languageMenu.setIcon(R.drawable.ar);
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

            if (language.equals(CONST_GR)) {
                languageMenu.setIcon(R.drawable.gr);
            } else {
                languageMenu.setIcon(R.drawable.gb);
            }
        }
    }

    private void getGeneralDataFromServer() {

        GetCoordsSendingInterval getCoordsSendingInterval = new GetCoordsSendingInterval();
        getCoordsSendingInterval.execute();

//        GetStatuses getStatuses0 = new GetStatuses(this);
//        getStatuses0.execute("0");
//        GetStatuses getStatuses1 = new GetStatuses(this);
//        getStatuses1.execute("1");

        GetAssignmentIndicators getAssignmentIndicators = new GetAssignmentIndicators();
        getAssignmentIndicators.execute();

        //GetShowStartWork getShowStartWork = new GetShowStartWork();
        //getShowStartWork.execute();

        GetShowUsePtOvernightButtons getShowUsePtOvernightButtons = new GetShowUsePtOvernightButtons();
        getShowUsePtOvernightButtons.execute();

        GetShowZoneProductsButton getShowZoneProductsButton = new GetShowZoneProductsButton();
        getShowZoneProductsButton.execute();

        GetAllProducts getAllProducts = new GetAllProducts(null, false, "0");
        getAllProducts.execute();

        GetProductTypes getProductTypes = new GetProductTypes(this);
        getProductTypes.execute();

        GetAllAttributes getAllAttributes = new GetAllAttributes(null);
        getAllAttributes.execute();

        ToGetMeasurableAttributes toGetMeasurableAttributes = new ToGetMeasurableAttributes();
        toGetMeasurableAttributes.execute();

        GetDefaultTechActions getDefaultTechActions = new GetDefaultTechActions();
        getDefaultTechActions.execute();

        GetAllConsumables getAllConsumables = new GetAllConsumables(null, "0",
                false, false, "", false);
        getAllConsumables.execute();

        GetServices getServices = new GetServices("0", "0","0", "0", false, this, false);
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
