package dc.gtest.vortex.support;

import dc.gtest.vortex.application.MyApplication;

import static android.content.Context.MODE_PRIVATE;

public class MyPrefs {

    // data for sync
    public static final String PREF_FILE_START_TRAVEL_DATA_TO_SYNC = "pref_file_start_travel_data_to_sync";
    public static final String PREF_FILE_CHECK_IN_DATA_TO_SYNC = "pref_file_check_in_data_to_sync";
    public static final String PREF_FILE_CHECK_OUT_DATA_TO_SYNC = "pref_file_check_out_data_to_sync";
    public static final String PREF_FILE_RETURN_TO_BASE_DATA_TO_SYNC = "pref_file_return_to_base_data_to_sync";

    public static final String PREF_FILE_NEW_ASSIGNMENT_FOR_SYNC = "pref_file_new_assignment_for_sync";
    public static final String PREF_FILE_NEW_CUSTOMER_FOR_SYNC = "pref_file_new_customer_for_sync";
    public static final String PREF_FILE_NEW_PRODUCTS_FOR_SYNC = "pref_file_new_products_for_sync";
    public static final String PREF_FILE_NEW_ATTRIBUTES_FOR_SYNC = "pref_file_new_attributes_for_sync";
    public static final String PREF_FILE_NEW_INSTALLATION_ZONES_FOR_SYNC = "pref_file_new_installation_zones_for_sync";
    public static final String PREF_FILE_CUSTOM_FIELDS_FOR_SYNC = "pref_file_custom_fields_for_sync";

    public static final String PREF_FILE_ZONE_MEASUREMENTS_MAP = "pref_file_zone_measurements_map";
    public static final String PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC = "pref_file_zone_measurement_for_checkout_sync";
    public static final String PREF_FILE_ZONE_PRODUCTS_FOR_SYNC = "pref_file_zone_products_for_sync";
    public static final String PREF_FILE_ZONES_WITH_NO_MEASUREMENTS = "pref_file_zones_with_no_measurements";
    public static final String PREF_FILE_ZONES_WITH_NO_MEASUREMENTS_FOR_SYNC = "pref_file_zones_with_no_measurements_for_sync";
    public static final String PREF_FILE_ZONES_WITH_MEASUREMENTS = "pref_file_zones_with_measurements";
    public static final String PREF_FILE_MANDATORY_TASKS_FOR_SYNC = "pref_file_mandatory_tasks_for_sync";
    public static final String PREF_FILE_USE_PT_OVERNIGHT_FOR_SYNC = "pref_file_use_pt_overnight_for_sync";
    public static final String PREF_FILE_IMAGE_FOR_SYNC = "pref_file_image_to_be_sync";
    public static final String PREF_FILE_ATTACHMENT_FOR_SYNC = "pref_file_attachment_to_be_sync";
    public static final String PREF_FILE_SEND_REPORT_VALUE_FOR_SYNC = "pref_file_send_report_value_for_sync";
    public static final String PREF_FILE_DET_CHILDREN_FOR_SYNC = "pref_file_det_children_for_sync";

    public static final String PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC = "pref_file_added_consumables_for_sync";
    public static final String PREF_FILE_ADDED_MEASUREMENTS_FOR_SYNC = "pref_file_added_measurements_for_sync";
    public static final String PREF_FILE_USED_SERVICES_FOR_SYNC = "pref_file_used_services_for_sync";

    public static final String PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC = "pref_file_updated_attributes_for_sync";
    public static final String PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SYNC = "pref_file_product_to_installation_for_sync";

