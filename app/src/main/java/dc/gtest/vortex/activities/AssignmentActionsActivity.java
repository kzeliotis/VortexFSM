package dc.gtest.vortex.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SwitchCompat;

import android.provider.OpenableColumns;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AssignmentAttachmentsRvAdapter;
import dc.gtest.vortex.adapters.MandatoryTasksRvAdapter;
import dc.gtest.vortex.adapters.MySpinnerAdapter;
import dc.gtest.vortex.adapters.PhotosRecyclerViewAdapter;
import dc.gtest.vortex.api.GetAssignmentCost;
import dc.gtest.vortex.api.GetDefaultTechActions;
import dc.gtest.vortex.api.GetReportPreview;
import dc.gtest.vortex.api.GetStatuses;
import dc.gtest.vortex.api.GetZones;
import dc.gtest.vortex.api.SendCheckIn;
import dc.gtest.vortex.api.SendCheckOut;
import dc.gtest.vortex.api.SendMandatoryTasks;
import dc.gtest.vortex.api.SendProductMeasurements;
import dc.gtest.vortex.api.SendUsePTOvernight;
import dc.gtest.vortex.api.SendZonesWithNoMeasurement;
import dc.gtest.vortex.data.MandatoryTasksData;
import dc.gtest.vortex.data.ZonesData;
import dc.gtest.vortex.items.ServicesListActivity;
import dc.gtest.vortex.models.CheckInCheckOutModel;
import dc.gtest.vortex.models.UsePTOvernightModel;
import dc.gtest.vortex.models.ZoneModel;
import dc.gtest.vortex.support.CaptureSignature;
import dc.gtest.vortex.support.MyDateTime;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyImages;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MySynchronize;
import dc.gtest.vortex.support.MyUtils;
import dc.gtest.vortex.support.TakeUploadPhoto;

