package am.gtest.vortex.activities;

import android.content.Context;
import android.content.Intent;
import android.os.*;
import androidx.annotation.NonNull;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.AssignmentsRvAdapter;
import am.gtest.vortex.adapters.MySpinnerAdapter;
import am.gtest.vortex.api.GetAssignments;
import am.gtest.vortex.api.GetMobileSettings;
import am.gtest.vortex.api.GetStatuses;
import am.gtest.vortex.api.SendLogin;
import am.gtest.vortex.data.AssignmentsData;
import am.gtest.vortex.data.StatusesData;
import am.gtest.vortex.support.CalendarView;
import am.gtest.vortex.support.MyDateTime;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MyUtils;
import am.gtest.vortex.support.PermGetLocation;

import static am.gtest.vortex.support.MyGlobals.ASSIGNMENTS_LIST;
import static am.gtest.vortex.support.MyGlobals.CALENDAR_EVENTS;
import static am.gtest.vortex.support.MyGlobals.CONST_SORTED_BY_DATE;
import static am.gtest.vortex.support.MyGlobals.CONST_SORTED_BY_DISTANCE;
import static am.gtest.vortex.support.MyGlobals.KEY_AFTER_LOGIN;
import static am.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static am.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_CHECK_LOCATION_SETTINGS;
import static am.gtest.vortex.support.MyGlobals.PERMISSIONS_FINE_LOCATION;
import static am.gtest.vortex.support.MyGlobals.STATUSES_LIST;
import static am.gtest.vortex.support.MyLocalization.localized_all_caps;
import static am.gtest.vortex.support.MyLocalization.localized_select_status;
import static am.gtest.vortex.support.MyLocalization.localized_selected_date;
import static am.gtest.vortex.support.MyLocalization.localized_to_exit;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_STATUSES;
import static am.gtest.vortex.support.MyPrefs.PREF_PASSWORD;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class AssignmentsActivity extends BaseDrawerActivity implements View.OnClickListener {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private AssignmentsRvAdapter assignmentsRvAdapter;

    private Spinner spStatusFilter;

    public static String selectedDate = "";
    public static String searchedText = "";
    public static String selectedStatus = "";
    public static String sortedBy = "";

    private CalendarView calendarView;
    private RecyclerView rvAssignments;

    private MenuItem itemSwitchView;
    private LinearLayout llSortingButtons;
    private ImageView ivSortByTime;
    private ImageView ivSortByDistance;
    //    private LinearLayout llDateAndClearFilters;
    private TextView tvSelectedDate;
    private SearchView searchView;

    private boolean doSpinnerItemSelected = true;
    private boolean doSearchTexChanged = true;

    private PermGetLocation permGetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permGetLocation = new PermGetLocation(this);
        //permGetLocation.myRequestPermission(PERMISSIONS_FINE_LOCATION);

        GetMobileSettings getMobileSettings = new GetMobileSettings(this, permGetLocation);
        getMobileSettings.execute();

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_assignments, flBaseContainer, true);

        llSortingButtons = findViewById(R.id.llSortingButtons);
        ivSortByTime = findViewById(R.id.ivSortByTime);
        ivSortByDistance = findViewById(R.id.ivSortByDistance);
//        llDateAndClearFilters = findViewById(R.id.llDateAndClearFilters);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        ImageView ivClearFilters = findViewById(R.id.ivClearFilters);
        rvAssignments = findViewById(R.id.rvAssignments);
        calendarView = findViewById(R.id.calendarView);
        spStatusFilter = findViewById(R.id.spStatusFilter);

        spStatusFilter.setVisibility(View.VISIBLE); // visibility is GONE for other pages

        ivSortByTime.setOnClickListener(this);
        ivSortByDistance.setOnClickListener(this);
        ivClearFilters.setOnClickListener(this);

        setupStatusesSpinner();
        spStatusFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                Log.e(LOG_TAG, "----------------------------------------------------- start onItemSelected doSpinnerItemSelected: " + doSpinnerItemSelected);
//                Log.e(LOG_TAG, "---------------------------- view.getId(): " + view.getId());
//                Log.e(LOG_TAG, "---------------------------- R.id.spProductTypes: " + R.id.spStatusFilter);

                if (view != null && view.getId() != R.id.spStatusFilter) {

                    if (doSpinnerItemSelected) {

                        if (position == 0) {
                            selectedStatus = "";
                        } else {
                            selectedStatus = spStatusFilter.getSelectedItem().toString().toLowerCase();
                        }

                        assignmentsRvAdapter.getFilter().filter(searchedText);
                    } else {
                        doSpinnerItemSelected = true;
                    }
                }

//                Log.e(LOG_TAG, "------------------------------------------------------------ end onItemSelected doSpinnerItemSelected: " + doSpinnerItemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //clearFilters();

