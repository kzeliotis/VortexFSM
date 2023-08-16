package dc.gtest.vortex.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.AddedConsumableModel;
import dc.gtest.vortex.models.AllConsumableModel;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;
import dc.gtest.vortex.unused.MinMaxFilter;


import static dc.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_CONSUMABLES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_FROM_PICKING_LIST;
import static dc.gtest.vortex.support.MyLocalization.localized_cancel;
import static dc.gtest.vortex.support.MyLocalization.localized_consumable_value;
import static dc.gtest.vortex.support.MyLocalization.localized_notes;
import static dc.gtest.vortex.support.MyLocalization.localized_notes_with_colon;
import static dc.gtest.vortex.support.MyLocalization.localized_quantity;
import static dc.gtest.vortex.support.MyLocalization.localized_save;
import static dc.gtest.vortex.support.MyLocalization.localized_suggested_value_with_colon;
import static dc.gtest.vortex.support.MyLocalization.localized_used_value_with_colon;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_QTY_LIMIT_CONSUMABLE_FROM_PICKING;

public class AllConsumablesRvAdapter extends RecyclerView.Adapter<AllConsumablesRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<AllConsumableModel> mValues;
    private final CustomFilter mFilter;
    private final boolean warehouseProducts;
    private final boolean pickingList;

    public AllConsumablesRvAdapter(List<AllConsumableModel> items, Context ctx, boolean WarehouseProducts, boolean PickingList) {
        this.ctx = ctx;
        mValues = items;
        warehouseProducts = WarehouseProducts;
        pickingList = PickingList;
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
        //holder.tvConsumableName.setText(holder.mItem.getConsumableName());

        if(!pickingList) {
            holder.tvAddedPickingNotes.setVisibility(View.GONE);
            holder.tvQTY.setVisibility(View.GONE);
            holder.etAddedPickingNotes.setVisibility(View.GONE);
            holder.etPickingQty.setVisibility(View.GONE);
        }else{
            holder.tvChevronConsumable.setVisibility(View.GONE);
            holder.tvQTY.setText(localized_used_value_with_colon);
            holder.tvAddedPickingNotes.setText(localized_notes_with_colon);
            holder.etAddedPickingNotes.setText(holder.mItem.getNotes());
            if(MyPrefs.getBoolean(PREF_QTY_LIMIT_CONSUMABLE_FROM_PICKING, false)){
                String stock = holder.mItem.getStock().replace(",", ".");
                holder.etPickingQty.setFilters(new InputFilter[]{new MinMaxFilter(0.0, Double.parseDouble(stock))});
            }
        }

        if(warehouseProducts || pickingList){
            String desc = holder.mItem.getConsumableName();
            String stock = holder.mItem.getStock();
            desc = desc + "\n\r" + "Qty: " + stock;
            holder.tvConsumableName.setText(desc);
        }else{
            holder.tvConsumableName.setText(holder.mItem.getConsumableName());
        }

        holder.etAddedPickingNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int detPickingId = holder.mItem.getDetPickingId();

                for(AddedConsumableModel m : SELECTED_FROM_PICKING_LIST){
                    if(m.getDetPickingId() == detPickingId){
                        SELECTED_FROM_PICKING_LIST.remove(m);
                    }
                }

                AddedConsumableModel addedConsumableModel = new AddedConsumableModel();

                addedConsumableModel.setName(MyUtils.ToJson(holder.mItem.getConsumableName()));
                addedConsumableModel.setNotes(MyUtils.ToJson(holder.etAddedPickingNotes.getText().toString().trim()));
                addedConsumableModel.setSuggested(holder.etPickingQty.getText().toString().trim());
                addedConsumableModel.setUsed(holder.etPickingQty.getText().toString().trim());
                addedConsumableModel.setProductId(holder.mItem.getProductId());
                addedConsumableModel.setDetPickingId(holder.mItem.getDetPickingId());
                String warehouseId = "0";
                addedConsumableModel.setWarehouseId(warehouseId);
                String spit = addedConsumableModel.getUsed().length() == 0 ? "0" : addedConsumableModel.getUsed();
                if(!spit.equals("0")) {
                    SELECTED_FROM_PICKING_LIST.add(addedConsumableModel);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        holder.etPickingQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int detPickingId = holder.mItem.getDetPickingId();

                for(AddedConsumableModel m : SELECTED_FROM_PICKING_LIST){
                    if(m.getDetPickingId() == detPickingId){
                        SELECTED_FROM_PICKING_LIST.remove(m);
                    }
                }

                AddedConsumableModel addedConsumableModel = new AddedConsumableModel();

                addedConsumableModel.setName(MyUtils.ToJson(holder.mItem.getConsumableName()));
                addedConsumableModel.setNotes(MyUtils.ToJson(holder.etAddedPickingNotes.getText().toString().trim()));
                addedConsumableModel.setSuggested(holder.etPickingQty.getText().toString().trim());
                addedConsumableModel.setUsed(holder.etPickingQty.getText().toString().trim());
                addedConsumableModel.setProductId(holder.mItem.getProductId());
                addedConsumableModel.setDetPickingId(holder.mItem.getDetPickingId());
                String warehouseId = "0";
                addedConsumableModel.setWarehouseId(warehouseId);
                String spit = addedConsumableModel.getUsed().length() == 0 ? "0" : addedConsumableModel.getUsed();
                if(!spit.equals("0")) {
                    SELECTED_FROM_PICKING_LIST.add(addedConsumableModel);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        if(!pickingList){
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

                String stock = "";
                if(warehouseProducts){
                    stock = holder.mItem.getStock().replace(",", ".");
                    etUsedConsumablesValue.setFilters(new InputFilter[]{new MinMaxFilter(0.0, Double.parseDouble(stock))});
                }

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

                                addedConsumableModel.setName(MyUtils.ToJson(holder.mItem.getConsumableName()));
                                addedConsumableModel.setNotes(MyUtils.ToJson(etConsumableNotes.getText().toString().trim()));
                                addedConsumableModel.setSuggested(etSuggestedConsumablesValue.getText().toString().trim());
                                addedConsumableModel.setUsed(etUsedConsumablesValue.getText().toString().trim());
                                addedConsumableModel.setProductId(holder.mItem.getProductId());
                                String warehouseId = "0";
                                if(warehouseProducts){
                                    warehouseId = MyPrefs.getString(MyPrefs.PREF_WAREHOUSEID, "0");
                                    addedConsumableModel.setStock(holder.mItem.getStock().replace(",", "."));
                                }
                                addedConsumableModel.setWarehouseId(warehouseId);

                                CONSUMABLES_TOADD_LIST.add(addedConsumableModel);
                                MyPrefs.setStringWithFileName(PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC, SELECTED_ASSIGNMENT.getAssignmentId(), CONSUMABLES_TOADD_LIST.toString());
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
        final TextView tvAddedPickingNotes;
        final TextView tvQTY;
        final TextView tvChevronConsumable;
        final EditText etAddedPickingNotes;
        final EditText etPickingQty;
        public AllConsumableModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvConsumableName = view.findViewById(R.id.tvConsumableName);
            tvAddedPickingNotes = view.findViewById(R.id.tvAddedPickingNotes);
            tvQTY = view.findViewById(R.id.tvQTY);
            etAddedPickingNotes = view.findViewById(R.id.etAddedPickingNotes);
            etPickingQty = view.findViewById(R.id.etPickingQty);
            tvChevronConsumable = view.findViewById(R.id.tvChevronConsumable);
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
            final FilterResults results = new FilterResults();
            if (warehouseProducts){
                ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED.clear();

                if (constraint.length() == 0) {
                    ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED.addAll(ALL_WAREHOUSE_CONSUMABLES_LIST);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final AllConsumableModel mWords : ALL_WAREHOUSE_CONSUMABLES_LIST) {
                        if (mWords.getConsumableName().toLowerCase().contains(filterPattern)) {
                            ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED.add(mWords);
                        }
                    }
                }

                results.values = ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED;
                results.count = ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED.size();
            } else {
                ALL_CONSUMABLES_LIST_FILTERED.clear();

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
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
