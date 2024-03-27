package dc.gtest.vortex.support;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.widget.EditText;

import com.amrdeveloper.treeview.TreeNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import dc.gtest.vortex.models.AddedConsumableModel;
import dc.gtest.vortex.models.AllAttributeModel;
import dc.gtest.vortex.models.AllConsumableModel;
import dc.gtest.vortex.models.AllProductModel;
import dc.gtest.vortex.models.AssignmentIndicatorModel;
import dc.gtest.vortex.models.AssignmentModel;
import dc.gtest.vortex.models.AssignmentTypeModel;
import dc.gtest.vortex.models.AttributeModel;
import dc.gtest.vortex.models.CompanyModel;
import dc.gtest.vortex.models.CustomFieldDetailModel;
import dc.gtest.vortex.models.CustomFieldModel;
import dc.gtest.vortex.models.HAssignmentModel;
import dc.gtest.vortex.models.InstallationModel;
import dc.gtest.vortex.models.MandatoryTaskModel;
import dc.gtest.vortex.models.ManualModel;
import dc.gtest.vortex.models.MasterProjectModel;
import dc.gtest.vortex.models.NewAssignmentModel;
import dc.gtest.vortex.models.ProductMeasurementModel;
import dc.gtest.vortex.models.ProductModel;
import dc.gtest.vortex.models.ProductTypeModel;
import dc.gtest.vortex.models.ProjectModel;
import dc.gtest.vortex.models.ProjectZoneModel;
import dc.gtest.vortex.models.ServiceModel;
import dc.gtest.vortex.models.StatusModel;
import dc.gtest.vortex.models.UserPartnerResourceModel;
import dc.gtest.vortex.models.ZoneModel;
import dc.gtest.vortex.models.ZoneProductModel;
import dc.gtest.vortex.models.MeasurementModel;

public class MyGlobals {
    // Data lists
    public static NewAssignmentModel NEW_ASSIGNMENT = new NewAssignmentModel();

    public static AssignmentModel SELECTED_ASSIGNMENT = new AssignmentModel();
    public static List<AssignmentModel> ASSIGNMENTS_LIST = new ArrayList<>();
    public static List<AssignmentModel> FILTERED_ASSIGNMENTS_LIST = new ArrayList<>();

    public static InstallationModel SELECTED_INSTALLATION = new InstallationModel();
    public static ProjectZoneModel SELECTED_INSTALLATION_ZONE = new ProjectZoneModel();
    public static ProjectZoneModel NEW_INSTALLATION_ZONE = new ProjectZoneModel();
    public static CustomFieldModel SELECTED_CUSTOM_FIELD = new CustomFieldModel();
    public static CustomFieldDetailModel SELECTED_CUSTOM_FIELD_DETAIL = new CustomFieldDetailModel();


    public static HashSet<Date> CALENDAR_EVENTS  = new HashSet<>();

    public static ProductModel SELECTED_PRODUCT = new ProductModel();
    public static List<ProductModel> PRODUCTS_LIST = new ArrayList<>();
    public static List<TreeNode> PRODUCTS_TREE_LIST = new ArrayList<>();
    public static List<TreeNode> PRODUCTS_TREE_LIST_SAVED_STATE = new ArrayList<>();

    public static List<AllProductModel> ALL_PRODUCTS_LIST = new ArrayList<>();
    public static List<AllProductModel> ALL_PRODUCTS_LIST_FILTERED = new ArrayList<>();

    public static List<AllConsumableModel> PICKING_PRODUCTS_LIST = new ArrayList<>();
    public static List<AllConsumableModel> PICKING_PRODUCTS_LIST_FILTERED = new ArrayList<>();

    public static List<AllProductModel> ALL_WAREHOUSE_PRODUCTS_LIST = new ArrayList<>();
    public static List<AllProductModel> ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED = new ArrayList<>();

    public static List<AttributeModel> NEW_ATTRIBUTES_LIST = new ArrayList<>();
    public static List<AttributeModel> ATTRIBUTES_LIST = new ArrayList<>();
    public static List<AllAttributeModel> ALL_ATTRIBUTES_LIST = new ArrayList<>();
    public static List<AllAttributeModel> ALL_ATTRIBUTES_LIST_FILTERED = new ArrayList<>();

    public static List<CustomFieldDetailModel> CUSTOM_FIELD_DETAILS_LIST = new ArrayList<>();
    public static List<CustomFieldDetailModel> CUSTOM_FIELD_DETAILS_LIST_FILTERED = new ArrayList<>();

//    public static Map<String, CheckInCheckOutModel> CHECKINOUTMODEL_LIST = new HashMap<>();

    public static List<AllConsumableModel> ALL_CONSUMABLES_LIST = new ArrayList<>();
    public static List<AllConsumableModel> ALL_CONSUMABLES_LIST_FILTERED = new ArrayList<>();
    public static List<AllConsumableModel> ALL_WAREHOUSE_CONSUMABLES_LIST = new ArrayList<>();
    public static List<AllConsumableModel> ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED = new ArrayList<>();

