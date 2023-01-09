package dc.gtest.vortex.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
//import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.SendNewAssignment;
import dc.gtest.vortex.data.UserPartnerResourcesData;
import dc.gtest.vortex.models.NewAssignmentModel;
import dc.gtest.vortex.models.ResourceLeaveModel;
import dc.gtest.vortex.models.UserPartnerResourceModel;
import dc.gtest.vortex.support.MyDateTime;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_SINGLE_SELECTION;
import static dc.gtest.vortex.support.MyGlobals.KEY_ASSIGNMENT_DATE;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.USER_PARTNER_RESOURCE_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_cancel;
import static dc.gtest.vortex.support.MyLocalization.localized_change_resource;
import static dc.gtest.vortex.support.MyLocalization.localized_end_date;
import static dc.gtest.vortex.support.MyLocalization.localized_end_time;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_connection;
import static dc.gtest.vortex.support.MyLocalization.localized_resource;
import static dc.gtest.vortex.support.MyLocalization.localized_resources_not_available;
import static dc.gtest.vortex.support.MyLocalization.localized_save;
import static dc.gtest.vortex.support.MyLocalization.localized_select_date_for_availability;
import static dc.gtest.vortex.support.MyLocalization.localized_select_resource;
import static dc.gtest.vortex.support.MyLocalization.localized_send_changes;
import static dc.gtest.vortex.support.MyLocalization.localized_set_end_date;
import static dc.gtest.vortex.support.MyLocalization.localized_set_start_date;
import static dc.gtest.vortex.support.MyLocalization.localized_start_date;
import static dc.gtest.vortex.support.MyLocalization.localized_start_time;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_USER_PARTNER_RESOURCES;

public class ChangeResourceActivity extends BaseDrawerActivity  implements View.OnClickListener{

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private TextInputLayout tilNewAssignmentStartDate;
    private EditText etNewAssignmentStartDate;
    private TextInputLayout tilNewAssignmentStartTime;
    private EditText etNewAssignmentStartTime;
    private TextInputLayout tilNewAssignmentEndDate;
    private EditText etNewAssignmentEndDate;
    private TextInputLayout tilNewAssignmentEndTime;
    private EditText etNewAssignmentEndTime;

    private TextView tvNewAssignmentResources;

    private Button btnSendChanges;

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
        getLayoutInflater().inflate(R.layout.content_change_resource, flBaseContainer, true);

        tilNewAssignmentStartDate = findViewById(R.id.tilNewAssignmentStartDate);
        etNewAssignmentStartDate = findViewById(R.id.etNewAssignmentStartDate);
        tilNewAssignmentStartTime = findViewById(R.id.tilNewAssignmentStartTime);
        etNewAssignmentStartTime = findViewById(R.id.etNewAssignmentStartTime);
        tilNewAssignmentEndDate = findViewById(R.id.tilNewAssignmentEndDate);
        etNewAssignmentEndDate = findViewById(R.id.etNewAssignmentEndDate);
        tilNewAssignmentEndTime = findViewById(R.id.tilNewAssignmentEndTime);
        etNewAssignmentEndTime = findViewById(R.id.etNewAssignmentEndTime);

        btnSendChanges = findViewById(R.id.btnSendChanges);
        tvNewAssignmentResources = findViewById(R.id.tvNewAssignmentResources);

        tvNewAssignmentResources.setOnClickListener(this);
        etNewAssignmentStartDate.setOnClickListener(this);
        etNewAssignmentStartTime.setOnClickListener(this);
        etNewAssignmentEndDate.setOnClickListener(this);
        etNewAssignmentEndTime.setOnClickListener(this);
        btnSendChanges.setOnClickListener(this);

        NEW_ASSIGNMENT = new NewAssignmentModel();
        UserPartnerResourcesData.generate(MyPrefs.getString(PREF_DATA_USER_PARTNER_RESOURCES, ""));

