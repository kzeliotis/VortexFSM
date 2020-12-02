package am.gtest.vortex.items;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.api.ToSendServices;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MySwitchLanguage;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.CONST_EN;
import static am.gtest.vortex.support.MyGlobals.CONST_FINISH_ACTIVITY;
import static am.gtest.vortex.support.MyLocalization.localized_select_service;
import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_MEASUREMENTS_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_SERVICES_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_USED_SERVICES_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_USED_SERVICES_FOR_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class ServicesToSelectActivity extends AppCompatActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private List<NewProductsList> filteredList;
    private static List<NewProductsList> ITEMS;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private String assignmentId = "";
    private AlertDialog alertDialog;

    private TextView tvAssignmentId;
    private TextView tvTop2;
    private Button btnBottom;

    private String language;
    private String userName;
    private String userNameTitle;
    private String assignmentIdTitle;
    private String noService;
    private String noInetDataSaved;
    private String sendServicesData;
    private String checkServices;
    private String sureToLeave;
    private String suggestedDone;

    private JSONArray jArrayCheckboxes = new JSONArray();
    private final JSONObject jObjectServices = new JSONObject();
    private JSONObject jObjectServicesToShow = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.items_list_content, flBaseContainer, true);

        language = MyPrefs.getString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);
        changeTextLanguage();

        userName = MyPrefs.getString(PREF_USER_NAME, "");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_select_service);
            getSupportActionBar().setSubtitle(userNameTitle + ": " + userName);
        }

        new MySliderMenu(this).mySliderMenu();

        assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        tvTop2 = findViewById(R.id.tvTop2);
        btnBottom = findViewById(R.id.btnBottom);

        if (tvAssignmentId != null) {
            String assignmentIdText = assignmentIdTitle + ": " + assignmentId;
            tvAssignmentId.setText(assignmentIdText);
        }

        if (tvTop2 != null) {
            tvTop2.setGravity(Gravity.END);
            tvTop2.setVisibility(View.VISIBLE);
            tvTop2.setText(suggestedDone);
        }

        btnBottom = findViewById(R.id.btnBottom);
        if (btnBottom != null) {
            btnBottom.setVisibility(View.VISIBLE);
            btnBottom.setText(sendServicesData);

            btnBottom.setOnClickListener(v -> {

                if (jArrayCheckboxes.length() > 0) {

                    try {
                        jObjectServices.put("AssignmentId", assignmentId);
                        jObjectServices.put("Services", jArrayCheckboxes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String ServicesForShow = MyPrefs.getStringWithFileName(PREF_FILE_USED_SERVICES_FOR_SHOW, assignmentId, "");
                    if (ServicesForShow.length() > 0){
                        try {
                            jObjectServicesToShow = new JSONObject(ServicesForShow);
                            JSONArray jArrayToShow = MyJsonParser.getJsonArrayValue(jObjectServicesToShow, "Services");
                            for (int i = 0; i < jArrayCheckboxes.length(); i++){
                                JSONObject srv = jArrayCheckboxes.getJSONObject(i);
                                jArrayToShow.put(srv);
                            }

                            jObjectServicesToShow.put("Services", jArrayToShow);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        jObjectServicesToShow = jObjectServices;
                    }


                    MyPrefs.setStringWithFileName(PREF_FILE_USED_SERVICES_FOR_SYNC, assignmentId, jObjectServices.toString());
                    MyPrefs.setStringWithFileName(PREF_FILE_USED_SERVICES_FOR_SHOW, assignmentId, jObjectServicesToShow.toString());

                    if (MyUtils.isNetworkAvailable()) {
                        ToSendServices toSendServices = new ToSendServices(ServicesToSelectActivity.this, CONST_FINISH_ACTIVITY);
                        toSendServices.execute(assignmentId);
                    } else {
                        MyDialogs.showOK(ServicesToSelectActivity.this, noInetDataSaved);
                    }
                } else {
                    MyDialogs.showOK(ServicesToSelectActivity.this, checkServices);
                }
            });
        }

        View recyclerView = findViewById(R.id.rvItemsList);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (alertDialog !=null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) itemSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.itemSynchronize) {
//            new MySynchronize(this).mySynchronize();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void onRadioButtonClicked(View view) {
        language = new MySwitchLanguage(this).mySwitchLanguage(view);
        changeTextLanguage();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_select_service);
            getSupportActionBar().setSubtitle(userNameTitle + ": " + userName);
        }

        if (tvAssignmentId != null) {
            String assignmentIdText = assignmentIdTitle + ": " + assignmentId;
            tvAssignmentId.setText(assignmentIdText);
        }

        if (tvTop2 != null) {
            tvTop2.setText(suggestedDone);
        }

        if (btnBottom != null) {
            btnBottom.setText(sendServicesData);
        }

        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void changeTextLanguage() {
        if (language.equals("gr")) {
            userNameTitle = getString(R.string.user_gr);
            assignmentIdTitle = getString(R.string.assignment_id_gr);
            noService = getString(R.string.no_service_gr);
            noInetDataSaved = getString(R.string.no_internet_data_saved_gr);
            sendServicesData = getString(R.string.send_services_caps_gr);
            checkServices = getString(R.string.check_services_gr);
            sureToLeave = getString(R.string.sure_to_leave_gr);
            suggestedDone = getString(R.string.suggested_done_gr);
        } else {
            userNameTitle = getString(R.string.user);
            assignmentIdTitle = getString(R.string.assignment_id);
            noService = getString(R.string.no_service);
            noInetDataSaved = getString(R.string.no_internet_data_saved);
            sendServicesData = getString(R.string.send_services_caps);
            checkServices = getString(R.string.check_services);
            sureToLeave = getString(R.string.sure_to_leave);
            suggestedDone = getString(R.string.suggested_done);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        ITEMS = new ArrayList<>();
        ITEMS.clear();


        String services = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_SERVICES_FOR_SHOW, assignmentId, "");//MyPrefs.getString(PREF_DATA_SERVICES, "");

        if (!services.equals("")) {
            try {
                JSONArray jsonArr = new JSONArray(services);

                JSONArray sortedJsonArray = new JSONArray();
                List<JSONObject> jsonValues = new ArrayList<>();

                for (int i = 0; i < jsonArr.length(); i++) {
                    jsonValues.add(jsonArr.getJSONObject(i));
                }

                Collections.sort(jsonValues, new Comparator<JSONObject>() {
                    //You can change "Name" with "ID" if you want to sort by ID
                    private static final String KEY_NAME = "ServiceDescription";

                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        String valA = "";
                        String valB = "";

                        try {
                            valA = (String) a.get(KEY_NAME);
                            valB = (String) b.get(KEY_NAME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return valA.compareTo(valB);
                        //if you want to change the sort order, simply use the following: return -valA.compareTo(valB);
                    }
                });

                for (int i = 0; i < jsonArr.length(); i++) {
                    sortedJsonArray.put(jsonValues.get(i));
                }

                for (int i = 0; i < sortedJsonArray.length(); i++) {
                    JSONObject oneObjectProductsData = sortedJsonArray.getJSONObject(i);
                    addItem(createDummyItem(oneObjectProductsData));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(ServicesToSelectActivity.this, noService, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(ServicesToSelectActivity.this, noService, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        filteredList = new ArrayList<>();
        filteredList.addAll(ITEMS);
        mAdapter = new SimpleItemRecyclerViewAdapter(filteredList);
        recyclerView.setAdapter(mAdapter);
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable {

        private final List<NewProductsList> mValues;
        private final CustomFilter mFilter;

        public SimpleItemRecyclerViewAdapter(List<NewProductsList> items) {
            mValues = items;
            mFilter = new CustomFilter(SimpleItemRecyclerViewAdapter.this);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_name_checkboxes, parent, false);

            LinearLayout llItemsRvNameChevron = view.findViewById(R.id.llItemsRvNameChevron);
            llItemsRvNameChevron.setBackgroundResource(R.drawable.rounded_layout_purple);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.tvItemName.setText(mValues.get(position).itemName);

            holder.chkSuggested.setChecked(holder.mItem.isSuggestedChecked);
            holder.chkUsed.setChecked(holder.mItem.isUsedChecked);

            holder.chkSuggested.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                holder.mItem.isSuggestedChecked = isChecked;

                boolean jArrayCheckboxesContainsCurrentItem = false;

                if (jArrayCheckboxes.length() > 0) {
                    for (int i = 0; i < jArrayCheckboxes.length(); i++) {
                        try {
                            JSONObject jObject = jArrayCheckboxes.getJSONObject(i);

                            if (jObject.getString("serviceId").equals(holder.mItem.serviceId) ) {
                                jArrayCheckboxes.getJSONObject(i).put("suggested", holder.chkSuggested.isChecked() ? "1" : "0");

                                if (!holder.chkSuggested.isChecked() && !holder.chkUsed.isChecked()) {
                                    JSONArray jArrayExcludedEmpty = new JSONArray();

                                    for (int j = 0; j < jArrayCheckboxes.length(); j++) {
                                        if (i != j) {
                                            jArrayExcludedEmpty.put(jArrayCheckboxes.get(j));
                                        }
                                    }

                                    jArrayCheckboxes = jArrayExcludedEmpty;
                                }

                                jArrayCheckboxesContainsCurrentItem = true;
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (!jArrayCheckboxesContainsCurrentItem) {
                        JSONObject jObject = new JSONObject();
                        try {
                            jObject.put("serviceId", holder.mItem.serviceId);
                            jObject.put("name", holder.mItem.itemName);
                            jObject.put("suggested", holder.chkSuggested.isChecked() ? "1" : "0");
                            jObject.put("used", holder.chkUsed.isChecked() ? "1" : "0");
                            jArrayCheckboxes.put(jObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    JSONObject jObject = new JSONObject();
                    try {
                        jObject.put("serviceId", holder.mItem.serviceId);
                        jObject.put("name", holder.mItem.itemName);
                        jObject.put("suggested", holder.chkSuggested.isChecked() ? "1" : "0");
                        jObject.put("used", holder.chkUsed.isChecked() ? "1" : "0");
                        jArrayCheckboxes.put(jObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Log.e(LOG_TAG, "jArrayCheckboxes: \n" + jArrayCheckboxes.toString(2));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            holder.chkUsed.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                holder.mItem.isUsedChecked = isChecked;

                boolean jArrayCheckboxesContainsCurrentItem = false;

                if (jArrayCheckboxes.length() > 0) {
                    for (int i = 0; i < jArrayCheckboxes.length(); i++) {
                        try {
                            JSONObject jObject = jArrayCheckboxes.getJSONObject(i);

                            if (jObject.getString("serviceId").equals(holder.mItem.serviceId) ) {
                                jArrayCheckboxes.getJSONObject(i).put("used", holder.chkUsed.isChecked() ? "1" : "0");

                                if (!holder.chkSuggested.isChecked() && !holder.chkUsed.isChecked()) {
                                    JSONArray jArrayExcludedEmpty = new JSONArray();

                                    for (int j = 0; j < jArrayCheckboxes.length(); j++) {
                                        if (i != j) {
                                            jArrayExcludedEmpty.put(jArrayCheckboxes.get(j));
                                        }
                                    }

                                    jArrayCheckboxes = jArrayExcludedEmpty;
                                }

                                jArrayCheckboxesContainsCurrentItem = true;
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (!jArrayCheckboxesContainsCurrentItem) {
                        JSONObject jObject = new JSONObject();
                        try {
                            jObject.put("serviceId", holder.mItem.serviceId);
                            jObject.put("name", holder.mItem.itemName);
                            jObject.put("suggested", holder.chkSuggested.isChecked() ? "1" : "0");
                            jObject.put("used", holder.chkUsed.isChecked() ? "1" : "0");
                            jArrayCheckboxes.put(jObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    JSONObject jObject = new JSONObject();
                    try {
                        jObject.put("serviceId", holder.mItem.serviceId);
                        jObject.put("name", holder.mItem.itemName);
                        jObject.put("suggested", holder.chkSuggested.isChecked() ? "1" : "0");
                        jObject.put("used", holder.chkUsed.isChecked() ? "1" : "0");
                        jArrayCheckboxes.put(jObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Log.e(LOG_TAG, "jArrayCheckboxes: \n" + jArrayCheckboxes.toString(2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            holder.mView.setOnClickListener(v -> {

            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tvItemName;
            public final CheckBox chkSuggested;
            public final CheckBox chkUsed;
            public NewProductsList mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvItemName = view.findViewById(R.id.tvItemName);
                chkSuggested = view.findViewById(R.id.chkSuggested);
                chkUsed = view.findViewById(R.id.chkUsed);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + tvItemName.getText() + "'";
            }
        }

        public class CustomFilter extends Filter {
            private final SimpleItemRecyclerViewAdapter mAdapter;

            private CustomFilter(SimpleItemRecyclerViewAdapter mAdapter) {
                super();
                this.mAdapter = mAdapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredList.clear();
                final FilterResults results = new FilterResults();

                if (constraint.length() == 0) {
                    filteredList.addAll(ITEMS);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final NewProductsList mWords : ITEMS) {
                        if (mWords.toString().toLowerCase().contains(filterPattern)) {
                            filteredList.add(mWords);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                this.mAdapter.notifyDataSetChanged();
            }
        }
    }





    private static void addItem(NewProductsList item) {
        ITEMS.add(item);
    }

    private static NewProductsList createDummyItem(JSONObject oneObject) {
        String itemName = "";
        String serviceId = "";
        boolean isSuggestedChecked = false;
        boolean isUsedChecked = false;

        try {
            itemName = oneObject.getString("ServiceDescription");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            serviceId = oneObject.getString("ServiceId");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NewProductsList(itemName, serviceId, isSuggestedChecked, isUsedChecked);
    }

    public static class NewProductsList {

        public final String itemName;
        public final String serviceId;
        public boolean isSuggestedChecked;
        public boolean isUsedChecked;

        public NewProductsList(String itemName, String serviceId, boolean isSuggestedChecked, boolean isUsedChecked) {
            this.itemName = itemName;
            this.serviceId = serviceId;
            this.isSuggestedChecked = isSuggestedChecked;
            this.isUsedChecked = isUsedChecked;
        }

        @Override
        public String toString() {
            return itemName;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            new AlertDialog.Builder(this)
                    .setMessage(sureToLeave)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
    }
}