//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        if (lm != null && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            new AlertDialog.Builder(AssignmentsActivity.this)
//                    .setMessage(localized_enable_gps)
//                    .setPositiveButton(R.string.ok, (dialog, which) -> {
//                        dialog.dismiss();
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    })
//                    .setNegativeButton(R.string.cancel, null)
//                    .show();
//        }


        createCalendar();
        AssignmentsData.generate(this);
        calendarView.updateCalendar(CALENDAR_EVENTS);
        assignmentsRvAdapter = new AssignmentsRvAdapter(ASSIGNMENTS_LIST, AssignmentsActivity.this);
        rvAssignments.setAdapter(assignmentsRvAdapter);

        if (MyUtils.isNetworkAvailable()) {
            boolean afterLogIn = getIntent().getBooleanExtra(KEY_AFTER_LOGIN, false);
            boolean downloadAllData = getIntent().getBooleanExtra(KEY_DOWNLOAD_ALL_DATA, true);
            if (!afterLogIn) {
                String password = MyPrefs.getStringWithFileName(PREF_PASSWORD, "1", "");
                String username = MyPrefs.getString(PREF_USER_NAME, "");
                SendLogin sendLogin = new SendLogin(AssignmentsActivity.this, true, downloadAllData);
                sendLogin.execute(username, password);
            } else {
                GetAssignments getAssignments = new GetAssignments(AssignmentsActivity.this, downloadAllData);
                getAssignments.execute();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Log.e(LOG_TAG, "---------------------------------------------------- onResume doSpinnerItemSelected: " + doSpinnerItemSelected);

        updateUiTexts();

        // moved to onCreate to avoid recreation of recycler view for coming back from other pages to the same item
//        createCalendar();
//        AssignmentsData.generate();
//        calendarView.updateCalendar(CALENDAR_EVENTS);
//        assignmentsRvAdapter = new AssignmentsRvAdapter(ASSIGNMENTS_LIST_FILTERED, AssignmentsActivity.this);
//        rvAssignments.setAdapter(assignmentsRvAdapter);

        assignmentsRvAdapter.getFilter().filter(searchedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        permGetLocation.stopLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_calendar_synch, menu);

        itemSwitchView = menu.findItem(R.id.itemSwitchView);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) itemSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (doSearchTexChanged) {
                    searchedText = newText;
                    assignmentsRvAdapter.getFilter().filter(newText);
                } else {
                    doSearchTexChanged = true;
                }
                return true;
            }
        });


