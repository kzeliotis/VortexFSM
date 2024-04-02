package dc.gtest.vortex.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.SendCheckOut;
import dc.gtest.vortex.api.SendStartTravel;
import dc.gtest.vortex.models.CheckInCheckOutModel;
import dc.gtest.vortex.support.MyDateTime;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;


import static dc.gtest.vortex.support.MyGlobals.CONST_SHOW_PROGRESS_AND_TOAST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.singleAssignmentResult;
import static dc.gtest.vortex.support.MyLocalization.localized_additional_technicians;
import static dc.gtest.vortex.support.MyLocalization.localized_address;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_details;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_cancel_start_travel;
import static dc.gtest.vortex.support.MyLocalization.localized_contact;
import static dc.gtest.vortex.support.MyLocalization.localized_contract;
import static dc.gtest.vortex.support.MyLocalization.localized_custom_fields;
import static dc.gtest.vortex.support.MyLocalization.localized_customer;
import static dc.gtest.vortex.support.MyLocalization.localized_customer_business;
import static dc.gtest.vortex.support.MyLocalization.localized_mobile;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_phone;
import static dc.gtest.vortex.support.MyLocalization.localized_picking_list;
import static dc.gtest.vortex.support.MyLocalization.localized_problem;
import static dc.gtest.vortex.support.MyLocalization.localized_product_description;
import static dc.gtest.vortex.support.MyLocalization.localized_project_description;
import static dc.gtest.vortex.support.MyLocalization.localized_revenue_service;
import static dc.gtest.vortex.support.MyLocalization.localized_service_description;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyLocalization.localized_vat_number;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_HAS_RETURNED_TO_BASE;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_TRAVEL_STARTED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RETURN_TO_BASE_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_START_TRAVEL_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_TRAVEL_TIME;
import static dc.gtest.vortex.support.MyPrefs.PREF_USERID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AssignmentDetailActivity extends BaseDrawerActivity implements OnMapReadyCallback, View.OnClickListener, View.OnLongClickListener {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private FloatingActionButton fabStartTravel;
    private FloatingActionButton fabChangeResource;
    private FloatingActionButton fabReturnToBase;
    private FloatingActionButton fabHistory;
    private FloatingActionButton fabSubAssignments;


    private TextView tvAssignmentId;
    private TextView tvAssignmentTime;
    private TextView tvServiceDescription;
    private TextView tvProductDescription;
    private TextView tvAdditionalTechnicians;
    private TextView tvProblem;
    private TextView tvCustomFields;
    private TextView tvPickingList;
    private TextView tvCustomerName;
    private TextView tvCustomerBusiness;
    private TextView tvCustomerVatNumber;
    private TextView tvCustomerRevenue;
    private TextView tvProjectDescription;
    private TextView tvContact;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvMobile;
    private TextView tvContract;

    private String assignmentId;

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_assignment_details, flBaseContainer, true);

        assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        tvAssignmentTime = findViewById(R.id.tvAssignmentTime);
        tvServiceDescription = findViewById(R.id.tvServiceDescription);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvAdditionalTechnicians = findViewById(R.id.tvAdditionalTechnicians);
        tvProblem = findViewById(R.id.tvProblem);
        tvCustomFields = findViewById(R.id.tvCustomFields);
        tvPickingList = findViewById(R.id.tvPickingList);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerBusiness = findViewById(R.id.tvCustomerBusiness);
        tvCustomerVatNumber = findViewById(R.id.tvCustomerVatNumber);
        tvCustomerRevenue = findViewById(R.id.tvCustomerRevenue);
        tvProjectDescription = findViewById(R.id.tvProjectDescription);
        tvContact = findViewById(R.id.tvContact);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvMobile = findViewById(R.id.tvMobile);
        tvContract = findViewById(R.id.tvContract);

        fabStartTravel = findViewById(R.id.fabStartTravel);
        fabChangeResource = findViewById(R.id.fabChangeResource);
        fabReturnToBase = findViewById(R.id.fabReturnToBase);
        FloatingActionButton fabAssignmentDetails = findViewById(R.id.fabAssignmentDetails);
        fabHistory = findViewById(R.id.fabHistory);
        FloatingActionButton fabGoToMap = findViewById(R.id.fabGoToMap);
        fabSubAssignments = findViewById(R.id.fabSubAssignments);

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false) ) {
            fabStartTravel.setOnClickListener(null);
            fabStartTravel.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
            fabChangeResource.setOnClickListener(null);
            fabChangeResource.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
        } else {
            fabStartTravel.setOnClickListener(AssignmentDetailActivity.this);
            fabStartTravel.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
            fabChangeResource.setOnClickListener(AssignmentDetailActivity.this);
            fabChangeResource.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false) ) {
            fabReturnToBase.show();
            fabStartTravel.hide();
            if (MyPrefs.getBooleanWithFileName(PREF_FILE_HAS_RETURNED_TO_BASE, assignmentId, false) ) {
                fabReturnToBase.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
                fabReturnToBase.setOnClickListener(null);
                fabReturnToBase.setOnLongClickListener(this);
            } else {
                fabReturnToBase.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                fabReturnToBase.setOnClickListener(this);
                fabReturnToBase.setOnLongClickListener(null);
            }
        } else {
            fabReturnToBase.hide();
            fabStartTravel.show();
        }

        tvPhone.setOnClickListener(this);
        tvMobile.setOnClickListener(this);
        fabAssignmentDetails.setOnClickListener(this);
        fabHistory.setOnClickListener(this);
        fabGoToMap.setOnClickListener(this);
        fabSubAssignments.setOnClickListener(this);

        if (assignmentId.contains("-")){
            fabChangeResource.setOnClickListener(null);
            fabChangeResource.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
            fabSubAssignments.setOnClickListener(null);
            fabSubAssignments.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
            fabHistory.setOnClickListener(null);
            fabHistory.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if(singleAssignmentResult){
            if (!MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false)){
                fabStartTravel.performClick();
            }
            fabAssignmentDetails.performClick();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUiTexts();

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false)) {
            fabStartTravel.setOnLongClickListener(null);
        } else if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false)){
            fabStartTravel.setOnLongClickListener(this);
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false) ) {
            fabReturnToBase.show();
            fabStartTravel.hide();
            if (MyPrefs.getBooleanWithFileName(PREF_FILE_HAS_RETURNED_TO_BASE, assignmentId, false) ) {
                fabReturnToBase.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
                fabReturnToBase.setOnClickListener(null);
                fabReturnToBase.setOnLongClickListener(this);
            } else {
                fabReturnToBase.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                fabReturnToBase.setOnClickListener(this);
                fabReturnToBase.setOnLongClickListener(null);
            }
        } else {
            fabReturnToBase.hide();
            fabStartTravel.show();
        }

        if (assignmentId.contains("-")){
            fabChangeResource.setOnClickListener(null);
            fabChangeResource.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
            fabSubAssignments.setOnClickListener(null);
            fabSubAssignments.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
            fabHistory.setOnClickListener(null);
            fabHistory.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_synch, menu);