    // data for temp show while no Internet
    public static final String PREF_FILE_MANDATORY_TASKS_FOR_SHOW = "pref_file_mandatory_tasks_for_show";
    public static final String PREF_FILE_DET_CHILDREN_FOR_SHOW = "pref_file_det_children_for_show";
    public static final String PREF_FILE_ZONE_PRODUCTS_FOR_SHOW = "pref_file_zone_products_for_show";
    public static final String PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW = "pref_file_zone_products_data_for_show";
    public static final String PREF_FILE_ZONE_CF_DEFAULT_VALUES_DATA_FOR_SHOW = "pref_file_zone_cf_default_values_data_for_show";
    public static final String PREF_FILE_INSTALLATIONS_CF_DEFAULT_VALUES_DATA_FOR_SHOW = "pref_file_installations_cf_default_values_data_for_show";
    public static final String PREF_FILE_INSTALLATIONS_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW = "pref_file_installations_cf_details_default_values_data_for_show";
    public static final String PREF_FILE_COMPANY_CF_DEFAULT_VALUES_DATA_FOR_SHOW = "pref_file_company_cf_default_values_data_for_show";
    public static final String PREF_FILE_DET_CF_DEFAULT_VALUES_DATA_FOR_SHOW = "pref_file_det_cf_default_values_data_for_show";
    public static final String PREF_FILE_COMPANY_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW = "pref_file_company_cf_details_default_values_data_for_show";
    public static final String PREF_FILE_DET_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW = "pref_file_det_cf_details_default_values_data_for_show";
    public static final String PREF_FILE_CUSTOM_FIELD_EMPTY_DETAILS = "pref_file_custom_field_empty_details";
    public static final String PREF_FILE_NEW_ZONE_CUSTOM_FIELDS_DATA_FOR_SHOW = "pref_file_new_zone_custom_fields_data_for_show";
    public static final String PREF_FILE_INSTALLATION_PRODUCTS_DATA_FOR_SHOW = "pref_file_installation_products_data_for_show";
    public static final String PREF_FILE_ZONE_PRODUCT_MEASUREMENTS = "pref_file_zone_product_measurements";
    public static final String PREF_FILE_ADDED_CONSUMABLES_FOR_SHOW = "pref_file_added_consumables_for_show";
    public static final String PREF_FILE_COMMENTS_FOR_SHOW = "pref_file_comments_for_show";
    public static final String PREF_FILE_INSTALLATION_WARNING_FOR_SHOW = "pref_file_installation_warning_for_show";
    public static final String PREF_FILE_SIGNATURENAME = "pref_file_signature";
    public static final String PREF_FILE_SIGNATUREEMAIL = "pref_file_signatureEmail";
    public static final String PREF_FILE_SELECTED_STATUS = "pref_file_select_status";
    public static final String PREF_FILE_NOTES_FOR_SHOW = "pref_file_notes_for_show";
    public static final String PREF_FILE_CHARGED_AMOUNT_FOR_SHOW = "pref_file_charged_amount_for_show";
    public static final String PREF_FILE_PAID_AMOUNT_FOR_SHOW = "pref_file_paid_amount_for_show";
    public static final String PREF_FILE_ADDED_MEASUREMENTS_FOR_SHOW = "pref_file_added_measurements_for_show";
    public static final String PREF_FILE_USED_SERVICES_FOR_SHOW = "pref_file_used_services_for_show";
    public static final String PREF_FILE_ZONES_DATA_FOR_SHOW = "pref_file_zones_data_for_show";
    public static final String PREF_FILE_INSTALLATIONS_DATA_FOR_SHOW = "pref_file_installations_data_for_show";
    public static final String PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW = "pref_file_installation_zones_data_for_show";
    public static final String PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW = "pref_file_installation_custom_fields_data_for_show";
    public static final String PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW = "prf_file_related_consumables_for_show";
    public static final String PREF_FILE_RELATED_WAREHOUSE_CONSUMABLES_FOR_SHOW = "prf_file_related_warehouse_consumables_for_show";
    public static final String PREF_FILE_PICKING_LIST_FOR_SHOW = "pref_file_picking_list_for_show";
    public static final String PREF_FILE_RELATED_SERVICES_FOR_SHOW = "pref_file_related_services_for_show";
    public static final String PREF_FILE_NEW_ASSIGNMENT_SERVICES_FOR_SHOW = "pref_file_new_assignment_services_for_show";
    public static final String PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW = "pref_file_company_custom_fields_data_for_show";
    public static final String PREF_FILE_DET_CUSTOM_FIELDS_DATA_FOR_SHOW = "pref_file_det_custom_fields_data_for_show";
    public static final String PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW = "pref_file_product_to_installation_for_show";
    // downloaded data
    public static final String PREF_FILE_HISTORY_DATA = "pref_file_history_data";
    public static final String PREF_FILE_PRODUCTS_DATA = "pref_file_products_data";
    public static final String PREF_FILE_NO_INSTALLATION_PRODUCTS_DATA = "pref_file_no_installation_products_data";
    public static final String PREF_FILE_ID_SEARCH_PRODUCTS_DATA = "pref_file_id_search_products_data";
    public static final String PREF_FILE_INSTALLATION_PRODUCTS_DATA = "pref_file_installation_products_data";
    public static final String PREF_FILE_SUBASSIGNMENTS_DATA = "pref_file_products_data";
    public static final String PREF_FILE_INSTALLATION_HISTORY_DATA = "pref_file_installation_history_data";

