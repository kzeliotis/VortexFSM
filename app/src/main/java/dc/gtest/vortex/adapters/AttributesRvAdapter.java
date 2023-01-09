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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.DeleteAttribute;
import dc.gtest.vortex.api.SendUpdatedAttribute;
import dc.gtest.vortex.models.AttributeModel;
import dc.gtest.vortex.support.MyCanEdit;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.attributeValueforScan;
import static dc.gtest.vortex.support.MyLocalization.localized_attribute_value;
import static dc.gtest.vortex.support.MyLocalization.localized_cancel;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_save;
import static dc.gtest.vortex.support.MyLocalization.localized_select_attribute;
import static dc.gtest.vortex.support.MyLocalization.localized_to_delete_attribute;
import static dc.gtest.vortex.support.MyLocalization.localized_update_attribute_value;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC;

public class AttributesRvAdapter extends RecyclerView.Adapter<AttributesRvAdapter.ViewHolder> implements Filterable {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;

    private final List<AttributeModel> allItems;
    private List<AttributeModel> filteredItems;

    public AttributesRvAdapter(List<AttributeModel> allItems, Context ctx) {
        this.allItems = allItems;
        filteredItems = allItems;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attribute, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = filteredItems.get(position);

        holder.tvAttributeDescription.setText(holder.mItem.getAttributeDescription());
        holder.tvAttributeValue.setText(holder.mItem.getAttributeValue());

        String attributeValue = MyPrefs.getStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_new_" + holder.mItem.getProjectProductId(),
                holder.mItem.getValueId(), holder.mItem.getAttributeValue());

        holder.tvAttributeValue.setText(attributeValue);

