package dc.gtest.vortex.items;

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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyCanEdit;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MySwitchLanguage;

import static dc.gtest.vortex.support.MyGlobals.CONST_EN;
import static dc.gtest.vortex.support.MyGlobals.KEY_ID_SEARCH;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_MEASUREMENTS_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class MeasurementsListActivity extends AppCompatActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private List<NewProductsList> filteredList;
    private static List<NewProductsList> ITEMS;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private String assignmentId = "";

    private TextView tvAssignmentId;
    private Button btnBottom;

    private String language;
    private String userName;
    private String userNameTitle;
    private String assignmentIdTitle;
    private String addNewMeasurable;
    private String noMeasurement;
    private String activityTitle;
    private Boolean searchSerial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.items_list_content, flBaseContainer, true);
        searchSerial = getIntent().getBooleanExtra(KEY_ID_SEARCH, false);

        language = MyPrefs.getString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);
        changeTextLanguage();

        userName = MyPrefs.getString(PREF_USER_NAME, "");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(activityTitle);
            getSupportActionBar().setSubtitle(userNameTitle + ": " + userName);
        }

        new MySliderMenu(this).mySliderMenu();

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        TextView tvTop2 = findViewById(R.id.tvTop2);
        btnBottom = findViewById(R.id.btnBottom);

        assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");

        if (tvAssignmentId != null && !searchSerial) {
            String assignmentIdText = assignmentIdTitle + ": " + assignmentId;
            tvAssignmentId.setText(assignmentIdText);
        }

        if (tvTop2 != null) {
            tvTop2.setVisibility(View.VISIBLE);
            tvTop2.setText(SELECTED_PRODUCT.getProductDescription());
        }

        View recyclerView = findViewById(R.id.rvItemsList);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (btnBottom != null) {
            btnBottom.setVisibility(View.VISIBLE);
            btnBottom.setText(addNewMeasurable);

            btnBottom.setOnClickListener(v -> {
                if (searchSerial || MyCanEdit.canEdit(assignmentId)) {
                    Intent intent = new Intent(MeasurementsListActivity.this, MeasurementsToSelectActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(KEY_ID_SEARCH,  searchSerial);
                    startActivity(intent);
                }
            });
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
            getSupportActionBar().setTitle(activityTitle);
            getSupportActionBar().setSubtitle(userNameTitle + ": " + userName);
        }

        if (tvAssignmentId != null && !searchSerial) {
            String assignmentIdText = assignmentIdTitle + ": " + assignmentId;
            tvAssignmentId.setText(assignmentIdText);
        }

        if (btnBottom != null) {
            btnBottom.setText(addNewMeasurable);
        }

        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void changeTextLanguage() {
        if (language.equals("gr")) {
            userNameTitle = getString(R.string.user_gr);
            assignmentIdTitle = getString(R.string.assignment_id_gr);
            addNewMeasurable = getString(R.string.add_measurement_caps_gr);
            noMeasurement = getString(R.string.no_measurement_gr);
            activityTitle = getString(R.string.measurements_gr);
        } else {
            userNameTitle = getString(R.string.user);
            assignmentIdTitle = getString(R.string.assignment_id);
            addNewMeasurable = getString(R.string.add_measurement);
            noMeasurement = getString(R.string.no_measurement);
            activityTitle = getString(R.string.measurements);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        ITEMS = new ArrayList<>();
        ITEMS.clear();

        String oldProductId = getSharedPreferences("ProjectProductId", MODE_PRIVATE).getString("projectProductId", "");
        String newMeasurements = MyPrefs.getStringWithFileName(PREF_FILE_ADDED_MEASUREMENTS_FOR_SHOW, oldProductId, "");

        if (!newMeasurements.equals("")) {
            try {
                JSONObject jObject = new JSONObject(newMeasurements);

                Log.e(LOG_TAG, "jObject newMeasurements: \n" + jObject.toString(2));

                if (jObject.optJSONObject("Measurements") != null && jObject.optJSONObject("Measurements").length() > 0) {
                    JSONObject jObjectConsumables = jObject.getJSONObject("Measurements");

                    int consumablesQuantity = jObjectConsumables.names().length();

                    for (int i = 0; i < consumablesQuantity; i++) {
                        String itemName = jObjectConsumables.names().getString(i);
                        String itemValue = jObjectConsumables.getString(itemName);

                        addItem(createDummyItem(itemName, itemValue));
                    }
                } else {
                    Toast toast = Toast.makeText(MeasurementsListActivity.this, noMeasurement, Toast.LENGTH_LONG);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_chevron, parent, false);
            TextView tvChevron = view.findViewById(R.id.tvChevron);
            tvChevron.setBackgroundResource(R.color.light_blue_50);
            tvChevron.setPadding(2,2,2,2);
            tvChevron.setTextSize(16);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.tvItemName.setText(mValues.get(position).itemName);
            holder.tvChevron.setText(mValues.get(position).itemValue);
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
            public final TextView tvChevron;
            public NewProductsList mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvItemName = view.findViewById(R.id.tvItemName);
                tvChevron = view.findViewById(R.id.tvChevron);
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

    @Override
    protected void onResume() {
        super.onResume();

        View recyclerView = findViewById(R.id.rvItemsList);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }



    private static void addItem(NewProductsList item) {
        ITEMS.add(item);
    }

    private static NewProductsList createDummyItem(String itemName, String itemValue) {

        return new NewProductsList(itemName, itemValue);
    }

    public static class NewProductsList {

        public final String itemName;
        public final String itemValue;

        public NewProductsList(String itemName, String itemValue) {
            this.itemName = itemName;
            this.itemValue = itemValue;
        }

        @Override
        public String toString() {
            return itemName + ", " + itemValue;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }
}