    public static final String PREF_DATA_ASSIGNMENTS = "pref_data_assignments";
    public static final String PREF_DATA_ALL_PRODUCTS = "pref_data_all_products";
    public static final String PREF_DATA_WAREHOUSE_PRODUCTS = "pref_data_warehouse_products";
    public static final String PREF_DATA_PRODUCT_TYPES = "pref_data_product_types";
    public static final String PREF_DATA_ALL_ATTRIBUTES = "pref_data_all_attributes";
    public static final String PREF_DATA_ALL_CONSUMABLES = "pref_data_all_consumables";
    public static final String PREF_DATA_ALL_WAREHOUSE_CONSUMABLES = "pref_data_all_warehouse_consumables";
    public static final String PREF_DATA_STATUSES = "pref_data_statuses";
    public static final String PREF_DATA_ALL_STATUSES = "pref_data_statuses";
    public static final String PREF_DATA_ZONES_LIST = "pref_data_zones_list";
    public static final String PREF_DATA_INSTALLATION_ZONES_LIST = "pref_data_installation_zones_list";
    //public static final String PREF_DATA_INSTALLATION_CUSTOM_FIELDS_LIST = "pref_data_installation_custom_fields_list";
    public static final String PREF_DATA_INSTALLATIONS_LIST = "pref_data_installations_list";
    public static final String PREF_DATA_SERVICES = "pref_data_services";
    public static final String PREF_DATA_USER_PARTNER_RESOURCES = "pref_data_user_partner_resources";
    public static final String PREF_DATA_MANUALS = "pref_data_manuals";
    public static final String PREF_DATA_DEFAULT_TECH_ACTIONS = "pref_data_default_tech_actions";
    public static final String PREF_DATA_ASSIGNMENT_TYPES = "pref_data_assignment_types";
    public static final String PREF_DATA_ASSIGNMENT_INDICATORS = "pref_data_assignment_indicators";
    public static final String PREF_DATA_COMPANY_CUSTOM_FIELDS_LIST = "pref_data_company_custom_fields_list";
    public static final String PREF_DATA_MASTER_PROJECTS = "pref_master_projects";

    // for keeping assignments conditions
    public static final String PREF_FILE_IS_TRAVEL_STARTED = "pref_file_is_travel_started";
    public static final String PREF_FILE_IS_CHECKED_IN = "pref_file_is_checked_in";
    public static final String PREF_FILE_IS_CHECKED_OUT = "pref_file_is_checked_out";
    public static final String PREF_FILE_IS_WORK_STARTED = "pref_file_is_work_started";
    public static final String PREF_FILE_IS_PT_STARTED = "pref_file_is_pt_started";
    public static final String PREF_FILE_IS_PT_FINISHED = "pref_file_is_pt_finished";
    public static final String PREF_FILE_IS_OVERNIGHT = "pref_file_is_overnight";
    public static final String PREF_FILE_HAS_RETURNED_TO_BASE = "pref_file_has_returned_to_base";
    public static final String PREF_FILE_IS_SCANNED = "pref_file_is_scanned";
    public static final String PREF_FILE_CONSUMABLES_FROM_PICKING_SENT = "pref_file_consumables_from_picking_sent";
    public static final String PREF_KEY_IS_LOGGED_IN = "pref_key_is_logged_in";
    public static final String PREF_KEY_SELECTED_LANGUAGE = "pref_key_selected_language";

    public static final String PREF_DEVICE_ID = "device_id";
    public static final String PREF_REGISTERED_APK = "registered_apk";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_DEV_LOGIN = "pref_dev_login";
    public static final String PREF_AES_KEY = "pref_aes_key";

