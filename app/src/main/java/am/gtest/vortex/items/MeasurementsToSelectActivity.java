package am.gtest.vortex.items;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.MySpinnerAdapter;
import am.gtest.vortex.api.ToSendNewMeasurement;
import am.gtest.vortex.models.MeasurementModel;
import am.gtest.vortex.support.MyDialogs;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MySwitchLanguage;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.CONST_EN;
import static am.gtest.vortex.support.MyGlobals.CONST_FINISH_ACTIVITY;
import static am.gtest.vortex.support.MyGlobals.MANDATORY_MEASUREMENTS_LIST;
import static am.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static am.gtest.vortex.support.MyGlobals.globalSelectedProductId;
import static am.gtest.vortex.support.MyLocalization.localized_cancel;
import static am.gtest.vortex.support.MyLocalization.localized_save;
import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_MEASUREMENTS_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_MEASUREMENTS_FOR_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static am.gtest.vortex.support.MyPrefs.PREF_MEASURABLE_ATTRIBUTES;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class MeasurementsToSelectActivity extends AppCompatActivity {

    //private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
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
    private String measurementValue;
    private String noMeasurement;
    private String noInetDataSaved;
    private String sendMeasurementData;
    private String activityTitle;
    private String selectSetSaveMeasurement;
    private String sureToLeave;
    private String ProjectProductId;

    private String savedMeasurements = "";
    private final List<MeasurementModel> MeasurementsToSend = new ArrayList<>();
    private String oldProductId;

    private String selectMeasurement;

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
        String newProductName = SELECTED_PRODUCT.getProductDescription();
        ProjectProductId = SELECTED_PRODUCT.getProjectProductId();
        oldProductId = getSharedPreferences("ProjectProductId", MODE_PRIVATE).getString("projectProductId", "");


        tvAssignmentId = findViewById(R.id.tvAssignmentId);
        TextView tvTop2 = findViewById(R.id.tvTop2);
        btnBottom = findViewById(R.id.btnBottom);

        if (tvAssignmentId != null) {
            String assignmentIdText = assignmentIdTitle + ": " + assignmentId;
            tvAssignmentId.setText(assignmentIdText);
        }

        if (tvTop2 != null) {
            tvTop2.setVisibility(View.VISIBLE);
            tvTop2.setText(newProductName);
        }

        btnBottom = findViewById(R.id.btnBottom);
        if (btnBottom != null) {
            btnBottom.setVisibility(View.VISIBLE);
            btnBottom.setText(sendMeasurementData);

            btnBottom.setOnClickListener(v -> {

                if (savedMeasurements != null && !savedMeasurements.equals("")) {

                    String newMeasurementsJsonString = "{\"assignmentId\":\"" + assignmentId +
                            "\",\"oldProductId\":\"" + oldProductId +
                            "\",\"Measurements\":{" + savedMeasurements + "}}";

                    String withoutLastComma = "";

                    if (!newMeasurementsJsonString.equals("")) {
                        String firstPart = newMeasurementsJsonString.substring(0, newMeasurementsJsonString.length()-3);
                        String lastPart = newMeasurementsJsonString.substring(newMeasurementsJsonString.length()-2);
                        withoutLastComma = firstPart + lastPart;
                    }

                    String MeasurmementsForShow = MyPrefs.getStringWithFileName(PREF_FILE_ADDED_MEASUREMENTS_FOR_SHOW, oldProductId, "");
                    if (MeasurmementsForShow.length() > 0){
                        MeasurmementsForShow = MeasurmementsForShow.substring(0, MeasurmementsForShow.length()-2);
                        MeasurmementsForShow = MeasurmementsForShow + ", " + savedMeasurements.substring(0, savedMeasurements.length()-1) + "}}";
                    } else {
                        MeasurmementsForShow = withoutLastComma;
                    }

                    MyPrefs.setStringWithFileName(PREF_FILE_ADDED_MEASUREMENTS_FOR_SYNC, oldProductId, withoutLastComma);
                    MyPrefs.setStringWithFileName(PREF_FILE_ADDED_MEASUREMENTS_FOR_SHOW, oldProductId, MeasurmementsForShow);


                    for(int i = MANDATORY_MEASUREMENTS_LIST.size()-1; i >= 0; i--){

                        String MeasurementName = MANDATORY_MEASUREMENTS_LIST.get(i).getAttributeName();
                        String ProjectProductId = MANDATORY_MEASUREMENTS_LIST.get(i).getProjectProductId();

                        if(ProjectProductId.equals(globalSelectedProductId)){
                            for(int m = 0; m < MeasurementsToSend.size(); m++){
                                if(MeasurementsToSend.get(m).getAttributeName().equalsIgnoreCase(MeasurementName) &&  MeasurementsToSend.get(m).getProjectProductId().equalsIgnoreCase(ProjectProductId)){
                                    MANDATORY_MEASUREMENTS_LIST.get(i).setMeasurementCompleted("Completed");
                                }
                            }
                        }

                    }

                    if (MyUtils.isNetworkAvailable()) {
                        ToSendNewMeasurement toSendNewMeasurement = new ToSendNewMeasurement(MeasurementsToSelectActivity.this, CONST_FINISH_ACTIVITY);
                        toSendNewMeasurement.execute(oldProductId);
                    } else {
                        MyDialogs.showOK(MeasurementsToSelectActivity.this, noInetDataSaved);
                    }
                } else {
                    MyDialogs.showOK(MeasurementsToSelectActivity.this, selectSetSaveMeasurement);
                }
            });
        }

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

        if (btnBottom != null) {
            btnBottom.setText(sendMeasurementData);
        }

        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void changeTextLanguage() {
        if (language.equals("gr")) {
            userNameTitle = getString(R.string.user_gr);
            assignmentIdTitle = getString(R.string.assignment_id_gr);
            measurementValue = getString(R.string.measurement_value_gr);
            noMeasurement = getString(R.string.no_measurement_gr);
            noInetDataSaved = getString(R.string.no_internet_data_saved_gr);
            sendMeasurementData = getString(R.string.send_measurement_data_caps_gr);
            activityTitle = getString(R.string.select_measurement_gr);
            selectSetSaveMeasurement = getString(R.string.select_set_save_measurement_gr);
            sureToLeave = getString(R.string.sure_to_leave_gr);
            selectMeasurement = getString(R.string.select_measurement_gr);
        } else {
            userNameTitle = getString(R.string.user);
            assignmentIdTitle = getString(R.string.assignment_id);
            measurementValue = getString(R.string.measurement_value);
            noMeasurement = getString(R.string.no_measurement);
            noInetDataSaved = getString(R.string.no_internet_data_saved);
            sendMeasurementData = getString(R.string.send_measurement_data_caps);
            activityTitle = getString(R.string.select_measurement);
            selectSetSaveMeasurement = getString(R.string.select_set_save_measurement);
            sureToLeave = getString(R.string.sure_to_leave);
            selectMeasurement = getString(R.string.select_measurement);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        ITEMS = new ArrayList<>();
        ITEMS.clear();

        String measurementsString = MyPrefs.getString(PREF_MEASURABLE_ATTRIBUTES, "");

        if (!measurementsString.equals("")) {
            try {
                JSONArray jsonArr = new JSONArray(measurementsString);

                JSONArray sortedJsonArray = new JSONArray();
                List<JSONObject> jsonValues = new ArrayList<>();

                for (int i = 0; i < jsonArr.length(); i++) {
                    jsonValues.add(jsonArr.getJSONObject(i));
                }

                Collections.sort(jsonValues, new Comparator<JSONObject>() {
                    //You can change "Name" with "ID" if you want to sort by ID
                    private static final String KEY_NAME = "MeasurableAttributeDescription";

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
            }
        } else {
            Toast toast = Toast.makeText(MeasurementsToSelectActivity.this, noMeasurement, Toast.LENGTH_LONG);
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

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_chevron, parent, false);

            LinearLayout llItemsRvNameChevron = view.findViewById(R.id.llItemsRvNameChevron);
            llItemsRvNameChevron.setBackgroundResource(R.drawable.rounded_layout_purple);

            TextView tvChevron = view.findViewById(R.id.tvChevron);
            tvChevron.setBackgroundResource(R.drawable.ic_chevron_right_purple_24dp);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.tvItemName.setText(mValues.get(position).itemName);

            holder.mView.setOnClickListener(v -> {
                try {
                    JSONArray jArrayDefaultValues = new JSONArray(holder.mItem.defaultValues);

                    if (jArrayDefaultValues.length() > 0) {
                        @SuppressLint("InflateParams")
                        LinearLayout llNewAttributeValue = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                        final TextView tvDialogSpinnerTitle = llNewAttributeValue.findViewById(R.id.tvDialogSpinnerTitle);
                        final Spinner spMeasurement = llNewAttributeValue.findViewById(R.id.spDialog);

                        tvDialogSpinnerTitle.setText(measurementValue);

                        JSONArray sortedJsonArray = new JSONArray();
                        List<JSONObject> jsonValues = new ArrayList<>();

                        for (int i = 0; i < jArrayDefaultValues.length(); i++) {
                            jsonValues.add(jArrayDefaultValues.getJSONObject(i));
                        }

                        Collections.sort(jsonValues, new Comparator<JSONObject>() {
                            //You can change "Name" with "ID" if you want to sort by ID
                            private static final String KEY_NAME = "DefaultValue";

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

                        for (int i = 0; i < jArrayDefaultValues.length(); i++) {
                            sortedJsonArray.put(jsonValues.get(i));
                        }

                        String[] newAttributesArray = new String[sortedJsonArray.length() + 1];

                        newAttributesArray[0] = selectMeasurement;

                        for (int i = 0; i < sortedJsonArray.length(); i++) {
                            JSONObject oneObject = sortedJsonArray.getJSONObject(i);
                            newAttributesArray[i+1] = oneObject.getString("DefaultValue");
                        }

                        if (spMeasurement != null) {
                            spMeasurement.setAdapter(new MySpinnerAdapter(MeasurementsToSelectActivity.this, newAttributesArray));
                        }

                        new AlertDialog.Builder(MeasurementsToSelectActivity.this)
                                .setView(llNewAttributeValue)
                                .setNegativeButton(localized_cancel, null)
                                .setPositiveButton(localized_save, (dialog, which) -> {
                                    if (spMeasurement != null) {
                                        if (spMeasurement.getSelectedItemPosition() != 0) {
                                            String keyNameWithReplacedSpace = holder.mItem.itemName.replace(" ", "_");

                                            savedMeasurements = savedMeasurements + "\"" +
                                                    keyNameWithReplacedSpace +
                                                    "\":\"" + spMeasurement.getSelectedItem().toString() + "\",";

                                            MeasurementModel measurement = new MeasurementModel();
                                            measurement.setAttributeName(holder.mItem.itemName);
                                            measurement.setProjectProductId(ProjectProductId);
                                            MeasurementsToSend.add(measurement);

                                        }
                                    }
                                })
                                .show();

                    } else {
                        @SuppressLint("InflateParams")
                        LinearLayout llNewAttributeValue = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
                        final TextView tvDialogEditTextTitle = llNewAttributeValue.findViewById(R.id.tvDialogEditTextTitle);
                        final EditText etNewAttributeValue = llNewAttributeValue.findViewById(R.id.etNewAttributeValue);

                        tvDialogEditTextTitle.setText(measurementValue);

                        new AlertDialog.Builder(MeasurementsToSelectActivity.this)
                                .setView(llNewAttributeValue)
                                .setNegativeButton(localized_cancel, null)
                                .setPositiveButton(localized_save, (dialog, which) -> {
                                    if (etNewAttributeValue != null && etNewAttributeValue.getText() != null
                                            && !etNewAttributeValue.getText().toString().equals("")) {

                                        String keyNameWithReplacedSpace = holder.mItem.itemName.replace(" ", "_");
                                        savedMeasurements = savedMeasurements + "\"" +
                                                keyNameWithReplacedSpace +
                                                "\":\"" + etNewAttributeValue.getText().toString() + "\",";

                                        MeasurementModel measurement = new MeasurementModel();
                                        measurement.setAttributeName(holder.mItem.itemName);
                                        measurement.setProjectProductId(ProjectProductId);
                                        MeasurementsToSend.add(measurement);

                                    }
                                })
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
            public NewProductsList mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvItemName = view.findViewById(R.id.tvItemName);
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
        String defaultValues = "";

        try {
            itemName = oneObject.getString("MeasurableAttributeDescription");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            defaultValues = oneObject.getString("MeasurableAttributeDefaultValues");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NewProductsList(itemName, defaultValues);
    }

    public static class NewProductsList {

        public final String itemName;
        public final String defaultValues;

        public NewProductsList(String itemName, String defaultValues) {
            this.itemName = itemName;
            this.defaultValues = defaultValues;
        }

        @Override
        public String toString() {
            return itemName + ", " + defaultValues;
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