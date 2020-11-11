package am.gtest.vortex.adapters;

//import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.AddedConsumableModel;

public class AddedConsumablesRvAdapter extends RecyclerView.Adapter<AddedConsumablesRvAdapter.ViewHolder> implements Filterable {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final List<AddedConsumableModel> mValues;
    private final List<AddedConsumableModel> mValuesFiltered;
    private final CustomFilter mFilter;

    public AddedConsumablesRvAdapter(List<AddedConsumableModel> items, List<AddedConsumableModel> itemsFiltered) {
        mValues = items;
        mValuesFiltered = itemsFiltered;
        mFilter = new CustomFilter(AddedConsumablesRvAdapter.this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_added_consumable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValuesFiltered.get(position);

        holder.tvConsumableName.setText(holder.mItem.getName());
        holder.tvSuggested.setText(holder.mItem.getSuggested());
        holder.tvUsed.setText(holder.mItem.getUsed());

        if (holder.mItem.getNotes().isEmpty()) {
            holder.tvConsumableNotes.setVisibility(View.GONE);
            holder.tvConsumableNotes.setText("");
        } else {
            holder.tvConsumableNotes.setVisibility(View.VISIBLE);
            holder.tvConsumableNotes.setText(holder.mItem.getNotes());
        }
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
        final TextView tvConsumableName;
        final TextView tvConsumableNotes;
        final TextView tvSuggested;
        final TextView tvUsed;
        public AddedConsumableModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvConsumableName = view.findViewById(R.id.tvConsumableName);
            tvConsumableNotes = view.findViewById(R.id.tvConsumableNotes);
            tvSuggested = view.findViewById(R.id.tvSuggested);
            tvUsed = view.findViewById(R.id.tvUsed);
        }
    }

    public class CustomFilter extends Filter {
        private final AddedConsumablesRvAdapter mAdapter;

        private CustomFilter(AddedConsumablesRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mValuesFiltered.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                mValuesFiltered.addAll(mValues);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final AddedConsumableModel mWords : mValues) {
                    if (mWords.getName().toLowerCase().contains(filterPattern)) {
                        mValuesFiltered.add(mWords);
                    }
                }
            }

            results.values = mValuesFiltered;
            results.count = mValuesFiltered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
