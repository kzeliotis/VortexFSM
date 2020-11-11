package am.gtest.vortex.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.FrameLayout;

import am.gtest.vortex.R;
import am.gtest.vortex.adapters.AssignmentTypesRvAdapter;

import static am.gtest.vortex.support.MyGlobals.ASSIGNMENT_TYPES_LIST;

public class AssignmentTypesActivity extends BaseDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (MyUtils.isNetworkAvailable()) {
//            GetUserPartnersResources getUserPartnersResources = new GetUserPartnersResources();
//            getUserPartnersResources.execute();
//        }

        FrameLayout flBaseContainer = findViewById(R.id.flBaseDrawerLayout);
        getLayoutInflater().inflate(R.layout.content_assignment_types, flBaseContainer, true);

        RecyclerView rvAssignmentTypes = findViewById(R.id.rvAssignmentTypes);

        AssignmentTypesRvAdapter assignmentTypesRvAdapter = new AssignmentTypesRvAdapter(ASSIGNMENT_TYPES_LIST, AssignmentTypesActivity.this);
        rvAssignmentTypes.setAdapter(assignmentTypesRvAdapter);
    }

}
