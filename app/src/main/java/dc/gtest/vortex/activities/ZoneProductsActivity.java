package dc.gtest.vortex.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AlertDialog;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.ZoneProductsRvAdapter;
import dc.gtest.vortex.api.GetZoneProducts;
import dc.gtest.vortex.api.SendProductMeasurements;
import dc.gtest.vortex.data.ZoneProductsData;
import dc.gtest.vortex.models.MeasurableAttributeModel;
import dc.gtest.vortex.models.ProductMeasurementModel;
import dc.gtest.vortex.models.ZoneProductModel;
import dc.gtest.vortex.support.MyCanEdit;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyImages;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;
import dc.gtest.vortex.support.TakeUploadPhoto;

import static dc.gtest.vortex.support.MyGlobals.CONST_ASSIGNMENT_PHOTOS_FOLDER;
import static dc.gtest.vortex.support.MyGlobals.CONST_FINISH_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_PICK_MANDATORY_TASK_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_PICK_MEASUREMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_TAKE_MANDATORY_TASK_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_TAKE_MEASUREMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.PICKFILE_RESULT_CODE;
import static dc.gtest.vortex.support.MyGlobals.PRODUCT_MEASUREMENTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_CAMERA_FOR_ASSIGNMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_CAMERA_FOR_MANDATORY_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_CAMERA_FOR_MEASUREMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_EXTERNAL_STORAGE_FOR_ASSIGNMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_EXTERNAL_STORAGE_FOR_MANDATORY_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.REQUEST_EXTERNAL_STORAGE_FOR_MEASUREMENT_PHOTO;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.ValueSelected;
import static dc.gtest.vortex.support.MyGlobals.ZONES_WITH_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyGlobals.ZONE_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.globalCurrentPhotoPath;
import static dc.gtest.vortex.support.MyGlobals.globalProductMeasurementPosition;
import static dc.gtest.vortex.support.MyGlobals.resendZoneMeasurements;
import static dc.gtest.vortex.support.MyGlobals.selectedProjectProductMeasurableAttributeId;
import static dc.gtest.vortex.support.MyLocalization.localized_ask_send_zone_measurements;
import static dc.gtest.vortex.support.MyLocalization.localized_file_size_limit;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_try_later_2_lines;
import static dc.gtest.vortex.support.MyLocalization.localized_nothing_is_changed;
import static dc.gtest.vortex.support.MyLocalization.localized_send_data_caps;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyLocalization.localized_zone_products;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_WITH_MEASUREMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCT_MEASUREMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_PROJECT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_SEND_ZONE_MEASUREMENTS_ON_CHECK_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class ZoneProductsActivity extends BaseDrawerActivity{

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private ZoneProductsRvAdapter zoneProductsRvAdapter;

    private Button btnSendZoneProduct;
    private String zoneId;
    private String prefKey;
    private SearchView searchView;
    private MenuItem mSearchMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_zone_products, flBaseContainer, true);

        TextView tvZoneName = findViewById(R.id.tvZoneName);
        btnSendZoneProduct = findViewById(R.id.btnSendZoneProduct);
        RecyclerView rvZoneProducts = findViewById(R.id.rvZoneProducts);

        String zoneName = getIntent().getStringExtra("zoneName");
        zoneId = getIntent().getStringExtra("zoneId");
        String AssignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
        prefKey = MyPrefs.getString(PREF_PROJECT_ID, "") + "_" + zoneId + "_" + AssignmentId;

        if (zoneName != null) {
            tvZoneName.setText(zoneName);
        } else {
            tvZoneName.setVisibility(View.GONE);
        }

        ZONE_PRODUCTS_LIST.clear();
        ZONE_PRODUCTS_LIST_FILTERED.clear();
        ValueSelected = false;

        if (MyPrefs.getStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_MAP, prefKey, "").length() > 0) {
            ZONE_MEASUREMENTS_MAP = new Gson().fromJson(MyPrefs.getStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_MAP, prefKey, ""), new TypeToken<HashMap<String, List<ProductMeasurementModel>>>(){}.getType());
        }

        if (MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_MEASUREMENTS, prefKey, "").length() > 0) {
            ZONES_WITH_MEASUREMENTS_MAP = new Gson().fromJson(MyPrefs.getStringWithFileName(PREF_FILE_ZONES_WITH_MEASUREMENTS, prefKey, ""), new TypeToken<HashMap<String, List<String>>>(){}.getType());
        }

        zoneProductsRvAdapter = new ZoneProductsRvAdapter(ZONE_PRODUCTS_LIST_FILTERED, ZoneProductsActivity.this, prefKey, AssignmentId);
        rvZoneProducts.setAdapter(zoneProductsRvAdapter);

        btnSendZoneProduct.setOnLongClickListener(v -> {
            boolean isCheckedOut = MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, AssignmentId, false);
            if(isCheckedOut){
                resendZoneMeasurements = true;
                btnSendZoneProduct.performClick();
            }
            return true;
                });

        btnSendZoneProduct.setOnClickListener(v -> {

            MyUtils.hideKeypad(ZoneProductsActivity.this, v);

            if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId()) || resendZoneMeasurements) {

                List<String> zoneIds = new ArrayList<>();
                if (ZONES_WITH_MEASUREMENTS_MAP.containsKey(AssignmentId)) {
                    zoneIds = ZONES_WITH_MEASUREMENTS_MAP.get(AssignmentId);
                }
                if(!zoneIds.contains(zoneId)){
                    zoneIds.add(zoneId);
                }
                ZONES_WITH_MEASUREMENTS_MAP.put(AssignmentId, zoneIds); //keeping record of all zones that have measurements for mandatorymeasurementsservice
                MyPrefs.setStringWithFileName(PREF_FILE_ZONES_WITH_MEASUREMENTS, prefKey, new Gson().toJson(ZONES_WITH_MEASUREMENTS_MAP));

                MyPrefs.removeStringWithFileName(PREF_FILE_ZONE_MEASUREMENTS_FOR_CHECKOUT_SYNC, prefKey);

                String zoneMeasurements = MyPrefs.getStringWithFileName(PREF_FILE_ZONE_PRODUCT_MEASUREMENTS, prefKey, "");

                //if (PRODUCT_MEASUREMENTS_LIST.size() > 0) {
                if (zoneMeasurements.length() > 0) {

                   //MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, prefKey, PRODUCT_MEASUREMENTS_LIST.toString());
                   MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, prefKey, zoneMeasurements);

                    //PRODUCT_MEASUREMENTS_LIST.clear();
                   // MyPrefs.removeStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, prefKey);
                    if (MyUtils.isNetworkAvailable()) {
                        SendProductMeasurements sendProductMeasurements = new SendProductMeasurements(ZoneProductsActivity.this, prefKey, CONST_FINISH_ACTIVITY, "0");
                        sendProductMeasurements.execute();
                    } else {
                        Toast.makeText(ZoneProductsActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
                    }
                } else {
                    ProductMeasurementModel productMeasurementModel = new ProductMeasurementModel();
                    productMeasurementModel.setAssignmentId(AssignmentId);
                    productMeasurementModel.setZoneProductId("0");
                    productMeasurementModel.setMeasurableAttributeId("0");
                    productMeasurementModel.setDefaultValueId("0");
                    productMeasurementModel.setProjectZoneId(zoneId);
                    boolean isExists = false;

                    for (int i = 0; i < PRODUCT_MEASUREMENTS_LIST.size(); i++) {
                        if (PRODUCT_MEASUREMENTS_LIST.get(i).getProjectZoneId().equals(zoneId)) {
                            isExists = true;
                        }
                    }

                    if (!isExists) {
                        PRODUCT_MEASUREMENTS_LIST.add(productMeasurementModel);
                        MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SYNC, prefKey, PRODUCT_MEASUREMENTS_LIST.toString());
                        if (MyUtils.isNetworkAvailable()) {
                            SendProductMeasurements sendProductMeasurements = new SendProductMeasurements(ZoneProductsActivity.this, prefKey, CONST_FINISH_ACTIVITY, "0");
                            sendProductMeasurements.execute();
                        } else {
                            Toast.makeText(ZoneProductsActivity.this, localized_no_internet_data_saved, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ZoneProductsActivity.this, localized_nothing_is_changed, Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUiTexts();

        String savedZoneProducts = MyPrefs.getStringWithFileName(PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW, prefKey, "");
        String savedChangedZoneProducts = MyPrefs.getStringWithFileName(PREF_FILE_ZONE_PRODUCTS_FOR_SHOW, prefKey, "");

        if (!savedChangedZoneProducts.isEmpty()) {
            ZoneProductsData.generate(savedChangedZoneProducts, zoneId, "", "");
            zoneProductsRvAdapter.notifyDataSetChanged();

        } else if (!savedZoneProducts.isEmpty()) {
            ZoneProductsData.generate(savedZoneProducts, zoneId, "", "");
            zoneProductsRvAdapter.notifyDataSetChanged();

        } else {
            if (MyUtils.isNetworkAvailable()) {
                if (zoneId != null) {
                    GetZoneProducts getZoneProducts = new GetZoneProducts(this, zoneProductsRvAdapter, "", "");
                    getZoneProducts.execute(zoneId);
                }
            } else {
                Toast.makeText(this, localized_no_internet_try_later_2_lines, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_scanner, menu);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) itemSearch.getActionView();

        mSearchMenu = itemSearch;

        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                zoneProductsRvAdapter.getFilter().filter(newText);
                return true;
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {

          @Override
           public void onClick(View v) {
               //Find EditText view
                EditText et = findViewById(R.id.search_src_text);

                //Clear the text from EditText view
               et.setText("");

                //Clear query
                searchView.setQuery("", false);
                //Collapse the action view
                searchView.onActionViewCollapsed();
                //Collapse the search widget
                mSearchMenu.collapseActionView();

           }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemScanner){
            IntentIntegrator integrator = new IntentIntegrator(ZoneProductsActivity.this);
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
    public void onActivityResult ( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 49374:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() != null) {
                        String ScannedCode = result.getContents();
                        //ScannedCode = ScannedCode + "|";
                        //searchView = (SearchView) mSearchMenu.getActionView();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                searchView.onActionViewExpanded();
                                searchView.setIconified(false);
                                searchView.setQuery(ScannedCode, false);
                                searchView.clearFocus();
                            }
                        }, 200);
                    }
                }
                break;

            case OTHER_APP_RESULT_TAKE_MEASUREMENT_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (selectedProjectProductMeasurableAttributeId > 0) {
                        try {
                            insertPhotoToDatasource(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        Log.e(LOG_TAG, "----------------onActivityResult MANDATORY_TASKS_LIST: \n" + MANDATORY_TASKS_LIST);

                        zoneProductsRvAdapter.notifyDataSetChanged();
                    }
                }
                break;

            case OTHER_APP_RESULT_PICK_MEASUREMENT_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (selectedProjectProductMeasurableAttributeId > 0) {
                            Uri selectedImage = data.getData();
                            File pickedFile = new File(getRealPathFromURI(selectedImage));
                            String assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
                            File newFileLocation = new File(this.getExternalFilesDir(null) + File.separator + assignmentId + CONST_ASSIGNMENT_PHOTOS_FOLDER);
                            File movedPhoto = new File(newFileLocation, pickedFile.getName());
                            movedPhoto = CopyFile(selectedImage, movedPhoto);
                            if (movedPhoto == null){return;}
                            //copyFileOrDirectory(pickedFile.getAbsolutePath(), newFileLocation.getAbsolutePath());
                            globalCurrentPhotoPath = movedPhoto.getAbsolutePath();

                            try {
                                insertPhotoToDatasource(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                        Log.e(LOG_TAG, "----------------onActivityResult MANDATORY_TASKS_LIST: \n" + MANDATORY_TASKS_LIST);

                            zoneProductsRvAdapter.notifyDataSetChanged();
                        }

                    }


                }


                break;
        }
    }


    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public File CopyFile(Uri attachmentFile, File outputFile){
        InputStream in = null;
        OutputStream out = null;
        long selectedFileSize = 0;
        boolean transferSuccessful = true;
        try {
            in = getContentResolver().openInputStream(attachmentFile);
            long sizeLimit = 30000000; //30mb
            selectedFileSize = in.available();
            //String conStr = MyPrefs.getString(PREF_AZURE_CONNECTION_STRING, "");
            if (selectedFileSize > sizeLimit){
                MyDialogs.showOK(ZoneProductsActivity.this, localized_file_size_limit);
                return null;
            }
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            out = new FileOutputStream(outputFile, true);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            if (in != null) {
                in.close();
            }
            if (out != null){
                out.close();
            }
        } catch (Exception ex) {
            transferSuccessful = false;
            ex.printStackTrace();
            MyDialogs.showOK(ZoneProductsActivity.this, ex.toString());
            outputFile = null;
        }

        return outputFile;
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_zone_products);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        btnSendZoneProduct.setText(localized_send_data_caps);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            boolean isCheckedOut = MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, SELECTED_ASSIGNMENT.getAssignmentId(), false);
            if (!isCheckedOut) {
                new AlertDialog.Builder(this)
                        .setMessage(localized_ask_send_zone_measurements)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            btnSendZoneProduct.performClick();
                            finish();
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .show();
            } else {
                super.onBackPressed();
                //finish();
            }

        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CAMERA_FOR_MEASUREMENT_PHOTO) {
                takeMeasurementPhotoWithCamera(this);
            } else if (requestCode == REQUEST_EXTERNAL_STORAGE_FOR_MEASUREMENT_PHOTO) {
                pickMeasurementPhotoFromStorage(this);
            }
        }
    }

    public static void pickMeasurementPhotoFromStorage(Activity activity) {
        globalCurrentPhotoPath = null;
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        activity.startActivityForResult(pickPhoto, OTHER_APP_RESULT_PICK_MEASUREMENT_PHOTO);
    }


    public static void takeMeasurementPhotoWithCamera(Context ctx) {
        String photoFolderName = SELECTED_ASSIGNMENT.getAssignmentId() + CONST_ASSIGNMENT_PHOTOS_FOLDER;
        new TakeUploadPhoto(ctx).dispatchTakePictureIntent(OTHER_APP_RESULT_TAKE_MEASUREMENT_PHOTO, photoFolderName);
    }

    public void insertPhotoToDatasource(boolean fromGallery){

        ProductMeasurementModel productMeasurementModel = new ProductMeasurementModel();
        String assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();

        for (ZoneProductModel pmm : ZONE_PRODUCTS_LIST_FILTERED) {
            List<MeasurableAttributeModel> measurements = pmm.getMeasurableAttributeModel();
            for (MeasurableAttributeModel measurement : measurements) {
                if (measurement.getProjectProductMeasurableAttributeId().equals(String.valueOf(selectedProjectProductMeasurableAttributeId))) {
                    productMeasurementModel.setAssignmentId(assignmentId);
                    productMeasurementModel.setZoneProductId(pmm.getZoneProductId());
                    productMeasurementModel.setMeasurableAttributeId(measurement.getAttributeId());
                    productMeasurementModel.setDefaultValueId("-1");
                    productMeasurementModel.setValue(measurement.getAttributeDefaultModel().get(0).getDefaultValueName());
                    productMeasurementModel.setMeasurementPhotoPath(globalCurrentPhotoPath);


                    measurement.setMeasurementPhotoPath(globalCurrentPhotoPath);

                    boolean isExists = false;

                    if(ZONE_MEASUREMENTS_MAP.containsKey(prefKey)){
                        PRODUCT_MEASUREMENTS_LIST = ZONE_MEASUREMENTS_MAP.get(prefKey);
                    } else{
                        PRODUCT_MEASUREMENTS_LIST.clear();
                    }

                    String measurementPhoto = MyImages.getImageBase64String(ZoneProductsActivity.this, globalCurrentPhotoPath, fromGallery);

                    for (int i = 0; i < PRODUCT_MEASUREMENTS_LIST.size(); i++) {
                        if (PRODUCT_MEASUREMENTS_LIST.get(i).getZoneProductId().equals(pmm.getZoneProductId()) &&
                                PRODUCT_MEASUREMENTS_LIST.get(i).getMeasurableAttributeId().equals(measurement.getAttributeId()))
                        {
                            PRODUCT_MEASUREMENTS_LIST.get(i).setValue(productMeasurementModel.getValue());
                            PRODUCT_MEASUREMENTS_LIST.get(i).setMeasurementPhoto(measurementPhoto);
                            PRODUCT_MEASUREMENTS_LIST.get(i).setMeasurementPhotoPath(globalCurrentPhotoPath);
                            isExists = true;
                        }
                    }

                    if (!isExists) {
                        productMeasurementModel.setMeasurementPhoto(measurementPhoto);
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


                    break;
                }
            }
        }






    }

}
