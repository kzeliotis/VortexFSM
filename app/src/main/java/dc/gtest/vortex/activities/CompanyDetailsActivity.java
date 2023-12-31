package dc.gtest.vortex.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.ProjectsRvAdapter;
import dc.gtest.vortex.data.ProjectsData;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;

import static dc.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.PROJECTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_COMPANY;
import static dc.gtest.vortex.support.MyLocalization.localized_business_title;
import static dc.gtest.vortex.support.MyLocalization.localized_business;
import static dc.gtest.vortex.support.MyLocalization.localized_company_details;
import static dc.gtest.vortex.support.MyLocalization.localized_company_projects_with_colon;
import static dc.gtest.vortex.support.MyLocalization.localized_discount_with_colon;
import static dc.gtest.vortex.support.MyLocalization.localized_revenue_with_colon;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyLocalization.localized_vat_number;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class CompanyDetailsActivity extends BaseDrawerActivity implements View.OnClickListener  {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private ProjectsRvAdapter projectsRvAdapter;
    private TextView tvCompanyBusinessTitle;
    private TextView tvCompanyBusiness;
    private TextView tvCompanyVatNumber;
    private TextView tvCompanyRevenue;
    private TextView tvCompanyDiscount;
    private TextView tvCompanyProjectsTitle;
    private TextView tvCompanyMobile;
    private TextView tvCompanyPhone;
    private SearchView searchView;

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
        projectsRvAdapter = new ProjectsRvAdapter(PROJECTS_LIST_FILTERED, CompanyDetailsActivity.this, isForNewAssignment);
        rvProjects.setAdapter(projectsRvAdapter);
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

        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }

        updateUiTexts();
    }

    @Override
    public void onBackPressed() {
        SELECTED_COMPANY.clearModel();
        finish();
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
                projectsRvAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
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
