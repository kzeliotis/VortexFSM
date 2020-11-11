package am.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.api.DeleteProduct;
import am.gtest.vortex.activities.AttributesActivity;
import am.gtest.vortex.items.MeasurementsListActivity;
import am.gtest.vortex.models.ProductModel;
import am.gtest.vortex.support.MyCanEdit;

import static am.gtest.vortex.activities.ProductsActivity.selectedType;
import static am.gtest.vortex.support.MyGlobals.MANDATORY_MEASUREMENTS_LIST;
import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static am.gtest.vortex.support.MyGlobals.globalSelectedProductId;
import static am.gtest.vortex.support.MyLocalization.localized_Mandatory_Measurements_Missing;
import static am.gtest.vortex.support.MyLocalization.localized_attributes;
import static am.gtest.vortex.support.MyLocalization.localized_measurements;
import static am.gtest.vortex.support.MyLocalization.localized_to_delete_product;
import static android.content.Context.MODE_PRIVATE;

public class ProductsRvAdapter extends RecyclerView.Adapter<ProductsRvAdapter.ViewHolder> implements Filterable {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;

    private List<ProductModel> allItems;
    private List<ProductModel> filteredItems;

    public ProductsRvAdapter(List<ProductModel> allItems, Context ctx) {
        this.allItems = allItems;
        filteredItems = allItems;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_chevron, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = filteredItems.get(position);
        holder.tvItemName.setText(holder.mItem.getProductDescription());

        if (holder.mItem.isNotSynchronized()) {
            holder.tvItemName.setTextColor(ContextCompat.getColor(ctx, R.color.red_700));
        } else {
            holder.tvItemName.setTextColor(ContextCompat.getColor(ctx, R.color.grey_900));
        }

        String attributesString = holder.mItem.getProductAttributesString();
        if (attributesString.length() > 0){
            holder.tvItemDetails.setVisibility(View.VISIBLE);
            holder.tvItemDetails.setText(attributesString);
        } else {
            holder.tvItemDetails.setVisibility(View.GONE);
        }


        holder.mView.setOnClickListener(v -> {

            ctx.getSharedPreferences("ProjectProductId", MODE_PRIVATE).edit().putString("projectProductId", holder.mItem.getProjectProductId()).apply();

            SELECTED_PRODUCT = holder.mItem;

            globalSelectedProductId = holder.mItem.getProjectProductId();

            new AlertDialog.Builder(ctx)
                    .setNeutralButton(localized_attributes, (dialog, which) -> {
                        dialog.dismiss();

                        Intent intent = new Intent(ctx, AttributesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ctx.startActivity(intent);
                    })
                    .setPositiveButton(localized_measurements, (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(ctx, MeasurementsListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ctx.startActivity(intent);
                    })
                    .show();
        });

        holder.mView.setOnLongClickListener(v -> {
            if (MyCanEdit.canEdit(SELECTED_ASSIGNMENT.getAssignmentId())) {
                new AlertDialog.Builder(ctx)
                        .setMessage(localized_to_delete_product)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            dialog.dismiss();
                            SELECTED_PRODUCT = holder.mItem;
                            if(CheckMandatoryAttributes()){
                                DeleteProduct deleteProduct = new DeleteProduct(ctx, SELECTED_ASSIGNMENT.getAssignmentId());
                                deleteProduct.execute(holder.mItem.getProjectProductId());
                            }

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
        public final View mView;
        public final TextView tvItemName;
        public final TextView tvItemDetails;
        public ProductModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvItemName);
            tvItemDetails = view.findViewById(R.id.tvItemDetails);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                final String filterPattern = constraint.toString().toLowerCase().trim();
                List<ProductModel> filteredList = new ArrayList<>();

                if (filterPattern.isEmpty() && selectedType.isEmpty()) {  // 0 0
                    filteredItems = allItems;
                } else if (!filterPattern.isEmpty() && selectedType.isEmpty()) {  // 1 0
                    for (final ProductModel mWords : allItems) {
                        if (mWords.getProductDescription().toLowerCase().contains(filterPattern) || mWords.getIdentityValue().toLowerCase().contains(filterPattern)) {
                            filteredList.add(mWords);
                        }
                    }

                    filteredItems = filteredList;

                } else if (filterPattern.isEmpty()) {  // 0 1
                    for (final ProductModel mWords : allItems) {
                        if (mWords.getTypeDescription().toLowerCase().equals(selectedType)) {
                            filteredList.add(mWords);
                        }
                    }

                    filteredItems = filteredList;

                } else {
                    for (final ProductModel mWords : allItems) {
                        if ( ( mWords.getProductDescription().toLowerCase().contains(filterPattern) || mWords.getIdentityValue().toLowerCase().contains(filterPattern) )
                                && mWords.getTypeDescription().toLowerCase().equals(selectedType)) {  // 1 1
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


    public boolean CheckMandatoryAttributes() {

        String ProjectProductId = SELECTED_PRODUCT.getProjectProductId();
        String MeasurementsString = "";

        for(int i = 0; i < MANDATORY_MEASUREMENTS_LIST.size(); i++){

            String iPId = MANDATORY_MEASUREMENTS_LIST.get(i).getProjectProductId();
            if(iPId.equals(ProjectProductId) && !MANDATORY_MEASUREMENTS_LIST.get(i).getMeasurementCompleted().equalsIgnoreCase("Completed")){

                if(MeasurementsString.length() == 0){
                    MeasurementsString = MANDATORY_MEASUREMENTS_LIST.get(i).getAttributeName();
                } else {
                    MeasurementsString += ", " + MANDATORY_MEASUREMENTS_LIST.get(i).getAttributeName();
                }

            }
        }

        if(MeasurementsString.length() > 0){
            Toast toast = Toast.makeText(ctx, localized_Mandatory_Measurements_Missing + ": " + MeasurementsString , Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            return false;
        }


        return true;
    }



    // TODO delete
//    public class CustomFilter extends Filter {
//        private final ProductsRvAdapter mAdapter;
//
//        private CustomFilter(ProductsRvAdapter mAdapter) {
//            super();
//            this.mAdapter = mAdapter;
//        }
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//
//            PRODUCTS_LIST_FILTERED.clear();
//
//            final FilterResults results = new FilterResults();
//
//            final String filterPattern = constraint.toString().toLowerCase().trim();
//
//            if (constraint.length() == 0 && selectedType.isEmpty()) {
//                PRODUCTS_LIST_FILTERED.addAll(PRODUCTS_LIST);
//            } else if (constraint.length() != 0 && selectedType.isEmpty()) {
//                for (final ProductModel mWords : PRODUCTS_LIST) {
//                    if (mWords.toString().toLowerCase().contains(filterPattern)) {
//                        PRODUCTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            } else if (constraint.length() == 0 && !selectedType.isEmpty()) {
//                for (final ProductModel mWords : PRODUCTS_LIST) {
//                    if (mWords.getTypeDescription().toLowerCase().equals(selectedType)) {
//                        PRODUCTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            } else {
//                for (final ProductModel mWords : PRODUCTS_LIST) {
//                    if (mWords.toString().toLowerCase().contains(filterPattern) &&
//                            mWords.getTypeDescription().toLowerCase().equals(selectedType)) {
//                        PRODUCTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            }
//
//            results.values = PRODUCTS_LIST_FILTERED;
//            results.count = PRODUCTS_LIST_FILTERED.size();
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            this.mAdapter.notifyDataSetChanged();
//        }
//    }
}
