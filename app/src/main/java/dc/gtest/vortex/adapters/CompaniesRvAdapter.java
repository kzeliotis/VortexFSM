package dc.gtest.vortex.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.GetCustomers;
import dc.gtest.vortex.models.CompanyModel;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.COMPANIES_LIST;
import static dc.gtest.vortex.support.MyGlobals.COMPANIES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_COMPANY;

public class CompaniesRvAdapter extends RecyclerView.Adapter<CompaniesRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<CompanyModel> mValues;
    private final CustomFilter mFilter;
    private final boolean isForNewAssignment;
    private final String scannedCodeForNewAssignment;

    public CompaniesRvAdapter(List<CompanyModel> items, Context ctx, boolean isForNewAssignment, String serial) {
        this.ctx = ctx;
        this.isForNewAssignment = isForNewAssignment;
        mValues = items;
        mFilter = new CustomFilter(CompaniesRvAdapter.this);
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
        holder.tvItemName.setText(holder.mItem.getCompanyName());

        holder.mView.setOnClickListener(v -> {
           //
            SELECTED_COMPANY.clearModel();
            //SELECTED_COMPANY = holder.mItem;

            if (MyUtils.isNetworkAvailable()) {
                new GetCustomers(ctx, isForNewAssignment, holder.mItem.getCompanyId(), "", scannedCodeForNewAssignment).execute(
                        "",
                        "",
                        "",
                        "",
                        ""
                );
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
        public CompanyModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvItemName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvItemName.getText() + "'";
        }
    }

    public class CustomFilter extends Filter {
        private final CompaniesRvAdapter mAdapter;

        private CustomFilter(CompaniesRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            COMPANIES_LIST_FILTERED.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                COMPANIES_LIST_FILTERED.addAll(COMPANIES_LIST);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final CompanyModel mWords : COMPANIES_LIST) {
                    if (mWords.toString().toLowerCase().contains(filterPattern)) {
                        COMPANIES_LIST_FILTERED.add(mWords);
                    }
                }
            }

            results.values = COMPANIES_LIST_FILTERED;
            results.count = COMPANIES_LIST_FILTERED.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
