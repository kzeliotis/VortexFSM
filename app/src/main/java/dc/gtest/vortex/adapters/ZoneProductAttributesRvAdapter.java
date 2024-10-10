package dc.gtest.vortex.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AssignmentActionsActivity;
import dc.gtest.vortex.activities.ZoneProductsActivity;
import dc.gtest.vortex.models.MeasurableAttributeModel;
import dc.gtest.vortex.models.ProductMeasurementModel;
import dc.gtest.vortex.support.MyImages;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.PERMISSIONS_STORAGE;
import static dc.gtest.vortex.support.MyGlobals.PERMISSIONS_STORAGE_NEW;
import static dc.gtest.vortex.support.MyGlobals.PRODUCT_MEASUREMENTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_CAMERA_FOR_MANDATORY_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_CAMERA_FOR_MEASUREMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_EXTERNAL_STORAGE_FOR_MANDATORY_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_EXTERNAL_STORAGE_FOR_MEASUREMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.ValueSelected;
import static dc.gtest.vortex.support.MyGlobals.ZONES_WITH_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyGlobals.ZONE_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.globalCurrentPhotoPath;
import static dc.gtest.vortex.support.MyGlobals.globalMandatoryTaskPosition;
import static dc.gtest.vortex.support.MyGlobals.globalProductMeasurementPosition;
import static dc.gtest.vortex.support.MyGlobals.mandatoryStepPhoto;
import static dc.gtest.vortex.support.MyGlobals.selectedProjectProductMeasurableAttributeId;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_WITH_MEASUREMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCT_MEASUREMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT;

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
                    productMeasurementModel.setMeasurementPhotoPath(holder.mItem.getMeasurementPhotoPath());
                    productMeasurementModel.setMeasurementPhoto(holder.mItem.getMeasurementPhoto());

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

                    boolean sendZoneMeasurements = MyPrefs.getBoolean(PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT,  false);
                    if (sendZoneMeasurements){
                        List<String> zoneIds = new ArrayList<>();
                        if (ZONES_WITH_MEASUREMENTS_MAP.containsKey(assignmentId)) {
                            zoneIds = ZONES_WITH_MEASUREMENTS_MAP.get(assignmentId);
                        }
                        String zone_id = prefKey.split("_")[1];
                        if(!zoneIds.contains(zone_id)){
                            zoneIds.add(zone_id);
                        }
                        ZONES_WITH_MEASUREMENTS_MAP.put(assignmentId, zoneIds); //keeping record of all zones that have measurements for mandatorymeasurementsservice

                        MyPrefs.setStringWithFileName(PREF_FILE_ZONES_WITH_MEASUREMENTS, prefKey, new Gson().toJson(ZONES_WITH_MEASUREMENTS_MAP));
                    }

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
                        productMeasurementModel.setMeasurementPhotoPath(holder.mItem.getMeasurementPhotoPath());
                        productMeasurementModel.setMeasurementPhoto(holder.mItem.getMeasurementPhoto());

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

                        boolean sendZoneMeasurements = MyPrefs.getBoolean(PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT,  false);
                        if (sendZoneMeasurements){
                            List<String> zoneIds = new ArrayList<>();
                            if (ZONES_WITH_MEASUREMENTS_MAP.containsKey(assignmentId)) {
                                zoneIds = ZONES_WITH_MEASUREMENTS_MAP.get(assignmentId);
                            }
                            String zone_id = prefKey.split("_")[1];
                            if(!zoneIds.contains(zone_id)){
                                zoneIds.add(zone_id);
                            }
                            ZONES_WITH_MEASUREMENTS_MAP.put(assignmentId, zoneIds); //keeping record of all zones that have measurements for mandatorymeasurementsservice

                            MyPrefs.setStringWithFileName(PREF_FILE_ZONES_WITH_MEASUREMENTS, prefKey, new Gson().toJson(ZONES_WITH_MEASUREMENTS_MAP));
                        }


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
        }

        // setup take photo
        holder.ivTakePhoto.setOnClickListener(v -> {
            globalProductMeasurementPosition = holder.getBindingAdapterPosition();
            selectedProjectProductMeasurableAttributeId = Integer.parseInt(holder.mItem.getProjectProductMeasurableAttributeId());
            Activity activity = (Activity) ctx;
            new AlertDialog.Builder(ctx)
                    .setNeutralButton("Gallery", (dialog, which) -> {
                        boolean tiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? true : false;
                        int permission = ContextCompat.checkSelfPermission(ctx, tiramisu ? Manifest.permission.READ_MEDIA_IMAGES: Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permission != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(activity, tiramisu ? PERMISSIONS_STORAGE_NEW : PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE_FOR_MEASUREMENT_PHOTO);
                        } else {
                            ZoneProductsActivity.pickMeasurementPhotoFromStorage(activity);
                        }
                    })
                    .setPositiveButton("Camera", (dialog, which) -> {
                        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED) {
                            mandatoryStepPhoto = true;
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_FOR_MEASUREMENT_PHOTO);
                        } else {
                            ZoneProductsActivity.takeMeasurementPhotoWithCamera(ctx);
                        }
                    })
                    .show();
        });

        // setup remove image
        holder.ivRemove.setOnClickListener(v -> {
            MyUtils.deleteFile(holder.mItem.getMeasurementPhotoPath());
            holder.mItem.setMeasurementPhotoPath("");
            holder.mItem.setMeasurementPhoto("");
            globalCurrentPhotoPath = "";

            holder.ivTaskPhoto.setImageDrawable(null);
            holder.ivRemove.setVisibility(View.GONE);
        });

        if (holder.mItem.getEnableMeasurementPhoto().equals("1")) {
            holder.llMandatoryPhoto.setVisibility(View.VISIBLE);
        } else {
            holder.llMandatoryPhoto.setVisibility(View.GONE);
        }

        if (!holder.mItem.getMeasurementPhotoPath().isEmpty()) {
            new MyImages.SetImageFromPath(holder.ivTaskPhoto).execute(holder.mItem.getMeasurementPhotoPath(), "64", "64");
            holder.ivRemove.setVisibility(View.VISIBLE);
        } else {
            holder.ivTaskPhoto.setImageDrawable(null);
            holder.ivRemove.setVisibility(View.GONE);
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {
            holder.etZoneProductAttribute.setEnabled(true);
            holder.spZoneProductAttributes.setEnabled(true);
            holder.ivTakePhoto.setEnabled(true);
            holder.ivTakePhoto.setImageResource(R.drawable.ic_add_a_photo_blue_24dp);
        } else {
            holder.etZoneProductAttribute.setEnabled(false);
            holder.spZoneProductAttributes.setEnabled(false);
            holder.ivRemove.setVisibility(View.GONE);
            holder.ivTakePhoto.setEnabled(false);
            holder.ivTakePhoto.setImageResource(R.drawable.ic_add_a_photo_grey_24dp);
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
        final LinearLayout llMandatoryPhoto;
        final ImageView ivTaskPhoto;
        final ImageView ivRemove;
        final ImageView ivTakePhoto;
        public MeasurableAttributeModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvZoneAttributeName = view.findViewById(R.id.tvZoneAttributeName);
            etZoneProductAttribute = view.findViewById(R.id.etZoneProductAttribute);
            spZoneProductAttributes = view.findViewById(R.id.spZoneProductAttributes);
            llMandatoryPhoto = view.findViewById(R.id.llMandatoryPhoto);
            ivTaskPhoto = view.findViewById(R.id.ivTaskPhoto);
            ivRemove = view.findViewById(R.id.ivRemove);
            ivTakePhoto = view.findViewById(R.id.ivTakePhoto);
        }
    }
}
