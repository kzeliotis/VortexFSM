package am.gtest.vortex.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import am.gtest.vortex.R;
import am.gtest.vortex.api.GetAssignmentTypes;
import am.gtest.vortex.api.GetCustomers;
import am.gtest.vortex.api.GetServices;
import am.gtest.vortex.api.SendNewAssignment;
import am.gtest.vortex.data.UserPartnerResourcesData;
import am.gtest.vortex.models.NewAssignmentModel;
import am.gtest.vortex.models.ResourceLeaveModel;
import am.gtest.vortex.models.UserPartnerResourceModel;
import am.gtest.vortex.support.MyDateTime;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.CONST_IS_FOR_NEW_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.KEY_ASSIGNMENT_DATE;
import static am.gtest.vortex.support.MyGlobals.KEY_CUSTOMERID;
import static am.gtest.vortex.support.MyGlobals.KEY_PRODUCTID;
import static am.gtest.vortex.support.MyGlobals.KEY_PROJECT_PRODUCT_ID;
import static am.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.USER_PARTNER_RESOURCE_LIST;
import static am.gtest.vortex.support.MyLocalization.localized_add_new_assignment_caps;
import static am.gtest.vortex.support.MyLocalization.localized_assignment_type;
import static am.gtest.vortex.support.MyLocalization.localized_cancel;
import static am.gtest.vortex.support.MyLocalization.localized_customer;
import static am.gtest.vortex.support.MyLocalization.localized_describe_problem;
import static am.gtest.vortex.support.MyLocalization.localized_end_date;
import static am.gtest.vortex.support.MyLocalization.localized_end_time;
import static am.gtest.vortex.support.MyLocalization.localized_existing_customer;
import static am.gtest.vortex.support.MyLocalization.localized_new_assignment;
import static am.gtest.vortex.support.MyLocalization.localized_new_customer;
import static am.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static am.gtest.vortex.support.MyLocalization.localized_choose_customer;
import static am.gtest.vortex.support.MyLocalization.localized_problem;
import static am.gtest.vortex.support.MyLocalization.localized_product;
import static am.gtest.vortex.support.MyLocalization.localized_project;
import static am.gtest.vortex.support.MyLocalization.localized_resources;
import static am.gtest.vortex.support.MyLocalization.localized_resources_not_available;
import static am.gtest.vortex.support.MyLocalization.localized_save;
import static am.gtest.vortex.support.MyLocalization.localized_select_assignment_type;
import static am.gtest.vortex.support.MyLocalization.localized_select_date_for_availability;
import static am.gtest.vortex.support.MyLocalization.localized_select_product;
import static am.gtest.vortex.support.MyLocalization.localized_select_product_from_project;
import static am.gtest.vortex.support.MyLocalization.localized_select_project;
import static am.gtest.vortex.support.MyLocalization.localized_select_resources;
import static am.gtest.vortex.support.MyLocalization.localized_select_service;
import static am.gtest.vortex.support.MyLocalization.localized_service;
import static am.gtest.vortex.support.MyLocalization.localized_set_end_date;
import static am.gtest.vortex.support.MyLocalization.localized_set_start_date;
import static am.gtest.vortex.support.MyLocalization.localized_start_date;
import static am.gtest.vortex.support.MyLocalization.localized_start_time;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_USER_PARTNER_RESOURCES;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class NewAssignmentActivity extends BaseDrawerActivity implements View.OnClickListener {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private TextView tvNewAssignmentCustomer;
    private TextView tvNewAssignmentProject;
    private TextView tvNewAssignmentProduct;
    private TextView tvNewAssignmentService;
    private TextView tvNewAssignmentResources;
    private TextView tvNewAssignmentType;

    private TextInputLayout tilNewAssignmentProblem;
    private EditText etNewAssignmentProblem;
    private TextInputLayout tilNewAssignmentStartDate;
    private EditText etNewAssignmentStartDate;
    private TextInputLayout tilNewAssignmentStartTime;
    private EditText etNewAssignmentStartTime;
    private TextInputLayout tilNewAssignmentEndDate;
    private EditText etNewAssignmentEndDate;
    private TextInputLayout tilNewAssignmentEndTime;
    private EditText etNewAssignmentEndTime;


    private Button btnAddNewAssignment;

    private Intent intent;

    private int dayStart = -1;
    private int monthStart = -1;
    private int yearStart = -1;
    private int hourStart = -1;
    private int minuteStart = -1;

    private int dayEnd = -1;
    private int monthEnd = -1;
    private int yearEnd = -1;
    private int hourEnd = -1;
    private int minuteEnd = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_new_assignment, flBaseContainer, true);

        tvNewAssignmentCustomer = findViewById(R.id.tvNewAssignmentCustomer);
        tvNewAssignmentProject = findViewById(R.id.tvNewAssignmentProject);
        tvNewAssignmentProduct = findViewById(R.id.tvNewAssignmentProduct);
        tvNewAssignmentService = findViewById(R.id.tvNewAssignmentService);
        tvNewAssignmentResources = findViewById(R.id.tvNewAssignmentResources);
        tvNewAssignmentType = findViewById(R.id.tvNewAssignmentType);

        tilNewAssignmentProblem = findViewById(R.id.tilNewAssignmentProblem);
        etNewAssignmentProblem = findViewById(R.id.etNewAssignmentProblem);
        tilNewAssignmentStartDate = findViewById(R.id.tilNewAssignmentStartDate);
        etNewAssignmentStartDate = findViewById(R.id.etNewAssignmentStartDate);
        tilNewAssignmentStartTime = findViewById(R.id.tilNewAssignmentStartTime);
        etNewAssignmentStartTime = findViewById(R.id.etNewAssignmentStartTime);
        tilNewAssignmentEndDate = findViewById(R.id.tilNewAssignmentEndDate);
        etNewAssignmentEndDate = findViewById(R.id.etNewAssignmentEndDate);
        tilNewAssignmentEndTime = findViewById(R.id.tilNewAssignmentEndTime);
        etNewAssignmentEndTime = findViewById(R.id.etNewAssignmentEndTime);


        btnAddNewAssignment = findViewById(R.id.btnAddNewAssignment);

        tvNewAssignmentCustomer.setOnClickListener(this);
        tvNewAssignmentProject.setOnClickListener(this);
        tvNewAssignmentProduct.setOnClickListener(this);
        tvNewAssignmentService.setOnClickListener(this);
        tvNewAssignmentResources.setOnClickListener(this);
        tvNewAssignmentType.setOnClickListener(this);


        etNewAssignmentStartDate.setOnClickListener(this);
        etNewAssignmentStartTime.setOnClickListener(this);
        etNewAssignmentEndDate.setOnClickListener(this);
        etNewAssignmentEndTime.setOnClickListener(this);

        btnAddNewAssignment.setOnClickListener(this);

        GetAssignmentTypes getAssignmentTypes = new GetAssignmentTypes();
        getAssignmentTypes.execute();

        NEW_ASSIGNMENT = new NewAssignmentModel();
        UserPartnerResourcesData.generate(MyPrefs.getString(PREF_DATA_USER_PARTNER_RESOURCES, ""));

        showDialogToSelectCustomer();
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
            getSupportActionBar().setTitle(localized_new_assignment);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        if (NEW_ASSIGNMENT.getCustomerId().isEmpty()) {
            tvNewAssignmentCustomer.setText(localized_choose_customer);
        } else {
            String customerNameText = localized_customer + ": " + NEW_ASSIGNMENT.getCustomerName() + " (id: " + NEW_ASSIGNMENT.getCustomerId() + ")";
            tvNewAssignmentCustomer.setText(customerNameText);
        }

        if (NEW_ASSIGNMENT.getProjectId().isEmpty()) {
            tvNewAssignmentProject.setText(localized_select_project);
        } else {
            String projectText = localized_project + ": " + NEW_ASSIGNMENT.getProjectDescription() + " (id: " + NEW_ASSIGNMENT.getProjectId() + ")";
            tvNewAssignmentProject.setText(projectText);
        }

        if (NEW_ASSIGNMENT.getProductDescription().isEmpty()) {
            tvNewAssignmentProduct.setText(localized_select_product);
        } else {
            String productText = localized_product + ": " + NEW_ASSIGNMENT.getProductDescription() + " (id: " + NEW_ASSIGNMENT.getProjectProductId() + ")";

            if (NEW_ASSIGNMENT.getProjectProductId().isEmpty()) {
                productText = localized_product + ": " + NEW_ASSIGNMENT.getProductDescription() + " (id: " + NEW_ASSIGNMENT.getProductId() + ")";
            }
            tvNewAssignmentProduct.setText(productText);
        }

        if (NEW_ASSIGNMENT.getServiceId().isEmpty()) {
            tvNewAssignmentService.setText(localized_select_service);
        } else {
            String serviceText = localized_service + ": " + NEW_ASSIGNMENT.getServiceDescription() + " (id: " + NEW_ASSIGNMENT.getServiceId() + ")";
            tvNewAssignmentService.setText(serviceText);
        }

        if (NEW_ASSIGNMENT.getResourceIds().isEmpty()) {
            tvNewAssignmentResources.setText(localized_select_resources);
        } else {
            String resourcesText = localized_resources + ": " + NEW_ASSIGNMENT.getResourceNames();
            tvNewAssignmentResources.setText(resourcesText);
        }

        if (NEW_ASSIGNMENT.getAssignmentType().isEmpty()) {
            tvNewAssignmentType.setText(localized_select_assignment_type);
        } else {
            String AssignmentTypeText = localized_assignment_type + " " + NEW_ASSIGNMENT.getAssignmentTypeDescription();
            tvNewAssignmentType.setText(AssignmentTypeText);
        }

        tilNewAssignmentProblem.setHint(localized_problem);       // hint
        tilNewAssignmentStartDate.setHint(localized_start_date);  // hint
        tilNewAssignmentStartTime.setHint(localized_start_time);  // hint
        tilNewAssignmentEndDate.setHint(localized_end_date);      // hint
        tilNewAssignmentEndTime.setHint(localized_end_time);      // hint

        btnAddNewAssignment.setText(localized_add_new_assignment_caps);
    }

    private void performDataSaveAndSend() {

//        String prefKey = etCustomerName.getText().toString().trim() + UUID.randomUUID().toString() + etCustomerName.getText().toString().trim();
//        MyPrefs.setStringWithFileName(PREF_FILE_NEW_CUSTOMER_FOR_SYNC, prefKey, postBody);
//
//        if (MyUtils.isNetworkAvailable()) {
//            SendNewCustomer sendNewCustomer = new SendNewCustomer(NewAssignmentActivity.this, prefKey, true, true, null);
//            sendNewCustomer.execute();
//        } else {
//            Toast.makeText(NewAssignmentActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
//            finish();
//        }
    }

    @Override
    public void onClick(View v) {

        intent = null;

        String customerid = "0";

        switch (v.getId()) {
            case R.id.tvNewAssignmentCustomer:
                showDialogToSelectCustomer();
                break;
            case R.id.tvNewAssignmentProject:
                if(!NEW_ASSIGNMENT.getCustomerId().isEmpty()){
                    if (MyUtils.isNetworkAvailable()) {
                        new GetCustomers(NewAssignmentActivity.this, true, NEW_ASSIGNMENT.getCustomerId(), "").execute(
                                "",
                                "",
                                "",
                                ""
                        );
                    }
                }

                break;

            case R.id.tvNewAssignmentProduct:
                if (!NEW_ASSIGNMENT.getProjectId().isEmpty()){

                    new AlertDialog.Builder(this)
                            .setMessage(localized_select_product_from_project)
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                dialog.dismiss();
                                intent = new Intent(NewAssignmentActivity.this, SearchProductsActivity.class);
                                intent.putExtra(CONST_IS_FOR_NEW_ASSIGNMENT, true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> {
                                dialog.dismiss();
                                intent = new Intent(NewAssignmentActivity.this, AllProductsActivity.class);
                                intent.putExtra(CONST_IS_FOR_NEW_ASSIGNMENT, true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            })
                            .show();
                }
                //intent = new Intent(NewAssignmentActivity.this, AllProductsActivity.class);

                break;

            case R.id.tvNewAssignmentService:

                String projectproductId = "0";
                String productId = "0";

                if(!NEW_ASSIGNMENT.getProjectProductId().isEmpty()){
                    projectproductId = NEW_ASSIGNMENT.getProjectProductId();
                }
                if(!NEW_ASSIGNMENT.getProductId().isEmpty()){
                    productId = NEW_ASSIGNMENT.getProductId();
                }
                if(!NEW_ASSIGNMENT.getCustomerId().isEmpty()){
                    customerid = NEW_ASSIGNMENT.getCustomerId();
                }

                intent = new Intent(NewAssignmentActivity.this, ServicesActivity.class);
                intent.putExtra(KEY_PROJECT_PRODUCT_ID, projectproductId);
                intent.putExtra(KEY_PRODUCTID, productId);
                intent.putExtra(KEY_CUSTOMERID, customerid);
                break;

            case R.id.tvNewAssignmentResources:

                String startDateTime = MyDateTime.getServerFormatFormAppDateTime(
                        etNewAssignmentStartDate.getText().toString().trim() + " 00:00");
                if(startDateTime.isEmpty()) {
                    Toast.makeText(this, localized_select_date_for_availability, Toast.LENGTH_SHORT).show();
                }else{
                    if(!NEW_ASSIGNMENT.getCustomerId().isEmpty()){
                        customerid = NEW_ASSIGNMENT.getCustomerId();
                    }
                    intent = new Intent(NewAssignmentActivity.this, UserPartnerResourcesActivity.class);
                    intent.putExtra(KEY_CUSTOMERID, customerid);
                    intent.putExtra(KEY_ASSIGNMENT_DATE, startDateTime);
                }

                break;

            case R.id.tvNewAssignmentType:
                intent = new Intent(NewAssignmentActivity.this, AssignmentTypesActivity.class);
                break;

            case R.id.etNewAssignmentStartDate:
                @SuppressLint("InflateParams")
                View datePickerStartView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

                DatePicker datePickerStart = datePickerStartView.findViewById(R.id.datePicker);
//                datePickerStart.setMinDate(new Date().getTime());

                if (yearStart != -1 && monthStart != -1 && dayStart != -1) {
                    datePickerStart.updateDate(yearStart, monthStart, dayStart);
                }

                new AlertDialog.Builder(NewAssignmentActivity.this)
                        .setView(datePickerStartView)
                        .setNegativeButton(localized_cancel, (dialog, which) -> MyUtils.hideKeypad(NewAssignmentActivity.this, datePickerStartView))
                        .setPositiveButton(localized_save, (dialog, which) -> {

                            MyUtils.hideKeypad(NewAssignmentActivity.this, datePickerStartView);

                            dayStart = datePickerStart.getDayOfMonth();
                            monthStart = datePickerStart.getMonth();
                            yearStart = datePickerStart.getYear();

                            etNewAssignmentStartDate.setText(MyDateTime.get_app_date_from_day_month_year_integers(dayStart, monthStart, yearStart));
                        })
                        .show();
                break;

            case R.id.etNewAssignmentStartTime:
                @SuppressLint("InflateParams")
                View timePickerStartView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);

                TimePicker timePickerStart = timePickerStartView.findViewById(R.id.timePicker);
                timePickerStart.setIs24HourView(true);

                if (hourStart != -1 && minuteStart!= -1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePickerStart.setHour(hourStart);
                        timePickerStart.setMinute(minuteStart);
                    } else {
                        timePickerStart.setCurrentHour(hourStart);
                        timePickerStart.setCurrentMinute(minuteStart);
                    }
                }

                new AlertDialog.Builder(NewAssignmentActivity.this)
                        .setView(timePickerStartView)
                        .setNegativeButton(localized_cancel, (dialog, which) -> MyUtils.hideKeypad(NewAssignmentActivity.this, timePickerStartView))
                        .setPositiveButton(localized_save, (dialog, which) -> {

                            MyUtils.hideKeypad(NewAssignmentActivity.this, timePickerStartView);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                hourStart = timePickerStart.getHour();
                                minuteStart = timePickerStart.getMinute();
                            } else {
                                hourStart = timePickerStart.getCurrentHour();
                                minuteStart = timePickerStart.getCurrentMinute();
                            }

                            etNewAssignmentStartTime.setText(MyDateTime.get_app_time_from_hour_minute_integers(hourStart, minuteStart));
                        })
                        .show();
                break;

            case R.id.etNewAssignmentEndDate:
                @SuppressLint("InflateParams")
                View datePickerEndView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

                DatePicker datePickerEnd = datePickerEndView.findViewById(R.id.datePicker);
