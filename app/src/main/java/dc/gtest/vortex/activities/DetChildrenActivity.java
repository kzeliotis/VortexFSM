package dc.gtest.vortex.activities;

import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_show_hide_all;
import static dc.gtest.vortex.support.MyLocalization.localized_tasks_per_item;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CHILDREN_FOR_SHOW;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.stream.Collectors;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.DetChildrenRvAdapter;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;

public class DetChildrenActivity extends BaseDrawerActivity {

    private SearchView searchView;
    private DetChildrenRvAdapter detChildrenRvAdapter;
    private RecyclerView rvDetChildren;
    private Button btnShowHide;
    private boolean showAll = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_det_children, flBaseContainer, true);

        rvDetChildren = findViewById(R.id.rvDetChildren);
        btnShowHide = findViewById(R.id.btnShowHide);

        String resourceId = SELECTED_ASSIGNMENT.getResourceId();
        String assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
        String detChildData = MyPrefs.getStringWithFileName(PREF_FILE_DET_CHILDREN_FOR_SHOW, assignmentId + "_" + resourceId, "");
        if ((detChildData.isEmpty() || detChildData.equals("[]")) && !SELECTED_ASSIGNMENT.getDetChildren().isEmpty() ) {
            MyPrefs.setStringWithFileName(PREF_FILE_DET_CHILDREN_FOR_SHOW, assignmentId + "_" + resourceId, SELECTED_ASSIGNMENT.getDetChildren().toString());
        }

        if(!SELECTED_ASSIGNMENT.getDetChildren().isEmpty() &&
                SELECTED_ASSIGNMENT.getDetChildren().stream().anyMatch(dc -> !SELECTED_ASSIGNMENT.getResourceId().equals(dc.getResourceId())))
        {
            btnShowHide.setVisibility(View.VISIBLE);
        }

        btnShowHide.setOnClickListener(v -> {
            if(showAll) {
                detChildrenRvAdapter.getFilter().filter(searchView.getQuery().toString());
                showAll = false;
            }else {
                detChildrenRvAdapter.getFilter().filter("@show_all" + searchView.getQuery().toString());
                showAll = true;
            }
        });

        detChildrenRvAdapter = new DetChildrenRvAdapter(SELECTED_ASSIGNMENT.getDetChildren(), DetChildrenActivity.this);
        rvDetChildren.setAdapter(detChildrenRvAdapter);
        detChildrenRvAdapter.getFilter().filter(""); //για να κάνει το αρχικό φιλτράρισμα
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_tasks_per_item);
            //getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        btnShowHide.setText(localized_show_hide_all);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String resourceId = SELECTED_ASSIGNMENT.getResourceId();
        String assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
        MyPrefs.setStringWithFileName(PREF_FILE_DET_CHILDREN_FOR_SHOW, assignmentId + "_" + resourceId, SELECTED_ASSIGNMENT.getDetChildren().toString());
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
               String showAllPrefix = showAll ? "@show_all" : "";
               detChildrenRvAdapter.getFilter().filter(showAllPrefix + newText);
               return true;
            }
        });

        return true;
    }
}
