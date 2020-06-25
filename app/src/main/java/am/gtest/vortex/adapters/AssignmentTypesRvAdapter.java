package am.gtest.vortex.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.AssignmentTypeModel;

import static am.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;

public class AssignmentTypesRvAdapter extends RecyclerView.Adapter<AssignmentTypesRvAdapter.ViewHolder> {

    private final Context ctx;
    private final List<AssignmentTypeModel> mValues;

    public AssignmentTypesRvAdapter(List<AssignmentTypeModel> items, Context ctx) {
        this.ctx = ctx;
        mValues = items;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment_type, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.tvItemName.setText(holder.mItem.getAssignmentTypeDescription());

        holder.mView.setOnClickListener(v -> {

            NEW_ASSIGNMENT.setAssignmentType(holder.mItem.getAssignmentTypeId());
            NEW_ASSIGNMENT.setAssignmentTypeDescription(holder.mItem.getAssignmentTypeDescription());

           ((AppCompatActivity) ctx).finish();
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvItemName;
        public AssignmentTypeModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvAssignmentTypeDescription);

        }
    }

}
