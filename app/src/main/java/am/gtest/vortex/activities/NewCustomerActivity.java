package am.gtest.vortex.activities;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.UUID;

import am.gtest.vortex.R;
import am.gtest.vortex.api.SendNewCustomer;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static am.gtest.vortex.support.MyLocalization.localized_add_new_customer_caps;
import static am.gtest.vortex.support.MyLocalization.localized_address;
import static am.gtest.vortex.support.MyLocalization.localized_business;
import static am.gtest.vortex.support.MyLocalization.localized_business_title;
import static am.gtest.vortex.support.MyLocalization.localized_city;
import static am.gtest.vortex.support.MyLocalization.localized_contact;
import static am.gtest.vortex.support.MyLocalization.localized_customer_exists;
import static am.gtest.vortex.support.MyLocalization.localized_mobile;
import static am.gtest.vortex.support.MyLocalization.localized_name;
import static am.gtest.vortex.support.MyLocalization.localized_new_customer;
import static am.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static am.gtest.vortex.support.MyLocalization.localized_phone;
import static am.gtest.vortex.support.MyLocalization.localized_revenue_service;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyLocalization.localized_vat_number;
import static am.gtest.vortex.support.MyLocalization.localized_zip;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_CUSTOMER_FOR_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class NewCustomerActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private TextView tvNewCustomerName;
    private TextView tvNewCustomerBusinessTitle;
    private TextView tvNewCustomerBusiness;
    private TextView tvNewCustomerMobile;
    private TextView tvNewCustomerPhone;
    private TextView tvNewCustomerZip;
    private TextView tvNewCustomerCity;
    private TextView tvNewCustomerAddress;
    private TextView tvNewCustomerContact;
    private TextView tvNewCustomerRevenueService;
    private TextView tvNewCustomerVatNumber;

    private EditText etNewCustomerName;
    private EditText etNewCustomerBusinessTitle;
    private EditText etNewCustomerBusiness;
    private EditText etNewCustomerMobile;
    private EditText etNewCustomerPhone;
    private EditText etNewCustomerEmail;
    private EditText etNewCustomerZip;
    private EditText etNewCustomerCity;
    private EditText etNewCustomerAddress;
    private EditText etNewCustomerContact;
    private EditText etNewCustomerRevenueService;
    private EditText etNewCustomerVatNumber;

    private Button btnNewCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_new_customer, flBaseContainer, true);

        tvNewCustomerName = findViewById(R.id.tvNewCustomerName);
        tvNewCustomerBusinessTitle = findViewById(R.id.tvNewCustomerBusinessTitle);
        tvNewCustomerBusiness = findViewById(R.id.tvNewCustomerBusiness);
        tvNewCustomerMobile = findViewById(R.id.tvNewCustomerMobile);
        tvNewCustomerPhone = findViewById(R.id.tvNewCustomerPhone);
        tvNewCustomerZip = findViewById(R.id.tvNewCustomerZip);
        tvNewCustomerCity = findViewById(R.id.tvNewCustomerCity);
        tvNewCustomerAddress = findViewById(R.id.tvNewCustomerAddress);
        tvNewCustomerContact = findViewById(R.id.tvNewCustomerContact);
        tvNewCustomerRevenueService = findViewById(R.id.tvNewCustomerRevenueService);
        tvNewCustomerVatNumber = findViewById(R.id.tvNewCustomerVatNumber);

        etNewCustomerName = findViewById(R.id.etNewCustomerName);
        etNewCustomerBusinessTitle = findViewById(R.id.etNewCustomerBusinessTitle);
        etNewCustomerBusiness = findViewById(R.id.etNewCustomerBusiness);
        etNewCustomerMobile = findViewById(R.id.etNewCustomerMobile);
        etNewCustomerPhone = findViewById(R.id.etNewCustomerPhone);
        etNewCustomerEmail = findViewById(R.id.etNewCustomerEmail);
        etNewCustomerZip = findViewById(R.id.etNewCustomerZip);
        etNewCustomerCity = findViewById(R.id.etNewCustomerCity);
        etNewCustomerAddress = findViewById(R.id.etNewCustomerAddress);
        etNewCustomerContact = findViewById(R.id.etNewCustomerContact);
        etNewCustomerRevenueService = findViewById(R.id.etNewCustomerRevenueService);
        etNewCustomerVatNumber = findViewById(R.id.etNewCustomerVatNumber);

        btnNewCustomer = findViewById(R.id.btnNewCustomer);

        btnNewCustomer.setOnClickListener(v -> {

            MyUtils.hideKeypad(NewCustomerActivity.this, btnNewCustomer);

            if (etNewCustomerName.getText().toString().isEmpty()) {
                etNewCustomerName.setError(getString(R.string.required));
            } else {
                boolean alreadyExists = false;

                Map<String, ?> newCustomersForSync = getSharedPreferences(PREF_FILE_NEW_CUSTOMER_FOR_SYNC, MODE_PRIVATE).getAll();
                for (Map.Entry<String, ?> entry : newCustomersForSync.entrySet()) {
                    String prefKey = entry.getKey().toLowerCase();

                    if (prefKey.startsWith(etNewCustomerName.getText().toString().trim().toLowerCase()) && prefKey.endsWith(etNewCustomerName.getText().toString().trim().toLowerCase())) {

                        alreadyExists = true;

                        new AlertDialog.Builder(NewCustomerActivity.this)
                                .setMessage(localized_customer_exists)
                                .setNegativeButton(R.string.no, null)
                                .setPositiveButton(R.string.yes, (dialog, which) -> performDataSaveAndSend())
                                .show();

                        break;
                    }
                }

                if (!alreadyExists) {
                    performDataSaveAndSend();
                }
            }
        });
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
            getSupportActionBar().setTitle(localized_new_customer);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        tvNewCustomerName.setText(localized_name);
        tvNewCustomerBusinessTitle.setText(localized_business_title);
        tvNewCustomerBusiness.setText(localized_business);
        tvNewCustomerMobile.setText(localized_mobile);
        tvNewCustomerPhone.setText(localized_phone);
        tvNewCustomerZip.setText(localized_zip);
        tvNewCustomerCity.setText(localized_city);
        tvNewCustomerAddress.setText(localized_address);
        tvNewCustomerContact.setText(localized_contact);
        tvNewCustomerRevenueService.setText(localized_revenue_service);
        tvNewCustomerVatNumber.setText(localized_vat_number);
        btnNewCustomer.setText(localized_add_new_customer_caps);
    }

    private void performDataSaveAndSend() {
        String postBody = "{\n" +
                "  \"Address\": \"" + etNewCustomerAddress.getText().toString().trim() + "\",\n" +
                "  \"Business\": \"" + etNewCustomerBusiness.getText().toString().trim() + "\",\n" +
                "  \"BusinessTitle\": \"" + etNewCustomerBusinessTitle.getText().toString().trim() + "\",\n" +
                "  \"City\": \"" + etNewCustomerCity.getText().toString().trim() + "\",\n" +
                "  \"Code\": \"\",\n" +
                "  \"Company\": \"" + etNewCustomerName.getText().toString().trim() + "\",\n" +
                "  \"Contact\": \"" + etNewCustomerContact.getText().toString().trim() + "\",\n" +
                "  \"CustomerId\": -1,\n" +
                "  \"Email\": \"" + etNewCustomerEmail.getText().toString().trim() + "\",\n" +
                "  \"Mobile\": \"" + etNewCustomerMobile.getText().toString().trim() + "\",\n" +
                "  \"RevenueService\": \"" + etNewCustomerRevenueService.getText().toString().trim() + "\",\n" +
                "  \"Tel\": \"" + etNewCustomerPhone.getText().toString().trim() + "\",\n" +
                "  \"VatNumber\": \"" + etNewCustomerVatNumber.getText().toString().trim() + "\",\n" +
                "  \"Zip\": \"" + etNewCustomerZip.getText().toString().trim() + "\"\n" +
                "}";

        String prefKey = etNewCustomerName.getText().toString().trim() + UUID.randomUUID().toString() + etNewCustomerName.getText().toString().trim();
        MyPrefs.setStringWithFileName(PREF_FILE_NEW_CUSTOMER_FOR_SYNC, prefKey, postBody);

        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);

        if (MyUtils.isNetworkAvailable()) {
            SendNewCustomer sendNewCustomer = new SendNewCustomer(NewCustomerActivity.this, prefKey, true, isForNewAssignment, etNewCustomerName.getText().toString().trim());
            sendNewCustomer.execute();
        } else {
            Toast.makeText(NewCustomerActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