    public static ProjectModel SELECTED_PROJECT = new ProjectModel();
    public static List<ProjectModel> PROJECTS_LIST = new ArrayList<>();
    public static List<ProjectModel> PROJECTS_LIST_FILTERED = new ArrayList<>();

    public static List<ProductMeasurementModel> PRODUCT_MEASUREMENTS_LIST = new ArrayList<>();
    public static List<MeasurementModel> MANDATORY_MEASUREMENTS_LIST = new ArrayList<>();
    public static Map<String, List<ProductMeasurementModel>> ZONE_MEASUREMENTS_MAP = new HashMap<>();
    //public static Map<String, Map<String, List<ProductMeasurementModel>>> ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC = new HashMap<>();
    public static Map<String, CustomFieldDetailModel> CUSTOM_FIELD_EMPTY_DETAILS_MAP = new HashMap<>();
    public static Map<String, List<String>> ZONES_WITH_NO_MEASUREMENTS_MAP = new HashMap<>();
    public static Map<String, List<String>> ZONES_WITH_MEASUREMENTS_MAP = new HashMap<>();

    public static CompanyModel SELECTED_COMPANY = new CompanyModel();
    public static List<CompanyModel> COMPANIES_LIST = new ArrayList<>();
    public static List<CompanyModel> COMPANIES_LIST_FILTERED = new ArrayList<>();

    public static List<ZoneProductModel> ZONE_PRODUCTS_LIST = new ArrayList<>();
    public static List<ZoneProductModel> ZONE_PRODUCTS_LIST_FILTERED = new ArrayList<>();

    public static List<ZoneModel> ZONES_LIST = new ArrayList<>();
    public static List<ZoneModel> ZONES_LIST_FILTERED = new ArrayList<>();

    public static List<ProjectZoneModel> INSTALLATION_ZONES_LIST = new ArrayList<>();
    public static List<ProjectZoneModel> INSTALLATION_ZONES_LIST_FILTERED = new ArrayList<>();

    public static List<CustomFieldModel> CUSTOM_FIELDS_LIST = new ArrayList<>();


    public static List<InstallationModel> INSTALLATION_LIST = new ArrayList<>();
    public static List<InstallationModel> INSTALLATION_LIST_FILTERED = new ArrayList<>();

    public static List<ProductTypeModel> PRODUCT_TYPES_LIST = new ArrayList<>();

    public static List<StatusModel> STATUSES_LIST = new ArrayList<>();
    public static List<StatusModel> ALL_STATUSES_LIST = new ArrayList<>();

    public static List<ServiceModel> SERVICES_LIST = new ArrayList<>();
    public static List<ServiceModel> SERVICES_LIST_FILTERED = new ArrayList<>();

    public static List<ServiceModel> RELATED_SERVICES_LIST = new ArrayList<>();
    public static List<ServiceModel> RELATED_SERVICES_LIST_FILTERED = new ArrayList<>();

    public static List<ServiceModel> SERVICES_FOR_NEW_ASSIGNMENT_LIST = new ArrayList<>();
    public static List<ServiceModel> SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED = new ArrayList<>();

    public static List<UserPartnerResourceModel> USER_PARTNER_RESOURCE_LIST = new ArrayList<>();

    public static List<AssignmentTypeModel> ASSIGNMENT_TYPES_LIST = new ArrayList<>();
    public static List<AssignmentIndicatorModel> ASSIGNMENT_INDICATORS_LIST = new ArrayList<>();
    public static List<MasterProjectModel> MASTER_PROJECTS_LIST = new ArrayList<>();
    public static List<MasterProjectModel> MASTER_PROJECTS_LIST_FILTERED = new ArrayList<>();
    public static List<MandatoryTaskModel> MANDATORY_TASKS_LIST = new ArrayList<>();

    public static List<AddedConsumableModel> ADDED_CONSUMABLES_LIST = new ArrayList<>();
    public static List<AddedConsumableModel> CONSUMABLES_TOADD_LIST = new ArrayList<>();
    public static List<AddedConsumableModel> ADDED_CONSUMABLES_LIST_FILTERED = new ArrayList<>();
    public static List<AddedConsumableModel> CONSUMABLES_TOADD_LIST_FILTERED = new ArrayList<>();
    public static List<AddedConsumableModel> SELECTED_FROM_PICKING_LIST = new ArrayList<>();
    public static List<HAssignmentModel> HISTORY_LIST = new ArrayList<>();

    public static List<ManualModel> MANUALS_LIST = new ArrayList<>();

    public static Context ASSIGNMENTS_CTX;

    // this app startActivityForResult from 1001
    public static final int RESULT_SIGNATURE = 1001;

