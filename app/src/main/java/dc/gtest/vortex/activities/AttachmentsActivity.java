package dc.gtest.vortex.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.adapters.AssignmentTypesRvAdapter;
import dc.gtest.vortex.adapters.AttachmentsRvAdapter;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENT_TYPES_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;

public class AttachmentsActivity extends BaseDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_attachments, flBaseContainer, true);

        RecyclerView rvAttachments = findViewById(R.id.rvAttachments);

        AttachmentsRvAdapter attachmentsRvAdapter = new AttachmentsRvAdapter(SELECTED_ASSIGNMENT.getAttachments(), AttachmentsActivity.this);
        rvAttachments.setAdapter(attachmentsRvAdapter);
    }

}
