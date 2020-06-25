package am.gtest.vortex.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.AddedConsumableModel;
import am.gtest.vortex.models.AllConsumableModel;

import static am.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST;
import static am.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST;
import static am.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST;
import static am.gtest.vortex.support.MyLocalization.localized_cancel;
import static am.gtest.vortex.support.MyLocalization.localized_consumable_value;
import static am.gtest.vortex.support.MyLocalization.localized_notes_with_colon;
import static am.gtest.vortex.support.MyLocalization.localized_save;
import static am.gtest.vortex.support.MyLocalization.localized_suggested_value_with_colon;
import static am.gtest.vortex.support.MyLocalization.localized_used_value_with_colon;

public class AllConsumablesRvAdapter extends RecyclerView.Adapter<AllConsumablesRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<AllConsumableModel> mValues;
    private final CustomFilter mFilter;

    public AllConsumablesRvAdapter(List<AllConsumableModel> items, Context ctx) {
        this.ctx = ctx;
        mValues = items;
        mFilter = new CustomFilter(AllConsumablesRvAdapter.this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_consumables, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvConsumableName.setText(holder.mItem.getConsumableName());

        holder.mView.setOnClickListener(v -> {
            @SuppressLint("InflateParams")
            View view = ((Activity)ctx).getLayoutInflater().inflate(R.layout.dialog_consumables, null);

            final TextView tvConsumableValueTitle = view.findViewById(R.id.tvConsumableValueTitle);
            final TextView tvSuggestedConsumableTitle = view.findViewById(R.id.tvSuggestedConsumableTitle);
            final TextView tvUsedConsumableTitle = view.findViewById(R.id.tvUsedConsumableTitle);
            final TextView tvConsumableNotesTitle = view.findViewById(R.id.tvConsumableNotesTitle);

            final EditText etSuggestedConsumablesValue = view.findViewById(R.id.etSuggestedConsumablesValue);
            final EditText etUsedConsumablesValue = view.findViewById(R.id.etUsedConsumablesValue);
            final EditText etConsumableNotes = view.findViewById(R.id.etConsumableNotes);

            tvConsumableValueTitle.setText(localized_consumable_value);
            tvSuggestedConsumableTitle.setText(localized_suggested_value_with_colon);
            tvUsedConsumableTitle.setText(localized_used_value_with_colon);
            tvConsumableNotesTitle.setText(localized_notes_with_colon);

            new AlertDialog.Builder(ctx)
                    .setView(view)
                    .setNegativeButton(localized_cancel, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(localized_save, (dialog, which) -> {
                        if (etUsedConsumablesValue != null && etUsedConsumablesValue.getText() != null
                                && !etUsedConsumablesValue.getText().toString().equals("")) {

                            AddedConsumableModel addedConsumableModel = new AddedConsumableModel();

                            addedConsumableModel.setName(holder.mItem.getConsumableName());
                            addedConsumableModel.setNotes(etConsumableNotes.getText().toString().trim());
                            addedConsumableModel.setSuggested(etSuggestedConsumablesValue.getText().toString().trim());
                            addedConsumableModel.setUsed(etUsedConsumablesValue.getText().toString().trim());
                            addedConsumableModel.setProductId(holder.mItem.getProductId());

                            CONSUMABLES_TOADD_LIST.add(addedConsumableModel);
                            ADDED_CONSUMABLES_LIST.add(addedConsumableModel);
  //                            savedConsumable = savedConsumable + "{" +
//                                    "\"name\":\"" + holder.mItem.getConsumableName() + "\"," +
//                                    "\"notes\":\"" + etConsumableNotes.getText().toString().trim() + "\"," +
//                                    "\"suggested\":\"" + etSuggestedConsumablesValue.getText().toString().trim() + "\"," +
//                                    "\"used\":\"" + etUsedConsumablesValue.getText().toString().trim() + "\""  +
//                                    "},";
                        }
                    })
                    .show();
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
        final TextView tvConsumableName;
        public AllConsumableModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvConsumableName = view.findViewById(R.id.tvConsumableName);
        }
    }

    public class CustomFilter extends Filter {
        private final AllConsumablesRvAdapter mAdapter;

        private CustomFilter(AllConsumablesRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ALL_CONSUMABLES_LIST_FILTERED.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                ALL_CONSUMABLES_LIST_FILTERED.addAll(ALL_CONSUMABLES_LIST);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final AllConsumableModel mWords : ALL_CONSUMABLES_LIST) {
                    if (mWords.getConsumableName().toLowerCase().contains(filterPattern)) {
                        ALL_CONSUMABLES_LIST_FILTERED.add(mWords);
                    }
                }
            }

            results.values = ALL_CONSUMABLES_LIST_FILTERED;
            results.count = ALL_CONSUMABLES_LIST_FILTERED.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
