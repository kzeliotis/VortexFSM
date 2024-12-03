package dc.gtest.vortex.activities;

import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_tasks_per_item;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CHILDREN_FOR_SHOW;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.DetChildrenRvAdapter;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySliderMenu;

public class DetChildrenActivity extends BaseDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_det_children, flBaseContainer, true);

        RecyclerView rvDetChildren = findViewById(R.id.rvDetChildren);

        String resourceId = SELECTED_ASSIGNMENT.getResourceId();
        String assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
        String detChildData = MyPrefs.getStringWithFileName(PREF_FILE_DET_CHILDREN_FOR_SHOW, assignmentId + "_" + resourceId, "");
        if ((detChildData.isEmpty() || detChildData.equals("[]")) && !SELECTED_ASSIGNMENT.getDetChildren().isEmpty() ) {
            MyPrefs.setStringWithFileName(PREF_FILE_DET_CHILDREN_FOR_SHOW, assignmentId + "_" + resourceId, SELECTED_ASSIGNMENT.getDetChildren().toString());
        }

        DetChildrenRvAdapter detChildrenRvAdapter = new DetChildrenRvAdapter(SELECTED_ASSIGNMENT.getDetChildren(), DetChildrenActivity.this);
        rvDetChildren.setAdapter(detChildrenRvAdapter);
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
    }
}
