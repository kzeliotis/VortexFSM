package am.gtest.vortex.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.SearchAttributesRvAdapter;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MySliderMenu;

import static am.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static am.gtest.vortex.support.MyGlobals.SELECTED_PROJECT;
import static am.gtest.vortex.support.MyGlobals.globalGetHistoryParameter;
import static am.gtest.vortex.support.MyLocalization.localized_attributes;
import static am.gtest.vortex.support.MyLocalization.localized_product_history;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class SearchAttributesActivity extends BaseDrawerActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private SearchAttributesRvAdapter searchAttributesRvAdapter;
    private Button btnBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_search_attributes, flBaseContainer, true);

        TextView tvProject = findViewById(R.id.tvProject);
        TextView tvProductDescription = findViewById(R.id.tvProductDescription);
        TextView tvProductType = findViewById(R.id.tvProductType);
        RecyclerView rvSearchAttributes = findViewById(R.id.rvSearchAttributes);

        tvProject.setText(SELECTED_PROJECT.getProjectDescription());
        tvProductDescription.setText(SELECTED_PRODUCT.getProductDescription());
        tvProductType.setText(SELECTED_PRODUCT.getTypeDescription());

        searchAttributesRvAdapter = new SearchAttributesRvAdapter(SELECTED_PRODUCT.getProductAttributes());
        rvSearchAttributes.setAdapter(searchAttributesRvAdapter);

        btnBottom = findViewById(R.id.btnBottom);
        if (btnBottom != null) {
            btnBottom.setVisibility(View.VISIBLE);
            btnBottom.setText(localized_product_history);

            btnBottom.setOnClickListener(v -> {
                globalGetHistoryParameter = "0;" + SELECTED_PRODUCT.getProjectProductId();
                Intent intent = new Intent(SearchAttributesActivity.this, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUiTexts();
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
                searchAttributesRvAdapter.getFilter().filter(newText);
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
            getSupportActionBar().setTitle(localized_attributes);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }
    }
}
