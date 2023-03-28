package dc.gtest.vortex.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.AssignmentIndicatorModel;
import dc.gtest.vortex.models.ResourceLeaveModel;
import dc.gtest.vortex.models.UserPartnerResourceModel;

import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.USER_PARTNER_RESOURCE_LIST;

public class AssignmentIndicatorRvAdapter extends RecyclerView.Adapter<AssignmentIndicatorRvAdapter.ViewHolder> {

    private final Context ctx;
    private final List<AssignmentIndicatorModel> mValues;
    //private final boolean isForNewAssignment;
    //private final boolean singleSelection;
    //private final String assignmentDate;
    private final List<String> rIDs;

    public AssignmentIndicatorRvAdapter(List<AssignmentIndicatorModel> items, Context ctx, List<String> rIDs) {
        this.ctx = ctx;
//        this.isForNewAssignment = isForNewAssignment;
//        this.singleSelection = singleSelection;
//        this.assignmentDate =AssignmentDate;
        this.rIDs = rIDs;
        mValues = items;
        NEW_ASSIGNMENT.setAssignmentIndicatorIds("");
        NEW_ASSIGNMENT.setAssignmentIndicatorsDescription("");
    }

    @NonNull
    @Override
    public AssignmentIndicatorRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_name_checkbox, parent, false);
        return new AssignmentIndicatorRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AssignmentIndicatorRvAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvItemName.setText(holder.mItem.getAssignmentIndicatorDescription());

//        try{
//            if(!filterResourceLeaves(holder.mItem.getResourceId(), assignmentDate)){
//                holder.chkBox.setChecked(false);
//                holder.chkBox.setEnabled(false);
//                holder.tvItemName.setEnabled(false);
//            }
//        } catch (Exception ex){
//            ex.printStackTrace();
//        }


        holder.chkBox.setOnCheckedChangeListener(null);

        holder.chkBox.setChecked(false);

        holder.chkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            holder.mItem.setChecked(isChecked);

            String selectedResourcesIds = "";
            String selectedResourcesNames = "";

            if (isChecked){
                    String assignmentIndicatorIDs = NEW_ASSIGNMENT.getAssignmentIndicatorIds() + holder.mItem.getAssignmentIndicatorId() + ", ";
                    String assignmentIndicators = NEW_ASSIGNMENT.getAssignmentIndicatorsDescription() + holder.mItem.getAssignmentIndicatorDescription() + ", ";
                    NEW_ASSIGNMENT.setAssignmentIndicatorIds(assignmentIndicatorIDs);
                    NEW_ASSIGNMENT.setAssignmentIndicatorsDescription(assignmentIndicators);

            } else {
                List<String> rIds_ = new ArrayList<>(Arrays.asList(NEW_ASSIGNMENT.getAssignmentIndicatorIds().split(", ")));
                List<String> rNs_ = new ArrayList<>(Arrays.asList(NEW_ASSIGNMENT.getAssignmentIndicatorsDescription().split(", ")));
                NEW_ASSIGNMENT.setAssignmentIndicatorIds("");
                NEW_ASSIGNMENT.setAssignmentIndicatorsDescription("");
                if (rIds_.contains(holder.mItem.getAssignmentIndicatorId())){
                    rIds_.remove(holder.mItem.getAssignmentIndicatorId());
                    rNs_.remove(holder.mItem.getAssignmentIndicatorDescription());
                    for (String id : rIds_)
                    {
                        NEW_ASSIGNMENT.setAssignmentIndicatorIds(NEW_ASSIGNMENT.getAssignmentIndicatorIds() + id + ", ");
                    }
                    for (String name : rNs_)
                    {
                        NEW_ASSIGNMENT.setAssignmentIndicatorsDescription(NEW_ASSIGNMENT.getAssignmentTypeDescription() + name + ", ");
                    }
                }
            }


//            if (singleSelection) {
//                ((Activity) ctx).finish();
//            }

        });


//        if (!singleSelection) {
            String i_id = holder.mItem.getAssignmentIndicatorId();
            holder.chkBox.setChecked(rIDs.contains(i_id));
//        }
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvItemName;
        public final CheckBox chkBox;
        public AssignmentIndicatorModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvItemName);
            chkBox = view.findViewById(R.id.chkBox);
        }
    }
}
