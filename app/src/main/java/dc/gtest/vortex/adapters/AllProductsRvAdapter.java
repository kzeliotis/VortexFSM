package dc.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AllAttributesActivity;
import dc.gtest.vortex.api.GetServices;
import dc.gtest.vortex.api.SendNewProduct;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.models.AllProductModel;
import dc.gtest.vortex.models.AttributeModel;
import dc.gtest.vortex.models.ProductModel;
import dc.gtest.vortex.support.MyDateTime;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.activities.AllAttributesActivity.savedAttributes;
import static dc.gtest.vortex.support.MyGlobals.ALL_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.CONST_PARENT_ALL_PRODUCTS_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.CONST_SHOW_PROGRESS_AND_TOAST;
import static dc.gtest.vortex.support.MyGlobals.KEY_MASTER_PRODUCT_COMPONENT_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_PARENT_ACTIVITY;
import static dc.gtest.vortex.support.MyGlobals.KEY_PRODUCT_COMPONENT_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_PRODUCT_DESCRIPTION;
import static dc.gtest.vortex.support.MyGlobals.KEY_PRODUCT_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_PROJECT_INSTALLATION_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_PROJECT_WAREHOUSE_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_REPLACE_PROJECT_PRODUCT_ID;
import static dc.gtest.vortex.support.MyGlobals.KEY_WAREHOUSE_ID;
import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.NEW_ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_add_product_components;
import static dc.gtest.vortex.support.MyLocalization.localized_ask_to_install_product;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_warranty_extension;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_PRODUCTS_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_DATA;
import static dc.gtest.vortex.support.MyPrefs.PREF_WARRANTY_EXTENSION_ON_PRODUCT_INSTALLATION;