        try {

            String StartDate = SELECTED_ASSIGNMENT.getStartDateTime();
            SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            Date SDate = serverDateFormat.parse(StartDate);
            if (StartDate.equals("1900-01-01T00:00:00")){
                SDate = new Date();
            }
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTime(SDate);
            dayStart = cal.get(Calendar.DAY_OF_MONTH);
            monthStart = cal.get(Calendar.MONTH);
            yearStart = cal.get(Calendar.YEAR);
            hourStart = cal.get(Calendar.HOUR_OF_DAY);
            minuteStart = cal.get(Calendar.MINUTE);

            dayEnd = cal.get(Calendar.DAY_OF_MONTH);
            monthEnd = cal.get(Calendar.MONTH);
            yearEnd = cal.get(Calendar.YEAR);
            hourEnd = cal.get(Calendar.HOUR_OF_DAY) + 1;
            minuteEnd = cal.get(Calendar.MINUTE);

            etNewAssignmentStartDate.setText(MyDateTime.get_app_date_from_day_month_year_integers(dayStart, monthStart, yearStart));
            etNewAssignmentStartTime.setText(MyDateTime.get_app_time_from_hour_minute_integers(hourStart, minuteStart));
            etNewAssignmentEndDate.setText(MyDateTime.get_app_date_from_day_month_year_integers(dayEnd, monthEnd, yearEnd));
            etNewAssignmentEndTime.setText(MyDateTime.get_app_time_from_hour_minute_integers(hourEnd, minuteEnd));

        } catch (Exception e) {
            e.printStackTrace();
        }



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
            getSupportActionBar().setTitle(localized_change_resource);
        }

        if (NEW_ASSIGNMENT.getResourceIds().isEmpty()) {
            tvNewAssignmentResources.setText(localized_select_resource);
        } else {
            String resourcesText = localized_resource + ": " + NEW_ASSIGNMENT.getResourceNames();
            tvNewAssignmentResources.setText(resourcesText);
        }

        tilNewAssignmentStartDate.setHint(localized_start_date);  // hint
        tilNewAssignmentStartTime.setHint(localized_start_time);  // hint
        tilNewAssignmentEndDate.setHint(localized_end_date);      // hint
        tilNewAssignmentEndTime.setHint(localized_end_time);      // hint

        btnSendChanges.setText(localized_send_changes);
    }

    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {

            case R.id.tvNewAssignmentResources:
                String startDateTime = MyDateTime.getServerFormatFormAppDateTime(
                        etNewAssignmentStartDate.getText().toString().trim() + " 00:00");
                if(startDateTime.isEmpty()) {
                    Toast.makeText(this, localized_select_date_for_availability, Toast.LENGTH_SHORT).show();
                }else{
                    intent = new Intent(ChangeResourceActivity.this, UserPartnerResourcesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(CONST_SINGLE_SELECTION, true);
                    intent.putExtra(KEY_ASSIGNMENT_DATE, startDateTime);
                    startActivity(intent);
                }

                break;

            case R.id.etNewAssignmentStartDate:

                @SuppressLint("InflateParams")
                View datePickerStartView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

                DatePicker datePickerStart = datePickerStartView.findViewById(R.id.datePicker);
//                datePickerStart.setMinDate(new Date().getTime());

                if (yearStart != -1 && monthStart != -1 && dayStart != -1) {
                    datePickerStart.updateDate(yearStart, monthStart, dayStart);
                }

                new AlertDialog.Builder(ChangeResourceActivity.this)
                        .setView(datePickerStartView)
                        .setNegativeButton(localized_cancel, (dialog, which) -> MyUtils.hideKeypad(ChangeResourceActivity.this, datePickerStartView))
                        .setPositiveButton(localized_save, (dialog, which) -> {

                            MyUtils.hideKeypad(ChangeResourceActivity.this, datePickerStartView);

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

                new AlertDialog.Builder(ChangeResourceActivity.this)
                        .setView(timePickerStartView)
                        .setNegativeButton(localized_cancel, (dialog, which) -> MyUtils.hideKeypad(ChangeResourceActivity.this, timePickerStartView))
                        .setPositiveButton(localized_save, (dialog, which) -> {

                            MyUtils.hideKeypad(ChangeResourceActivity.this, timePickerStartView);

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

                new AlertDialog.Builder(ChangeResourceActivity.this)
                        .setView(datePickerEndView)
                        .setNegativeButton(localized_cancel, (dialog, which) -> MyUtils.hideKeypad(ChangeResourceActivity.this, datePickerEndView))
                        .setPositiveButton(localized_save, (dialog, which) -> {

                            MyUtils.hideKeypad(ChangeResourceActivity.this, datePickerEndView);

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

                new AlertDialog.Builder(ChangeResourceActivity.this)
                        .setView(timePickerEndView)
                        .setNegativeButton(localized_cancel, (dialog, which) -> MyUtils.hideKeypad(ChangeResourceActivity.this, timePickerEndView))
                        .setPositiveButton(localized_save, (dialog, which) -> {

                            MyUtils.hideKeypad(ChangeResourceActivity.this, timePickerEndView);

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

            case R.id.btnSendChanges:
                performSend();
                break;
        }

    }



    private void performSend() {


        String startDateTime = MyDateTime.getServerFormatFormAppDateTime(
                etNewAssignmentStartDate.getText().toString().trim() + " " + etNewAssignmentStartTime.getText().toString().trim());
        NEW_ASSIGNMENT.setDateStart(startDateTime);

        String endDateTime = MyDateTime.getServerFormatFormAppDateTime(
                etNewAssignmentEndDate.getText().toString().trim() + " " + etNewAssignmentEndTime.getText().toString().trim());
        NEW_ASSIGNMENT.setDateEnd(endDateTime);

        String assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();


        NEW_ASSIGNMENT.setAssignmentId(assignmentId);

        if (NEW_ASSIGNMENT.getResourceIds().isEmpty()&& NEW_ASSIGNMENT.getDateEnd().isEmpty() && NEW_ASSIGNMENT.getDateStart().isEmpty()) {
            Toast.makeText(this, localized_select_resource, Toast.LENGTH_SHORT).show();
            return;
        }


        if (NEW_ASSIGNMENT.getDateStart().isEmpty() && !NEW_ASSIGNMENT.getDateEnd().isEmpty()) {
            Toast.makeText(this, localized_set_start_date, Toast.LENGTH_SHORT).show();
            return;
        }

        if (NEW_ASSIGNMENT.getDateEnd().isEmpty()&& !NEW_ASSIGNMENT.getDateStart().isEmpty()) {
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

        Log.e(LOG_TAG, "==================== CHANGE_RESOURCE_DATE: \n" + NEW_ASSIGNMENT.toString());

        //String prefKey = UUID.randomUUID().toString();
        //MyPrefs.setStringWithFileName(PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC, prefKey, NEW_ASSIGNMENT.toString());

        if (MyUtils.isNetworkAvailable()) {
            SendNewAssignment sendNewAssignment = new SendNewAssignment(ChangeResourceActivity.this, "-1", true);
            sendNewAssignment.execute("-1");
        } else {
            Toast.makeText(ChangeResourceActivity.this, localized_no_internet_connection, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean checkResourcesAvailability() throws ParseException {

        String resourceIds = NEW_ASSIGNMENT.getResourceIds();
        if (resourceIds.length()==0){
            resourceIds = SELECTED_ASSIGNMENT.getResourceId();
        }else{
            resourceIds = resourceIds.substring(0, resourceIds.length() - 2);
        }

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
