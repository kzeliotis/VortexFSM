package am.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.ZoneProductsActivity;
import am.gtest.vortex.models.ZoneModel;

import static am.gtest.vortex.support.MyGlobals.ZONES_LIST;
import static am.gtest.vortex.support.MyGlobals.ZONES_LIST_FILTERED;

public class ZonesRvAdapter extends RecyclerView.Adapter<ZonesRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<ZoneModel> mValues;
    private final CustomFilter mFilter;

    public ZonesRvAdapter(List<ZoneModel> items, Context ctx) {
        this.ctx = ctx;
        mValues = items;
        mFilter = new CustomFilter(ZonesRvAdapter.this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zone, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.tvZoneDescription.setText(holder.mItem.getZoneFullName());

        if (holder.mItem.getZoneNotes().isEmpty()) {
            holder.tvZoneNotes.setVisibility(View.GONE);
        } else {
            holder.tvZoneNotes.setVisibility(View.VISIBLE);
            holder.tvZoneNotes.setText(holder.mItem.getZoneNotes());
        }

        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, ZoneProductsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("zoneName", holder.mItem.getZoneName());
            intent.putExtra("zoneId", holder.mItem.getZoneId());
            ctx.startActivity(intent);
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
        final TextView tvZoneDescription;
        final TextView tvZoneNotes;
        public ZoneModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvZoneDescription = view.findViewById(R.id.tvZoneDescription);
            tvZoneNotes = view.findViewById(R.id.tvZoneNotes);
        }
    }

    public class CustomFilter extends Filter {
        private final ZonesRvAdapter mAdapter;

        private CustomFilter(ZonesRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ZONES_LIST_FILTERED.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                ZONES_LIST_FILTERED.addAll(ZONES_LIST);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final ZoneModel mWords : ZONES_LIST) {
                    if (mWords.toString().toLowerCase().contains(filterPattern)) {
                        ZONES_LIST_FILTERED.add(mWords);
                    }
                }
            }

            results.values = ZONES_LIST_FILTERED;
            results.count = ZONES_LIST_FILTERED.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