//        Log.e(LOG_TAG, "------------------- onCreateOptionsMenu.");

        return true;
    }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            int id = item.getItemId();

            if (id == R.id.itemSwitchView) {
                if (item.getTitle().equals("Calendar")) {

                    item.setIcon(R.drawable.ic_view_list_black_24dp);
                    item.setTitle("List");

                    llSortingButtons.setVisibility(View.GONE);
//                llDateAndClearFilters.setVisibility(View.GONE);
                    rvAssignments.setVisibility(View.GONE);
                    calendarView.setVisibility(View.VISIBLE);
                    return true;
                } else {
                    item.setIcon(R.drawable.ic_date_range_black_24dp);
                    item.setTitle("Calendar");

                    calendarView.setVisibility(View.GONE);
                    llSortingButtons.setVisibility(View.VISIBLE);
//                llDateAndClearFilters.setVisibility(View.VISIBLE);
                    rvAssignments.setVisibility(View.VISIBLE);

                    assignmentsRvAdapter.getFilter().filter("");
                    spStatusFilter.setSelection(0);
                    return true;
                }
            } else if (id == R.id.itemScanner) {
                //final Activity activity = this;

                IntentIntegrator integrator = new IntentIntegrator(AssignmentsActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(true);
                integrator.initiateScan();

                return true;
            }

            return super.onOptionsItemSelected(item);
        }





        @Override
        public void onClick (View view){
            switch (view.getId()) {
                case R.id.ivSortByTime:

                    ivSortByTime.setBackgroundResource(R.color.light_blue_50);
                    ivSortByDistance.setBackgroundResource(R.color.grey_50);

                    sortedBy = CONST_SORTED_BY_DATE;

                    Collections.sort(ASSIGNMENTS_LIST, (a, b) -> a.getStartDateTime().compareTo(b.getStartDateTime()));
                    assignmentsRvAdapter.notifyDataSetChanged();
                    break;

                case R.id.ivSortByDistance:

                    ivSortByTime.setBackgroundResource(R.color.grey_50);
                    ivSortByDistance.setBackgroundResource(R.color.light_blue_50);

                    sortedBy = CONST_SORTED_BY_DISTANCE;

                    Collections.sort(ASSIGNMENTS_LIST, (a, b) -> {
                        Double distance = (double) a.getDistance();
                        Double distance1 = (double) b.getDistance();
                        if (distance.compareTo(distance1) < 0) {
                            return -1;
                        } else if (distance.compareTo(distance1) > 0) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });

                    assignmentsRvAdapter.notifyDataSetChanged();
                    break;

                case R.id.ivClearFilters:
                    clearFilters();
                    assignmentsRvAdapter.getFilter().filter("");

                    break;

            }
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            switch (requestCode) {
                case PERMISSIONS_FINE_LOCATION:
                    permGetLocation.myPermissionsResult(requestCode, permissions, grantResults);
                    break;
            }
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            switch (requestCode) {
                case OTHER_APP_RESULT_CHECK_LOCATION_SETTINGS:
                    permGetLocation.myActivityResult(requestCode, resultCode, data);
                    break;
                case 49374:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode,  resultCode, data);
                    if (result != null){
                        if (result.getContents() != null){
                            String ScannedCode = result.getContents();
                           // assignmentsRvAdapter.getFilter().filter(ScannedCode);
                            //Toast.makeText(this,  ScannedCode, Toast.LENGTH_LONG).show();
                            searchView.onActionViewExpanded();
                            searchView.setIconified(false);
                            searchView.setQuery(ScannedCode, false);
                            searchView.clearFocus();

                        }
                    }
                    break;
            }
        }

        private void clearFilters () {

//        Log.e(LOG_TAG, " -------------- inside clearFilters.");

            selectedDate = "";
            searchedText = "";
            selectedStatus = "";
            sortedBy = "";

            ivSortByTime.setBackgroundResource(R.color.grey_50);
            ivSortByDistance.setBackgroundResource(R.color.grey_50);

            String selectedDateText = localized_selected_date + ": " + localized_all_caps;
            tvSelectedDate.setText(selectedDateText);

            doSearchTexChanged = false;
            if (searchView != null) {
                searchView.setQuery("", false);
                searchView.setIconified(true);
            }
            doSearchTexChanged = true;

            if (spStatusFilter.getSelectedItemPosition() != 0) {
                doSpinnerItemSelected = false;
            }
            spStatusFilter.setSelection(0);
        }

        private void createCalendar () {

            CALENDAR_EVENTS = new HashSet<>();
//        events.add(new Date());
//        calendarView.updateCalendar(events);

            calendarView.setClickEventHandler(date -> {

                selectedDate = MyDateTime.getAppDateFromDate(date);

                Toast.makeText(AssignmentsActivity.this, selectedDate, Toast.LENGTH_SHORT).show();

                Log.e(LOG_TAG, " -------------- selectedDate: " + selectedDate + "; date: " + date);

                assignmentsRvAdapter.getFilter().filter(searchedText);

                itemSwitchView.setIcon(R.drawable.ic_date_range_black_24dp);
                itemSwitchView.setTitle("Calendar");

                calendarView.setVisibility(View.GONE);
                llSortingButtons.setVisibility(View.VISIBLE);
//            llDateAndClearFilters.setVisibility(View.VISIBLE);
                rvAssignments.setVisibility(View.VISIBLE);

                String selectedDateText = localized_selected_date + ": " + selectedDate;
                tvSelectedDate.setText(selectedDateText);
            });

            // assign event handler
            calendarView.setEventHandler(date -> {
                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(AssignmentsActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            });
        }

        private void setupStatusesSpinner () {
            String statuses = MyPrefs.getString(PREF_DATA_STATUSES, "");

            if (statuses.isEmpty()) {
                if (MyUtils.isNetworkAvailable()) {
                    GetStatuses getStatuses = new GetStatuses(this);
                    getStatuses.execute();
                }
            } else {
                StatusesData.generate(statuses);
                setAdapterOnSpinner(this, spStatusFilter);
            }
        }

        public static void setAdapterOnSpinner (Context ctx, Spinner spStatusFilter){

            String[] statusesArray = new String[STATUSES_LIST.size() + 1];
            statusesArray[0] = localized_select_status + ":";

            for (int i = 0; i < STATUSES_LIST.size(); i++) {
                statusesArray[i + 1] = STATUSES_LIST.get(i).getStatusDescription().toUpperCase();
            }

            int oldPosition = spStatusFilter.getSelectedItemPosition();

            spStatusFilter.setAdapter(new MySpinnerAdapter(ctx, statusesArray));

//        if (oldPosition != -1 && oldPosition != 0) {
            spStatusFilter.setSelection(oldPosition);
//        }
        }

        public void onRadioButtonClicked (View view){
            MyLocalization.saveNewLanguage(this, view);
            updateUiTexts();
            setAdapterOnSpinner(this, spStatusFilter);
            new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
        }

        private void updateUiTexts () {

            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
            }

//        btnSortByTime.setText(localized_sort_by_time);
//        btnSortByDistance.setText(localized_sort_by_distance);

            String selectedDateText = localized_selected_date + ": " + localized_all_caps;
            if (!selectedDate.isEmpty()) {
                selectedDateText = localized_selected_date + ": " + selectedDate;
            }
            tvSelectedDate.setText(selectedDateText);
        }

        @Override
        public void onBackPressed () {

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                new AlertDialog.Builder(AssignmentsActivity.this)
                        .setMessage(localized_to_exit)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            dialog.dismiss();
                            finishAffinity();
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        }
    }
