package dc.gtest.vortex.unused;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MySwitchLanguage;

import static dc.gtest.vortex.support.MyGlobals.CONST_EN;
import static dc.gtest.vortex.support.MyLocalization.localized_product;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class HistoryListActivity extends AppCompatActivity {

    //private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private List<NewProductsList> filteredList;
    private static List<NewProductsList> ITEMS;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private String assignmentId = "";

    private TextView tvAssignmentId;
    private TextView tvResourceTitle;
    private TextView tvProblemTitle;
    private TextView tvSolutionTitle;
    private TextView tvMeasurementsTitle;
    private TextView tvConsumablesTitle;

    private TextView tvProductTitle;

    private String language;
    private String userName;
    private String userNameTitle;
    private String assignmentIdTitle;
    private String noHistory;
    private String dateTitle;
    private String resourceTitle;
    private String problemTitle;
    private String solutionTitle;
    private String measurementsTitle;
    private String consumablesTitle;
    private String history;
    private String productTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        language = MyPrefs.getString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);

        changeTextLanguage();

        userName = MyPrefs.getString(PREF_USER_NAME, "");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(history);
            getSupportActionBar().setSubtitle(userNameTitle + ": " + userName);
        }

        new MySliderMenu(this).mySliderMenu();

        assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");

        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        tvResourceTitle = findViewById(R.id.tvResourceTitle);
        tvProblemTitle = findViewById(R.id.tvProblemTitle);
        tvSolutionTitle = findViewById(R.id.tvSolutionTitle);
        tvMeasurementsTitle = findViewById(R.id.tvMeasurementsTitle);
        tvConsumablesTitle = findViewById(R.id.tvConsumablesTitle);

        View recyclerView = findViewById(R.id.rvHistoryList);

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.item_history_product, null);
        tvProductTitle = view.findViewById(R.id.tvProductTitle);

        if (tvAssignmentId != null) {
            String assignmentIdText = assignmentIdTitle + ": " + assignmentId;
            tvAssignmentId.setText(assignmentIdText);
        }

        if (tvResourceTitle != null) {
            tvResourceTitle.setText(resourceTitle);
        }

        if (tvProblemTitle != null) {
            tvProblemTitle.setText(problemTitle);
        }

        if (tvSolutionTitle != null) {
            tvSolutionTitle.setText(solutionTitle);
        }

        if (tvMeasurementsTitle != null) {
            tvMeasurementsTitle.setText(measurementsTitle);
        }

        if (tvConsumablesTitle != null) {
            tvConsumablesTitle.setText(consumablesTitle);
        }

        if (tvProductTitle != null) {
            tvProductTitle.setText(productTitle);
        }

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
            getSupportActionBar().setTitle(history);
            getSupportActionBar().setSubtitle(userNameTitle + ": " + userName);
        }

        if (tvAssignmentId != null) {
            String assignmentIdText = assignmentIdTitle + ": " + assignmentId;
            tvAssignmentId.setText(assignmentIdText);
        }

        if (tvResourceTitle != null) {
            tvResourceTitle.setText(resourceTitle);
        }

        if (tvProblemTitle != null) {
            tvProblemTitle.setText(problemTitle);
        }

        if (tvSolutionTitle != null) {
            tvSolutionTitle.setText(solutionTitle);
        }

        if (tvMeasurementsTitle != null) {
            tvMeasurementsTitle.setText(measurementsTitle);
        }

        if (tvConsumablesTitle != null) {
            tvConsumablesTitle.setText(consumablesTitle);
        }

        if (tvProductTitle != null) {
            tvProductTitle.setText(productTitle);
        }

        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void changeTextLanguage() {
        if (language.equals("gr")) {
            userNameTitle = getString(R.string.user_gr);
            assignmentIdTitle = getString(R.string.assignment_id_gr);
            noHistory = getString(R.string.no_history_gr);
            dateTitle = getString(R.string.date_gr);
            resourceTitle = getString(R.string.resource_gr);
            problemTitle = getString(R.string.problem_gr);
            solutionTitle = getString(R.string.solution_gr);
            measurementsTitle = getString(R.string.measurements_gr);
            consumablesTitle = getString(R.string.consumables_gr);
            history = getString(R.string.history_gr);
            productTitle = localized_product + ": ";
        } else {
            userNameTitle = getString(R.string.user);
            assignmentIdTitle = getString(R.string.assignment_id);
            noHistory = getString(R.string.no_history);
            dateTitle = getString(R.string.date);
            resourceTitle = getString(R.string.resource);
            problemTitle = getString(R.string.problem);
            solutionTitle = getString(R.string.solution);
            measurementsTitle = getString(R.string.measurements);
            consumablesTitle = getString(R.string.consumables);
            history = getString(R.string.history);
            productTitle = localized_product + ": ";
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        ITEMS = new ArrayList<>();
        ITEMS.clear();

        String assignmentData = MyPrefs.getString(PREF_DATA_ASSIGNMENTS, "");

        if (!assignmentData.equals("")) {
            try {
                JSONArray jArrayAssignments = new JSONArray(assignmentData);

                for (int j = 0; j < jArrayAssignments.length(); j++) {
                    JSONObject oneObject = jArrayAssignments.getJSONObject(j);

                    if (oneObject.getString("AssignmentId").equals(assignmentId)) {

                        if (oneObject.optJSONArray("HAssignments") != null && oneObject.optJSONArray("HAssignments").length() > 0) {
                            JSONArray jsonArr = oneObject.getJSONArray("HAssignments");

                            JSONArray sortedJsonArray = new JSONArray();
                            List<JSONObject> jsonValues = new ArrayList<>();

                            for (int i = 0; i < jsonArr.length(); i++) {
                                jsonValues.add(jsonArr.getJSONObject(i));
                            }

                            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                                //You can change "Name" with "ID" if you want to sort by ID
                                private static final String KEY_NAME = "HAssignmentId";

                                @Override
                                public int compare(JSONObject a, JSONObject b) {
                                    String valA = "";
                                    String valB = "";

                                    try {
                                        valA = String.valueOf(a.get(KEY_NAME));
                                        valB = String.valueOf(b.get(KEY_NAME));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //return valA.compareTo(valB);
                                    return -valA.compareTo(valB);
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

                        } else {
                            Toast toast = Toast.makeText(MyApplication.getContext(), noHistory, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
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

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            String fullDate = dateTitle + " " + mValues.get(position).date;
            holder.tvDate.setText(fullDate);

            String fullAssignmentId = "ID: " + " " + mValues.get(position).assignmentId;
            holder.tvAssignmentId.setText(fullAssignmentId);

            holder.tvResource.setText(mValues.get(position).resource);
            holder.tvProductMain.setText(mValues.get(position).productMain);
            holder.tvService.setText(mValues.get(position).service);
            holder.tvProblem.setText(mValues.get(position).problem);
            holder.tvSolution.setText(mValues.get(position).solution);

            HistoryProductsRecyclerView historyProductsRecyclerView = new HistoryProductsRecyclerView();
            historyProductsRecyclerView.setupRecyclerView(holder.rvHistoryMeasurements, mValues.get(position).measurements);

            HistoryConsumablesRecyclerView historyConsumablesRecyclerView = new HistoryConsumablesRecyclerView();
            historyConsumablesRecyclerView.setupRecyclerView(holder.rvConsumables, mValues.get(position).consumables);

            holder.mView.setOnClickListener(v -> {

            });

            holder.mView.setOnLongClickListener(v -> true);
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
            public final TextView tvDate;
            public final TextView tvAssignmentId;
            public final TextView tvResource;
            public final TextView tvProductMain;
            public final TextView tvService;
            public final TextView tvProblem;
            public final TextView tvSolution;
            public final RecyclerView rvHistoryMeasurements;
            public final RecyclerView rvConsumables;
            public NewProductsList mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvDate = view.findViewById(R.id.tvDate);
                tvAssignmentId = view.findViewById(R.id.tvAssignmentId);
                tvResource = view.findViewById(R.id.tvResource);
                tvProductMain = view.findViewById(R.id.tvProductMain);
                tvService = view.findViewById(R.id.tvService);
                tvProblem = view.findViewById(R.id.tvProblem);
                tvSolution = view.findViewById(R.id.tvSolution);
                rvHistoryMeasurements = view.findViewById(R.id.rvHistoryMeasurements);
                rvConsumables = view.findViewById(R.id.rvConsumables);
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
        String date = null;
        String assignmentId = null;
        String resource = null;
        String productMain = null;
        String service = null;
        String problem = null;
        String solution = null;
        String measurements = null;
        String consumables = null;

        try {
            date = oneObject.getString("DateStart");
            assignmentId = oneObject.getString("HAssignmentId");
            resource = oneObject.getString("ResourceName");
            productMain = oneObject.getString("Product");
            service = oneObject.getString("Service");
            problem = oneObject.getString("Problem");
            solution = oneObject.getString("Solution");
            measurements = oneObject.getString("HMeasurements");
            consumables = oneObject.getString("HConsumables");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NewProductsList(date, assignmentId, resource, productMain, service, problem, solution,
                measurements, consumables);
    }

    public static class NewProductsList {

        public final String date;
        public final String assignmentId;
        public final String resource;
        public final String productMain;
        public final String service;
        public final String problem;
        public final String solution;
        public final String measurements;
        public final String consumables;

        public NewProductsList(String date, String assignmentId, String resource, String productMain,
                               String service, String problem, String solution,
                               String measurements, String consumables) {
            this.date = date;
            this.assignmentId = assignmentId;
            this.resource = resource;
            this.productMain = productMain;
            this.service = service;
            this.problem = problem;
            this.solution = solution;
            this.measurements = measurements;
            this.consumables = consumables;
        }

        @Override
        public String toString() {
            return date + ", " + assignmentId + ", " + resource + ", " + problem + ", " +
                    solution + ", " + measurements + ", " + consumables + ", " + productMain + ", " +
                    service;
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