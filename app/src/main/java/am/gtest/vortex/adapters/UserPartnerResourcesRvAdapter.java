package am.gtest.vortex.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.UserPartnerResourceModel;

import static am.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.USER_PARTNER_RESOURCE_LIST;

public class UserPartnerResourcesRvAdapter extends RecyclerView.Adapter<UserPartnerResourcesRvAdapter.ViewHolder> {

    private final Context ctx;
    private final List<UserPartnerResourceModel> mValues;
    private final boolean isForNewAssignment;
    private final boolean singleSelection;

    public UserPartnerResourcesRvAdapter(List<UserPartnerResourceModel> items, Context ctx, boolean isForNewAssignment, boolean singleSelection) {
        this.ctx = ctx;
        this.isForNewAssignment = isForNewAssignment;
        this.singleSelection = singleSelection;
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

        holder.chkBox.setOnCheckedChangeListener(null);

        holder.chkBox.setChecked(holder.mItem.isChecked());

        holder.chkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            holder.mItem.setChecked(isChecked);

            StringBuilder selectedResourcesIds = new StringBuilder();
            StringBuilder selectedResourcesNames = new StringBuilder();

            if (singleSelection) {
                for (int i = 0; i < USER_PARTNER_RESOURCE_LIST.size(); i++) {
                    if ( USER_PARTNER_RESOURCE_LIST.get(i).isChecked() ) {
                        selectedResourcesIds.append(USER_PARTNER_RESOURCE_LIST.get(i).getResourceId());
                        selectedResourcesNames.append(USER_PARTNER_RESOURCE_LIST.get(i).getResourceName());
                    }
                }
            } else {
                for (int i = 0; i < USER_PARTNER_RESOURCE_LIST.size(); i++) {
                    if ( USER_PARTNER_RESOURCE_LIST.get(i).isChecked() ) {
                        selectedResourcesIds.append(USER_PARTNER_RESOURCE_LIST.get(i).getResourceId()).append(", ");
                        selectedResourcesNames.append(USER_PARTNER_RESOURCE_LIST.get(i).getResourceName()).append(", ");
                    }
                }
            }


            NEW_ASSIGNMENT.setResourceIds(selectedResourcesIds.toString());
            NEW_ASSIGNMENT.setResourceNames(selectedResourcesNames.toString());

            if (singleSelection) {
                ((Activity) ctx).finish();
            }

        });
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
