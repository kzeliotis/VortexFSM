package dc.gtest.vortex.adapters;

import static dc.gtest.vortex.support.MyGlobals.COMPANIES_LIST;
import static dc.gtest.vortex.support.MyGlobals.COMPANIES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.MASTER_PROJECTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.MASTER_PROJECTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.AssignmentTypeModel;
import dc.gtest.vortex.models.CompanyModel;
import dc.gtest.vortex.models.MasterProjectModel;

public class MasterProjectsRvAdapter extends RecyclerView.Adapter<MasterProjectsRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<MasterProjectModel> mValues;
    private final CustomFilter mFilter;

    public MasterProjectsRvAdapter(List<MasterProjectModel> items, Context ctx) {
        this.ctx = ctx;
        mValues = items;
        mFilter = new CustomFilter(MasterProjectsRvAdapter.this);
    }

    @NonNull
    @Override

    public MasterProjectsRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_master_project, parent, false);
        return new MasterProjectsRvAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MasterProjectsRvAdapter.ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.tvItemName.setText(holder.mItem.getMasterProjectDescription());

        holder.mView.setOnClickListener(v -> {

            NEW_ASSIGNMENT.setMasterProjectId(holder.mItem.getMasterProjectId());
            NEW_ASSIGNMENT.setMasterProjectDescription(holder.mItem.getMasterProjectDescription());

            ((AppCompatActivity) ctx).finish();
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
        public MasterProjectModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvMasterProjectDescription);

        }
    }

    public class CustomFilter extends Filter {
        private final MasterProjectsRvAdapter mAdapter;

        private CustomFilter(MasterProjectsRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            MASTER_PROJECTS_LIST_FILTERED.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                MASTER_PROJECTS_LIST_FILTERED.addAll(MASTER_PROJECTS_LIST);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final MasterProjectModel mWords : MASTER_PROJECTS_LIST) {
                    if (mWords.toString().toLowerCase().contains(filterPattern)) {
                        MASTER_PROJECTS_LIST_FILTERED.add(mWords);
                    }
                }
            }

            results.values = MASTER_PROJECTS_LIST_FILTERED;
            results.count = MASTER_PROJECTS_LIST_FILTERED.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

}
