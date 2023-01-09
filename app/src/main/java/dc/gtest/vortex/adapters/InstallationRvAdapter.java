package dc.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.InstallationDetailActivity;
import dc.gtest.vortex.models.InstallationModel;

import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_LIST;
import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;

public class InstallationRvAdapter extends RecyclerView.Adapter<InstallationRvAdapter.ViewHolder> implements Filterable  {

        private final Context ctx;
        private final List<InstallationModel> mValues;
        private final CustomFilter mFilter;

    public InstallationRvAdapter(List<InstallationModel> items, Context ctx) {
        this.ctx = ctx;
        mValues = items;
        mFilter = new CustomFilter(InstallationRvAdapter.this);
    }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installation, parent, false);
        return new ViewHolder(view);
    }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvInstallationDescription.setText(holder.mItem.getProjectInstallationFullDescription());

        if (holder.mItem.getProjectInstallationNotes().isEmpty()) {
            holder.tvInstallationNotes.setVisibility(View.GONE);
        } else {
            holder.tvInstallationNotes.setVisibility(View.VISIBLE);
            holder.tvInstallationNotes.setText(holder.mItem.getProjectInstallationNotes());
        }

        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, InstallationDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("zoneName", holder.mItem.getZoneName());
//            intent.putExtra("zoneId", holder.mItem.getZoneId());
            SELECTED_INSTALLATION = holder.mItem;
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
            final TextView tvInstallationDescription;
            final TextView tvInstallationNotes;
            public InstallationModel mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvInstallationDescription = view.findViewById(R.id.tvInstallationDescription);
                tvInstallationNotes = view.findViewById(R.id.tvInstallationNotes);
            }
        }

        public class CustomFilter extends Filter {
            private final InstallationRvAdapter mAdapter;

            private CustomFilter(InstallationRvAdapter mAdapter) {
                super();
                this.mAdapter = mAdapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                INSTALLATION_LIST_FILTERED.clear();
                final FilterResults results = new FilterResults();

                if (constraint.length() == 0) {
                    INSTALLATION_LIST_FILTERED.addAll(INSTALLATION_LIST);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final InstallationModel mWords : INSTALLATION_LIST) {
                        if (mWords.toString().toLowerCase().contains(filterPattern)) {
                            INSTALLATION_LIST_FILTERED.add(mWords);
                        }
                    }
                }

                results.values = INSTALLATION_LIST_FILTERED;
                results.count = INSTALLATION_LIST_FILTERED.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                this.mAdapter.notifyDataSetChanged();
            }
        }

}
