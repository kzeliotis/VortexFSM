package am.gtest.vortex.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.ResourceLeaveModel;
import am.gtest.vortex.models.UserPartnerResourceModel;

import static am.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.USER_PARTNER_RESOURCE_LIST;
import static am.gtest.vortex.support.MyLocalization.localized_resources_not_available;

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

            if (singleSelection) {
                for (int i = 0; i < USER_PARTNER_RESOURCE_LIST.size(); i++) {
                    if ( USER_PARTNER_RESOURCE_LIST.get(i).isChecked() ) {
                        selectedResourcesIds = USER_PARTNER_RESOURCE_LIST.get(i).getResourceId() + "  ";
                        selectedResourcesNames = USER_PARTNER_RESOURCE_LIST.get(i).getResourceName() + "  ";
                    }
                }
            } else {
                for (int i = 0; i < USER_PARTNER_RESOURCE_LIST.size(); i++) {
                    if ( USER_PARTNER_RESOURCE_LIST.get(i).isChecked() ) {
                        selectedResourcesIds += USER_PARTNER_RESOURCE_LIST.get(i).getResourceId() + ", ";
                        selectedResourcesNames += USER_PARTNER_RESOURCE_LIST.get(i).getResourceName() + ", ";
                    }
                }
            }


            try{
                if(selectedResourcesIds.endsWith("  ") || selectedResourcesIds.endsWith(", ")){
                    selectedResourcesIds = selectedResourcesIds.substring(0, selectedResourcesIds.length() - 2);
                }
                if(selectedResourcesNames.endsWith("  ") || selectedResourcesNames.endsWith(", ")){
                    selectedResourcesNames = selectedResourcesNames.substring(0, selectedResourcesNames.length() - 2);
                }
            } catch (Exception ex){
                Toast.makeText(ctx, selectedResourcesIds + "\n\r" + selectedResourcesNames + "\n\r" + ex.getMessage(), Toast.LENGTH_LONG).show();
                ((Activity) ctx).finish();
            }

            NEW_ASSIGNMENT.setResourceIds(selectedResourcesIds);
            NEW_ASSIGNMENT.setResourceNames(selectedResourcesNames);

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
