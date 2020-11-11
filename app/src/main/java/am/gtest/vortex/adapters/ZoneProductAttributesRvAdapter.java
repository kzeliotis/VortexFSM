package am.gtest.vortex.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.MeasurableAttributeModel;
import am.gtest.vortex.models.ProductMeasurementModel;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.PRODUCT_MEASUREMENTS_LIST;
import static am.gtest.vortex.support.MyGlobals.ValueSelected;
import static am.gtest.vortex.support.MyGlobals.ZONE_MEASUREMENTS_MAP;
import static am.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST;
import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_MEASUREMENTS_MAP;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCT_MEASUREMENTS;

public class ZoneProductAttributesRvAdapter extends RecyclerView.Adapter<ZoneProductAttributesRvAdapter.ViewHolder> {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;
    private final List<MeasurableAttributeModel> mValues;
    private final String zoneProductId;
    //private Boolean ValueSelected = false;

    private final String prefKey;
    private final String assignmentId;
    private int defaultPosition = 0;
    private boolean isEdited = false;

    public ZoneProductAttributesRvAdapter(List<MeasurableAttributeModel> items, Context ctx, String zoneProductId, String prefKey, String assignmentId) {
        this.ctx = ctx;
        mValues = items;
        this.zoneProductId = zoneProductId;
        this.prefKey = prefKey;
        this.assignmentId = assignmentId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zone_product_attribute, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvZoneAttributeName.setText(holder.mItem.getAttributeName());

        String assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");


        if (holder.mItem.getAttributeDefaultModel().get(0).getDefaultValueId().equals("-1")) {
            holder.etZoneProductAttribute.setVisibility(View.VISIBLE);
            holder.spZoneProductAttributes.setVisibility(View.GONE);

            holder.etZoneProductAttribute.setText(holder.mItem.getAttributeDefaultModel().get(0).getDefaultValueName());

            holder.etZoneProductAttribute.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    holder.mItem.getAttributeDefaultModel().get(0).setDefaultValueName(holder.etZoneProductAttribute.getText().toString());

                    ProductMeasurementModel productMeasurementModel = new ProductMeasurementModel();

                    productMeasurementModel.setAssignmentId(assignmentId);
                    productMeasurementModel.setZoneProductId(zoneProductId);
                    productMeasurementModel.setMeasurableAttributeId(holder.mItem.getAttributeId());
                    productMeasurementModel.setDefaultValueId("-1");
                    productMeasurementModel.setValue(holder.etZoneProductAttribute.getText().toString());

                    boolean isExists = false;

                    if(ZONE_MEASUREMENTS_MAP.containsKey(prefKey)){
                        PRODUCT_MEASUREMENTS_LIST = ZONE_MEASUREMENTS_MAP.get(prefKey);
                    } else{
                        PRODUCT_MEASUREMENTS_LIST.clear();
                    }

                    for (int i = 0; i < PRODUCT_MEASUREMENTS_LIST.size(); i++) {
                        if (PRODUCT_MEASUREMENTS_LIST.get(i).getZoneProductId().equals(zoneProductId) &&
                            PRODUCT_MEASUREMENTS_LIST.get(i).getMeasurableAttributeId().equals(holder.mItem.getAttributeId())) {
                            PRODUCT_MEASUREMENTS_LIST.get(i).setValue(holder.etZoneProductAttribute.getText().toString());

                            isExists = true;
                        }
                    }

                    if (!isExists) {
                        PRODUCT_MEASUREMENTS_LIST.add(productMeasurementModel);
                    }

                    ZONE_MEASUREMENTS_MAP.remove(prefKey);
                    ZONE_MEASUREMENTS_MAP.put(prefKey, PRODUCT_MEASUREMENTS_LIST);

                    //ZONE_MEASUREMENTES_FOR_CHECKOUT_SYNC.put(assignmentId, ZONE_MEASUREMENTS_MAP);

                    MyPrefs.setStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_MAP, prefKey, new Gson().toJson(ZONE_MEASUREMENTS_MAP));
                    MyPrefs.setStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC, prefKey, PRODUCT_MEASUREMENTS_LIST.toString());
                    MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCT_MEASUREMENTS, prefKey, PRODUCT_MEASUREMENTS_LIST.toString());
                    MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SHOW, prefKey, ZONE_PRODUCTS_LIST.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            holder.spZoneProductAttributes.setOnItemSelectedListener(null);
        } else {
            holder.etZoneProductAttribute.setVisibility(View.GONE);
            holder.spZoneProductAttributes.setVisibility(View.VISIBLE);

            holder.etZoneProductAttribute.addTextChangedListener(null);

            List<String> attributesList = new ArrayList<>();

            for (int i = 0; i < holder.mItem.getAttributeDefaultModel().size(); i++) {
                attributesList.add(holder.mItem.getAttributeDefaultModel().get(i).getDefaultValueName());

                if (holder.mItem.getAttributeDefaultModel().get(i).getInitial()) {
                    defaultPosition = i;
                }
            }

            SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, attributesList);
            holder.spZoneProductAttributes.setAdapter(spinnerAdapter);
            holder.spZoneProductAttributes.setSelection(defaultPosition);
            holder.spZoneProductAttributes.setTag(defaultPosition);
            holder.spZoneProductAttributes.setTag(R.id.TAG_ID, false);  //Sets that no value has been selected.


            holder.spZoneProductAttributes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    defaultPosition = Integer.parseInt(holder.spZoneProductAttributes.getTag().toString());

                    if (defaultPosition != position){
                        holder.spZoneProductAttributes.setTag(R.id.TAG_ID, true); //ValueSelected = true;
                    }

                    isEdited = holder.mItem.getAttributeDefaultModel().get(position).getIsEdited();
                    ValueSelected = Boolean.parseBoolean(holder.spZoneProductAttributes.getTag(R.id.TAG_ID).toString());

                    if (ValueSelected || isEdited) {  //defaultPosition != position
                        for (int i = 0; i < holder.mItem.getAttributeDefaultModel().size(); i++) {
                            holder.mItem.getAttributeDefaultModel().get(i).setInitial(false);
                            holder.mItem.getAttributeDefaultModel().get(position).setIsEdited(false);
                        }

                        holder.mItem.getAttributeDefaultModel().get(position).setInitial(true);
                        holder.mItem.getAttributeDefaultModel().get(position).setIsEdited(true);

                        ProductMeasurementModel productMeasurementModel = new ProductMeasurementModel();

                        productMeasurementModel.setAssignmentId(assignmentId);
                        productMeasurementModel.setZoneProductId(zoneProductId);
                        productMeasurementModel.setMeasurableAttributeId(holder.mItem.getAttributeId());
                        productMeasurementModel.setDefaultValueId(holder.mItem.getAttributeDefaultModel().get(position).getDefaultValueId());
                        productMeasurementModel.setValue(holder.mItem.getAttributeDefaultModel().get(position).getDefaultValueName());
                        productMeasurementModel.setProjectZoneId("");

                        boolean isExists = false;

                        List<ProductMeasurementModel> productMeasurementList = new ArrayList<>();

                        if(ZONE_MEASUREMENTS_MAP.containsKey(prefKey)){
                            productMeasurementList = ZONE_MEASUREMENTS_MAP.get(prefKey);
                        }

                        for (int i = 0; i < productMeasurementList.size(); i++) {
                            if (productMeasurementList.get(i).getZoneProductId().equals(zoneProductId) &&
                                    productMeasurementList.get(i).getMeasurableAttributeId().equals(holder.mItem.getAttributeId())) {
                                productMeasurementList.get(i).setDefaultValueId(holder.mItem.getAttributeDefaultModel().get(position).getDefaultValueId());
                                productMeasurementList.get(i).setValue(holder.mItem.getAttributeDefaultModel().get(position).getDefaultValueName());

                                isExists = true;
                            }
                        }

                        if (!isExists) {
                            productMeasurementList.add(productMeasurementModel);
                        }

                        ZONE_MEASUREMENTS_MAP.remove(prefKey);
                        ZONE_MEASUREMENTS_MAP.put(prefKey, productMeasurementList);


                        MyPrefs.setStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_MAP, prefKey, new Gson().toJson(ZONE_MEASUREMENTS_MAP));
                        MyPrefs.setStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC, prefKey, productMeasurementList.toString());
                        MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCT_MEASUREMENTS, prefKey, productMeasurementList.toString());
                        MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SHOW, prefKey, ZONE_PRODUCTS_LIST.toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            });

           // holder.spZoneProductAttributes.setOnTouchListener((parent, event) -> {
            //    if (event.getAction() == MotionEvent.ACTION_DOWN) {
           //         ValueSelected = true;
           //     }
            //    return false;
           // });
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {
            holder.etZoneProductAttribute.setEnabled(true);
            holder.spZoneProductAttributes.setEnabled(true);
        } else {
            holder.etZoneProductAttribute.setEnabled(false);
            holder.spZoneProductAttributes.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        final TextView tvZoneAttributeName;
        final EditText etZoneProductAttribute;
        final Spinner spZoneProductAttributes;
        public MeasurableAttributeModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvZoneAttributeName = view.findViewById(R.id.tvZoneAttributeName);
            etZoneProductAttribute = view.findViewById(R.id.etZoneProductAttribute);
            spZoneProductAttributes = view.findViewById(R.id.spZoneProductAttributes);
        }
    }
}
