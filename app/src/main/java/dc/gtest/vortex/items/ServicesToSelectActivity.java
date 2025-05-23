package dc.gtest.vortex.items;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.ToSendServices;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MySwitchLanguage;
import dc.gtest.vortex.support.MyUtils;
import dc.gtest.vortex.unused.MinMaxFilter;

import static dc.gtest.vortex.support.MyGlobals.CONST_EN;
import static dc.gtest.vortex.support.MyGlobals.CONST_FINISH_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.CONST_SELECT_SERVICES_FROM_PICKING;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_quantity;
import static dc.gtest.vortex.support.MyLocalization.localized_select_service;
import static dc.gtest.vortex.support.MyLocalization.localized_suggested_used;
import static dc.gtest.vortex.support.MyLocalization.localized_used_value_with_colon;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PICKING_SERVICES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_SERVICES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_SERVICES_FROM_PICKING_SENT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_USED_SERVICES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_USED_SERVICES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static dc.gtest.vortex.support.MyPrefs.PREF_QTY_LIMIT_SERVICES_FROM_PICKING;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

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
    private Button btnServiceFromPicking;
    private TextView tvQuantity;

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
    private String quantity_description;
    private boolean fromPicking;

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

        fromPicking = getIntent().getBooleanExtra(CONST_SELECT_SERVICES_FROM_PICKING, false);

        btnServiceFromPicking = findViewById(R.id.btnServiceFromPicking);
        if (btnServiceFromPicking != null) {
            btnServiceFromPicking.setVisibility(View.GONE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_select_service);
            getSupportActionBar().setSubtitle(userNameTitle + ": " + userName);
        }

        new MySliderMenu(this).mySliderMenu();

        assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");

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

                    if(fromPicking) {MyPrefs.setBooleanWithFileName(PREF_FILE_SERVICES_FROM_PICKING_SENT, assignmentId, true);}
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

        quantity_description = fromPicking ? localized_used_value_with_colon : localized_quantity;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        ITEMS = new ArrayList<>();
        ITEMS.clear();


        String services = "";

        if(fromPicking){
            services = MyPrefs.getStringWithFileName(PREF_FILE_PICKING_SERVICES_FOR_SHOW, assignmentId, "");
        }else{
            services = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_SERVICES_FOR_SHOW, assignmentId, "");
        }

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

                if (fromPicking && MyPrefs.getBoolean(PREF_QTY_LIMIT_SERVICES_FROM_PICKING, false)){
                    String addedServices = MyPrefs.getStringWithFileName(PREF_FILE_USED_SERVICES_FOR_SHOW, assignmentId, "");
                    if (!addedServices.equals("")) {
                        for (NewProductsList srv: ITEMS) {
                            JSONObject jObject = new JSONObject(addedServices);

                            if (jObject.getJSONArray("Services").length() > 0) {
                                JSONArray jArrayServices = jObject.getJSONArray("Services");
                                double addedQty = 0.0;
                                for (int i = 0; i < jArrayServices.length(); i++) {
                                    JSONObject jObjectService = jArrayServices.getJSONObject(i);

                                    if(jObjectService.getString("DetPickingId").equals(srv.DetPickingId)){
                                        addedQty += Double.parseDouble(jObjectService.getString("quantity").replace(",", "."));
                                    }
                                }
                                String stock = srv.PickingQTY.replace(",", ".");
                                double stock_d = Double.parseDouble(stock);
                                stock_d -= addedQty;
                                srv.PickingQTY = String.valueOf(stock_d);
                            }
                        }
                    }
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
            LinearLayout llServiceQuanity = view.findViewById(R.id.llServiceQuanity);
            llServiceQuanity.setVisibility(View.VISIBLE);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.tvItemName.setText(mValues.get(position).itemName);

            holder.chkSuggested.setChecked(holder.mItem.isSuggestedChecked);
            holder.chkUsed.setChecked(holder.mItem.isUsedChecked);
            holder.etServiceQuantity.setText(holder.mItem.quantity);

            if(fromPicking){
                String desc = holder.mItem.itemName;
                String stock = holder.mItem.PickingQTY;
                //String stock_s = stock_d.toString().replace(",", ".");
                desc = desc + "\n\r" + "Qty: " + stock;
                holder.tvItemName.setText(desc);
            }

            if(fromPicking && MyPrefs.getBoolean(PREF_QTY_LIMIT_SERVICES_FROM_PICKING, false)){
                String pickingQTY = holder.mItem.PickingQTY.replace(",", ".");
                double pickingQTY_d = Double.parseDouble(pickingQTY);
                if(pickingQTY_d <= 0){
                    holder.chkUsed.setEnabled(false);
                    holder.etServiceQuantity.setEnabled(false);
                }
                holder.etServiceQuantity.setFilters(new InputFilter[]{new MinMaxFilter(0.0, pickingQTY_d)});
            }

            holder.etServiceQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (jArrayCheckboxes.length() > 0) {
                        for (int i = 0; i < jArrayCheckboxes.length(); i++) {
                            try {
                                JSONObject jObject = jArrayCheckboxes.getJSONObject(i);

                                if (jObject.getString("serviceId").equals(holder.mItem.serviceId)) {
                                    jArrayCheckboxes.getJSONObject(i).put("quantity", holder.etServiceQuantity.getText().toString());
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

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
                            jObject.put("quantity", holder.etServiceQuantity.getText().toString());
                            jObject.put("DetPickingId", holder.mItem.DetPickingId);
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
                        jObject.put("quantity", holder.etServiceQuantity.getText().toString());
                        jObject.put("DetPickingId", holder.mItem.DetPickingId);
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
                            jObject.put("quantity", holder.etServiceQuantity.getText().toString());
                            jObject.put("DetPickingId", holder.mItem.DetPickingId);
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
                        jObject.put("quantity", holder.etServiceQuantity.getText().toString());
                        jObject.put("DetPickingId", holder.mItem.DetPickingId);
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
            public final EditText etServiceQuantity;
            public final TextView tvQuantity;
            public NewProductsList mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvItemName = view.findViewById(R.id.tvItemName);
                chkSuggested = view.findViewById(R.id.chkSuggested);
                chkUsed = view.findViewById(R.id.chkUsed);
                etServiceQuantity = view.findViewById(R.id.etServiceQuantity);
                tvQuantity = view.findViewById(R.id.tvQuantity);
                tvQuantity.setText(quantity_description);
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
        String quantity = "1";
        String detPickingId = "0";
        String pickingtQTY = "";

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

        try {
            detPickingId = oneObject.getString("DetPickingId");
            pickingtQTY = oneObject.getString("Quantity");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NewProductsList(itemName, serviceId, isSuggestedChecked, isUsedChecked, quantity, detPickingId, pickingtQTY);
    }

    public static class NewProductsList {

        public final String itemName;
        public final String serviceId;
        public boolean isSuggestedChecked;
        public boolean isUsedChecked;
        public final String quantity;
        public final String DetPickingId;
        public String PickingQTY;

        public NewProductsList(String itemName, String serviceId, boolean isSuggestedChecked,
                               boolean isUsedChecked, String quantity, String DetPickingId, String PickingQTY) {
            this.itemName = itemName;
            this.serviceId = serviceId;
            this.isSuggestedChecked = isSuggestedChecked;
            this.isUsedChecked = isUsedChecked;
            this.quantity = quantity;
            this.DetPickingId = DetPickingId;
            this.PickingQTY = PickingQTY;
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