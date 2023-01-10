package dc.gtest.vortex.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.AllAttributeModel;
import dc.gtest.vortex.models.AttributeModel;
import dc.gtest.vortex.support.MyJsonParser;

import static dc.gtest.vortex.activities.AllAttributesActivity.savedAttributes;
import static dc.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.NEW_ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.attributeValueforScan;
import static dc.gtest.vortex.support.MyLocalization.localized_attribute_value;
import static dc.gtest.vortex.support.MyLocalization.localized_cancel;
import static dc.gtest.vortex.support.MyLocalization.localized_save;
import static dc.gtest.vortex.support.MyLocalization.localized_select_attribute;

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
                    attributeValueforScan = view.findViewById(R.id.etNewAttributeValue);

                    tvDialogEditTextTitle.setText(localized_attribute_value);

//                    new AlertDialog.Builder(ctx)
//                            .setView(view)
//                            .setNegativeButton(localized_cancel, (dialog, which) -> dialog.dismiss())
//                            .setPositiveButton(localized_save, (dialog, which) -> {
//                                if (attributevalue_scan != null && attributevalue_scan.getText() != null
//                                        && !attributevalue_scan.getText().toString().equals("")) {
//
//                                    // this is used to send to server
//                                    savedAttributes = savedAttributes + "\"" + holder.mItem.getAttributeDescription() +
//                                            "\": \"" + attributevalue_scan.getText().toString() + "\",\n";
//
//                                    // this is used to update offline data
//                                    AttributeModel attributeModel = new AttributeModel();
//                                    attributeModel.setAttributeId(holder.mItem.getAttributeId());
//                                    attributeModel.setAttributeDescription(holder.mItem.getAttributeDescription());
//                                    attributeModel.setAttributeValue(attributevalue_scan.getText().toString());
//                                    NEW_ATTRIBUTES_LIST.add(attributeModel);
//                                }
//                            })
//                            .show();

                    AlertDialog b = new AlertDialog.Builder(ctx).create();
                    b.setView(view);
                    b.setButton(AlertDialog.BUTTON_NEGATIVE, localized_cancel, (DialogInterface.OnClickListener)null);
                    b.setButton(AlertDialog.BUTTON_POSITIVE, localized_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (attributeValueforScan != null && attributeValueforScan.getText() != null
                                    && !attributeValueforScan.getText().toString().equals("")) {

                                // this is used to send to server
                                savedAttributes = savedAttributes + "\"" + holder.mItem.getAttributeDescription() +
                                        "\": \"" + attributeValueforScan.getText().toString() + "\",\n";

                                // this is used to update offline data
                                AttributeModel attributeModel = new AttributeModel();
                                attributeModel.setAttributeId(holder.mItem.getAttributeId());
                                attributeModel.setAttributeDescription(holder.mItem.getAttributeDescription());
                                attributeModel.setAttributeValue(attributeValueforScan.getText().toString());
                                NEW_ATTRIBUTES_LIST.add(attributeModel);
                            }
                        }
                    });
                    b.setButton(AlertDialog.BUTTON_NEUTRAL, "Scan", (DialogInterface.OnClickListener)null);
                    b.show();

                    final Button scan = b.getButton(AlertDialog.BUTTON_NEUTRAL);
                    scan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntentIntegrator integrator = new IntentIntegrator((Activity) ctx);
                            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                            integrator.setPrompt("Scan");
                            integrator.setCameraId(0);
                            integrator.setBeepEnabled(false);
                            integrator.setBarcodeImageEnabled(false);
                            integrator.setOrientationLocked(true);
                            integrator.initiateScan();
                        }
                    });
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
