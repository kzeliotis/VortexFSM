package am.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.InstallationZoneEditActivity;
import am.gtest.vortex.api.DeleteProjectZone;
import am.gtest.vortex.models.ProjectZoneModel;
import static am.gtest.vortex.support.MyGlobals.INSTALLATION_ZONES_LIST;
import static am.gtest.vortex.support.MyGlobals.INSTALLATION_ZONES_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.NEW_INSTALLATION_ZONE;
import static am.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static am.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION_ZONE;
import static am.gtest.vortex.support.MyLocalization.localized_delete;
import static am.gtest.vortex.support.MyLocalization.localized_edit;
import static am.gtest.vortex.support.MyLocalization.localized_no;
import static am.gtest.vortex.support.MyLocalization.localized_to_delete_zone;
import static am.gtest.vortex.support.MyLocalization.localized_yes;

public class InstallationZonesRvAdapter extends RecyclerView.Adapter<InstallationZonesRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<ProjectZoneModel> mValues;
    private final CustomFilter mFilter;

    public InstallationZonesRvAdapter(List<ProjectZoneModel> items, Context ctx) {
        this.ctx = ctx;
        mValues = items;
        mFilter = new CustomFilter(InstallationZonesRvAdapter.this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installation_zone, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.tvZoneDescription.setText(holder.mItem.getZoneFullName());

        if (holder.mItem.getCustomFieldsString().isEmpty()) {
            holder.tvZoneInfo.setVisibility(View.GONE);
        } else {
            holder.tvZoneInfo.setVisibility(View.VISIBLE);
            holder.tvZoneInfo.setText(holder.mItem.getCustomFieldsString());
        }

        holder.mView.setOnClickListener(v -> {
            new AlertDialog.Builder(ctx)
                    .setNeutralButton(localized_delete, (dialog, which) -> {
                        dialog.dismiss();
                        deleteZone(holder.mItem.getZoneId());

                    })
                    .setPositiveButton(localized_edit, (dialog, which) -> {
                        dialog.dismiss();
                        NEW_INSTALLATION_ZONE = holder.mItem;
                        Intent intent = new Intent(ctx, InstallationZoneEditActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ctx.startActivity(intent);
                    })
                    .show();



//            Intent intent = new Intent(ctx, ZoneProductsActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("zoneName", holder.mItem.getZoneName());
//            intent.putExtra("zoneId", holder.mItem.getZoneId());
//            ctx.startActivity(intent);

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
        final TextView tvZoneInfo;
        public ProjectZoneModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvZoneDescription = view.findViewById(R.id.tvZoneDescription);
            tvZoneInfo = view.findViewById(R.id.tvZoneInfo);
        }
    }

    public class CustomFilter extends Filter {
        private final InstallationZonesRvAdapter mAdapter;

        private CustomFilter(InstallationZonesRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            INSTALLATION_ZONES_LIST_FILTERED.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                INSTALLATION_ZONES_LIST_FILTERED.addAll(INSTALLATION_ZONES_LIST);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final ProjectZoneModel mWords : INSTALLATION_ZONES_LIST) {
                    if (mWords.toString().toLowerCase().contains(filterPattern)) {
                        INSTALLATION_ZONES_LIST_FILTERED.add(mWords);
                    }
                }
            }

            results.values = INSTALLATION_ZONES_LIST_FILTERED;
            results.count = INSTALLATION_ZONES_LIST_FILTERED.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void deleteZone(String projectZoneId){
        new AlertDialog.Builder(ctx)
                .setMessage(localized_to_delete_zone)
                .setNegativeButton(localized_no, ((dialog, which) -> {
                    dialog.dismiss();
                }))
                .setPositiveButton(localized_yes, ((dialog, which) -> {
                    dialog.dismiss();
                    DeleteProjectZone deleteProjectZone = new DeleteProjectZone(ctx, SELECTED_INSTALLATION.getProjectInstallationId());
                    deleteProjectZone.execute(projectZoneId);

                }))
                .show();


    }

}