//        return true;
//    }

   // @Override
   // protected void onPause() {
    //    super.onPause();
   //     if( != null && progressDialog.isShowing())
   //         progressDialog.dismiss();

   // }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {

        String assignmentIdText = localized_assignment_id + ": " + SELECTED_ASSIGNMENT.getAssignmentId();
        String serviceDescription = localized_service_description + ": " + SELECTED_ASSIGNMENT.getServiceDescription();
        String productDescription = localized_product_description + ": " + SELECTED_ASSIGNMENT.getProductDescription();
        String problem = localized_problem + ": " + SELECTED_ASSIGNMENT.getProblem();
        String customFields = localized_custom_fields + ": " + SELECTED_ASSIGNMENT.getCustomFields();
        String pickingList = localized_picking_list + ": " + SELECTED_ASSIGNMENT.getPickingList();
        String customerName = localized_customer + ": " + SELECTED_ASSIGNMENT.getCustomerName();
        String customerBusiness = localized_customer_business + ": " + SELECTED_ASSIGNMENT.getCustomerBusiness();
        String customerVatNumber = localized_vat_number + ": " + SELECTED_ASSIGNMENT.getCustomerVatNumber();
        String customerRevenue = localized_revenue_service + ": " + SELECTED_ASSIGNMENT.getCustomerRevenue();
        String projectDescription = localized_project_description + ": " + SELECTED_ASSIGNMENT.getProjectDescription();
        String contact = localized_contact + ": " + SELECTED_ASSIGNMENT.getContact();
        String address = localized_address + ": " + SELECTED_ASSIGNMENT.getAddress();
        String phone = localized_phone + ": " + SELECTED_ASSIGNMENT.getPhone();
        String mobile = localized_mobile + ": " + SELECTED_ASSIGNMENT.getMobile();
        String additionalTechs = localized_additional_technicians + ": " + SELECTED_ASSIGNMENT.getAdditionalTechnicians();
        String assignmentTime = SELECTED_ASSIGNMENT.getAssignmentTime();
        String contract = localized_contract + ": " + SELECTED_ASSIGNMENT.getContract();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_assignment_details);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        if (SELECTED_ASSIGNMENT.getAdditionalTechnicians().equals("")){
            tvAdditionalTechnicians.setVisibility(View.GONE);
        } else {
            tvAdditionalTechnicians.setVisibility(View.VISIBLE);
            tvAdditionalTechnicians.setText(additionalTechs);
        }

        if (SELECTED_ASSIGNMENT.getContract().equals("")){
            tvContract.setVisibility(View.GONE);
        } else {
            tvContract.setVisibility(View.VISIBLE);
            tvContract.setText(contract);
        }

        tvAssignmentId.setText(assignmentIdText);
        tvAssignmentTime.setText(assignmentTime);
        tvServiceDescription.setText(serviceDescription);
        tvProductDescription.setText(productDescription);
        tvProblem.setText(problem);
        tvCustomFields.setText(customFields);
        tvPickingList.setText(pickingList);
        tvCustomerName.setText(customerName);
        tvCustomerBusiness.setText(customerBusiness);
        tvCustomerVatNumber.setText(customerVatNumber);
        tvCustomerRevenue.setText(customerRevenue);
        tvProjectDescription.setText(projectDescription);
        tvContact.setText(contact);
        tvAddress.setText(address);
        tvPhone.setText(phone);
        tvMobile.setText(mobile);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            double cabLat = Double.parseDouble(SELECTED_ASSIGNMENT.getCabLat());
            double cabLng = Double.parseDouble(SELECTED_ASSIGNMENT.getCabLng());
            LatLng cabinetLatLng = new LatLng(cabLat, cabLng);
            googleMap.addMarker(new MarkerOptions().position(cabinetLatLng).title(SELECTED_ASSIGNMENT.getAssignmentTime()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cabinetLatLng, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.tvPhone:
                if (!SELECTED_ASSIGNMENT.getPhone().isEmpty()) {
                    try {
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + SELECTED_ASSIGNMENT.getPhone()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.tvMobile:
                if (!SELECTED_ASSIGNMENT.getMobile().isEmpty()) {
                    try {
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + SELECTED_ASSIGNMENT.getMobile()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.fabReturnToBase:

                fabReturnToBase.setOnClickListener(null);
                fabReturnToBase.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AssignmentDetailActivity.this, R.color.grey_500)));

                fabReturnToBase.setOnLongClickListener(this);

                MyPrefs.setBooleanWithFileName(PREF_FILE_HAS_RETURNED_TO_BASE, assignmentId, true);

                CheckInCheckOutModel rtnToBase = new CheckInCheckOutModel();

                rtnToBase.setAssignmentId(assignmentId);
                rtnToBase.setReturnToBaseTime(MyDateTime.getCurrentTime());
                rtnToBase.setUserId(MyPrefs.getString(PREF_USERID, ""));

                MyPrefs.setStringWithFileName(PREF_FILE_RETURN_TO_BASE_DATA_TO_SYNC, assignmentId, rtnToBase.toString());

                if (MyUtils.isNetworkAvailable()) {
                    SendCheckOut sendCheckOut = new SendCheckOut(AssignmentDetailActivity.this, assignmentId, CONST_SHOW_PROGRESS_AND_TOAST, true);

                    if (!sendCheckOut.isCheckingOut()) {
                        sendCheckOut.execute();
                    }

                } else {
                    MyDialogs.showOK(AssignmentDetailActivity.this, localized_no_internet_data_saved);
                }


                break;

            case R.id.fabStartTravel:

                fabStartTravel.setOnClickListener(null);
                fabStartTravel.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AssignmentDetailActivity.this, R.color.grey_500)));

                fabChangeResource.setOnClickListener(null);
                fabChangeResource.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_500)));

                fabStartTravel.setOnLongClickListener(this);

                MyPrefs.setBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, true);

                String assignmentData = MyPrefs.getString(PREF_DATA_ASSIGNMENTS, "");

                if (!assignmentData.equals("")) {
                    try {
                        JSONArray jsonArr = new JSONArray(assignmentData);

                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject oneObject = jsonArr.getJSONObject(i);
                            String assignmentId = oneObject.getString("AssignmentId");
                            String masterAssignment = oneObject.getString("MasterAssignment");

                            Log.e(LOG_TAG, "------- assignmentId: " + assignmentId + "-" + masterAssignment);
                            Log.e(LOG_TAG, "------- assignmentId: " + SELECTED_ASSIGNMENT.getAssignmentId());
                            Log.e(LOG_TAG, "--- masterAssignment: " + SELECTED_ASSIGNMENT.getMasterAssignment());

                            if (!masterAssignment.equals("") && masterAssignment.equals(SELECTED_ASSIGNMENT.getMasterAssignment())) {
                                MyPrefs.setBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, true);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

 //               GetCurrentLocation getCurrentLocation = new GetCurrentLocation();
//                getCurrentLocation.getLocation();


                MyPrefs.setStringWithFileName(assignmentId, PREF_START_TRAVEL_TIME, MyDateTime.getCurrentTime());
                MyPrefs.setStringWithFileName(assignmentId, PREF_START_LAT, MyPrefs.getString(PREF_CURRENT_LAT, ""));
                MyPrefs.setStringWithFileName(assignmentId, PREF_START_LNG, MyPrefs.getString(PREF_CURRENT_LNG, ""));

                CheckInCheckOutModel checkInCheckOutModel = new CheckInCheckOutModel();

                checkInCheckOutModel.setAssignmentId(assignmentId);
                checkInCheckOutModel.setStartTravelTime(MyDateTime.getCurrentTime());
                checkInCheckOutModel.setStartLat(MyPrefs.getString(PREF_CURRENT_LAT, ""));
                checkInCheckOutModel.setStartLng(MyPrefs.getString(PREF_CURRENT_LNG, ""));
                checkInCheckOutModel.setUserId(MyPrefs.getString(PREF_USERID, ""));

                Log.e(LOG_TAG, "------- checkInCheckOutModel.toString(): \n" + checkInCheckOutModel);

                MyPrefs.setStringWithFileName(PREF_FILE_START_TRAVEL_DATA_TO_SYNC, assignmentId, checkInCheckOutModel.toString());

                if (MyUtils.isNetworkAvailable() ) {
                    SendStartTravel sendStartTravel = new SendStartTravel(AssignmentDetailActivity.this, assignmentId, CONST_SHOW_PROGRESS_AND_TOAST);
                    sendStartTravel.execute();
                } else {
                    MyDialogs.showOK(AssignmentDetailActivity.this, localized_no_internet_data_saved);
                }

                break;

            case R.id.fabAssignmentDetails:
                intent = new Intent(AssignmentDetailActivity.this, AssignmentActionsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //if(singleAssignmentResult){intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);}
                startActivity(intent);
                break;

            case R.id.fabHistory:
                intent = new Intent(AssignmentDetailActivity.this, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.fabSubAssignments:
                intent = new Intent(AssignmentDetailActivity.this, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("SubAssignments", "true");
                startActivity(intent);
                break;

            case R.id.fabGoToMap:
                intent = new Intent(AssignmentDetailActivity.this, MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.fabChangeResource:
                intent = new Intent(AssignmentDetailActivity.this, ChangeResourceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.fabStartTravel) {
            new AlertDialog.Builder(AssignmentDetailActivity.this)
                    .setMessage(localized_cancel_start_travel)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        fabStartTravel.setOnLongClickListener(null);
                        fabStartTravel.setOnClickListener(AssignmentDetailActivity.this);

                        fabChangeResource.setOnClickListener(AssignmentDetailActivity.this);
                        fabChangeResource.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));

                        fabStartTravel.setBackgroundTintList(ColorStateList.valueOf(
                                ContextCompat.getColor(AssignmentDetailActivity.this, R.color.colorPrimary)));

                        MyPrefs.setBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false);

                        String assignmentData = MyPrefs.getString(PREF_DATA_ASSIGNMENTS, "");

                        if (!assignmentData.equals("")) {
                            try {
                                JSONArray jsonArr = new JSONArray(assignmentData);

                                for (int i = 0; i < jsonArr.length(); i++) {
                                    JSONObject oneObject = jsonArr.getJSONObject(i);
                                    String assignmentId = oneObject.getString("AssignmentId");
                                    String masterAssignment = oneObject.getString("MasterAssignment");

                                    if (!masterAssignment.equals("") && masterAssignment.equals(SELECTED_ASSIGNMENT.getMasterAssignment())) {
                                        MyPrefs.setBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }

        return true;
    }
}