import static dc.gtest.vortex.support.MyGlobals.CONST_ASSIGNMENT_ATTACHMENTS_FOLDER;
import static dc.gtest.vortex.support.MyGlobals.CONST_ASSIGNMENT_PHOTOS_FOLDER;
import static dc.gtest.vortex.support.MyGlobals.CONST_DO_NOT_FINISH_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.CONST_SHOW_PROGRESS_AND_TOAST;
import static dc.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static dc.gtest.vortex.support.MyGlobals.KEY_REFRESH_INSTALLATIONS;
import static dc.gtest.vortex.support.MyGlobals.KEY_REFRESH_ZONES;
import static dc.gtest.vortex.support.MyGlobals.MANDATORY_TASKS_LIST;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_PICK_ASSIGNMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_PICK_MANDATORY_TASK_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_TAKE_ASSIGNMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_TAKE_MANDATORY_TASK_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.PERMISSIONS_STORAGE;
import static dc.gtest.vortex.support.MyGlobals.PICKFILE_RESULT_CODE;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_CAMERA_FOR_ASSIGNMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_CAMERA_FOR_MANDATORY_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_EXTERNAL_STORAGE_FOR_ASSIGNMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_EXTERNAL_STORAGE_FOR_MANDATORY_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.RESULT_SIGNATURE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.STATUSES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ZONES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ZONES_WITH_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyGlobals.ZONES_WITH_NO_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyGlobals.globalCurrentAttachmentPath;
import static dc.gtest.vortex.support.MyGlobals.globalCurrentPhotoPath;
import static dc.gtest.vortex.support.MyGlobals.globalMandatoryTaskPosition;
import static dc.gtest.vortex.support.MyGlobals.singleAssignmentResult;
import static dc.gtest.vortex.support.MyLocalization.localized_assignmentActions;
import static dc.gtest.vortex.support.MyLocalization.localized_assignment_id;
import static dc.gtest.vortex.support.MyLocalization.localized_attachments_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_calculate_cost;
import static dc.gtest.vortex.support.MyLocalization.localized_cancel;
import static dc.gtest.vortex.support.MyLocalization.localized_changeStatus;
import static dc.gtest.vortex.support.MyLocalization.localized_charged;
import static dc.gtest.vortex.support.MyLocalization.localized_clickToSign;
import static dc.gtest.vortex.support.MyLocalization.localized_commentsSolution;
import static dc.gtest.vortex.support.MyLocalization.localized_complete_measurements;
import static dc.gtest.vortex.support.MyLocalization.localized_consumables_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_default_actions;
import static dc.gtest.vortex.support.MyLocalization.localized_file_size_limit;
import static dc.gtest.vortex.support.MyLocalization.localized_fillComments;
import static dc.gtest.vortex.support.MyLocalization.localized_fillMandatoryTasks;
import static dc.gtest.vortex.support.MyLocalization.localized_fillSignature;
import static dc.gtest.vortex.support.MyLocalization.localized_installations_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_internal_communication;
import static dc.gtest.vortex.support.MyLocalization.localized_mandatory_tasks;
import static dc.gtest.vortex.support.MyLocalization.localized_minimum_payment;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_overnight;
import static dc.gtest.vortex.support.MyLocalization.localized_paid;
import static dc.gtest.vortex.support.MyLocalization.localized_preview_report;
import static dc.gtest.vortex.support.MyLocalization.localized_problem;
import static dc.gtest.vortex.support.MyLocalization.localized_products;
import static dc.gtest.vortex.support.MyLocalization.localized_select;
import static dc.gtest.vortex.support.MyLocalization.localized_select_action;
import static dc.gtest.vortex.support.MyLocalization.localized_select_measurement;
import static dc.gtest.vortex.support.MyLocalization.localized_send_report;
import static dc.gtest.vortex.support.MyLocalization.localized_services;
import static dc.gtest.vortex.support.MyLocalization.localized_status;
import static dc.gtest.vortex.support.MyLocalization.localized_use_pt;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyLocalization.localized_zones;
import static dc.gtest.vortex.support.MyPrefs.PREF_AZURE_CONNECTION_STRING;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_TIME;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_TIME_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_OUT_TIME_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_DEFAULT_TECH_ACTIONS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ATTACHMENT_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CHARGED_AMOUNT_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CHECK_IN_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CHECK_OUT_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_COMMENTS_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_WARNING_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_SCANNED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_SELECTED_STATUS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_SEND_REPORT_VALUE_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_SIGNATURENAME;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IMAGE_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_OVERNIGHT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_PT_STARTED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_TRAVEL_STARTED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_WORK_STARTED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_MANDATORY_TASKS_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_MANDATORY_TASKS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NOTES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PAID_AMOUNT_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_USE_PT_OVERNIGHT_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_WITH_MEASUREMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_WITH_NO_MEASUREMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_HIDE_INTERNAL_NOTES;
import static dc.gtest.vortex.support.MyPrefs.PREF_MANDATORY_SIGNATURE;
import static dc.gtest.vortex.support.MyPrefs.PREF_ONLY_WIFI;
import static dc.gtest.vortex.support.MyPrefs.PREF_SCROLLABLE_PROBLEM_DESCRIPTION;
import static dc.gtest.vortex.support.MyPrefs.PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_GET_ASSIGNMENT_COST;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_INSTALLATIONS_BUTTON;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_SEND_REPORT_CHECKBOX;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_START_WORK;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_USE_PT_OVERNIGHT_BUTTONS;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_ZONE_PRODUCTS_BUTTON;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_TRAVEL_TIME;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_WORK_TIME;
import static dc.gtest.vortex.support.MyPrefs.PREF_USERID;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AssignmentActionsActivity extends BaseDrawerActivity implements View.OnClickListener, View.OnLongClickListener {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private RecyclerView rvMandatoryTasks;
    private TextView tvAssignmentId;
    private TextView tvProblem;
    private TextView tvStatusTitle;
    private TextView tvSignatureImage;
    private TextView tvSignatureName;
    private TextView tvSolutionTitle;
    private TextView tvInternalNotesTitle;
    private TextView tvMandatoryTasksTitle;
    private TextView tvChargedAmountTitle;
    private TextView tvPaidAmountTitle;
    private EditText etCommentsSolution;
    private EditText etNotes;
    private EditText etChargedAmount;
    private EditText etPaidAmount;
    private EditText etWarning;
    private Spinner spStatus;
    private Button btnCheckIn;
    private Button btnCheckOut;
    private Button btnStartWork;
    private Button btnGetCost;
    private SwitchCompat swUsePt;
    private Button btnOvernight;
    private Button btnToProducts;
    private Button btnToConsumables;
    private Button btnToServices;
    private Button btnZones;
    private Button btnTakePhoto;
    private Button btnAddAttachment;
    private Button btnPreviewReport;
    private Button btnDetAttachments;
    private LinearLayout llPayment;
    private FrameLayout flSignatureImage;
    private Button btnInstallations;
    private TextView tvSendReport;
    private CheckBox chkSendReportFile;
    private ScrollView scrollView1;

    private MandatoryTasksRvAdapter mandatoryTasksRvAdapter;
    private PhotosRecyclerViewAdapter photosRecyclerViewAdapter;
    private AssignmentAttachmentsRvAdapter attachmentsRvAdapter;

    private String assignmentId;
    private String imagePath = "";
    private String signatureName = "";
    private String signatureEmail = "";

    private final List<String> PHOTO_ITEMS = new ArrayList<>();
    private final List<String> ATTACHMENT_ITEMS = new ArrayList<>();

    private boolean isSigned = false;
    private boolean resetStatus = false;
    private boolean hideInternalNotes = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_assignment_actions, flBaseContainer, true);

        assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();

        hideInternalNotes = MyPrefs.getBoolean(PREF_HIDE_INTERNAL_NOTES, false);

        PHOTO_ITEMS.clear();
        String photoFolderName = assignmentId + CONST_ASSIGNMENT_PHOTOS_FOLDER;
        File path = new File(getExternalFilesDir(null) + File.separator + photoFolderName);
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                PHOTO_ITEMS.add(String.valueOf(file));
            }
        }

        ATTACHMENT_ITEMS.clear();
        String attachmentsFolderName = assignmentId + CONST_ASSIGNMENT_ATTACHMENTS_FOLDER;
        File _path = new File(getExternalFilesDir(null) + File.separator + attachmentsFolderName);
        if (_path.exists()) {
            File[] files = _path.listFiles();
            for (File file : files) {
                ATTACHMENT_ITEMS.add(String.valueOf(file));
            }
        }

        rvMandatoryTasks = findViewById(R.id.rvMandatoryTasks);
        RecyclerView rvProjectPhotos = findViewById(R.id.rvProjectPhotos);
        RecyclerView rvAttachments = findViewById(R.id.rvDetAttachments);
        etCommentsSolution = findViewById(R.id.etCommentsSolution);
        //etCommentsSolution.setMovementMethod(LinkMovementMethod.getInstance());
        etNotes = findViewById(R.id.etNotes);
        etChargedAmount = findViewById(R.id.etChargedAmount);
        etPaidAmount = findViewById(R.id.etPaidAmount);
        etWarning = findViewById(R.id.etWarning);
        flSignatureImage = findViewById(R.id.flSignatureImage);
        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        scrollView1 = findViewById(R.id.ScrollView1);
        tvProblem = findViewById(R.id.tvProblem);
        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        btnCheckIn = findViewById(R.id.btnCheckIn);
        btnCheckOut = findViewById(R.id.btnCheckOut);
        btnStartWork = findViewById(R.id.btnStartWork);
        btnGetCost = findViewById(R.id.btnGetCost);
        LinearLayout llUsePtOvernightButtons = findViewById(R.id.llUsePtOvernightButtons);
        swUsePt = findViewById(R.id.swUsePt);
        btnOvernight = findViewById(R.id.btnOvernight);
        btnToProducts = findViewById(R.id.btnToProducts);
        btnDetAttachments = findViewById(R.id.btnDetAttachments);
        btnToConsumables = findViewById(R.id.btnToConsumables);
        btnToServices = findViewById(R.id.btnToServices);
        btnZones = findViewById(R.id.btnZones);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnAddAttachment = findViewById(R.id.btnAddAttachment);
        llPayment = findViewById(R.id.llPayment);
        tvSignatureImage = findViewById(R.id.tvSignatureImage);
        tvSignatureName = findViewById(R.id.tvSignatureName);
        tvMandatoryTasksTitle = findViewById(R.id.tvMandatoryTasksTitle);
        tvChargedAmountTitle = findViewById(R.id.tvChargedAmountTitle);
        tvPaidAmountTitle = findViewById(R.id.tvPaidAmountTitle);
        spStatus = findViewById(R.id.spStatus);
        tvSolutionTitle = findViewById(R.id.tvSolutionTitle);
        tvInternalNotesTitle = findViewById(R.id.tvInternalNotesTitle);
        btnInstallations = findViewById(R.id.btnInstallations);
        btnPreviewReport = findViewById(R.id.btnPreviewReport);
        tvSendReport = findViewById(R.id.tvSendReport);
        chkSendReportFile = findViewById(R.id.chkSendReport);

        if (MyPrefs.getBoolean(PREF_SCROLLABLE_PROBLEM_DESCRIPTION, false)){

            tvProblem.setMaxLines(6);

            scrollView1.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    tvProblem.getParent().requestDisallowInterceptTouchEvent(false);

                    return false;
                }
            });

            tvProblem.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    tvProblem.getParent().requestDisallowInterceptTouchEvent(true);

                    return false;
                }
            });
        }


        rvProjectPhotos.setLayoutManager(new GridLayoutManager(AssignmentActionsActivity.this, 3, GridLayoutManager.VERTICAL, false));
        photosRecyclerViewAdapter = new PhotosRecyclerViewAdapter(PHOTO_ITEMS, AssignmentActionsActivity.this);
        rvProjectPhotos.setAdapter(photosRecyclerViewAdapter);

        attachmentsRvAdapter = new AssignmentAttachmentsRvAdapter(ATTACHMENT_ITEMS, AssignmentActionsActivity.this);
        rvAttachments.setAdapter(attachmentsRvAdapter);


        if (MyPrefs.getBoolean(PREF_SHOW_START_WORK, false)) {
            btnStartWork.setVisibility(View.VISIBLE);
        }

        if (MyPrefs.getBoolean(PREF_SHOW_INSTALLATIONS_BUTTON, false)) {
            btnInstallations.setVisibility(View.VISIBLE);
        }

        if (MyPrefs.getBoolean(PREF_SHOW_USE_PT_OVERNIGHT_BUTTONS, false)) {
            llUsePtOvernightButtons.setVisibility(View.VISIBLE);
        }

        if (MyPrefs.getBoolean(PREF_SHOW_ZONE_PRODUCTS_BUTTON, false)) {
            btnZones.setVisibility(View.VISIBLE);
        }

        if (MyPrefs.getBoolean(PREF_SHOW_GET_ASSIGNMENT_COST,  false)) {
            btnGetCost.setVisibility(View.VISIBLE);
        }

        if (MyPrefs.getBoolean(PREF_SHOW_SEND_REPORT_CHECKBOX,  false)) {
            tvSendReport.setVisibility(View.VISIBLE);
            chkSendReportFile.setVisibility(View.VISIBLE);
        }

        if (assignmentId.contains("-")) {
            btnStartWork.setVisibility(View.GONE);
            btnGetCost.setVisibility(View.GONE);
            btnToProducts.setVisibility(View.GONE);
            btnDetAttachments.setVisibility(View.GONE);
            btnToConsumables.setVisibility(View.GONE);
            btnToServices.setVisibility(View.GONE);
            btnZones.setVisibility(View.GONE);
            btnInstallations.setVisibility(View.GONE);
            btnTakePhoto.setVisibility(View.GONE);
            btnAddAttachment.setVisibility(View.GONE);
        }

        setupStatusesSpinner();

        tvInternalNotesTitle.setEnabled(true);

        // Start: Hide Mandatory Tasks view if there is no Mandatory task
        if (SELECTED_ASSIGNMENT.getMandatoryTasks().length() > 0) {
            tvMandatoryTasksTitle.setVisibility(View.VISIBLE);
            rvMandatoryTasks.setVisibility(View.VISIBLE);
        } else {
            tvMandatoryTasksTitle.setVisibility(View.GONE);
            rvMandatoryTasks.setVisibility(View.GONE);
        }
        // End: Hide Mandatory Tasks view if there is no Mandatory task

        if (SELECTED_ASSIGNMENT.getInstallationWarning().length() > 0) {
            etWarning.setVisibility(View.VISIBLE);
        } else {
            etWarning.setVisibility(View.GONE);
        }

        MandatoryTasksData.generate();

        mandatoryTasksRvAdapter = new MandatoryTasksRvAdapter(MANDATORY_TASKS_LIST, AssignmentActionsActivity.this, assignmentId, localized_select_measurement);
        rvMandatoryTasks.setAdapter(mandatoryTasksRvAdapter);


        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false)) {
            btnCheckIn.setEnabled(true);
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false) &&
                MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_WORK_STARTED, assignmentId, false)) {
            btnStartWork.setEnabled(true);
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false) &&
                MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {
            btnGetCost.setEnabled(true);
        }

