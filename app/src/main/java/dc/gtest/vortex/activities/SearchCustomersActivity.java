package dc.gtest.vortex.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.GetCustomers;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_address;
import static dc.gtest.vortex.support.MyLocalization.localized_name;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_try_later_2_lines;
import static dc.gtest.vortex.support.MyLocalization.localized_phone;
import static dc.gtest.vortex.support.MyLocalization.localized_project_description;
import static dc.gtest.vortex.support.MyLocalization.localized_search_customers;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class SearchCustomersActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private TextView tvSearchCustomersName;
    private TextView tvSearchCustomersPhone;
    private TextView tvSearchCustomersAddress;
    private TextView tvSearchCustomersProject;
    private EditText etSearchCustomersName;
    private EditText etSearchCustomersPhone;
    private EditText etSearchCustomersAddress;
    private EditText etSearchCustomersProject;
    private Button btnSearchCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_search_customers, flBaseContainer, true);

        tvSearchCustomersName = findViewById(R.id.tvSearchCustomersName);
        tvSearchCustomersPhone = findViewById(R.id.tvSearchCustomersPhone);
        tvSearchCustomersAddress = findViewById(R.id.tvSearchCustomersAddress);
        tvSearchCustomersProject = findViewById(R.id.tvSearchCustomersProject);
        etSearchCustomersName = findViewById(R.id.etSearchCustomersName);
        etSearchCustomersPhone = findViewById(R.id.etSearchCustomersPhone);
        etSearchCustomersAddress = findViewById(R.id.etSearchCustomersAddress);
        etSearchCustomersProject = findViewById(R.id.etSearchCustomersProject);
        btnSearchCustomers = findViewById(R.id.btnSearchCustomers);

        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);

        btnSearchCustomers.setOnClickListener(v -> {
            MyUtils.hideKeypad(SearchCustomersActivity.this, btnSearchCustomers);

            if (MyUtils.isNetworkAvailable()) {
                new GetCustomers(SearchCustomersActivity.this, isForNewAssignment, "", "").execute(
                        etSearchCustomersName.getText().toString(),
                        etSearchCustomersPhone.getText().toString(),
                        etSearchCustomersAddress.getText().toString(),
                        etSearchCustomersProject.getText().toString()
                );
            } else {
                Toast.makeText(SearchCustomersActivity.this, localized_no_internet_try_later_2_lines, Toast.LENGTH_LONG).show();
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
            getSupportActionBar().setTitle(localized_search_customers);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String nameText = localized_name + ":";
        String phoneText = localized_phone + ":";
        String addressText = localized_address + ":";
        String projectDescriptionText = localized_project_description + ":";

        tvSearchCustomersName.setText(nameText);
        tvSearchCustomersPhone.setText(phoneText);
        tvSearchCustomersAddress.setText(addressText);
        tvSearchCustomersProject.setText(projectDescriptionText);
        btnSearchCustomers.setText(localized_search_customers);
    }
}
