package dc.gtest.vortex.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.ResourceLeaveModel;
import dc.gtest.vortex.models.UserPartnerResourceModel;

import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.USER_PARTNER_RESOURCE_LIST;

public class UserPartnerResourcesRvAdapter extends RecyclerView.Adapter<UserPartnerResourcesRvAdapter.ViewHolder> {

    private final Context ctx;
    private final List<UserPartnerResourceModel> mValues;
    private final boolean isForNewAssignment;
    private final boolean singleSelection;
    private final String assignmentDate;
    private final List<String> rIDs;

    public UserPartnerResourcesRvAdapter(List<UserPartnerResourceModel> items, Context ctx, boolean isForNewAssignment, boolean singleSelection, String AssignmentDate, List<String> rIDs) {
        this.ctx = ctx;
        this.isForNewAssignment = isForNewAssignment;
        this.singleSelection = singleSelection;
        this.assignmentDate =AssignmentDate;
        this.rIDs = rIDs;
        mValues = items;
        NEW_ASSIGNMENT.setResourceIds("");
        NEW_ASSIGNMENT.setResourceNames("");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_name_checkbox, parent, false);
        return new ViewHolder(view);




    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvItemName.setText(holder.mItem.getResourceName());

        try{
            if(!filterResourceLeaves(holder.mItem.getResourceId(), assignmentDate)){
                holder.chkBox.setChecked(false);
                holder.chkBox.setEnabled(false);
                holder.tvItemName.setEnabled(false);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }


        holder.chkBox.setOnCheckedChangeListener(null);

        holder.chkBox.setChecked(false);

        holder.chkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            holder.mItem.setChecked(isChecked);

            String selectedResourcesIds = "";
            String selectedResourcesNames = "";

            if (isChecked){
                if (singleSelection) {
                    NEW_ASSIGNMENT.setResourceIds(holder.mItem.getResourceId());
                    NEW_ASSIGNMENT.setResourceNames(holder.mItem.getResourceName());
                } else {
                    String resourceIDs = NEW_ASSIGNMENT.getResourceIds() + holder.mItem.getResourceId() + ", ";
                    String resourceNames = NEW_ASSIGNMENT.getResourceNames() + holder.mItem.getResourceName() + ", ";
                    NEW_ASSIGNMENT.setResourceIds(resourceIDs);
                    NEW_ASSIGNMENT.setResourceNames(resourceNames);
                }
            } else {
                List<String> rIds_ = new ArrayList<>(Arrays.asList(NEW_ASSIGNMENT.getResourceIds().split(", ")));
                List<String> rNs_ = new ArrayList<>(Arrays.asList(NEW_ASSIGNMENT.getResourceNames().split(", ")));
                NEW_ASSIGNMENT.setResourceIds("");
                NEW_ASSIGNMENT.setResourceNames("");
                if (rIds_.contains(holder.mItem.getResourceId())){
                    rIds_.remove(holder.mItem.getResourceId());
                    rNs_.remove(holder.mItem.getResourceName());
                    for (String id : rIds_)
                    {
                        NEW_ASSIGNMENT.setResourceIds(NEW_ASSIGNMENT.getResourceIds() + id + ", ");
                    }
                    for (String name : rNs_)
                    {
                        NEW_ASSIGNMENT.setResourceNames(NEW_ASSIGNMENT.getResourceNames() + name + ", ");
                    }
                }
            }

//            try{
//                if(NEW_ASSIGNMENT.getResourceIds().endsWith("  ") || NEW_ASSIGNMENT.getResourceIds().endsWith(", ")){
//                    NEW_ASSIGNMENT.setResourceIds(NEW_ASSIGNMENT.getResourceIds().substring(0, NEW_ASSIGNMENT.getResourceIds().length() - 2));
//                }
//                if(NEW_ASSIGNMENT.getResourceNames().endsWith("  ") || NEW_ASSIGNMENT.getResourceNames().endsWith(", ")){
//                    NEW_ASSIGNMENT.setResourceNames(NEW_ASSIGNMENT.getResourceNames().substring(0, NEW_ASSIGNMENT.getResourceNames().length() - 2));
//                }
//            } catch (Exception ex){
//                Toast.makeText(ctx, selectedResourcesIds + "\n\r" + selectedResourcesNames + "\n\r" + ex.getMessage(), Toast.LENGTH_LONG).show();
//                ((Activity) ctx).finish();
//            }


            if (singleSelection) {
                ((Activity) ctx).finish();
            }

        });

        //holder.chkBox.setChecked(holder.mItem.isChecked());
        if (!singleSelection) {
            String r_id = holder.mItem.getResourceId();
            holder.chkBox.setChecked(rIDs.contains(r_id));
        }
    }

    private boolean filterResourceLeaves(String resourceId, String assDate) throws ParseException {
        Date ass_date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(assDate);

        for(UserPartnerResourceModel upm : USER_PARTNER_RESOURCE_LIST){

            if (upm.getResourceId().equals(resourceId)){
                List<ResourceLeaveModel> leaves = upm.getLeaves();

                for(ResourceLeaveModel rlm : leaves){
                    String startDateStr = rlm.getLeaveStart();
                    String endDateStr = rlm.getLeaveEnd();
                    Date DateStart = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
                    Date DateEnd = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
                    if(!ass_date.before(DateStart) && !ass_date.after(DateEnd)){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvItemName;
        public final CheckBox chkBox;
        public UserPartnerResourceModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvItemName);
            chkBox = view.findViewById(R.id.chkBox);
        }
    }

}