    public static final String PREF_SHOW_USE_PT_OVERNIGHT_BUTTONS = "pref_show_use_pt_overnight_buttons";
    public static final String PREF_SHOW_ZONE_PRODUCTS_BUTTON = "pref_show_zone_products_button";
    public static final String PREF_SHOW_START_WORK = "pref_show_start_work";
    public static final String PREF_SHOW_GET_ASSIGNMENT_COST = "pref_show_get_assignment_cost";
    public static final String PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT = "pref_send_zone_measurements_on_check_out";
    public static final String PREF_SHOW_INSTALLATIONS_BUTTON = "pref_show_installations_button";
    public static final String PREF_SHOW_SEND_REPORT_CHECKBOX = "pref_show_send_report_checkbox";
    public static final String PREF_KEEP_GPS_LOG = "pref_keep_gps_log";
    public static final String PREF_ENABLE_LOCATION_SERVICE = "pref_enable_location_service";
    public static final String PREF_DOWNLOAD_ALL_DATA = "pref_download_all_data";
    public static final String PREF_SCROLLABLE_PROBLEM_DESCRIPTION = "pref_scrollable_problem_description";
    public static final String PREF_AZURE_CONNECTION_STRING = "pref_azure_connection_string";
    public static final String PREF_SHOW_ALL_MASTER_PROJECTS = "pref_show_all_master_projects";
    public static final String PREF_MANDATORY_SIGNATURE = "pref_mandatory_signaure";
    public static final String PREF_SHOW_MANDATORY_TASKS_COMMENTS = "pref_show_mandatory_tasks_comments";
    public static final String PREF_HIDE_INTERNAL_NOTES = "pref_hide_internal_notes";
    public static final String PREF_CURRENT_APK_VERSION = "pref_current_apk_version";
    public static final String PREF_LOCATION_REFRESH_INTERVAL = "pref_location_refresh_interval";
    public static final String PREF_API_CONNECTION_TIMEOUT = "pref_api_connection_timeout";
    public static final String PREF_GPS_PRIORITY = "pref_GPS_priority";
    public static final String PREF_PROCESS_ASSIGNMENT_ON_SCAN = "pref_process_assignment_on_scan";
    public static final String PREF_ALLOW_MULTIPLE_CHECK_INS = "pref_allow_multiple_check_ins";
    public static final String PREF_SHOW_CHARGE_FIELD = "pref_show_charge_field";
    public static final String PREF_ADD_CONSUMABLE_FROM_LIST = "pref_add_consumable_from_list";
    public static final String PREF_ADD_CONSUMABLE_FROM_WAREHOUSE = "pref_add_consumable_from_warehouse";
    public static final String PREF_ADD_CONSUMABLE_FROM_PICKING = "pref_add_consumable_from_picking";
    public static final String PREF_QTY_LIMIT_CONSUMABLE_FROM_PICKING = "pref_qty_limit_consumable_from_picking";
    public static final String PREF_MANDATORY_CONSUMABLES_FROM_PICKING = "pref_mandatory_consumables_from_picking";
    public static final String PREF_ALLOW_CHECKIN_OUT_SUBASSIGNMENTS = "pref_allow_checkin_out_subassignments";
    public static final String PREF_SHOW_DET_CHILDREN = "pref_show_det_children";
    public static final String PREF_SHOW_PAYMENT_FILED = "pref_show_payment_field";
    public static final String PREF_START_TRAVEL_TIME = "start_travel_time";
    public static final String PREF_START_WORK_TIME = "start_work_time";
    public static final String PREF_CHECK_IN_TIME = "check_in_time";
    public static final String PREF_CHECK_IN_TIME_FOR_SHOW = "check_in_time_for_show";
    public static final String PREF_CHECK_OUT_TIME_FOR_SHOW = "check_out_time_for_show";
    public static final String PREF_START_LAT = "start_lat";
    public static final String PREF_CHECK_IN_LAT = "check_in_lat";
    public static final String PREF_START_LNG = "start_lng";
    public static final String PREF_CHECK_IN_LNG = "check_in_lng";
    public static final String PREF_PROJECT_ID = "project_id";
    public static final String PREF_ASSIGNMENT_ID = "assignment_id";
    public static final String PREF_IMAGE_SIZE = "image_size";
    public static final String PREF_ONLY_WIFI = "only_wifi";
    public static final String PREF_MEASURABLE_ATTRIBUTES = "measurable_attributes";
    public static final String PREF_LOCATION_SENDING_INTERVAL = "location_sending_interval";
    public static final String PREF_LOCATION_COUNTDOWN = "location_countdown";
    public static final String PREF_FIRST_HOST_URL = "first_host_url";
    public static final String PREF_ALTERNATIVE_HOST_URL = "alternative_host_url";
    public static final String PREF_BASE_HOST_URL = "base_host_url";
    public static final String PREF_CURRENT_LAT = "current_lat";
    public static final String PREF_CURRENT_LNG = "current_lng";
    public static final String PREF_USER_NAME = "user_name";