        holder.tvAttributeOldValue.setText(MyPrefs.getStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_old_" + holder.mItem.getProjectProductId(),
                holder.mItem.getValueId(), ""));

        holder.mView.setOnClickListener(v -> {

            if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {
                try {
                    JSONArray jArrayDefaultValues = holder.mItem.getAttributeDefaultValues();

                    if (jArrayDefaultValues.length() > 0) {
                        @SuppressLint("InflateParams")
                        LinearLayout llNewAttributeValue = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.dialog_spinner, null);
                        final TextView tvDialogSpinnerTitle = llNewAttributeValue.findViewById(R.id.tvDialogSpinnerTitle);
                        final Spinner spDialog = llNewAttributeValue.findViewById(R.id.spDialog);

                        String dialogSpinnerTitle;

                        if(attributeValue.length() > 0){
                            dialogSpinnerTitle = localized_attribute_value + ":" + "\n\n" + attributeValue;
                        } else {
                            dialogSpinnerTitle = localized_attribute_value;
                        }

                        tvDialogSpinnerTitle.setText(dialogSpinnerTitle);

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

                                    dialog.dismiss();

                                    if (spDialog.getSelectedItemPosition() != 0) {

                                        // saving old value
                                        MyPrefs.setStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_old_" + holder.mItem.getProjectProductId(),
                                                holder.mItem.getValueId(), holder.mItem.getAttributeValue());

                                        // saving new value
                                        MyPrefs.setStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_new_" + holder.mItem.getProjectProductId(),
                                                holder.mItem.getValueId(), spDialog.getSelectedItem().toString());

                                        holder.tvAttributeOldValue.setText(holder.mItem.getAttributeValue());

                                        holder.tvAttributeValue.setText(spDialog.getSelectedItem().toString());

                                        String prefKey = UUID.randomUUID().toString();

                                        String newValue = spDialog.getSelectedItem().toString();
                                        newValue = escapeUrlCharacters(newValue);
                                        // In old version attribute new value was encoded by URLEncoder. Check if it is need.
                                        String urlSuffix = "ProjectProductId=" + holder.mItem.getProjectProductId() +
                                                "&AttributeId=" + holder.mItem.getAttributeId() +
                                                "&Value=" + newValue +
                                                "&UserId=" + MyPrefs.getString(MyPrefs.PREF_USERID, "0") +
                                                "&ValueId=" + holder.mItem.getValueId();

                                        MyPrefs.setStringWithFileName(PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC, prefKey, urlSuffix);

                                        if (MyUtils.isNetworkAvailable()) {
                                            SendUpdatedAttribute sendUpdatedAttribute = new SendUpdatedAttribute(ctx, holder.mItem.getProjectProductId(), holder.mItem.getValueId());
                                            sendUpdatedAttribute.execute(prefKey);
                                        } else {
                                            Toast.makeText(ctx, localized_no_internet_data_saved, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .show();

                    } else {
                        @SuppressLint("InflateParams")
                        LinearLayout llNewAttributeValue = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.dialog_edit_text, null);
                        final TextView tvDialogEditTextTitle = llNewAttributeValue.findViewById(R.id.tvDialogEditTextTitle);
                        //final EditText etNewAttributeValue = llNewAttributeValue.findViewById(R.id.etNewAttributeValue);
                        attributeValueforScan = llNewAttributeValue.findViewById(R.id.etNewAttributeValue);

                        String dialogEditTextTitle;

                        if(attributeValue.length() > 0){
                            dialogEditTextTitle = localized_update_attribute_value + ":" + "\n\n" + attributeValue;
                        } else {
                            dialogEditTextTitle = localized_update_attribute_value;
                        }


                        tvDialogEditTextTitle.setText(dialogEditTextTitle);

                        AlertDialog b = new AlertDialog.Builder(ctx).create();
                        b.setView(llNewAttributeValue);
                        b.setButton(AlertDialog.BUTTON_NEGATIVE, localized_cancel, (DialogInterface.OnClickListener)null);
                        b.setButton(AlertDialog.BUTTON_POSITIVE, localized_save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyUtils.hideKeypad(ctx, v);

                                if (attributeValueforScan != null && attributeValueforScan.getText() != null && !attributeValueforScan.getText().toString().trim().isEmpty()) {

                                    // saving old value
                                    MyPrefs.setStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_old_" + holder.mItem.getProjectProductId(),
                                            holder.mItem.getValueId(), holder.mItem.getAttributeValue());

                                    // saving new value
                                    MyPrefs.setStringWithFileName(SELECTED_ASSIGNMENT.getAssignmentId() + "_new_" + holder.mItem.getProjectProductId(),
                                            holder.mItem.getValueId(),attributeValueforScan.getText().toString().trim());

                                    holder.tvAttributeOldValue.setText(holder.mItem.getAttributeValue());

                                    holder.tvAttributeValue.setText(attributeValueforScan.getText().toString().trim());

                                    String prefKey = UUID.randomUUID().toString();

                                    String newValue = attributeValueforScan.getText().toString().trim();
                                    newValue = escapeUrlCharacters(newValue);
                                    // In old version attribute new value was encoded by URLEncoder. Check if it is need.
                                    String urlSuffix = "ProjectProductId=" + holder.mItem.getProjectProductId() +
                                            "&AttributeId=" + holder.mItem.getAttributeId() +
                                            "&Value=" + newValue +
                                            "&UserId=" + MyPrefs.getString(MyPrefs.PREF_USERID, "0") +
                                            "&ValueId=" + holder.mItem.getValueId();

                                    MyPrefs.setStringWithFileName(PREF_FILE_UPDATED_ATTRIBUTES_FOR_SYNC, prefKey, urlSuffix);

                                    if (MyUtils.isNetworkAvailable()) {
                                        SendUpdatedAttribute sendUpdatedAttribute = new SendUpdatedAttribute(ctx, holder.mItem.getProjectProductId(), holder.mItem.getValueId());
                                        sendUpdatedAttribute.execute(prefKey);
                                    } else {
                                        Toast.makeText(ctx, localized_no_internet_data_saved, Toast.LENGTH_SHORT).show();
                                    }
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
            }
        });

        holder.mView.setOnLongClickListener(v -> {

            if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {
                new AlertDialog.Builder(ctx)
                        .setMessage(localized_to_delete_attribute)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            dialog.dismiss();

                            DeleteAttribute deleteAttribute = new DeleteAttribute(ctx);
                            deleteAttribute.execute(holder.mItem.getProjectProductId(), holder.mItem.getAttributeId(), holder.mItem.getValueId());
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView tvAttributeDescription;
        final TextView tvAttributeValue;
        final TextView tvAttributeOldValue;
        AttributeModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvAttributeDescription = view.findViewById(R.id.tvAttributeDescription);
            tvAttributeValue = view.findViewById(R.id.tvAttributeValue);
            tvAttributeOldValue = view.findViewById(R.id.tvAttributeOldValue);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (constraint.length() == 0) {
                    filteredItems = allItems;
                } else {
                    List<AttributeModel> filteredList = new ArrayList<>();

                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (AttributeModel mWords : allItems) {
                        if (mWords.getAttributeDescription().toLowerCase().contains(filterPattern)) {
                            filteredList.add(mWords);
                        }
                    }

                    filteredItems = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItems;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
//                filteredItems = (ArrayList<TopicModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    static String escapeUrlCharacters(String textValue) {
        try{
            textValue = textValue.replace("%", "%25");
            textValue = textValue.replace("&", "%26");
            textValue = textValue.replace("$", "%24");
            textValue = textValue.replace(":", "%3A");
            textValue = textValue.replace("@", "%40");
            textValue = textValue.replace("/", "%2F");
            textValue = textValue.replace(";", "%3B");
            textValue = textValue.replace("@", "%40");
            textValue = textValue.replace(" ", "%20");
            textValue = textValue.replace("<", "%3C");
            textValue = textValue.replace(">", "%3E");
            textValue = textValue.replace("#", "%23");
            textValue = textValue.replace("+", "%2B");
            textValue = textValue.replace("{", "%7B");
            textValue = textValue.replace("}", "%7D");
            textValue = textValue.replace("|", "%7C");
            textValue = textValue.replace("\\", "%5C");
            textValue = textValue.replace("^", "%5E");
            textValue = textValue.replace("~", "%7E");
            textValue = textValue.replace("[", "%5B");
            textValue = textValue.replace("]", "%5D");
            textValue = textValue.replace("‘", "%60");
            textValue = textValue.replace("?", "%3F");
            textValue = textValue.replace("=", "%3D");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return textValue;
    }


}
