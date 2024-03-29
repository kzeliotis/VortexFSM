package dc.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.CustomFieldDetailsRvAdapter;
import dc.gtest.vortex.models.CustomFieldDetailModel;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;

import static dc.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_EMPTY_DETAILS_MAP;
import static dc.gtest.vortex.support.MyGlobals.KEY_VORTEX_TABLE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD_DETAIL;
import static dc.gtest.vortex.support.MyGlobals._rvCustomFieldDetails;
import static dc.gtest.vortex.support.MyLocalization.localized_new_record;
import static dc.gtest.vortex.support.MyLocalization.localized_user;
import static dc.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST;
import static dc.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CUSTOM_FIELD_EMPTY_DETAILS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW;


public class CustomFieldDetailsActivity extends BaseDrawerActivity implements View.OnClickListener {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private CustomFieldDetailsRvAdapter customFieldDetailsRvAdapter;
    private SearchView searchView;
    private Button btnAddRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_custom_field_details, flBaseContainer, true);

        _rvCustomFieldDetails = findViewById(R.id.rvCustomFieldDetails);

        CUSTOM_FIELD_DETAILS_LIST = SELECTED_CUSTOM_FIELD.getCustomFieldDetails();
        CUSTOM_FIELD_DETAILS_LIST_FILTERED.clear();
        CUSTOM_FIELD_DETAILS_LIST_FILTERED.addAll(CUSTOM_FIELD_DETAILS_LIST);

        btnAddRecord = findViewById(R.id.btnAddRecord);

        if(SELECTED_CUSTOM_FIELD.getEditable()){
            btnAddRecord.setEnabled(true);
            btnAddRecord.setOnClickListener(this);
        }else{
            btnAddRecord.setEnabled(false);
        }

        customFieldDetailsRvAdapter = new CustomFieldDetailsRvAdapter(CUSTOM_FIELD_DETAILS_LIST_FILTERED, this, SELECTED_CUSTOM_FIELD.getObjectTable());

        _rvCustomFieldDetails.setAdapter(customFieldDetailsRvAdapter);

    }

    @Override
    public void onClick(View v){

        if (v.getId() == R.id.btnAddRecord) {
            String vortexTable = SELECTED_CUSTOM_FIELD.getObjectTable();
            String customFieldId = SELECTED_CUSTOM_FIELD.getCustomFieldId();


            if (MyPrefs.getStringWithFileName(PREF_FILE_CUSTOM_FIELD_EMPTY_DETAILS, vortexTable, "").length() > 0) {
                CUSTOM_FIELD_EMPTY_DETAILS_MAP = new Gson().fromJson(MyPrefs.getStringWithFileName(PREF_FILE_CUSTOM_FIELD_EMPTY_DETAILS, vortexTable, ""), new TypeToken<HashMap<String, CustomFieldDetailModel>>() {
                }.getType());
            }

            SELECTED_CUSTOM_FIELD_DETAIL = CUSTOM_FIELD_EMPTY_DETAILS_MAP.get(customFieldId);
            SELECTED_CUSTOM_FIELD_DETAIL.setVortexTableId(SELECTED_CUSTOM_FIELD.getObjectTableId());

            Intent intent = new Intent(CustomFieldDetailsActivity.this, CustomFieldDetailsEditActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_VORTEX_TABLE, vortexTable);
            startActivity(intent);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }

        updateUiTexts();

        customFieldDetailsRvAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        SELECTED_CUSTOM_FIELD_DETAIL = null;
        _rvCustomFieldDetails = null;

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) itemSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customFieldDetailsRvAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(SELECTED_CUSTOM_FIELD.getCustomFieldDescription());
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(MyPrefs.PREF_USER_NAME, ""));
        }

        btnAddRecord.setText(localized_new_record);

    }
}