public class AllProductsRvAdapter extends RecyclerView.Adapter<AllProductsRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<AllProductModel> mValues;
    private final CustomFilter mFilter;
    private final boolean warehouseProducts;
    private final boolean isForNewAssignment;
    private final String projectInstallationId;

    private final String replaceProjectProductId;
    private final String replaceProductComponentId;
    private final String projectWarehouseId;


    public AllProductsRvAdapter(List<AllProductModel> items, Context ctx, boolean isForNewAssignment,
                                boolean warehouseProducts, String projectInstallationId,
                                String replaceProjectProductId, String replaceProductComponentId,
                                String ProjectWarehouseId) {
        this.ctx = ctx;
        this.isForNewAssignment = isForNewAssignment;
        this.warehouseProducts = warehouseProducts;
        this.projectInstallationId = projectInstallationId;
        this.replaceProjectProductId = replaceProjectProductId;
        this.replaceProductComponentId = replaceProductComponentId;
        this.projectWarehouseId = ProjectWarehouseId;
        mValues = items;
        mFilter = new CustomFilter(AllProductsRvAdapter.this);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvAllProductDescription.setText(holder.mItem.getProductDescription());

        if(warehouseProducts){
            String Stock = holder.mItem.getStock();
            String BasicValue = holder.mItem.getBasicValue();
            String AttributeDescription = holder.mItem.getAttributeDescription();
            String Details;
            if(!BasicValue.isEmpty()) {
                Details = "Qty: " + Stock + "|" + AttributeDescription + ": " + BasicValue;
            }else{
                Details = "Qty: " + Stock;
            }

            holder.tvAllProductNotes.setVisibility(View.VISIBLE);
            holder.tvAllProductNotes.setText(Details);
        }else{
            if (holder.mItem.getNotes().isEmpty() || holder.mItem.getNotes().equals("-")) {
                holder.tvAllProductNotes.setVisibility(View.GONE);
            } else {
                holder.tvAllProductNotes.setVisibility(View.VISIBLE);
                holder.tvAllProductNotes.setText(holder.mItem.getNotes());
            }
        }


        holder.mView.setOnClickListener(v -> {
            if (isForNewAssignment) {
                NEW_ASSIGNMENT.setProductId(holder.mItem.getProductId());
                NEW_ASSIGNMENT.setProjectProductId("");
                NEW_ASSIGNMENT.setProductDescription(MyUtils.ToJson(holder.mItem.getProductDescription()));
                String customerid = "0";
                if(!NEW_ASSIGNMENT.getCustomerId().isEmpty()){
                    customerid = NEW_ASSIGNMENT.getCustomerId();
                }
                if (MyUtils.isNetworkAvailable()) {
                    GetServices getServices = new GetServices("0", "0", holder.mItem.getProductId(), customerid, false, ctx, false);
                    getServices.execute();
                }
                ((AppCompatActivity) ctx).finish(); // finish activity to go back to new assignment
            } else {

                String w_id = warehouseProducts ? MyPrefs.getString(MyPrefs.PREF_WAREHOUSEID, "0") : "0";
                if (!projectWarehouseId.isEmpty() && !projectWarehouseId.equals("0")) {w_id = "0";}

                final String _WarehouseId = w_id;

                String masterProductComponentId = holder.mItem.getMasterProductComponentId();

                if (!masterProductComponentId.equals("0") && !masterProductComponentId.isEmpty()) {
                    new AlertDialog.Builder(ctx)
                            .setMessage(localized_add_product_components)
                            .setNegativeButton(R.string.no, (dialog, which) -> {
                                dialog.dismiss();
                                SendItem(holder, _WarehouseId, "0", projectWarehouseId);
                            })
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                dialog.dismiss();
                                SendItem(holder, _WarehouseId, masterProductComponentId, projectWarehouseId);
                            })
                            .show();
                } else {
                    SendItem(holder, _WarehouseId, "0", projectWarehouseId);
                }

            }
        });
    }


    public void SendItem(ViewHolder holder, String WarehouseId, String masterProductComponentId, String projectWarehouseId){

        String ProjectProductId = holder.mItem.getProjectProductId();

        if(ProjectProductId.equals("0") || ProjectProductId.isEmpty()){

            Intent intent = new Intent(ctx, AllAttributesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_PARENT_ACTIVITY, CONST_PARENT_ALL_PRODUCTS_ACTIVITY);
            intent.putExtra(KEY_PRODUCT_DESCRIPTION, holder.mItem.getProductDescription());
            intent.putExtra(KEY_WAREHOUSE_ID, WarehouseId);
            intent.putExtra(KEY_PROJECT_WAREHOUSE_ID, projectWarehouseId);
            intent.putExtra(KEY_PRODUCT_ID, holder.mItem.getProductId());
            intent.putExtra(KEY_PROJECT_INSTALLATION_ID, projectInstallationId);
            intent.putExtra(KEY_REPLACE_PROJECT_PRODUCT_ID, replaceProjectProductId);
            intent.putExtra(KEY_PRODUCT_COMPONENT_ID, replaceProductComponentId);
            intent.putExtra(KEY_MASTER_PRODUCT_COMPONENT_ID, masterProductComponentId);
            ctx.startActivity(intent);
            ((AppCompatActivity) ctx).finish(); // finish activity to go to ProductsActivity when going back from AllAttributesActivity

        } else {

            LayoutInflater inflater = LayoutInflater.from(ctx);
            View dialogView = inflater.inflate(R.layout.item_warranty_extension, null);

            TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
            EditText warrantyInput = dialogView.findViewById(R.id.warranty_input);
            LinearLayout llWarranty = dialogView.findViewById(R.id.warranty_input_layout);
            TextView warrantyLabel = dialogView.findViewById(R.id.warranty_input_label);
            warrantyLabel.setText(localized_warranty_extension);

            if(!MyPrefs.getBoolean(PREF_WARRANTY_EXTENSION_ON_PRODUCT_INSTALLATION, false)){
                llWarranty.setVisibility(View.GONE);
            }

            messageTextView.setText(localized_ask_to_install_product + " " + SELECTED_ASSIGNMENT.getProjectDescription());

            new AlertDialog.Builder(ctx)
                    .setView(dialogView)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        savedAttributes = "";

                        // Get warranty value from input
                        String warrantyText = warrantyInput.getText().toString().trim();
                        float warrantyValue = 0.0f;

                        if (!warrantyText.isEmpty()) {
                            try {
                                warrantyValue = Float.parseFloat(warrantyText);
                            } catch (NumberFormatException e) {
                                warrantyValue = 0.0f; // Default value if parsing fails
                            }
                        }

                        String newProductJsonString =
                                "{\n" +
                                        "  \"assignmentId\": \"" + MyPrefs.getString(PREF_ASSIGNMENT_ID, "") + "\",\n" +
                                        "  \"newProductName\": \"" + MyUtils.ToJson(holder.mItem.getProductDescription()) + "\",\n" +
                                        "  \"WarehouseId\": \"" + MyPrefs.getString(MyPrefs.PREF_WAREHOUSEID, "0") + "\",\n" +
                                        "  \"projectWarehouseId\": \"" + projectWarehouseId + "\",\n" +
                                        "  \"ReplaceProjectProductId\": \"" + replaceProjectProductId + "\",\n" +
                                        "  \"ReplaceProductComponentId\": \"" + replaceProductComponentId + "\",\n" +
                                        "  \"ProjectProductId\": \"" + ProjectProductId + "\",\n" +
                                        "  \"ProjectInstallationId\": \"" + projectInstallationId + "\",\n" +
                                        "  \"MasterProductComponentId\": \"" + masterProductComponentId + "\",\n" +
                                        "  \"ProductId\": \"" + holder.mItem.getProductId() + "\",\n" +
                                        "  \"WarrantyExtension\": " + warrantyValue + ",\n" +
                                        "  \"Attributes\": {\n" +
                                        "    " + savedAttributes + "\n" +
                                        "  }\n" +
                                        "}";

                        String prefKey = UUID.randomUUID().toString();
                        MyPrefs.setStringWithFileName(PREF_FILE_NEW_PRODUCTS_FOR_SYNC, prefKey, newProductJsonString);

                        AttributeModel attributeModel = new AttributeModel();
                        attributeModel.setAttributeId(holder.mItem.getAttributeId());
                        attributeModel.setAttributeDescription(holder.mItem.getAttributeDescription());
                        attributeModel.setAttributeValue(holder.mItem.getBasicValue());
                        NEW_ATTRIBUTES_LIST.add(attributeModel);

                        ProductModel productModel = new ProductModel();
                        productModel.setInstallationDate(MyDateTime.get_MM_dd_yyyy_HH_mm_from_now());
                        productModel.setProductDescription(MyUtils.ToJson(holder.mItem.getProductDescription()));
                        productModel.setProductAttributes(NEW_ATTRIBUTES_LIST);
                        productModel.setNotSynchronized(true);

                        String productsData = MyPrefs.getStringWithFileName(PREF_FILE_PRODUCTS_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), "");

                        if (!productsData.isEmpty()) {
                            productsData = productsData.substring(0, productsData.length() - 1) + "," + productModel.toString() + "]";
                        } else {
                            productsData = "[" + productModel.toString() + "]";
                        }

                        MyPrefs.setStringWithFileName(PREF_FILE_PRODUCTS_DATA, SELECTED_ASSIGNMENT.getAssignmentId(), productsData);

                        NEW_ATTRIBUTES_LIST.clear();

                        if (MyUtils.isNetworkAvailable()) {
                            SendNewProduct sendNewProduct = new SendNewProduct(ctx, prefKey, CONST_SHOW_PROGRESS_AND_TOAST);
                            sendNewProduct.execute();
                        } else {
                            Toast.makeText(MyApplication.getContext(), localized_no_internet_data_saved, Toast.LENGTH_SHORT).show();
                        }

                        ((AppCompatActivity) ctx).finish();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();



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
        final TextView tvAllProductDescription;
        final TextView tvAllProductNotes;
        public AllProductModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvAllProductDescription = view.findViewById(R.id.tvAllProductDescription);
            tvAllProductNotes = view.findViewById(R.id.tvAllProductNotes);
        }
    }

    public class CustomFilter extends Filter {
        private final AllProductsRvAdapter mAdapter;

        private CustomFilter(AllProductsRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();

            if (warehouseProducts) {
                ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED.clear();

                if (constraint.length() == 0) {
                    ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED.addAll(ALL_WAREHOUSE_PRODUCTS_LIST);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final AllProductModel mWords : ALL_WAREHOUSE_PRODUCTS_LIST) {
                        if (mWords.toString().toLowerCase().contains(filterPattern)) {
                            ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED.add(mWords);
                        }
                    }
                }

                results.values = ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED;
                results.count = ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED.size();
            } else {
            ALL_PRODUCTS_LIST_FILTERED.clear();

            if (constraint.length() == 0) {
                ALL_PRODUCTS_LIST_FILTERED.addAll(ALL_PRODUCTS_LIST);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final AllProductModel mWords : ALL_PRODUCTS_LIST) {
                    if (mWords.toString().toLowerCase().contains(filterPattern)) {
                        ALL_PRODUCTS_LIST_FILTERED.add(mWords);
                    }
                }
            }

            results.values = ALL_PRODUCTS_LIST_FILTERED;
            results.count = ALL_PRODUCTS_LIST_FILTERED.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }




}
