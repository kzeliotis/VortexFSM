package dc.gtest.vortex.adapters;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.GetCustomers;
import dc.gtest.vortex.models.ProjectModel;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.PROJECTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.PROJECTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_COMPANY;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PROJECT;

public class ProjectsRvAdapter extends RecyclerView.Adapter<ProjectsRvAdapter.ViewHolder> implements Filterable {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;
    private final List<ProjectModel> mValues;
    private final CustomFilter mFilter;
    private final boolean isForNewAssignment;
    private final String scannedCodeForNewAssignment;

    public ProjectsRvAdapter(List<ProjectModel> items, Context ctx, boolean isForNewAssignment, String serial) {
        this.ctx = ctx;
        this.isForNewAssignment = isForNewAssignment;
        mValues = items;
        mFilter = new CustomFilter(ProjectsRvAdapter.this);
        this.scannedCodeForNewAssignment = serial;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_chevron, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvItemName.setText(holder.mItem.getProjectDescription());

        holder.mView.setOnClickListener(v -> {
            //SELECTED_PROJECT = holder.mItem;

            Log.e(LOG_TAG, "---------- SELECTED_PROJECT: \n" + SELECTED_PROJECT);
            if(!scannedCodeForNewAssignment.isEmpty()){
                NEW_ASSIGNMENT.setProjectId(holder.mItem.getProjectId());
                NEW_ASSIGNMENT.setProjectDescription(holder.mItem.getProjectDescription());
                NEW_ASSIGNMENT.setProductId("");        // clear selected product when selecting project
                NEW_ASSIGNMENT.setProjectProductId("");
                NEW_ASSIGNMENT.setProductDescription("");
                ((AppCompatActivity) ctx).finish();
            } else {
                SELECTED_PROJECT.clearModel();
                String CustomerId = SELECTED_COMPANY.getCompanyId();
                SELECTED_COMPANY.clearModel();


                if (MyUtils.isNetworkAvailable()) {
                    new GetCustomers(ctx, isForNewAssignment,CustomerId, holder.mItem.getProjectId(), scannedCodeForNewAssignment).execute(
                            "",
                            "",
                            "",
                            "",
                            ""
                    );
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvItemName;
        public ProjectModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvItemName);
        }
    }

    public class CustomFilter extends Filter {
        private final ProjectsRvAdapter mAdapter;

        private CustomFilter(ProjectsRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            PROJECTS_LIST_FILTERED.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                PROJECTS_LIST_FILTERED.addAll(PROJECTS_LIST);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final ProjectModel mWords : PROJECTS_LIST) {
                    if (mWords.toString().toLowerCase().contains(filterPattern)) {
                        PROJECTS_LIST_FILTERED.add(mWords);
                    }
                }
            }

            results.values = PROJECTS_LIST_FILTERED;
            results.count = PROJECTS_LIST_FILTERED.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}