//        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false) &&
//                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
//                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_PT_FINISHED, assignmentId, false)) {
//            swUsePt.setEnabled(true);
//        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false)) {
            swUsePt.setEnabled(true);
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_OVERNIGHT, assignmentId, false)) {
            btnOvernight.setEnabled(true);
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false)){
            String chkIn = MyPrefs.getStringWithFileName(PREF_CHECK_IN_TIME_FOR_SHOW, assignmentId, "");
            if (!chkIn.isEmpty()){
                btnCheckIn.setText(chkIn);
            }
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)){
            String chkOut = MyPrefs.getStringWithFileName(PREF_CHECK_OUT_TIME_FOR_SHOW, assignmentId, "");
            if (!chkOut.isEmpty()){
                btnCheckOut.setText(chkOut);
            }
        }


        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {

            btnCheckOut.setEnabled(true);
            spStatus.setEnabled(true);
            etCommentsSolution.setEnabled(true);
            etNotes.setEnabled(true);
            tvInternalNotesTitle.setEnabled(true);
            tvSolutionTitle.setEnabled(true);
            btnTakePhoto.setEnabled(true);
            btnAddAttachment.setEnabled(true);
            etChargedAmount.setEnabled(true);
            etPaidAmount.setEnabled(true);
            tvSignatureImage.setEnabled(true);

            rvMandatoryTasks.setBackgroundResource(R.drawable.rounded_layout_blue);
            llPayment.setBackgroundResource(R.drawable.rounded_layout_white);
            flSignatureImage.setBackgroundResource(R.drawable.rounded_layout_blue);
        }

        if (SELECTED_ASSIGNMENT.getLockStatusChange().equals("1")){
            spStatus.setEnabled(false);
        }

        swUsePt.setChecked(MyPrefs.getBooleanWithFileName(PREF_FILE_IS_PT_STARTED, assignmentId, false));

        swUsePt.setOnCheckedChangeListener((buttonView, isChecked) -> {

            Log.e(LOG_TAG, "------------------------------------------------------------ inside onCheckedChanged.");

            UsePTOvernightModel usePTOvernightModel = new UsePTOvernightModel();

            if (isChecked) {
                usePTOvernightModel.setAssignmentId(assignmentId);
                usePTOvernightModel.setTransitDateTime(MyDateTime.getCurrentTime());
                usePTOvernightModel.setTransitLat(MyPrefs.getString(PREF_CURRENT_LAT, ""));
                usePTOvernightModel.setTransitLon(MyPrefs.getString(PREF_CURRENT_LNG, ""));
                usePTOvernightModel.setTransitStatus(1);
                usePTOvernightModel.setOvernight(false);

                MyPrefs.setBooleanWithFileName(PREF_FILE_IS_PT_STARTED, assignmentId, true);

            } else {
                usePTOvernightModel.setAssignmentId(assignmentId);
                usePTOvernightModel.setTransitDateTime(MyDateTime.getCurrentTime());
                usePTOvernightModel.setTransitLat(MyPrefs.getString(PREF_CURRENT_LAT, ""));
                usePTOvernightModel.setTransitLon(MyPrefs.getString(PREF_CURRENT_LNG, ""));
                usePTOvernightModel.setTransitStatus(0);
                usePTOvernightModel.setOvernight(false);

                MyPrefs.setBooleanWithFileName(PREF_FILE_IS_PT_STARTED, assignmentId, false);

//                MyPrefs.setBooleanWithFileName(PREF_FILE_IS_PT_FINISHED, assignmentId, true);
//                swUsePt.setEnabled(false);
            }

            String prefKey = UUID.randomUUID().toString();

            MyPrefs.setStringWithFileName(PREF_FILE_USE_PT_OVERNIGHT_FOR_SYNC, prefKey, usePTOvernightModel.toString());

            SendUsePTOvernight sendUsePTOvernight = new SendUsePTOvernight();
            sendUsePTOvernight.execute(prefKey);
        });

        btnCheckIn.setOnClickListener(this);
        btnCheckOut.setOnClickListener(this);
        btnStartWork.setOnClickListener(this);
        btnOvernight.setOnClickListener(this);
        btnToProducts.setOnClickListener(this);
        btnDetAttachments.setOnClickListener(this);
        btnToConsumables.setOnClickListener(this);
        btnToServices.setOnClickListener(this);
        btnZones.setOnClickListener(this);
        btnZones.setOnLongClickListener(this);
        btnTakePhoto.setOnClickListener(this);
        btnAddAttachment.setOnClickListener(this);
        tvSignatureImage.setOnClickListener(this);
        btnGetCost.setOnClickListener(this);
        btnInstallations.setOnClickListener(this);
        btnInstallations.setOnLongClickListener(this);
        btnPreviewReport.setOnClickListener(this);

        tvMandatoryTasksTitle.setOnLongClickListener(this);
        //etCommentsSolution.setOnLongClickListener(this);
        //etNotes.setOnLongClickListener(this);
        tvSolutionTitle.setOnLongClickListener(this);
        tvInternalNotesTitle.setOnLongClickListener(this);

        if(singleAssignmentResult){
            singleAssignmentResult = false;
            MyPrefs.setBooleanWithFileName(PREF_FILE_IS_SCANNED, assignmentId, true);
            if(MyPrefs.getBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, false) &&
                    !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false)){
                btnCheckIn.performClick();
            }else if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                    !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)){
                btnCheckOut.performClick();
            }
        } else {
            MyPrefs.setBooleanWithFileName(PREF_FILE_IS_SCANNED, assignmentId, false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUiTexts();

        etCommentsSolution.setText(MyPrefs.getStringWithFileName(PREF_FILE_COMMENTS_FOR_SHOW, assignmentId, SELECTED_ASSIGNMENT.getCommentsSolution()));

        etWarning.setText(MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_WARNING_FOR_SHOW, assignmentId, SELECTED_ASSIGNMENT.getInstallationWarning()));

        etNotes.setText(MyPrefs.getStringWithFileName(PREF_FILE_NOTES_FOR_SHOW, assignmentId, SELECTED_ASSIGNMENT.getNotes()));
        //etNotes.setText(SELECTED_ASSIGNMENT.getNotes());
        etNotes.setMovementMethod(LinkMovementMethod.getInstance());
        if(hideInternalNotes){
            etNotes.setText("");
            etNotes.setMinLines(0);
            etNotes.setInputType(InputType.TYPE_CLASS_TEXT);
            etNotes.setInputType(InputType.TYPE_NULL);
        }
        etChargedAmount.setText(MyPrefs.getStringWithFileName(PREF_FILE_CHARGED_AMOUNT_FOR_SHOW, assignmentId, SELECTED_ASSIGNMENT.getChargedAmount()));
        etPaidAmount.setText(MyPrefs.getStringWithFileName(PREF_FILE_PAID_AMOUNT_FOR_SHOW, assignmentId, SELECTED_ASSIGNMENT.getPaidAmount()));
        tvSignatureName.setText(MyPrefs.getStringWithFileName(PREF_FILE_SIGNATURENAME, assignmentId, SELECTED_ASSIGNMENT.getSignatureName()));
        String selectedStatus = MyPrefs.getStringWithFileName(PREF_FILE_SELECTED_STATUS, assignmentId, "");
        if (!selectedStatus.isEmpty()) {
            spStatus.setSelection(Integer.parseInt(selectedStatus));
        }

        photosRecyclerViewAdapter.notifyDataSetChanged();

//        Log.e(LOG_TAG, "-------------------------------------- onResume.");
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyPrefs.setStringWithFileName(PREF_FILE_COMMENTS_FOR_SHOW, assignmentId, etCommentsSolution.getText().toString().trim());
        String internalNotes = etNotes.getText().toString().trim();
        if(!internalNotes.isEmpty()){
            MyPrefs.setStringWithFileName(PREF_FILE_NOTES_FOR_SHOW, assignmentId, internalNotes);
        }
        MyPrefs.setStringWithFileName(PREF_FILE_CHARGED_AMOUNT_FOR_SHOW, assignmentId, etChargedAmount.getText().toString().trim());
        MyPrefs.setStringWithFileName(PREF_FILE_PAID_AMOUNT_FOR_SHOW, assignmentId, etPaidAmount.getText().toString().trim());
        MyPrefs.setStringWithFileName(PREF_FILE_MANDATORY_TASKS_FOR_SHOW, assignmentId, MANDATORY_TASKS_LIST.toString());
        MyPrefs.setStringWithFileName(PREF_FILE_SIGNATURENAME, assignmentId, tvSignatureName.getText().toString().trim());
        if(resetStatus){
            MyPrefs.setStringWithFileName(PREF_FILE_SELECTED_STATUS, assignmentId, "");
            resetStatus = false;
        }else{
            MyPrefs.setStringWithFileName(PREF_FILE_SELECTED_STATUS, assignmentId, String.valueOf(spStatus.getSelectedItemPosition()));
        }

        MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATION_WARNING_FOR_SHOW, assignmentId, etWarning.getText().toString().trim());

