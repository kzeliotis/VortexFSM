package am.gtest.vortex.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.ProjectsRvAdapter;
import am.gtest.vortex.data.ProjectsData;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;

import static am.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.PROJECTS_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.SELECTED_COMPANY;
import static am.gtest.vortex.support.MyLocalization.localized_business_title;
import static am.gtest.vortex.support.MyLocalization.localized_business;
import static am.gtest.vortex.support.MyLocalization.localized_company_details;
import static am.gtest.vortex.support.MyLocalization.localized_company_projects_with_colon;
import static am.gtest.vortex.support.MyLocalization.localized_discount_with_colon;
import static am.gtest.vortex.support.MyLocalization.localized_revenue_with_colon;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyLocalization.localized_vat_number;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class CompanyDetailsActivity extends BaseDrawerActivity implements View.OnClickListener  {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private TextView tvCompanyBusinessTitle;
    private TextView tvCompanyBusiness;
    private TextView tvCompanyVatNumber;
    private TextView tvCompanyRevenue;
    private TextView tvCompanyDiscount;
    private TextView tvCompanyProjectsTitle;
    private TextView tvCompanyMobile;
    private TextView tvCompanyPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_company_details, flBaseContainer, true);

        TextView tvCompanyId = findViewById(R.id.tvCompanyId);
        TextView tvCompanyName = findViewById(R.id.tvCompanyName);
        tvCompanyBusinessTitle = findViewById(R.id.tvCompanyBusinessTitle);
        tvCompanyBusiness = findViewById(R.id.tvCompanyBusiness);
        tvCompanyVatNumber = findViewById(R.id.tvCompanyVatNumber);
        tvCompanyRevenue = findViewById(R.id.tvCompanyRevenue);
        tvCompanyDiscount = findViewById(R.id.tvCompanyDiscount);
        TextView tvCompanyContact = findViewById(R.id.tvCompanyContact);
        TextView tvCompanyAddress = findViewById(R.id.tvCompanyAddress);
        tvCompanyMobile = findViewById(R.id.tvCompanyMobile);
        tvCompanyPhone = findViewById(R.id.tvCompanyPhone);
        TextView tvCompanyEmail = findViewById(R.id.tvCompanyEmail);
        tvCompanyProjectsTitle = findViewById(R.id.tvCompanyProjectsTitle);
        RecyclerView rvProjects = findViewById(R.id.rvProjects);

        tvCompanyMobile.setOnClickListener(this);
        tvCompanyPhone.setOnClickListener(this);



//        int i = 0;
//        while (SELECTED_COMPANY.getCompanyName() == ""){
//            i++;
//        }

        String addressFull = "";
        String companyZip = SELECTED_COMPANY.getCompanyZip();
        String companyCity = SELECTED_COMPANY.getCompanyCity();
        String companyAddress = SELECTED_COMPANY.getCompanyAddress();

        if (!companyZip.isEmpty()) {
            addressFull = companyZip + ", ";
        }

        if (!companyCity.isEmpty()) {
            addressFull = addressFull + companyCity + ", ";
        }

        if (!companyAddress.isEmpty()) {
            addressFull = addressFull + companyAddress;
        }

        tvCompanyId.setText(SELECTED_COMPANY.getCompanyCode());
        tvCompanyName.setText(SELECTED_COMPANY.getCompanyName());
        tvCompanyContact.setText(SELECTED_COMPANY.getCompanyContact());
        tvCompanyAddress.setText(addressFull);
        tvCompanyMobile.setText(SELECTED_COMPANY.getCompanyMobile());
        tvCompanyPhone.setText(SELECTED_COMPANY.getCompanyTel());
        tvCompanyEmail.setText(SELECTED_COMPANY.getCompanyEmail());

        ProjectsData.generate("");

        boolean isForNewAssignment = getIntent().getBooleanExtra(CONST_IS_FOR_NEW_ASSIGNMENT, false);
        rvProjects.setAdapter(new ProjectsRvAdapter(PROJECTS_LIST_FILTERED, CompanyDetailsActivity.this, isForNewAssignment));
    }


    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.tvCompanyPhone:
                if (tvCompanyPhone.getText().length() > 0) {
                    try {
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + tvCompanyPhone.getText()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.tvCompanyMobile:
                if (tvCompanyMobile.getText().length() > 0) {
                    try {
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + tvCompanyMobile.getText()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        updateUiTexts();
    }

    @Override
    public void onBackPressed() {
        SELECTED_COMPANY.clearModel();
        finish();
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_company_details);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String businessTitleText = localized_business_title + ": " + SELECTED_COMPANY.getCompanyBusinessTitle();
        tvCompanyBusinessTitle.setText(businessTitleText);

        String businessText = localized_business + ": " + SELECTED_COMPANY.getCompanyBusiness();
        tvCompanyBusiness.setText(businessText);

        String vatNumberText = localized_vat_number + ": " + SELECTED_COMPANY.getCompanyVatNumber();
        tvCompanyVatNumber.setText(vatNumberText);

        String revenueText = localized_revenue_with_colon + " " + SELECTED_COMPANY.getCompanyRevenue();
        tvCompanyRevenue.setText(revenueText);

        String discountText = localized_discount_with_colon + " " + SELECTED_COMPANY.getCompanyDiscount();
        tvCompanyDiscount.setText(discountText);

        tvCompanyProjectsTitle.setText(localized_company_projects_with_colon);
    }
}
