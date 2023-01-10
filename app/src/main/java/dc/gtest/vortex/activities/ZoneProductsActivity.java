package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.ZoneProductsRvAdapter;
import dc.gtest.vortex.api.GetZoneProducts;
import dc.gtest.vortex.api.SendProductMeasurements;
import dc.gtest.vortex.data.ZoneProductsData;
import dc.gtest.vortex.models.ProductMeasurementModel;
import dc.gtest.vortex.support.MyCanEdit;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_FINISH_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.PRODUCT_MEASUREMENTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.ValueSelected;
import static dc.gtest.vortex.support.MyGlobals.ZONES_WITH_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyGlobals.ZONE_MEASUREMENTS_MAP;
import static dc.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.resendZoneMeasurements;
import static dc.gtest.vortex.support.MyLocalization.localized_ask_send_zone_measurements;
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
            ZoneProductsData.generate(savedChangedZoneProducts, zoneId);
            zoneProductsRvAdapter.notifyDataSetChanged();

        } else if (!savedZoneProducts.isEmpty()) {
            ZoneProductsData.generate(savedZoneProducts, zoneId);
            zoneProductsRvAdapter.notifyDataSetChanged();

        } else {
            if (MyUtils.isNetworkAvailable()) {
                if (zoneId != null) {
                    GetZoneProducts getZoneProducts = new GetZoneProducts(this, zoneProductsRvAdapter);
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

//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        searchView.onActionViewExpanded();
//                        searchView.setIconified(false);
//                        searchView.setQuery("666777", false);
//                        searchView.clearFocus();
//                    }
//                }, 1000);

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
        }
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
            if(!isCheckedOut) {
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
                finish();
            }

        }
    }
//    public static void hideKeyboard(Activity activity) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        //Find the currently focused view, so we can grab the correct window token from it.
//        View view = activity.getCurrentFocus();
//        //If no view currently has focus, create a new one, just so we can grab a window token from it
//        if (view == null) {
//            view = new View(activity);
//        }
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

}
