/*
 * Copyright (c) 2016. Developed by GTest Development
 */

package am.gtest.vortex.items;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.AssignmentActionsActivity;
import am.gtest.vortex.api.GetServices;
import am.gtest.vortex.support.MyCanEdit;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MySwitchLanguage;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.CONST_EN;
import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_SERVICES_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_USED_SERVICES_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class ServicesListActivity extends AppCompatActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private List<NewProductsList> filteredList;
    private static List<NewProductsList> ITEMS;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private String assignmentId = "";

    private TextView tvAssignmentId;
    private TextView tvTop2;
    private Button btnBottom;

    private String language;
    private String userName;
    private String userNameTitle;
    private String assignmentIdTitle;
    private String addNewService;
    private String noService;
    private String startTravelFirst;
    private String checkInFirst;
    private String noChangesAllowed;
    private String activityTitle;
    private String suggestedDone;

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
            getSupportActionBar().setTitle(activityTitle);
            getSupportActionBar().setSubtitle(userNameTitle + ": " + userName);
        }

        new MySliderMenu(this).mySliderMenu();

        assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");

        String RelatedServices = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_SERVICES_FOR_SHOW, assignmentId, "");

        if (RelatedServices.isEmpty() ||  MyUtils.isNetworkAvailable()){
            GetServices getServices = new GetServices(assignmentId, "0","0", "0");
            getServices.execute();
        }

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        tvTop2 = findViewById(R.id.tvTop2);

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
            btnBottom.setText(addNewService);

            btnBottom.setOnClickListener(v -> {
                if (MyCanEdit.canEdit(assignmentId)) {
                    Intent intent = new Intent(ServicesListActivity.this, ServicesToSelectActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        View recyclerView = findViewById(R.id.rvItemsList);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
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
            getSupportActionBar().setTitle(activityTitle);
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
            btnBottom.setText(addNewService);
        }

        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void changeTextLanguage() {
        if (language.equals("gr")) {
            userNameTitle = getString(R.string.user_gr);
            assignmentIdTitle = getString(R.string.assignment_id_gr);
            addNewService = getString(R.string.add_new_service_caps_gr);
            noService = getString(R.string.no_service_gr);
            startTravelFirst = getString(R.string.start_travel_first_gr);
            checkInFirst = getString(R.string.check_in_first_gr);
            noChangesAllowed = getString(R.string.no_changes_allowed_gr);
            activityTitle = getString(R.string.services_gr);
            suggestedDone = getString(R.string.suggested_done_gr);
        } else {
            userNameTitle = getString(R.string.user);
            assignmentIdTitle = getString(R.string.assignment_id);
            addNewService = getString(R.string.add_new_service_caps);
            noService = getString(R.string.no_service);
            startTravelFirst = getString(R.string.start_travel_first);
            checkInFirst = getString(R.string.check_in_first);
            noChangesAllowed = getString(R.string.no_changes_allowed);
            activityTitle = getString(R.string.services);
            suggestedDone = getString(R.string.suggested_done);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        ITEMS = new ArrayList<>();
        ITEMS.clear();

        String servicesString = MyPrefs.getStringWithFileName(PREF_FILE_USED_SERVICES_FOR_SHOW, assignmentId, "");

        if (!servicesString.equals("")) {
            try {
                JSONObject jObject = new JSONObject(servicesString);

                Log.e(LOG_TAG, "jObject Services: \n" + jObject.toString(2));

                if (jObject.getJSONArray("Services") != null && jObject.getJSONArray("Services").length() > 0) {
                    JSONArray jArrayServices = jObject.getJSONArray("Services");

                    for (int i = 0; i < jArrayServices.length(); i++) {
                        JSONObject jObjectServices = jArrayServices.getJSONObject(i);

                        addItem(createDummyItem(jObjectServices));
                    }
                } else {
                    Toast toast = Toast.makeText(ServicesListActivity.this, noService, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.tvItemName.setText(mValues.get(position).itemName);

            holder.chkSuggested.setChecked(holder.mItem.isSuggestedChecked.equals("1"));
            holder.chkSuggested.setEnabled(false);

            holder.chkUsed.setChecked(holder.mItem.isUsedChecked.equals("1"));
            holder.chkUsed.setEnabled(false);

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
        String isSuggestedChecked = "";
        String isUsedChecked = "";

        try {
            itemName = oneObject.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            serviceId = oneObject.getString("serviceId");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            isSuggestedChecked = oneObject.getString("suggested");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            isUsedChecked = oneObject.getString("used");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NewProductsList(itemName, serviceId, isSuggestedChecked, isUsedChecked);
    }

    public static class NewProductsList {

        public final String itemName;
        public final String serviceId;
        public final String isSuggestedChecked;
        public final String isUsedChecked;

        public NewProductsList(String itemName, String serviceId, String isSuggestedChecked, String isUsedChecked) {
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
            finish();
            Intent intent = new Intent(ServicesListActivity.this, AssignmentActionsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