//        Log.e(LOG_TAG, "----------------onPause MANDATORY_TASKS_LIST: \n" + MANDATORY_TASKS_LIST);
    }

    @Override
    public void onBackPressed(){
        resetStatus = true;
        finish();
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_assignmentActions);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        String assignmentIdText = localized_assignment_id + ": " + assignmentId;
        tvAssignmentId.setText(assignmentIdText);

        String problemText = localized_problem + ": " + SELECTED_ASSIGNMENT.getProblem();
        tvProblem.setText(problemText);
        if (MyPrefs.getBoolean(PREF_SCROLLABLE_PROBLEM_DESCRIPTION, false)){
            tvProblem.setMovementMethod(new ScrollingMovementMethod());
        }


        String SignatureName = SELECTED_ASSIGNMENT.getSignatureName();

        if (isSigned) {
            tvSignatureImage.setText("");
            tvSignatureName.setText(SignatureName);

        } else {
            tvSignatureImage.setText(localized_clickToSign);
            tvSignatureName.setText("");
        }

        swUsePt.setText(localized_use_pt);
        btnOvernight.setText(localized_overnight);
        tvStatusTitle.setText(localized_status);
        tvMandatoryTasksTitle.setText(localized_mandatory_tasks);
        tvChargedAmountTitle.setText(localized_charged);
        tvPaidAmountTitle.setText(localized_paid);
        etCommentsSolution.setHint(localized_commentsSolution);
        etNotes.setHint(localized_internal_communication);
        tvSolutionTitle.setText(localized_commentsSolution);
        tvInternalNotesTitle.setText(localized_internal_communication);
        btnToProducts.setText(localized_products);
        btnDetAttachments.setText(localized_attachments_caps);
        btnToConsumables.setText(localized_consumables_caps);
        btnToServices.setText(localized_services);
        btnZones.setText(localized_zones);
        btnGetCost.setText(localized_calculate_cost);
        btnInstallations.setText(localized_installations_caps);
        btnPreviewReport.setText(localized_preview_report);
        tvSendReport.setText(localized_send_report);

    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.btnZones:
                Intent intent = new Intent(AssignmentActionsActivity.this, ZonesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_REFRESH_ZONES,  true);
                startActivity(intent);
                break;

            case R.id.btnInstallations:
                Intent intentInstallations = new Intent(AssignmentActionsActivity.this, ProjectInstallationActivity.class);
                intentInstallations.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentInstallations.putExtra(KEY_REFRESH_INSTALLATIONS,  true);
                startActivity(intentInstallations);
                break;

            case R.id.tvMandatoryTasksTitle:
                boolean isCheckedOut = MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false);
                if(isCheckedOut){
                    MyPrefs.setStringWithFileName(PREF_FILE_MANDATORY_TASKS_FOR_SYNC, assignmentId, MANDATORY_TASKS_LIST.toString());

                    SendMandatoryTasks sendMandatoryTasks = new SendMandatoryTasks(AssignmentActionsActivity.this);
                    sendMandatoryTasks.execute(assignmentId, "true");
//                } else {
//                    MandatoryTasksData.generate(true);
//                    rvMandatoryTasks.getAdapter().notifyDataSetChanged();
                }
                break;

            case R.id.tvInternalNotesTitle:
                String internalNotes = etNotes.getText().toString().trim();


                if(hideInternalNotes){
                    MyPrefs.setBoolean(PREF_HIDE_INTERNAL_NOTES, false);
                    hideInternalNotes = false;
                    etNotes.setMinLines(5);
                    etNotes.setTextColor(Color.BLACK);
//                    etNotes.setFocusable(true);
                    etNotes.setInputType(InputType.TYPE_CLASS_TEXT);
                    String int_notes = MyPrefs.getStringWithFileName(PREF_FILE_NOTES_FOR_SHOW, assignmentId, "");
                    if(etNotes.isEnabled()){
                        etNotes.setText(int_notes);
                    }else{
                        etNotes.setEnabled(true);
                        etNotes.setText(int_notes);
                        etCommentsSolution.requestFocus();
                        //etNotes.setEnabled(false);
                    }
                    etNotes.setTextColor(Color.BLACK);
                }else{
                    MyPrefs.setBoolean(PREF_HIDE_INTERNAL_NOTES, true);
                    hideInternalNotes = true;
                    if(!MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)){
                        MyPrefs.setStringWithFileName(PREF_FILE_NOTES_FOR_SHOW, assignmentId, internalNotes);
                    }
                    etNotes.setText("");
                    etNotes.setMinLines(0);
                    //etNotes.setTextColor(Color.TRANSPARENT);
                    etNotes.setInputType(InputType.TYPE_NULL);
                }

                break;

            case R.id.tvSolutionTitle:

            String defautActions = MyPrefs.getString(PREF_DATA_DEFAULT_TECH_ACTIONS, "");

            if (defautActions.isEmpty() || defautActions.equals("[]")){
                if (MyUtils.isNetworkAvailable()){
                    GetDefaultTechActions getDefaultTechActions = new GetDefaultTechActions();
                    getDefaultTechActions.execute();
                }
            }

            if (!defautActions.isEmpty() || !defautActions.equals("[]")){
                @SuppressLint("InflateParams")
                LinearLayout llNewAttributeValue = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                final TextView tvDialogSpinnerTitle = llNewAttributeValue.findViewById(R.id.tvDialogSpinnerTitle);
                final Spinner spTechActions = llNewAttributeValue.findViewById(R.id.spDialog);

                tvDialogSpinnerTitle.setText(localized_default_actions);

                try {
                    JSONArray jArray = new JSONArray(defautActions);

                    String[] techActionsArray = new String[jArray.length() + 1];
                    techActionsArray[0] = localized_select_action;

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        techActionsArray[i+1] = oneObject.getString("Action");
                    }

                    if (spTechActions != null) {
                        spTechActions.setAdapter(new MySpinnerAdapter(AssignmentActionsActivity.this, techActionsArray));
                    }

                    new androidx.appcompat.app.AlertDialog.Builder(AssignmentActionsActivity.this)
                            .setView(llNewAttributeValue)
                            .setNegativeButton(localized_cancel, null)
                            .setPositiveButton(localized_select, (dialog, which) -> {
                                if (spTechActions != null) {
                                    if (spTechActions.getSelectedItemPosition() != 0) {
                                        String comments = etCommentsSolution.getText().toString().trim();
                                        if(comments.length() > 0){
                                            comments += "\r\n" + spTechActions.getSelectedItem().toString();
                                        } else {
                                            comments = spTechActions.getSelectedItem().toString();
                                        }
                                        etCommentsSolution.setText(comments);
                                    }
                                }
                            })
                            .show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            break;
        }
       return true;
    }


    @Override
    public void onClick(View v) {

        Intent intent;
        CheckInCheckOutModel checkInCheckOutModel;
        String btn = "";
        try {
             btn = ((Button) v).getText().toString();
        } catch (Exception ex){

        }
        FirebaseCrashlytics.getInstance().log("v.getId: " + btn + " -UserId: " + MyPrefs.getString(PREF_USERID, "") + " -Url: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));


        switch (v.getId()) {

            case R.id.btnDetAttachments:
                intent = new Intent(AssignmentActionsActivity.this, AttachmentsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btnPreviewReport:
                GetReportPreview getReportPreview = new GetReportPreview(AssignmentActionsActivity.this, assignmentId, "", "", "", "");
                getReportPreview.execute();
                break;

            case R.id.btnToProducts:
                intent = new Intent(AssignmentActionsActivity.this, ProductsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btnToConsumables:
                intent = new Intent(AssignmentActionsActivity.this, AddedConsumablesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btnToServices:
                intent = new Intent(AssignmentActionsActivity.this, ServicesListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btnZones:
                //btnZones.setOnLongClickListener(this);
                intent = new Intent(AssignmentActionsActivity.this, ZonesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btnInstallations:
                //btnZones.setOnLongClickListener(this);
                intent = new Intent(AssignmentActionsActivity.this, ProjectInstallationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btnTakePhoto:

                new AlertDialog.Builder(this)
                        .setNeutralButton("Gallery",(dialog, which) -> {
                            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (permission != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE_FOR_ASSIGNMENT_PHOTO);
                            } else {
                                pickAssignmentPhotoFromStorage(this);
                            }
                        })
                        .setPositiveButton("Camera", (dialog, which) -> {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_DENIED) {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_FOR_ASSIGNMENT_PHOTO);
                            } else {
                                takeAssignmentPhotoWithCamera(AssignmentActionsActivity.this);
                            }
                        })
                        .show();
                break;

            case R.id.btnAddAttachment:

                int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, PICKFILE_RESULT_CODE);
                } else {
                    pickFileFromStorage(this);
                }

                break;

            case R.id.tvSignatureImage:
                if(!hideInternalNotes) {etNotes.setVisibility(View.GONE);}
                intent = new Intent(AssignmentActionsActivity.this, CaptureSignature.class);
                startActivityForResult(intent, RESULT_SIGNATURE);
                break;

            case R.id.btnCheckIn:
                MyPrefs.setBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, true);

                btnCheckIn.setEnabled(false);

                String chkIn = "CHECK IN" + System.getProperty("line.separator") + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(new Date());
                MyPrefs.setStringWithFileName(PREF_CHECK_IN_TIME_FOR_SHOW, assignmentId, chkIn);
                btnCheckIn.setText(chkIn);

                btnCheckOut.setEnabled(true);
                btnStartWork.setEnabled(true);
//                swUsePt.setEnabled(false);
                spStatus.setEnabled(true);
                if (SELECTED_ASSIGNMENT.getLockStatusChange().equals("1")){
                    spStatus.setEnabled(false);
                }
                etCommentsSolution.setEnabled(true);
                etNotes.setEnabled(true);
                tvInternalNotesTitle.setEnabled(true);
                tvSolutionTitle.setEnabled(true);
                btnTakePhoto.setEnabled(true);
                btnAddAttachment.setEnabled(true);
                etChargedAmount.setEnabled(true);
                etPaidAmount.setEnabled(true);
                tvSignatureImage.setEnabled(true);
                btnGetCost.setEnabled(true);

                rvMandatoryTasks.setBackgroundResource(R.drawable.rounded_layout_blue);
                llPayment.setBackgroundResource(R.drawable.rounded_layout_white);
                flSignatureImage.setBackgroundResource(R.drawable.rounded_layout_blue);

                // refresh recycler view to enable fields
                mandatoryTasksRvAdapter.notifyDataSetChanged();

                MyPrefs.setStringWithFileName(assignmentId, PREF_CHECK_IN_TIME, MyDateTime.getCurrentTime());
                MyPrefs.setStringWithFileName(assignmentId, PREF_CHECK_IN_LAT, MyPrefs.getString(PREF_CURRENT_LAT, ""));
                MyPrefs.setStringWithFileName(assignmentId, PREF_CHECK_IN_LNG, MyPrefs.getString(PREF_CURRENT_LNG, ""));

                checkInCheckOutModel = new CheckInCheckOutModel();

                checkInCheckOutModel.setAssignmentId(assignmentId);
                checkInCheckOutModel.setStartTravelTime(MyPrefs.getStringWithFileName(assignmentId, PREF_START_TRAVEL_TIME, ""));
                checkInCheckOutModel.setCheckInTime(MyDateTime.getCurrentTime());
                checkInCheckOutModel.setStartLat(MyPrefs.getStringWithFileName(assignmentId, PREF_START_LAT, ""));
                checkInCheckOutModel.setStartLng(MyPrefs.getStringWithFileName(assignmentId, PREF_START_LNG, ""));
                checkInCheckOutModel.setCheckInLat(MyPrefs.getString(PREF_CURRENT_LAT, ""));
                checkInCheckOutModel.setCheckInLng(MyPrefs.getString(PREF_CURRENT_LNG, ""));
                checkInCheckOutModel.setUserId(MyPrefs.getString(PREF_USERID, ""));

//                Log.e(LOG_TAG, "------- checkInCheckOutModel.toString(): \n" + checkInCheckOutModel.toString());

                MyPrefs.setStringWithFileName(PREF_FILE_CHECK_IN_DATA_TO_SYNC, assignmentId, checkInCheckOutModel.toString());

                if (MyUtils.isNetworkAvailable()) {
                    SendCheckIn sendCheckIn = new SendCheckIn(AssignmentActionsActivity.this, assignmentId, CONST_SHOW_PROGRESS_AND_TOAST);
                    sendCheckIn.execute();
                } else {
                    MyDialogs.showOK(AssignmentActionsActivity.this, localized_no_internet_data_saved);
                }

                break;

            case R.id.btnCheckOut:
                String solution = etCommentsSolution.getText().toString().trim();
                String notes = etNotes.getText().toString().trim();

                String sendreport = "0";
                if(!chkSendReportFile.isShown()) {
                    sendreport = "1";
                }else{
                    sendreport = chkSendReportFile.isChecked() ? "1" : "0";
                }

                if (hideInternalNotes){
                    notes = MyPrefs.getStringWithFileName(PREF_FILE_NOTES_FOR_SHOW, assignmentId, "");
                }
                String chargedAmount = etChargedAmount.getText().toString();
                String paidAmount = etPaidAmount.getText().toString();

                boolean areAllRequiredFieldsFilled = true;

                if (solution.isEmpty() && !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_SCANNED, assignmentId, false)) {
                    areAllRequiredFieldsFilled = false;
                    MyDialogs.showOK(AssignmentActionsActivity.this, localized_fillComments);
                }

                FirebaseCrashlytics.getInstance().log("GetSelectedStatus" + " -UserId: " + MyPrefs.getString(PREF_USERID, "") + " -Url: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));
                String selectedStatusId = "0";
                try {
                    selectedStatusId = STATUSES_LIST.get(spStatus.getSelectedItemPosition()).getStatusId();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (MyUtils.isNetworkAvailable()) {
                        GetStatuses getStatuses = new GetStatuses(this);
                        getStatuses.execute();
                    }
                }

                if (selectedStatusId.equals("0")) {
                    areAllRequiredFieldsFilled = false;
                    MyDialogs.showOK(AssignmentActionsActivity.this, localized_changeStatus);
                }

                boolean MandatorySignature = MyPrefs.getBoolean(PREF_MANDATORY_SIGNATURE, false);
                if (!isSigned && MandatorySignature && !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_SCANNED, assignmentId, false)){
                    areAllRequiredFieldsFilled = false;
                    MyDialogs.showOK(AssignmentActionsActivity.this, localized_fillSignature);
                }

                int mandatoryMinimumPayment = STATUSES_LIST.get(spStatus.getSelectedItemPosition()).getMandatoryMinimumPayment();
                Double minimumPayment = Double.parseDouble(SELECTED_ASSIGNMENT.getMinimumPayment().replace(",", "."));
                String _paid = paidAmount.replace("\n", " ").replace("\r", " ").replace(",", ".");
                if (_paid.length() == 0 ) {_paid = "0";}
                Double paid = Double.parseDouble(_paid);
                if(minimumPayment>0 && minimumPayment>paid && mandatoryMinimumPayment>0){
                    areAllRequiredFieldsFilled = false;
                    MyDialogs.showOK(AssignmentActionsActivity.this, String.format(localized_minimum_payment, SELECTED_ASSIGNMENT.getMinimumPayment()));
                    break;
                }


                FirebaseCrashlytics.getInstance().log("GetSelectedStatusMandatorySteps" + " -UserId: " + MyPrefs.getString(PREF_USERID, "") + " -Url: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));
                int MandatorySteps = STATUSES_LIST.get(spStatus.getSelectedItemPosition()).getMandatorySteps();
                if (MandatorySteps != 0){
                    FirebaseCrashlytics.getInstance().log("CheckMandatoryTasks" + " -UserId: " + MyPrefs.getString(PREF_USERID, "") + " -Url: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));
                    for (int i = 0; i < MANDATORY_TASKS_LIST.size(); i++) {

                        if (!MANDATORY_TASKS_LIST.get(i).getStepDescription().isEmpty() &&
                                !MANDATORY_TASKS_LIST.get(i).getIsOptional().equals("1") &&
                                MANDATORY_TASKS_LIST.get(i).getMeasurementValue().isEmpty() ||
                                MANDATORY_TASKS_LIST.get(i).getRequiresPhoto().equals("1") &&
                                        MANDATORY_TASKS_LIST.get(i).getStepPhoto().isEmpty()) {

                            areAllRequiredFieldsFilled = false;
                            MyDialogs.showOK(AssignmentActionsActivity.this, localized_fillMandatoryTasks);
                            break;
                        }
                    }
                }

                if(SELECTED_ASSIGNMENT.getMandatoryZoneMeasurementsService().equals("1")){
                    FirebaseCrashlytics.getInstance().log("CheckZoneMeasurements" + " -UserId: " + MyPrefs.getString(PREF_USERID, "") + " -Url: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));
                    if (MyPrefs.getBoolean(PREF_SHOW_ZONE_PRODUCTS_BUTTON, false)) {
                        String Zones = MyPrefs.getStringWithFileName(PREF_FILE_ZONES_DATA_FOR_SHOW, assignmentId, "");
                        if (Zones.isEmpty()) {
                            if (MyUtils.isNetworkAvailable()) {
                                if (!SELECTED_ASSIGNMENT.getProjectId().isEmpty()) {
                                    GetZones getZones = new GetZones(this, null, true, "0");
                                    try{
                                        String result_ = getZones.execute(SELECTED_ASSIGNMENT.getProjectId()).get();
                                        result_= "";
                                        ZonesData.generate(true);
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }


                        if (MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_NO_MEASUREMENTS, assignmentId, "").length() > 0) {
                            ZONES_WITH_NO_MEASUREMENTS_MAP = new Gson().fromJson(MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_NO_MEASUREMENTS, assignmentId, ""), new TypeToken<HashMap<String, List<String>>>(){}.getType());
                        }
                        List<String> zoneIds_without = new ArrayList<>();
                        if (ZONES_WITH_NO_MEASUREMENTS_MAP != null && ZONES_WITH_NO_MEASUREMENTS_MAP.containsKey(assignmentId)) {
                            zoneIds_without = ZONES_WITH_NO_MEASUREMENTS_MAP.get(assignmentId);
                        }

                        String zones_that_need_measurement = "";
                        for (ZoneModel zm : ZONES_LIST){
                            String zone_id = zm.getZoneId();
                            String zone_desc = zm.getZoneName();
                            String pref_key = SELECTED_ASSIGNMENT.getProjectId() + "_" + zone_id + "_" + assignmentId;
                            if (MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_MEASUREMENTS, pref_key, "").length() > 0) {
                                ZONES_WITH_MEASUREMENTS_MAP = new Gson().fromJson(MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_MEASUREMENTS, pref_key, ""), new TypeToken<HashMap<String, List<String>>>(){}.getType());
                            }
                            List<String> zoneIds = new ArrayList<>();
                            if (ZONES_WITH_MEASUREMENTS_MAP != null && ZONES_WITH_MEASUREMENTS_MAP.containsKey(assignmentId)) {
                                zoneIds = ZONES_WITH_MEASUREMENTS_MAP.get(assignmentId);
                            }

                            if(!zoneIds.contains(zone_id) && !zoneIds_without.contains(zone_id)){
                                if(zones_that_need_measurement.length() == 0){
                                    zones_that_need_measurement = zone_desc;
                                }else{
                                    zones_that_need_measurement += "\r\n" + zone_desc;
                                }
                            }
                        }

                        if(zones_that_need_measurement.length() > 0){
                            areAllRequiredFieldsFilled = false;
                            MyDialogs.showOK(AssignmentActionsActivity.this, localized_complete_measurements + "\r\n" + zones_that_need_measurement);
                            break;
                        }
                    }


                }


                FirebaseCrashlytics.getInstance().log("areAllRequiredFieldsFilled" + " -UserId: " + MyPrefs.getString(PREF_USERID, "") + " -Url: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));
                if (areAllRequiredFieldsFilled) {
                    String encodedSignature;
                    InputStream inputStream = null;

                    try {
                        inputStream = new FileInputStream(imagePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    byte[] bytes;
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    try {
                        if (inputStream != null) {
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    bytes = output.toByteArray();
                    encodedSignature = Base64.encodeToString(bytes, Base64.NO_WRAP);

                    try {
                        solution = URLEncoder.encode(solution,"UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                        solution = "";
                    }

                    try {
                        notes = URLEncoder.encode(notes,"UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                        notes = "";
                    }

                    checkInCheckOutModel = new CheckInCheckOutModel();
                    FirebaseCrashlytics.getInstance().log("FillCheckoutModel" + " -UserId: " + MyPrefs.getString(PREF_USERID, "") + " -Url: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));
                    checkInCheckOutModel.setAssignmentId(assignmentId);
                    checkInCheckOutModel.setStartWorkTime(MyPrefs.getStringWithFileName(assignmentId, PREF_START_WORK_TIME, ""));
                    checkInCheckOutModel.setStartTravelTime(MyPrefs.getStringWithFileName(assignmentId, PREF_START_TRAVEL_TIME, ""));
                    checkInCheckOutModel.setCheckInTime(MyPrefs.getStringWithFileName(assignmentId, PREF_CHECK_IN_TIME, ""));
                    checkInCheckOutModel.setCheckOutTime(MyDateTime.getCurrentTime());
                    checkInCheckOutModel.setStartLat(MyPrefs.getStringWithFileName(assignmentId, PREF_START_LAT, ""));
                    checkInCheckOutModel.setStartLng(MyPrefs.getStringWithFileName(assignmentId, PREF_START_LNG, ""));
                    checkInCheckOutModel.setCheckInLat(MyPrefs.getStringWithFileName(assignmentId, PREF_CHECK_IN_LAT, ""));
                    checkInCheckOutModel.setCheckInLng(MyPrefs.getStringWithFileName(assignmentId, PREF_CHECK_IN_LNG, ""));
                    checkInCheckOutModel.setCheckOutLat(MyPrefs.getString(PREF_CURRENT_LAT, ""));
                    checkInCheckOutModel.setCheckOutLng(MyPrefs.getString(PREF_CURRENT_LNG, ""));
                    checkInCheckOutModel.setSolution(solution.replace("\n", " ").replace("\r", " "));
                    checkInCheckOutModel.setNotes(notes.replace("\n", " ").replace("\r", " "));
                    checkInCheckOutModel.setChargedAmount(chargedAmount.replace("\n", " ").replace("\r", " "));
                    checkInCheckOutModel.setPaidAmount(paidAmount.replace("\n", " ").replace("\r", " "));
                    checkInCheckOutModel.setStatus(spStatus.getSelectedItem().toString());
                    checkInCheckOutModel.setStatusCode(getStatusIdFromSpinner());
                    checkInCheckOutModel.setSignatureName(signatureName.replace("\n", " ").replace("\r", " "));
                    checkInCheckOutModel.setSignatureEmail(signatureEmail.replace("\n", " ").replace("\r", " "));
                    checkInCheckOutModel.setEncodedSignature(encodedSignature);
                    checkInCheckOutModel.setSendReport(sendreport);
                    checkInCheckOutModel.setUserId(MyPrefs.getString(PREF_USERID, ""));

//                        Log.e(LOG_TAG, "------- checkInCheckOutModel.toString(): \n" + checkInCheckOutModel.toString());

                    btnCheckOut.setEnabled(false);
                    String chkOut = "CHECK OUT" + System.getProperty("line.separator") + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(new Date());
                    MyPrefs.setStringWithFileName(PREF_CHECK_OUT_TIME_FOR_SHOW, assignmentId, chkOut);
                    btnCheckOut.setText(chkOut);
                    btnOvernight.setEnabled(true);
                    spStatus.setEnabled(false);
                    etCommentsSolution.setEnabled(false);
                    etNotes.setEnabled(false);
                    tvSolutionTitle.setEnabled(false);
                    tvInternalNotesTitle.setEnabled(true);
                    btnTakePhoto.setEnabled(false);
                    btnAddAttachment.setEnabled(false);
                    etChargedAmount.setEnabled(false);
                    etPaidAmount.setEnabled(false);
                    tvSignatureImage.setEnabled(false);

                    rvMandatoryTasks.setBackgroundResource(R.drawable.rounded_layout_grey);
                    llPayment.setBackgroundResource(R.drawable.rounded_layout_grey);
                    flSignatureImage.setBackgroundResource(R.drawable.rounded_layout_white);

                    // refresh recycler view to enable fields
                    mandatoryTasksRvAdapter.notifyDataSetChanged();

//                    for (int i = 0; i < rvMandatoryTasks.getChildCount(); i++) {
//                        LinearLayout llMandatoryTasksContent = (LinearLayout) rvMandatoryTasks.getChildAt(i);
//                        llMandatoryTasksContent.setBackgroundColor(ContextCompat.getColor(AssignmentActionsActivity.this, R.color.grey_300));
//
//                        LinearLayout llMandatoryTasksTitleRow = (LinearLayout) llMandatoryTasksContent.getChildAt(0);
//                        llMandatoryTasksTitleRow.getChildAt(2).setEnabled(false);
//
//                        LinearLayout llMandatoryTasksMeasurableRow = (LinearLayout) llMandatoryTasksContent.getChildAt(1);
//                        llMandatoryTasksMeasurableRow.getChildAt(1).setEnabled(false);
//                    }

                    updateStatusData();

                    MyPrefs.setStringWithFileName(PREF_FILE_MANDATORY_TASKS_FOR_SYNC, assignmentId, MANDATORY_TASKS_LIST.toString());
                    MyPrefs.setStringWithFileName(PREF_FILE_CHECK_OUT_DATA_TO_SYNC, assignmentId, checkInCheckOutModel.toString());
                    MyPrefs.setBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, true);

//                    for (int i = 0; i < STATUSES_LIST.size(); i++) {
//                        if (STATUSES_LIST.get(i).getStatusId().equals(selectedStatusId)) {
//                            if (STATUSES_LIST.get(i).getIsRollback() == 1) {
//                                MyPrefs.setBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false);
//                            }
//                            break;
//                        }
//                    }


                    if(MANDATORY_TASKS_LIST.size() > 0){
                        MyPrefs.setStringWithFileName(PREF_FILE_SEND_REPORT_VALUE_FOR_SYNC, assignmentId, sendreport);
                    }


                    if (MyUtils.isNetworkAvailable()) {

                        if (MyPrefs.getBoolean(PREF_ONLY_WIFI, false)) {
                            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mWifi;

                            if (connManager != null) {
                                mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                                if (mWifi.isConnected()) {
                                    MySynchronize.synchronizeSavedImages(AssignmentActionsActivity.this);
                                    MySynchronize.synchronizeSavedAttachments(AssignmentActionsActivity.this);
                                }
                            }
                        } else {
                            MySynchronize.synchronizeSavedImages(AssignmentActionsActivity.this);
                            MySynchronize.synchronizeSavedAttachments(AssignmentActionsActivity.this);
                        }

                        SendCheckOut sendCheckOut = new SendCheckOut(AssignmentActionsActivity.this, assignmentId, CONST_SHOW_PROGRESS_AND_TOAST, false);

                        if (!sendCheckOut.isCheckingOut()) {
                            sendCheckOut.execute();
                        }

                        SendMandatoryTasks sendMandatoryTasks = new SendMandatoryTasks(AssignmentActionsActivity.this);
                        sendMandatoryTasks.execute(assignmentId, "");

                        boolean sendZoneMeasurements = MyPrefs.getBoolean(PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT,  false);
                        FirebaseCrashlytics.getInstance().log("SEND_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC" + " -UserId: " + MyPrefs.getString(PREF_USERID, "") + " -Url: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));
                        Map<String, ?> Zone_Measurements = this.getSharedPreferences(PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC, MODE_PRIVATE).getAll();
                        for (Map.Entry<String, ?> entry : Zone_Measurements.entrySet()) {
                            String prefKey = entry.getKey();
                            if (prefKey.split("_")[2].equals(assignmentId)) {
                                if (sendZoneMeasurements) {
                                    SendProductMeasurements sendProductMeasurements = new SendProductMeasurements(AssignmentActionsActivity.this, prefKey, CONST_DO_NOT_FINISH_ACTIVITY, assignmentId);
                                    sendProductMeasurements.execute();
                                } else {
                                    MyPrefs.removeStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC, prefKey);
                                }
                            }
                        }

                        MyPrefs.removeStringWithFileName(PREF_FILE_ZONES_WITH_MEASUREMENTS, assignmentId);

                        FirebaseCrashlytics.getInstance().log("SEND_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC" + " -UserId: " + MyPrefs.getString(PREF_USERID, "") + " -Url: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));
                        if (MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC, assignmentId, "").length() > 0) {
                            SendZonesWithNoMeasurement sendZonesWithNoMeasurement = new SendZonesWithNoMeasurement(AssignmentActionsActivity.this, assignmentId);
                            sendZonesWithNoMeasurement.execute();
                        }


                    } else {
                        new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setMessage(localized_no_internet_data_saved)
                                .setPositiveButton(R.string.ok, (dialog, which) -> {
                                    dialog.dismiss();
                                    this.finishAffinity();
                                    Intent _intent = new Intent(AssignmentActionsActivity.this, AssignmentsActivity.class);
                                    _intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    _intent.putExtra(KEY_DOWNLOAD_ALL_DATA, false);
                                    AssignmentActionsActivity.this.startActivity(_intent);
                                })
                                .show();

                        //MyDialogs.showOK(AssignmentActionsActivity.this, localized_no_internet_data_saved);
                    }
                }

                break;

            case R.id.btnStartWork:
                MyPrefs.setStringWithFileName(assignmentId, PREF_START_WORK_TIME, MyDateTime.getCurrentTime());
                MyPrefs.setBooleanWithFileName(PREF_FILE_IS_WORK_STARTED, assignmentId, true);

                btnStartWork.setEnabled(false);
                break;

            case R.id.btnGetCost:
                GetAssignmentCost getAssignmentCost = new GetAssignmentCost(this, assignmentId);
                getAssignmentCost.execute();
                break;
            case R.id.btnOvernight:

                UsePTOvernightModel usePTOvernightModel = new UsePTOvernightModel();

                usePTOvernightModel.setAssignmentId(assignmentId);
                usePTOvernightModel.setTransitDateTime(MyDateTime.getCurrentTime());
                usePTOvernightModel.setTransitLat(MyPrefs.getString(PREF_CURRENT_LAT, ""));
                usePTOvernightModel.setTransitLon(MyPrefs.getString(PREF_CURRENT_LNG, ""));
                usePTOvernightModel.setTransitStatus(0);
                usePTOvernightModel.setOvernight(true);

                String prefKey = UUID.randomUUID().toString();

                MyPrefs.setStringWithFileName(PREF_FILE_USE_PT_OVERNIGHT_FOR_SYNC, prefKey, usePTOvernightModel.toString());

                SendUsePTOvernight sendUsePTOvernight = new SendUsePTOvernight();
                sendUsePTOvernight.execute(prefKey);

                MyPrefs.setBooleanWithFileName(PREF_FILE_IS_OVERNIGHT, assignmentId, true);

                btnOvernight.setEnabled(false);
                break;

            default:
                break;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_synch, menu);
//        return true;
//    }

    @SuppressLint("MissingSuperCall") //Suggested
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data); //Suggested
        Log.e(LOG_TAG, "-------------------------------------- onActivityResult.");

        switch (requestCode) {

            case RESULT_SIGNATURE:
                if (!hideInternalNotes) {
                    etNotes.setVisibility(View.VISIBLE);
                }
                if (resultCode == RESULT_OK) {
                    if (data != null && data.hasExtra("status") && data.getStringExtra("status").equals("done")) {

                        if (flSignatureImage != null) {
                            flSignatureImage.setBackgroundResource(R.drawable.rounded_layout_white);
                        }

                        signatureName = data.getStringExtra("signatureName");

                        imagePath = data.getStringExtra("imagePath");

                        signatureEmail = data.getStringExtra("signatureEmail");

                        TextView tvSignatureImage = findViewById(R.id.tvSignatureImage);
                        TextView tvSignatureName = findViewById(R.id.tvSignatureName);
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                        if (tvSignatureImage != null) {
                            tvSignatureImage.setBackground(new BitmapDrawable(getResources(), bitmap));
                            if (signatureEmail.length() > 0) {
                                MyPrefs.setStringWithFileName(PREF_FILE_SIGNATURENAME, assignmentId, signatureName + " (" + signatureEmail + ")");
                            } else {
                                MyPrefs.setStringWithFileName(PREF_FILE_SIGNATURENAME, assignmentId, signatureName);
                            }
                            //SELECTED_ASSIGNMENT.setSignatureName(signatureName);
                            isSigned = true;
                        }
                    }
                }
                break;


            case OTHER_APP_RESULT_PICK_ASSIGNMENT_PHOTO:
                if (resultCode == RESULT_OK) {

                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri selectedImage = data.getClipData().getItemAt(i).getUri();
                            File pickedFile = new File(getRealPathFromURI(selectedImage));
                            File newFileLocation = new File(this.getExternalFilesDir(null) + File.separator + assignmentId + CONST_ASSIGNMENT_PHOTOS_FOLDER);
                            File movedPhoto = new File(newFileLocation, pickedFile.getName());
                            movedPhoto = CopyFile(selectedImage, movedPhoto);
                            if (movedPhoto == null){return;}
                            //copyFileOrDirectory(pickedFile.getAbsolutePath(), newFileLocation.getAbsolutePath());
                            globalCurrentPhotoPath = movedPhoto.getAbsolutePath();
                            PHOTO_ITEMS.add(globalCurrentPhotoPath);
                            photosRecyclerViewAdapter.notifyDataSetChanged();
                            prepareImageForSending(true);
                        }
                    } else if (data != null) {
                        Uri selectedImage = data.getData();
                        File pickedFile = new File(getRealPathFromURI(selectedImage));
                        File newFileLocation = new File(this.getExternalFilesDir(null) + File.separator + assignmentId + CONST_ASSIGNMENT_PHOTOS_FOLDER);
                        File movedPhoto = new File(newFileLocation, pickedFile.getName());
                        movedPhoto = CopyFile(selectedImage, movedPhoto);
                        if (movedPhoto == null){return;}
                        //copyFileOrDirectory(pickedFile.getAbsolutePath(), newFileLocation.getAbsolutePath());
                        globalCurrentPhotoPath = movedPhoto.getAbsolutePath();
                        PHOTO_ITEMS.add(globalCurrentPhotoPath);
                        photosRecyclerViewAdapter.notifyDataSetChanged();
                        prepareImageForSending(true);
                    }
                }
                break;

            case OTHER_APP_RESULT_TAKE_ASSIGNMENT_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (globalCurrentPhotoPath != null) {
                        PHOTO_ITEMS.add(globalCurrentPhotoPath);
                        photosRecyclerViewAdapter.notifyDataSetChanged();
                        prepareImageForSending(false);
                    }
                }
                break;

            case OTHER_APP_RESULT_TAKE_MANDATORY_TASK_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (globalMandatoryTaskPosition != -1) {
                        try {
                            MANDATORY_TASKS_LIST.get(globalMandatoryTaskPosition).setStepPhotoPath(globalCurrentPhotoPath);
                            MANDATORY_TASKS_LIST.get(globalMandatoryTaskPosition).setStepPhoto(MyImages.getImageBase64String(AssignmentActionsActivity.this, globalCurrentPhotoPath, true));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        Log.e(LOG_TAG, "----------------onActivityResult MANDATORY_TASKS_LIST: \n" + MANDATORY_TASKS_LIST);

                        mandatoryTasksRvAdapter.notifyDataSetChanged();
                    }
                }
                break;

            case OTHER_APP_RESULT_PICK_MANDATORY_TASK_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (globalMandatoryTaskPosition != -1) {
                            Uri selectedImage = data.getData();
                            File pickedFile = new File(getRealPathFromURI(selectedImage));
                            File newFileLocation = new File(this.getExternalFilesDir(null) + File.separator + assignmentId + CONST_ASSIGNMENT_PHOTOS_FOLDER);
                            File movedPhoto = new File(newFileLocation, pickedFile.getName());
                            movedPhoto = CopyFile(selectedImage, movedPhoto);
                            if (movedPhoto == null){return;}
                            //copyFileOrDirectory(pickedFile.getAbsolutePath(), newFileLocation.getAbsolutePath());
                            globalCurrentPhotoPath = movedPhoto.getAbsolutePath();

                            try {
                                MANDATORY_TASKS_LIST.get(globalMandatoryTaskPosition).setStepPhotoPath(globalCurrentPhotoPath);
                                MANDATORY_TASKS_LIST.get(globalMandatoryTaskPosition).setStepPhoto(MyImages.getImageBase64String(AssignmentActionsActivity.this, globalCurrentPhotoPath, false));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                        Log.e(LOG_TAG, "----------------onActivityResult MANDATORY_TASKS_LIST: \n" + MANDATORY_TASKS_LIST);

                            mandatoryTasksRvAdapter.notifyDataSetChanged();
                        }

                    }


                }


                break;

            case PICKFILE_RESULT_CODE:
                if (data != null) {
                    Uri attachmentFile = data.getData();
                    String Filename = getFileName(attachmentFile);
                    File outputFile = new File(this.getExternalFilesDir(null) + File.separator + assignmentId + CONST_ASSIGNMENT_ATTACHMENTS_FOLDER + "/" + Filename);
                    InputStream in = null;
                    OutputStream out = null;
                    long selectedFileSize = 0;
                    boolean transferSuccessful = true;
                    try {
                        in = getContentResolver().openInputStream(attachmentFile);
                        long sizeLimit = 30000000; //30mb
                        selectedFileSize = in.available();
                        String conStr = MyPrefs.getString(PREF_AZURE_CONNECTION_STRING, "");
                        if (selectedFileSize > sizeLimit && conStr.length()==0){
                            MyDialogs.showOK(AssignmentActionsActivity.this, localized_file_size_limit);
                            return;
                        }
                        outputFile.getParentFile().mkdirs();
                        outputFile.createNewFile();
                        out = new FileOutputStream(outputFile, true);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                        if (in != null) {
                            in.close();
                        }
                        if (out != null){
                            out.close();
                        }
                    } catch (Exception ex) {
                        transferSuccessful = false;
                        ex.printStackTrace();
                        MyDialogs.showOK(AssignmentActionsActivity.this, ex.toString());
                    }

                    if (transferSuccessful){
                        globalCurrentAttachmentPath = outputFile.getAbsolutePath(); //movedPhoto.getAbsolutePath();
                        ATTACHMENT_ITEMS.add(globalCurrentAttachmentPath);
                        attachmentsRvAdapter.notifyDataSetChanged();
                        prepareAttachmentForSending(selectedFileSize);
                    }

                }


        }
    }

    public File CopyFile(Uri attachmentFile, File outputFile){
        InputStream in = null;
        OutputStream out = null;
        long selectedFileSize = 0;
        boolean transferSuccessful = true;
        try {
            in = getContentResolver().openInputStream(attachmentFile);
            long sizeLimit = 30000000; //30mb
            selectedFileSize = in.available();
            //String conStr = MyPrefs.getString(PREF_AZURE_CONNECTION_STRING, "");
            if (selectedFileSize > sizeLimit){
                MyDialogs.showOK(AssignmentActionsActivity.this, localized_file_size_limit);
                return null;
            }
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            out = new FileOutputStream(outputFile, true);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            if (in != null) {
                in.close();
            }
            if (out != null){
                out.close();
            }
        } catch (Exception ex) {
            transferSuccessful = false;
            ex.printStackTrace();
            MyDialogs.showOK(AssignmentActionsActivity.this, ex.toString());
            outputFile = null;
        }

        return outputFile;
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



    private void prepareImageForSending(boolean fromGallery) {
        String photoBase64String = MyImages.getImageBase64String(AssignmentActionsActivity.this, globalCurrentPhotoPath, fromGallery);

        int lastIndex = globalCurrentPhotoPath.lastIndexOf("/");
        String photoFileName = globalCurrentPhotoPath.substring(lastIndex + 1);
        String prefKey = assignmentId + "_" + photoFileName;

        String postBody = "{\n" +
                "  \"AssignmentId\": \"" + assignmentId + "\",\n" +
                "  \"Photo64\": \"" + photoBase64String + "\"\n" +
                "}";

        MyPrefs.setStringWithFileName(PREF_FILE_IMAGE_FOR_SYNC, prefKey, postBody);
        String PostBodyLog = "";
        if (postBody.length() > 59) {PostBodyLog = postBody.substring(0, 60) + " " + prefKey;} else {PostBodyLog = postBody;}
        MyLogs.writeFile_FullLog("myLogs: PrepareImage", "", PostBodyLog, 0, prefKey, globalCurrentPhotoPath);
    }



    private void prepareAttachmentForSending(long filesize) {

        String conStr = MyPrefs.getString(PREF_AZURE_CONNECTION_STRING, "");

        File file = new File(globalCurrentAttachmentPath);
        String fileBase64String = "";

        if(conStr.length() == 0){
            fileBase64String = encodeFileToBase64(file);
            if (fileBase64String.length() == 0){ return;}
        }


        int lastIndex = globalCurrentAttachmentPath.lastIndexOf("/");
        String attachmentFileName = globalCurrentAttachmentPath.substring(lastIndex + 1);
        String prefKey = assignmentId + "_" + attachmentFileName;

        String postBody = "{\n" +
                "  \"ObjectId\": \"" + assignmentId + "\",\n" +
                "  \"Filename\": \"" + attachmentFileName + "\",\n" +
                "  \"Attachment64\": \"" + fileBase64String + "\"\n" +
                "}";

        if (conStr.length()>0){
            postBody = assignmentId + "|" + globalCurrentAttachmentPath + "|" + filesize;
        }

        MyPrefs.setStringWithFileName(PREF_FILE_ATTACHMENT_FOR_SYNC, prefKey, postBody);
        String PostBodyLog = "";
        if (postBody.length() > 59) {PostBodyLog = postBody.substring(0, 60) + " " + prefKey;} else {PostBodyLog = postBody;}
        MyLogs.writeFile_FullLog("myLogs: PrepareImage", "", PostBodyLog, 0, prefKey, globalCurrentAttachmentPath);
    }

    private static String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.encodeToString(fileContent, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setupStatusesSpinner() {
        String[] statusesArray = new String[STATUSES_LIST.size()];

        Log.e(LOG_TAG, "========================== STATUSES_LIST:\n" + STATUSES_LIST);

        for (int i = 0; i < STATUSES_LIST.size(); i++) {
            statusesArray[i] = STATUSES_LIST.get(i).getStatusDescription().toUpperCase();
        }

        spStatus.setEnabled(false);
        spStatus.setAdapter(new MySpinnerAdapter(this, statusesArray));

        for (int i = 0; i < STATUSES_LIST.size(); i++) {
            if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)){
                if (STATUSES_LIST.get(i).getStatusId().equals(SELECTED_ASSIGNMENT.getStatusId())) {
                    spStatus.setSelection(i);
                }
            } else {
                String statusToSelect = "";
                if (SELECTED_ASSIGNMENT.getProposedCheckOutStatus().equals("0")){
                    statusToSelect = SELECTED_ASSIGNMENT.getStatusId();
                } else {
                    statusToSelect = SELECTED_ASSIGNMENT.getProposedCheckOutStatus();
                }
                if (STATUSES_LIST.get(i).getStatusId().equals(statusToSelect)) {
                    spStatus.setSelection(i);
                }
            }

        }
    }

    private String getStatusIdFromSpinner(){

        for (int i = 0; i < STATUSES_LIST.size(); i++) {
                if (STATUSES_LIST.get(i).getStatusDescription().toUpperCase().equals(spStatus.getSelectedItem().toString())) {
                    return STATUSES_LIST.get(i).getStatusId();
                }
        }

        return "-1";
    }

    private void updateStatusData() {
        String assignmentData = MyPrefs.getString(PREF_DATA_ASSIGNMENTS, "");

        if (!assignmentData.equals("")) {
            try {
                JSONArray jsonArr = new JSONArray(assignmentData);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject oneObject = jsonArr.getJSONObject(i);

                    if (oneObject.getString("AssignmentId").equals(assignmentId)) {
                        jsonArr.getJSONObject(i).put("Status", spStatus.getSelectedItem().toString());
                        jsonArr.getJSONObject(i).put("StatusId", getStatusIdFromSpinner());
                    }
                }

                MyPrefs.setString(PREF_DATA_ASSIGNMENTS, jsonArr.toString());


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_EXTERNAL_STORAGE_FOR_ASSIGNMENT_PHOTO) {
                pickAssignmentPhotoFromStorage(this);
            } else if (requestCode == REQUEST_EXTERNAL_STORAGE_FOR_MANDATORY_PHOTO) {
                pickMandatoryPhotoFromStorage(this);
            } else if (requestCode == REQUEST_CAMERA_FOR_ASSIGNMENT_PHOTO) {
                takeAssignmentPhotoWithCamera(this);
            } else if (requestCode == REQUEST_CAMERA_FOR_MANDATORY_PHOTO) {
                takeMandatoryPhotoWithCamera(this);
            } else if (requestCode == PICKFILE_RESULT_CODE){
                pickFileFromStorage(this);
            }
        }
     /*   } else {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mandatoryStepPhoto) {
                    mandatoryStepPhoto = false;
                    String photoFolderName = assignmentId + CONST_ASSIGNMENT_PHOTOS_FOLDER;
                    new TakeUploadPhoto(AssignmentActionsActivity.this).dispatchTakePictureIntent(OTHER_APP_RESULT_TAKE_MANDATORY_TASK_PHOTO, photoFolderName);
                } else {
                    String photoFolderName = assignmentId + CONST_ASSIGNMENT_PHOTOS_FOLDER;
                    new TakeUploadPhoto(AssignmentActionsActivity.this).dispatchTakePictureIntent(OTHER_APP_RESULT_TAKE_ASSIGNMENT_PHOTO, photoFolderName);
                }
            }*/
    }


    //deprecated
    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String[] files = src.list();
                int filesLength = files.length;
                for (String file : files) {
                    String src1 = (new File(src, file).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }catch(Exception ex){
            ex.printStackTrace();
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


    public static void pickAssignmentPhotoFromStorage(Activity activity){
        globalCurrentPhotoPath = null;
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(pickPhoto, OTHER_APP_RESULT_PICK_ASSIGNMENT_PHOTO);
    }

    public static void pickMandatoryPhotoFromStorage(Activity activity) {
        globalCurrentPhotoPath = null;
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        activity.startActivityForResult(pickPhoto, OTHER_APP_RESULT_PICK_MANDATORY_TASK_PHOTO);
    }

    public static void takeAssignmentPhotoWithCamera(Context ctx) {
        String photoFolderName = SELECTED_ASSIGNMENT.getAssignmentId() + CONST_ASSIGNMENT_PHOTOS_FOLDER;
        new TakeUploadPhoto(ctx).dispatchTakePictureIntent(OTHER_APP_RESULT_TAKE_ASSIGNMENT_PHOTO, photoFolderName);
    }

    public static void takeMandatoryPhotoWithCamera(Context ctx) {
        String photoFolderName = SELECTED_ASSIGNMENT.getAssignmentId() + CONST_ASSIGNMENT_PHOTOS_FOLDER;
        new TakeUploadPhoto(ctx).dispatchTakePictureIntent(OTHER_APP_RESULT_TAKE_MANDATORY_TASK_PHOTO, photoFolderName);
    }

    public static void pickFileFromStorage(Activity activity){
        globalCurrentAttachmentPath = null;
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        activity.startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
    }

}
