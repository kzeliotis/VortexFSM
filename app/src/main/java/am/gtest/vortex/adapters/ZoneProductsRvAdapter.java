package am.gtest.vortex.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.ZoneProductModel;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyGlobals.ValueSelected;
import static am.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST;
import static am.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST_FILTERED;
import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;

public class ZoneProductsRvAdapter extends RecyclerView.Adapter<ZoneProductsRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<ZoneProductModel> mValues;
    private final CustomFilter mFilter;

    private final String prefKey;
    private final String assignmentId;

    public ZoneProductsRvAdapter(List<ZoneProductModel> items, Context ctx, String prefKey, String assignmentId) {
        this.ctx = ctx;
        mValues = items;
        mFilter = new CustomFilter(ZoneProductsRvAdapter.this);
        this.prefKey = prefKey;
        this.assignmentId = assignmentId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zone_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvZoneProductName.setText(holder.mItem.getZoneProductName());
        holder.tvZoneProductIdentity.setText(holder.mItem.getZoneProductIdentity());

        holder.rvZoneProductAttributes.setAdapter(new ZoneProductAttributesRvAdapter(holder.mItem.getMeasurableAttributeModel(), ctx, holder.mItem.getZoneProductId(), prefKey, assignmentId));

        String assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {
            holder.llZoneProductsContainer.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
        } else {
            holder.llZoneProductsContainer.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_300));
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        final LinearLayout llZoneProductsContainer;
        final TextView tvZoneProductName;
        final TextView tvZoneProductIdentity;
        final RecyclerView rvZoneProductAttributes;
        public ZoneProductModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            llZoneProductsContainer = view.findViewById(R.id.llZoneProductsContainer);
            tvZoneProductName = view.findViewById(R.id.tvZoneProductName);
            tvZoneProductIdentity = view.findViewById(R.id.tvZoneProductIdentity);
            rvZoneProductAttributes = view.findViewById(R.id.rvZoneProductAttributes);
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class CustomFilter extends Filter {
        private final ZoneProductsRvAdapter mAdapter;

        private CustomFilter(ZoneProductsRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ZONE_PRODUCTS_LIST_FILTERED.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                ZONE_PRODUCTS_LIST_FILTERED.addAll(ZONE_PRODUCTS_LIST);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final ZoneProductModel mWords : ZONE_PRODUCTS_LIST) {
                    if (mWords.getZoneProductName().toLowerCase().contains(filterPattern) ||
                            mWords.getZoneProductIdentity().toLowerCase().contains(filterPattern)) {
                        ZONE_PRODUCTS_LIST_FILTERED.add(mWords);
                    }
                }
            }

            results.values = ZONE_PRODUCTS_LIST_FILTERED;
            results.count = ZONE_PRODUCTS_LIST_FILTERED.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