    // other apps startActivityForResult from 2001
    public static final int OTHER_APP_RESULT_TAKE_ASSIGNMENT_PHOTO = 2001;
    public static final int OTHER_APP_RESULT_TAKE_MANDATORY_TASK_PHOTO = 2002;
    public static final int OTHER_APP_RESULT_CHECK_LOCATION_SETTINGS = 2003;
    public static final int OTHER_APP_RESULT_PICK_ASSIGNMENT_PHOTO = 2004;
    public static final int OTHER_APP_RESULT_PICK_MANDATORY_TASK_PHOTO = 2005;
    public static final int REQUEST_EXTERNAL_STORAGE_FOR_ASSIGNMENT_PHOTO = 2006;
    public static final int REQUEST_EXTERNAL_STORAGE_FOR_MANDATORY_PHOTO = 2007;
    public static final int REQUEST_CAMERA_FOR_ASSIGNMENT_PHOTO = 2008;
    public static final int REQUEST_CAMERA_FOR_MANDATORY_PHOTO = 2009;
    public static final int PICKFILE_RESULT_CODE = 2010;

    // requests for permissions from 3001
    public static final int PERMISSIONS_FINE_LOCATION = 3001;
    public static final int PERMISSIONS_BACKGROUND_LOCATION = 3003;

    //
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String[] PERMISSIONS_STORAGE_NEW = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    // global variables
    public static String globalSelectedProductId;
    public static String globalCurrentPhotoPath;
    public static String globalCurrentAttachmentPath;
    public static String globalGetHistoryParameter = "";
    public static String globalExternalFileDir = "";

    public static int globalMandatoryTaskPosition = -1;
    public static boolean ValueSelected;
    public static boolean mandatoryStepPhoto = false;
    public static boolean resendZoneMeasurements;
    public static boolean singleAssignmentResult = false;
    public static boolean codeScanned = false;
    public static EditText attributeValueforScan;

    // static constants, texts, numbers, etc.
    public static final String CONST_EN = "en";
    public static final String CONST_GR = "gr";
    public static final String CONST_AR = "ar";
    public static final String CONST_SORTED_BY_DATE = "sorted_by_date";
    public static final String CONST_SORTED_BY_DISTANCE = "sorted_by_distance";
    public static final String CONST_PARENT_ALL_PRODUCTS_ACTIVITY = "const_parent_all_products_activity";
    public static final String CONST_PARENT_ATTRIBUTES_ACTIVITY = "const_parent_attributes_activity";
    public static final String CONST_ASSIGNMENT_PHOTOS_FOLDER = "_Assignment_Photos";
    public static final String CONST_ASSIGNMENT_ATTACHMENTS_FOLDER = "_Assignment_Attachments";
    public static final String CONST_MANDATORY_TASKS_PHOTOS_FOLDER = "_Mandatory_Tasks";
    public static final String CONST_IS_FOR_NEW_ASSIGNMENT = "const_is_for_new_assignment";
    public static final String CONST_SINGLE_SELECTION = "const_single_selection";
    public static final String CONST_WAREHOUSE_PRODUCTS = "const_warehouse_products";
    public static final String CONST_EDIT_CONSUMABLES = "const_edit_consumables";
    public static final String CONST_SELECT_FROM_PICKING = "const_select_from_picking";

    public static final boolean CONST_FINISH_ACTIVITY = true;
    public static final boolean CONST_DO_NOT_FINISH_ACTIVITY = false;



    public static final boolean CONST_SHOW_PROGRESS_AND_TOAST = true;
    public static final boolean CONST_DO_NOT_SHOW_PROGRESS_AND_TOAST = false;

    // keys
    public static final String KEY_DOWNLOAD_ALL_DATA = "key_download_all_data";
    public static final String KEY_PRODUCT_DESCRIPTION = "key_product_description";
    public static final String KEY_PRODUCT_ATTRIBUTES = "key_product_attributes";
    public static final String KEY_PARENT_ACTIVITY = "key_parent_activity";
    public static final String KEY_PROJECT_PRODUCT_ID = "key_project_product_id";
    public static final String KEY_REPLACE_PROJECT_PRODUCT_ID = "key_replace_project_product_id";
    public static final String KEY_WAREHOUSE_ID = "key_warehouse_id";
    public static final String KEY_PRODUCT_ID = "key_product_id";
    public static final String KEY_PROJECT_INSTALLATION_ID = "key_project_installation_id";
    public static final String KEY_REFRESH_ZONES = "key_refresh_zones";
    public static final String KEY_REFRESH_INSTALLATIONS = "key_refresh_installations";
    public static final String KEY_AFTER_LOGIN = "key_after_login";
    public static final String KEY_PRODUCTID = "key_productId";
    public static final String KEY_CUSTOMERID = "key_customerId";
    public static final String KEY_GROUPED_ASSIGNMENT = "key_grouped_assignment";
    public static final String KEY_REFRESH_CUSTOM_FIELDS = "key_refresh_custom_fields";
    public static final String KEY_VORTEX_TABLE = "key_vortex_table";
    public static final String KEY_ASSIGNMENT_DATE = "key_assignment_date";
    public static final String KEY_SELECT_PRODUCTS_TO_ADD = "key_select_products_to_add";

}
