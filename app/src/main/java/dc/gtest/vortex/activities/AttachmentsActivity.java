package dc.gtest.vortex.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AssignmentTypesRvAdapter;
import dc.gtest.vortex.adapters.AttachmentsRvAdapter;
import dc.gtest.vortex.models.AttachmentModel;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MySliderMenu;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENT_TYPES_LIST;
import static dc.gtest.vortex.support.MyGlobals.KEY_REFRESH_CUSTOM_FIELDS;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_attachments;
import static dc.gtest.vortex.support.MyLocalization.localized_user;

import java.util.ArrayList;
import java.util.List;


public class AttachmentsActivity extends BaseDrawerActivity {

    public static boolean isProjectAttachments = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_attachments, flBaseContainer, true);

        RecyclerView rvAttachments = findViewById(R.id.rvAttachments);

        List<AttachmentModel> attachments = new ArrayList<>();
        isProjectAttachments = getIntent().getBooleanExtra("IsProjectAttachments", false);

        if(isProjectAttachments){
            attachments = SELECTED_ASSIGNMENT.getProjectAttachments();
        }else{
            attachments = SELECTED_ASSIGNMENT.getAttachments();
        }

        AttachmentsRvAdapter attachmentsRvAdapter = new AttachmentsRvAdapter(attachments, AttachmentsActivity.this);
        rvAttachments.setAdapter(attachmentsRvAdapter);
    }

    public void onRadioButtonClicked(View view) {
        MyLocalization.saveNewLanguage(this, view);
        updateUiTexts();
        new MySliderMenu(this).mySwitchSliderMenuLanguage(view);
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_attachments);
            if(isProjectAttachments){
                getSupportActionBar().setSubtitle(SELECTED_ASSIGNMENT.getProjectDescription());
            }

            //getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }
    }

}
