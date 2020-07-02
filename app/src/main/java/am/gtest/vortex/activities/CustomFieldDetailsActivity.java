package am.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.CustomFieldDetailsRvAdapter;
import am.gtest.vortex.api.GetCustomFields;
import am.gtest.vortex.data.CustomFieldsData;
import am.gtest.vortex.models.CustomFieldDetailColumnModel;
import am.gtest.vortex.models.CustomFieldDetailModel;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_EMPTY_DETAILS_MAP;
import static am.gtest.vortex.support.MyGlobals.KEY_VORTEX_TABLE;
import static am.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD;
import static am.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD_DETAIL;
import static am.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static am.gtest.vortex.support.MyLocalization.localized_new_record;
import static am.gtest.vortex.support.MyLocalization.localized_no_internet_try_later_2_lines;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST;
import static am.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST_FILTERED;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_CUSTOM_FIELD_EMPTY_DETAILS;


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

        RecyclerView rvCustomFieldDetails = findViewById(R.id.rvCustomFieldDetails);

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

        rvCustomFieldDetails.setAdapter(customFieldDetailsRvAdapter);

    }

    @Override
    public void onClick(View v){

        switch (v.getId()){

            case R.id.btnAddRecord:
                String vortexTable = SELECTED_CUSTOM_FIELD.getObjectTable();
                String customFieldId = SELECTED_CUSTOM_FIELD.getCustomFieldId();


                if (MyPrefs.getStringWithFileName(PREF_FILE_CUSTOM_FIELD_EMPTY_DETAILS, vortexTable, "").length() > 0) {
                    CUSTOM_FIELD_EMPTY_DETAILS_MAP = new Gson().fromJson(MyPrefs.getStringWithFileName(PREF_FILE_CUSTOM_FIELD_EMPTY_DETAILS, vortexTable, ""), new TypeToken<HashMap<String, CustomFieldDetailModel>>(){}.getType());
                }

                SELECTED_CUSTOM_FIELD_DETAIL = CUSTOM_FIELD_EMPTY_DETAILS_MAP.get(customFieldId);
                SELECTED_CUSTOM_FIELD_DETAIL.setVortexTableId(SELECTED_CUSTOM_FIELD.getObjectTableId());

                Intent intent = new Intent(CustomFieldDetailsActivity.this, CustomFieldDetailsEditActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_VORTEX_TABLE, vortexTable);
                startActivity(intent);
                break;
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
