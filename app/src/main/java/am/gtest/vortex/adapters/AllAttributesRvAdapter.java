package am.gtest.vortex.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.AllAttributeModel;
import am.gtest.vortex.models.AttributeModel;
import am.gtest.vortex.support.MyJsonParser;

import static am.gtest.vortex.activities.AllAttributesActivity.savedAttributes;
import static am.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST;
import static am.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.NEW_ATTRIBUTES_LIST;
import static am.gtest.vortex.support.MyLocalization.localized_attribute_value;
import static am.gtest.vortex.support.MyLocalization.localized_cancel;
import static am.gtest.vortex.support.MyLocalization.localized_save;
import static am.gtest.vortex.support.MyLocalization.localized_select_attribute;

public class AllAttributesRvAdapter extends RecyclerView.Adapter<AllAttributesRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<AllAttributeModel> mValues;
    private final CustomFilter mFilter;

    public AllAttributesRvAdapter(List<AllAttributeModel> items, Context ctx) {
        this.ctx = ctx;
        mValues = items;
        mFilter = new CustomFilter(AllAttributesRvAdapter.this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_attributes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvAttributeDescription.setText(holder.mItem.getAttributeDescription());

        holder.mView.setOnClickListener(v -> {

            try {
                JSONArray jArrayDefaultValues = holder.mItem.getAttributeDefaultValues();

                if (jArrayDefaultValues.length() > 0) {
                    @SuppressLint("InflateParams")
                    LinearLayout llNewAttributeValue = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.dialog_spinner, null);
                    final TextView tvDialogSpinnerTitle = llNewAttributeValue.findViewById(R.id.tvDialogSpinnerTitle);
                    final Spinner spDialog = llNewAttributeValue.findViewById(R.id.spDialog);

                    tvDialogSpinnerTitle.setText(localized_attribute_value);

                    String[] newAttributesArray = new String[jArrayDefaultValues.length() + 1];

                    newAttributesArray[0] = localized_select_attribute;

                    for (int i = 0; i < jArrayDefaultValues.length(); i++) {
                        JSONObject oneObject = jArrayDefaultValues.getJSONObject(i);
                        newAttributesArray[i+1] = oneObject.getString("DefaultValue");
                    }

                    spDialog.setAdapter(new MySpinnerAdapter(ctx, newAttributesArray));

                    for (int i = 0; i < jArrayDefaultValues.length(); i++) {
                        JSONObject oneObject = jArrayDefaultValues.getJSONObject(i);

                        if (MyJsonParser.getBooleanValue(oneObject, "Initial", false)) {
                            spDialog.setSelection(i+1);
                        }
                    }

                    new AlertDialog.Builder(ctx)
                            .setView(llNewAttributeValue)
                            .setNegativeButton(localized_cancel, null)
                            .setPositiveButton(localized_save, (dialog, which) -> {
                                if (spDialog.getSelectedItemPosition() != 0) {

                                    // this is used to send to server
                                    savedAttributes = savedAttributes + "\"" + holder.mItem.getAttributeDescription() +
                                            "\": \"" + spDialog.getSelectedItem().toString() + "\",\n";

                                    // this is used to update offline data
                                    AttributeModel attributeModel = new AttributeModel();
                                    attributeModel.setAttributeId(holder.mItem.getAttributeId());
                                    attributeModel.setAttributeDescription(holder.mItem.getAttributeDescription());
                                    attributeModel.setAttributeValue(spDialog.getSelectedItem().toString());
                                    NEW_ATTRIBUTES_LIST.add(attributeModel);
                                }
                            })
                            .show();

                } else {
                    @SuppressLint("InflateParams")
                    View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_edit_text, null);
                    final TextView tvDialogEditTextTitle = view.findViewById(R.id.tvDialogEditTextTitle);
                    final EditText etNewAttributeValue = view.findViewById(R.id.etNewAttributeValue);

                    tvDialogEditTextTitle.setText(localized_attribute_value);

                    new AlertDialog.Builder(ctx)
                            .setView(view)
                            .setNegativeButton(localized_cancel, (dialog, which) -> dialog.dismiss())
                            .setPositiveButton(localized_save, (dialog, which) -> {
                                if (etNewAttributeValue != null && etNewAttributeValue.getText() != null
                                        && !etNewAttributeValue.getText().toString().equals("")) {

                                    // this is used to send to server
                                    savedAttributes = savedAttributes + "\"" + holder.mItem.getAttributeDescription() +
                                            "\": \"" + etNewAttributeValue.getText().toString() + "\",\n";

                                    // this is used to update offline data
                                    AttributeModel attributeModel = new AttributeModel();
                                    attributeModel.setAttributeId(holder.mItem.getAttributeId());
                                    attributeModel.setAttributeDescription(holder.mItem.getAttributeDescription());
                                    attributeModel.setAttributeValue(etNewAttributeValue.getText().toString());
                                    NEW_ATTRIBUTES_LIST.add(attributeModel);
                                }
                            })
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        final TextView tvAttributeDescription;
        public AllAttributeModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvAttributeDescription = view.findViewById(R.id.tvAttributeDescription);
        }
    }

    public class CustomFilter extends Filter {
        private final AllAttributesRvAdapter mAdapter;

        private CustomFilter(AllAttributesRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ALL_ATTRIBUTES_LIST_FILTERED.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                ALL_ATTRIBUTES_LIST_FILTERED.addAll(ALL_ATTRIBUTES_LIST);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final AllAttributeModel mWords : ALL_ATTRIBUTES_LIST) {
                    if (mWords.toString().toLowerCase().contains(filterPattern)) {
                        ALL_ATTRIBUTES_LIST_FILTERED.add(mWords);
                    }
                }
            }

            results.values = ALL_ATTRIBUTES_LIST_FILTERED;
            results.count = ALL_ATTRIBUTES_LIST_FILTERED.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
