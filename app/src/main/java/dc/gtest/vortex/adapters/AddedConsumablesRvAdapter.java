package dc.gtest.vortex.adapters;

//import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.data.ConsumablesToAddData;
import dc.gtest.vortex.models.AddedConsumableModel;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;
import dc.gtest.vortex.unused.MinMaxFilter;

import static dc.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST;
import static dc.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_cancel;
import static dc.gtest.vortex.support.MyLocalization.localized_consumable_value;
import static dc.gtest.vortex.support.MyLocalization.localized_delete;
import static dc.gtest.vortex.support.MyLocalization.localized_notes_with_colon;
import static dc.gtest.vortex.support.MyLocalization.localized_save;
import static dc.gtest.vortex.support.MyLocalization.localized_suggested_value_with_colon;
import static dc.gtest.vortex.support.MyLocalization.localized_used_value_with_colon;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC;

public class AddedConsumablesRvAdapter extends RecyclerView.Adapter<AddedConsumablesRvAdapter.ViewHolder> implements Filterable {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private final Context ctx;
    private final List<AddedConsumableModel> mValues;
    private final List<AddedConsumableModel> mValuesFiltered;
    private final boolean edit;
    private final CustomFilter mFilter;
    private AddedConsumableModel _selectedModel;
    private AddedConsumableModel _selectedModelAdded;


    public AddedConsumablesRvAdapter(Context ctx, List<AddedConsumableModel> items, List<AddedConsumableModel> itemsFiltered, boolean edit) {
        mValues = items;
        mValuesFiltered = itemsFiltered;
        mFilter = new CustomFilter(AddedConsumablesRvAdapter.this);
        this.ctx = ctx;
        this.edit = edit;
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

        if(edit){
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

                etSuggestedConsumablesValue.setText(holder.mItem.getSuggested());
                etUsedConsumablesValue.setText(holder.mItem.getUsed());
                etConsumableNotes.setText(holder.mItem.getNotes());

                String stock = holder.mItem.getStock().replace(",", ".");
                if(!stock.equals("0")){
                    etUsedConsumablesValue.setFilters(new InputFilter[]{new MinMaxFilter(0.0, Double.parseDouble(stock))});
                }

                tvConsumableValueTitle.setText(localized_consumable_value);
                tvSuggestedConsumableTitle.setText(localized_suggested_value_with_colon);
                tvUsedConsumableTitle.setText(localized_used_value_with_colon);
                tvConsumableNotesTitle.setText(localized_notes_with_colon);

                for(AddedConsumableModel m : CONSUMABLES_TOADD_LIST){
                    if(m.getProductId() == holder.mItem.getProductId() && m.getUsed().equals(holder.mItem.getUsed()) && m.getSuggested().equals(holder.mItem.getSuggested()) &&
                            m.getNotes().equals(holder.mItem.getNotes())){
                        _selectedModel = m;
                    }
                }

                for(AddedConsumableModel m : ADDED_CONSUMABLES_LIST){
                    if(m.getProductId() == holder.mItem.getProductId() && m.getUsed().equals(holder.mItem.getUsed()) && m.getSuggested().equals(holder.mItem.getSuggested()) &&
                            m.getNotes().equals(holder.mItem.getNotes())){
                        _selectedModelAdded = m;
                    }
                }

                new AlertDialog.Builder(ctx)
                        .setView(view)
                        .setNegativeButton(localized_cancel, (dialog, which) -> dialog.dismiss())
                        .setNeutralButton(localized_delete, (dialog, which) -> {
                            CONSUMABLES_TOADD_LIST.remove(_selectedModel);
                            MyPrefs.setStringWithFileName(PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC, SELECTED_ASSIGNMENT.getAssignmentId(), CONSUMABLES_TOADD_LIST.toString());
                            ADDED_CONSUMABLES_LIST.remove(_selectedModelAdded);
                            ConsumablesToAddData.generate(SELECTED_ASSIGNMENT.getAssignmentId());
                            AddedConsumablesRvAdapter.this.notifyDataSetChanged();
                        })
                        .setPositiveButton(localized_save, (dialog, which) -> {
                            if (etUsedConsumablesValue != null && etUsedConsumablesValue.getText() != null
                                    && !etUsedConsumablesValue.getText().toString().equals("")) {

                                int idx_to_add = CONSUMABLES_TOADD_LIST.indexOf(_selectedModel);
                                CONSUMABLES_TOADD_LIST.remove(_selectedModel);
                                int idx_added = ADDED_CONSUMABLES_LIST.indexOf(_selectedModelAdded);
                                ADDED_CONSUMABLES_LIST.remove(_selectedModelAdded);

                                AddedConsumableModel addedConsumableModel = new AddedConsumableModel();

                                addedConsumableModel.setName(MyUtils.ToJson(holder.mItem.getName()));
                                addedConsumableModel.setNotes(MyUtils.ToJson(etConsumableNotes.getText().toString().trim()));
                                addedConsumableModel.setSuggested(etSuggestedConsumablesValue.getText().toString().trim());
                                addedConsumableModel.setUsed(etUsedConsumablesValue.getText().toString().trim());
                                addedConsumableModel.setProductId(holder.mItem.getProductId());
                                String warehouseId = "0";
                                String _stock = holder.mItem.getStock().replace(",", ".");
                                if(!_stock.equals("0")){
                                    warehouseId = MyPrefs.getString(MyPrefs.PREF_WAREHOUSEID, "0");
                                    addedConsumableModel.setStock(_stock);
                                }
                                addedConsumableModel.setWarehouseId(warehouseId);

                                CONSUMABLES_TOADD_LIST.add(idx_to_add, addedConsumableModel);
                                MyPrefs.setStringWithFileName(PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC, SELECTED_ASSIGNMENT.getAssignmentId(), CONSUMABLES_TOADD_LIST.toString());
                                if(idx_added >= 0) {ADDED_CONSUMABLES_LIST.add(idx_added, addedConsumableModel);}

                                ConsumablesToAddData.generate(SELECTED_ASSIGNMENT.getAssignmentId());
                                AddedConsumablesRvAdapter.this.notifyDataSetChanged();
                            }
                        })
                        .show();
            });
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