    public static final String PREF_SELECTED_CUSTOMER_ID = "selected_customer_id";
    public static final String PREF_WAREHOUSEID = "pref_warehouseid";
    public static final String PREF_USERID = "pref_userid";

    public static void clearUserData() {
        MyApplication.getContext().getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply();
    }

    public static void setString(String key, String value) {
        MyApplication.getContext().getSharedPreferences("user", MODE_PRIVATE).edit().putString(key, value).apply();
    }

    public static String getString(String key, String defaultValue) {
        return MyApplication.getContext().getSharedPreferences("user", MODE_PRIVATE).getString(key, defaultValue);
    }

    public static void setBoolean(String key, boolean value) {
        MyApplication.getContext().getSharedPreferences("user", MODE_PRIVATE).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return MyApplication.getContext().getSharedPreferences("user", MODE_PRIVATE).getBoolean(key, defaultValue);
    }

    public static void setInt(String key, int value) {
        MyApplication.getContext().getSharedPreferences("user", MODE_PRIVATE).edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defaultValue) {
        return MyApplication.getContext().getSharedPreferences("user", MODE_PRIVATE).getInt(key, defaultValue);
    }



    // separate setter and getter for device ID to keep saved device ID even after logout
    public static void setDeviceId(String key, String value) {
        MyApplication.getContext().getSharedPreferences(PREF_DEVICE_ID, MODE_PRIVATE).edit().putString(key, value).apply();
    }

    public static String getDeviceId(String key, String defaultValue) {
        return MyApplication.getContext().getSharedPreferences(PREF_DEVICE_ID, MODE_PRIVATE).getString(key, defaultValue);
    }



//    public static void setOneAssignmentString(String assignmentId, String key, String value) {
//        MyApplication.getContext().getSharedPreferences(assignmentId, MODE_PRIVATE).edit().putString(key, value).apply();
//    }

//    public static String getOneAssignmentString(String assignmentId, String key) {
//        return MyApplication.getContext().getSharedPreferences(assignmentId, MODE_PRIVATE).getString(key, "");
//    }

    public static void clearOneAssignmentData(String assignmentId) {
        MyApplication.getContext().getSharedPreferences(assignmentId, MODE_PRIVATE).edit().clear().apply();
    }



    public static void setStringWithFileName(String fileName, String key, String value) {
        MyApplication.getContext().getSharedPreferences(fileName, MODE_PRIVATE).edit().putString(key, value).apply();
    }

    public static String getStringWithFileName(String fileName, String key, String defaultValue) {
        return MyApplication.getContext().getSharedPreferences(fileName, MODE_PRIVATE).getString(key, defaultValue);
    }

    public static void removeStringWithFileName(String fileName, String assignmentId) {
        MyApplication.getContext().getSharedPreferences(fileName, MODE_PRIVATE).edit().remove(assignmentId).apply();
    }

    public static void setBooleanWithFileName(String fileName, String key, boolean value) {
        MyApplication.getContext().getSharedPreferences(fileName, MODE_PRIVATE).edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanWithFileName(String fileName, String key, boolean defaultValue) {
        return MyApplication.getContext().getSharedPreferences(fileName, MODE_PRIVATE).getBoolean(key, defaultValue);
    }
}