//                datePickerEnd.setMinDate(new Date().getTime());

                if (yearEnd != -1 && monthEnd != -1 && dayEnd != -1) {
                    datePickerEnd.updateDate(yearEnd, monthEnd, dayEnd);
                }

                new AlertDialog.Builder(NewAssignmentActivity.this)
                        .setView(datePickerEndView)
                        .setNegativeButton(localized_cancel, (dialog, which) -> MyUtils.hideKeypad(NewAssignmentActivity.this, datePickerEndView))
                        .setPositiveButton(localized_save, (dialog, which) -> {

                            MyUtils.hideKeypad(NewAssignmentActivity.this, datePickerEndView);

                            dayEnd = datePickerEnd.getDayOfMonth();
                            monthEnd = datePickerEnd.getMonth();
                            yearEnd = datePickerEnd.getYear();

                            etNewAssignmentEndDate.setText(MyDateTime.get_app_date_from_day_month_year_integers(dayEnd, monthEnd, yearEnd));
                        })
                        .show();
                break;

            case R.id.etNewAssignmentEndTime:
                @SuppressLint("InflateParams")
                View timePickerEndView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);

                TimePicker timePickerEnd = timePickerEndView.findViewById(R.id.timePicker);
                timePickerEnd.setIs24HourView(true);

                if (hourEnd != -1 && minuteEnd != -1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePickerEnd.setHour(hourEnd);
                        timePickerEnd.setMinute(minuteEnd);
                    } else {
                        timePickerEnd.setCurrentHour(hourEnd);
                        timePickerEnd.setCurrentMinute(minuteEnd);
                    }
                }

                new AlertDialog.Builder(NewAssignmentActivity.this)
                        .setView(timePickerEndView)
                        .setNegativeButton(localized_cancel, (dialog, which) -> MyUtils.hideKeypad(NewAssignmentActivity.this, timePickerEndView))
                        .setPositiveButton(localized_save, (dialog, which) -> {

                            MyUtils.hideKeypad(NewAssignmentActivity.this, timePickerEndView);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                hourEnd = timePickerEnd.getHour();
                                minuteEnd = timePickerEnd.getMinute();
                            } else {
                                hourEnd = timePickerEnd.getCurrentHour();
                                minuteEnd = timePickerEnd.getCurrentMinute();
                            }

                            etNewAssignmentEndTime.setText(MyDateTime.get_app_time_from_hour_minute_integers(hourEnd, minuteEnd));
                        })
                        .show();
                break;

            case R.id.btnAddNewAssignment:
                performSend();
                break;
        }

        if (intent != null) {
            intent.putExtra(CONST_IS_FOR_NEW_ASSIGNMENT, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void performSend() {
        MyUtils.hideKeypad(this, etNewAssignmentProblem);
        etNewAssignmentProblem.clearFocus();

        NEW_ASSIGNMENT.setProblem(etNewAssignmentProblem.getText().toString().trim().replace("\n", " ").replace("\r", " "));
        NEW_ASSIGNMENT.setCurrentTime(MyDateTime.get_server_short_format_current_date_time());

        String startDateTime = MyDateTime.getServerFormatFormAppDateTime(
                etNewAssignmentStartDate.getText().toString().trim() + " " + etNewAssignmentStartTime.getText().toString().trim());
        NEW_ASSIGNMENT.setDateStart(startDateTime);

        String endDateTime = MyDateTime.getServerFormatFormAppDateTime(
                etNewAssignmentEndDate.getText().toString().trim() + " " + etNewAssignmentEndTime.getText().toString().trim());
        NEW_ASSIGNMENT.setDateEnd(endDateTime);

        if (NEW_ASSIGNMENT.getCustomerId().isEmpty()) {
            Toast.makeText(this, localized_choose_customer, Toast.LENGTH_SHORT).show();
            return;
        }

        if (NEW_ASSIGNMENT.getProjectId().isEmpty()) {
            Toast.makeText(this, localized_select_project, Toast.LENGTH_SHORT).show();
            return;
        }

        if (NEW_ASSIGNMENT.getProductId().isEmpty() && NEW_ASSIGNMENT.getProjectProductId().isEmpty()) {
            Toast.makeText(this, localized_select_product, Toast.LENGTH_SHORT).show();
            return;
        }

        if (NEW_ASSIGNMENT.getServiceId().isEmpty()) {
            Toast.makeText(this, localized_select_service, Toast.LENGTH_SHORT).show();
            return;
        }

        if (NEW_ASSIGNMENT.getResourceIds().isEmpty()) {
            Toast.makeText(this, localized_select_resources, Toast.LENGTH_SHORT).show();
            return;
        }

        if (NEW_ASSIGNMENT.getAssignmentType().isEmpty()) {
            Toast.makeText(this, localized_select_assignment_type, Toast.LENGTH_SHORT).show();
            return;
        }

        if (NEW_ASSIGNMENT.getProblem().isEmpty()) {
            Toast.makeText(this, localized_describe_problem, Toast.LENGTH_SHORT).show();
            return;
        }

        if (NEW_ASSIGNMENT.getDateStart().isEmpty()) {
            Toast.makeText(this, localized_set_start_date, Toast.LENGTH_SHORT).show();
            return;
        }

        if (NEW_ASSIGNMENT.getDateEnd().isEmpty()) {
            Toast.makeText(this, localized_set_end_date, Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            if(!checkResourcesAvailability()){
                return;
            }
        } catch (Exception ex) {
                ex.printStackTrace();
        }


        NEW_ASSIGNMENT.setAssignmentSourceProcedure("Assignment Creation");

        Log.e(LOG_TAG, "==================== NEW_ASSIGNMENT: \n" + NEW_ASSIGNMENT.toString());

        String prefKey = UUID.randomUUID().toString();
        MyPrefs.setStringWithFileName(PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC, prefKey, NEW_ASSIGNMENT.toString());

        if (MyUtils.isNetworkAvailable()) {
            SendNewAssignment sendNewAssignment = new SendNewAssignment(NewAssignmentActivity.this, prefKey, true);
            sendNewAssignment.execute(prefKey);
        } else {
            Toast.makeText(NewAssignmentActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void showDialogToSelectCustomer() {
        new AlertDialog.Builder(NewAssignmentActivity.this)
                .setMessage(localized_choose_customer)
                .setNeutralButton(localized_new_customer, (dialog, which) -> {
                    dialog.dismiss();

                    Intent intentNewAssignment = new Intent(NewAssignmentActivity.this, NewCustomerActivity.class);
                    intentNewAssignment.putExtra(CONST_IS_FOR_NEW_ASSIGNMENT, true);
                    intentNewAssignment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentNewAssignment);
                })
                .setPositiveButton(localized_existing_customer, (dialog, which) -> {
                    dialog.dismiss();

                    Intent intentNewAssignment = new Intent(NewAssignmentActivity.this, SearchCustomersActivity.class);
                    intentNewAssignment.putExtra(CONST_IS_FOR_NEW_ASSIGNMENT, true);
                    intentNewAssignment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentNewAssignment);
                })
                .show();
    }

    private boolean checkResourcesAvailability() throws ParseException {

        String resourceIds = NEW_ASSIGNMENT.getResourceIds();
        resourceIds = resourceIds.substring(0, resourceIds.length() - 2);
        String[] ResourceList = resourceIds.split(", ");

        String ResourcesNotAvailable = "";

        String startDateTime = MyDateTime.getServerFormatFormAppDateTime(
                etNewAssignmentStartDate.getText().toString().trim() + " 00:00");
        Date ass_date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startDateTime);

        for(String resourceId : ResourceList){
            for(UserPartnerResourceModel upm : USER_PARTNER_RESOURCE_LIST){

                if (upm.getResourceId().equals(resourceId)){
                    List<ResourceLeaveModel> leaves = upm.getLeaves();

                    for(ResourceLeaveModel rlm : leaves){
                        String startDateStr = rlm.getLeaveStart();
                        String endDateStr = rlm.getLeaveEnd();
                        Date DateStart = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
                        Date DateEnd = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
                        if(!ass_date.before(DateStart) && !ass_date.after(DateEnd)){
                            if(ResourcesNotAvailable.length() == 0){
                                ResourcesNotAvailable = upm.getResourceName();
                            }else{
                                ResourcesNotAvailable += "\r\n" + upm.getResourceName();
                            }

                        }
                    }
                }
            }
        }

        if(ResourcesNotAvailable.length() > 0){
            Toast.makeText(this, localized_resources_not_available + "\r\n" + ResourcesNotAvailable, Toast.LENGTH_LONG).show();
            return false;
        }else {
            return true;
        }

    }

}
