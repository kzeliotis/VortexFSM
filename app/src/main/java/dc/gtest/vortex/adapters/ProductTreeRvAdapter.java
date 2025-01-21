package dc.gtest.vortex.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amrdeveloper.treeview.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.ProductTreeActivity;
import dc.gtest.vortex.api.DeleteProduct;
import dc.gtest.vortex.activities.AttributesActivity;
import dc.gtest.vortex.items.MeasurementsListActivity;
import dc.gtest.vortex.models.ProductModel;
import dc.gtest.vortex.support.MyCanEdit;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.activities.ProductsActivity.selectedType;
import static dc.gtest.vortex.support.MyGlobals.MANDATORY_MEASUREMENTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_TREE_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_TREE_LIST_SAVED_STATE;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static dc.gtest.vortex.support.MyGlobals.globalSelectedProductId;
import static dc.gtest.vortex.support.MyLocalization.localized_Mandatory_Measurements_Missing;
import static dc.gtest.vortex.support.MyLocalization.localized_attributes;
import static dc.gtest.vortex.support.MyLocalization.localized_choose_from_warehouse;
import static dc.gtest.vortex.support.MyLocalization.localized_delete;
import static dc.gtest.vortex.support.MyLocalization.localized_measurements;
import static dc.gtest.vortex.support.MyLocalization.localized_remove_product_components;
import static dc.gtest.vortex.support.MyLocalization.localized_replace;
import static dc.gtest.vortex.support.MyLocalization.localized_replace_product_components;
import static dc.gtest.vortex.support.MyLocalization.localized_to_delete_product;
import static android.content.Context.MODE_PRIVATE;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW;

public class ProductTreeRvAdapter extends RecyclerView.Adapter<ProductTreeRvAdapter.ViewHolder> implements Filterable {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;

    private final List<TreeNode> allItems;
    private List<TreeNode> filteredItems;
    private List<TreeNode> visibleNodes;
    private final int projectInstallationId;
    private final int nodePadding = 55;

    public ProductTreeRvAdapter(List<TreeNode> allItems, Context ctx, int ProjectInstallationId) {
        this.allItems = allItems;
        filteredItems = allItems;
        visibleNodes = allItems.stream().filter(TreeNode::isSelected).collect(Collectors.toList());
        this.ctx = ctx;
        this.projectInstallationId = ProjectInstallationId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_chevron, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        TreeNode treenode = filteredItems.get(position);

        int padding = treenode.getLevel() * nodePadding;
        if (padding == 0) {padding = 10;}

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.leftMargin = padding;
        holder.itemView.setLayoutParams(layoutParams);

        //holder.llItemsRvNameChevron.setPadding(padding, holder.llItemsRvNameChevron.getPaddingTop(), holder.llItemsRvNameChevron.getPaddingRight(), holder.llItemsRvNameChevron.getPaddingBottom());
        //holder.itemView.setPadding(padding, holder.itemView.getPaddingTop(), holder.itemView.getPaddingRight(), holder.itemView.getPaddingBottom());

        if(treenode.getChildren().isEmpty()) {
            holder.tvExpandCollapse.setVisibility(View.INVISIBLE);
        } else {
            holder.tvExpandCollapse.setVisibility(View.VISIBLE);
            int stateIcon = treenode.isExpanded() ? R.drawable.ic_collapse : R.drawable.ic_expand;
            holder.tvExpandCollapse.setBackgroundResource(stateIcon);
        }

        holder.mItem = (ProductModel) treenode.getValue();
        holder.tvItemName.setText(holder.mItem.getProductDescription());

        if (holder.mItem.isNotSynchronized()) {
            holder.tvItemName.setTextColor(ContextCompat.getColor(ctx, R.color.red_700));
        } else {
            holder.tvItemName.setTextColor(ContextCompat.getColor(ctx, R.color.grey_900));
        }

        if (holder.mItem.getProjectProductId().equals(SELECTED_ASSIGNMENT.getprojectProductId())){
            holder.itemView.setBackgroundResource(R.drawable.rounded_layout_purple);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.rounded_layout_blue);
        }

        String attributesString = holder.mItem.getProductAttributesString();
        if (attributesString.length() > 0){
            holder.tvItemDetails.setVisibility(View.VISIBLE);
            holder.tvItemDetails.setText(attributesString);
        } else {
            holder.tvItemDetails.setVisibility(View.GONE);
        }

        if (projectInstallationId > 0) {
            holder.chkSelectToAdd.setVisibility(View.VISIBLE);
            holder.chkSelectToAdd.setOnCheckedChangeListener((buttonView, isChecked) -> {

                holder.mItem.setChecked(isChecked);

                List<String> ppIds = new ArrayList<>(Arrays.asList(MyPrefs.getStringWithFileName(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW, String.valueOf(projectInstallationId), "").split(",")));
                MyPrefs.setStringWithFileName(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW, String.valueOf(projectInstallationId), "");

                if (isChecked){
                    if(!ppIds.contains(holder.mItem.getProjectProductId())){
                        ppIds.add(holder.mItem.getProjectProductId());
                    }
                } else {
                    ppIds.remove(holder.mItem.getProjectProductId());
                }

                String ids = "";
                for(String id : ppIds){
                    if(ids.length() > 0){
                        ids += "," + id;
                    }else{
                        ids = id;
                    }
                }

                MyPrefs.setStringWithFileName(PREF_FILE_PRODUCTS_TO_INSTALLATION_FOR_SHOW, String.valueOf(projectInstallationId), ids);

            });
        } else {
            holder.chkSelectToAdd.setVisibility(View.GONE);
        }

        if (projectInstallationId == 0) {

            holder.tvExpandCollapse.setOnClickListener(v -> {

                ProductModel pm = holder.mItem;
                TreeNode selectedNode = allItems.stream()
                        .filter(obj -> obj.getValue() == pm)
                        .findFirst()
                        .orElse(null);
                if (selectedNode != null){

                    if(selectedNode.isExpanded()){
                        collapseNode(selectedNode);
                    }else{
                        expandNode(selectedNode);
                    }

                    PRODUCTS_TREE_LIST_SAVED_STATE.clear();
                    for(TreeNode node : PRODUCTS_TREE_LIST){
                        PRODUCTS_TREE_LIST_SAVED_STATE.add(MyUtils.cloneTreeNode(node));
                    }

                    SearchView searchview = ((ProductTreeActivity) ctx).getSearchView();
                    String searchText = searchview.getQuery().toString();
                    getFilter().filter(searchText);
                }

            });

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

                    String replaceProjectProductId = holder.mItem.getProjectProductId();
                    String productComponentId = holder.mItem.getProductComponentId();

                    new AlertDialog.Builder(ctx)
                            .setNeutralButton(localized_replace, (dialog, which) -> {
                                dialog.dismiss();
                                if(productComponentId != null && !productComponentId.equals("0")){
                                    new AlertDialog.Builder(ctx)
                                            .setMessage(localized_replace_product_components)
                                            .setPositiveButton(R.string.yes, (dialog1, which1) -> {replaceItem(replaceProjectProductId, productComponentId);})
                                            .setNegativeButton(R.string.no, (dialog1, which1) -> {replaceItem(replaceProjectProductId, "0");})
                                            .show();
                                } else {
                                    replaceItem(replaceProjectProductId, "0");
                                }


                            })
                            .setPositiveButton(localized_delete, (dialog, which) -> {
                                dialog.dismiss();
                                new AlertDialog.Builder(ctx)
                                        .setMessage(localized_to_delete_product)
                                        .setPositiveButton(R.string.yes, (dialog1, which1) -> {
                                            dialog1.dismiss();
                                            SELECTED_PRODUCT = holder.mItem;
                                            if(CheckMandatoryAttributes()){
                                                DeleteProduct deleteProduct = new DeleteProduct(ctx, SELECTED_ASSIGNMENT.getAssignmentId());
                                                new AlertDialog.Builder(ctx)
                                                        .setMessage(localized_remove_product_components)
                                                        .setPositiveButton(R.string.yes, (dialog3, which3) -> {
                                                            deleteProduct.execute(holder.mItem.getProjectProductId(), productComponentId);
                                                        })
                                                        .setNegativeButton(R.string.no, (dialog3, which3) -> {
                                                            deleteProduct.execute(holder.mItem.getProjectProductId(), "0");
                                                        })
                                                        .show();
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, null)
                                        .show();
                            })
                            .show();

                }

                return true;
            });
        }

    }

    public void replaceItem(String replaceProjectProductId, String productComponentId){
        new AlertDialog.Builder(ctx)
                .setMessage(localized_choose_from_warehouse)
                .setPositiveButton(R.string.yes, (dialog2, which2) -> ((ProductTreeActivity) ctx).startAllProductsActivity(true, replaceProjectProductId, productComponentId))
                .setNegativeButton(R.string.no, (dialog2, which2) -> ((ProductTreeActivity) ctx).startAllProductsActivity(false, replaceProjectProductId, productComponentId))
                .show();
    }


    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvItemName;
        public final TextView tvItemDetails;
        public final CheckBox chkSelectToAdd;
        public final TextView tvExpandCollapse;
        public ProductModel mItem;
        public final LinearLayout llItemsRvNameChevron;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvItemName);
            tvItemDetails = view.findViewById(R.id.tvItemDetails);
            chkSelectToAdd = view.findViewById(R.id.chkSelectToAdd);
            tvExpandCollapse = view.findViewById(R.id.tvExpand);
            llItemsRvNameChevron = view.findViewById(R.id.llItemsRvNameChevron);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                visibleNodes = allItems.stream().filter(TreeNode::isSelected).collect(Collectors.toList());

                final String filterPattern = constraint.toString().toLowerCase().trim();
                List<TreeNode> filteredList = new ArrayList<>();

                if (filterPattern.isEmpty() && selectedType.isEmpty()) {  // 0 0
                    filteredItems = visibleNodes;
                } else if (!filterPattern.isEmpty() && selectedType.isEmpty()) {  // 1 0
                    for (final TreeNode mWords : visibleNodes) {
                        final ProductModel pm = (ProductModel) mWords.getValue();
                        if (pm.getProductDescription().toLowerCase().contains(filterPattern) ||
                                pm.getIdentityValue().toLowerCase().contains(filterPattern) ||
                                pm.getProductAttributesString().toLowerCase().contains(filterPattern)) {
                            filteredList.add(mWords);
                        }
                    }

                    filteredItems = filteredList;

                } else if (filterPattern.isEmpty()) {  // 0 1
                    for (final TreeNode mWords : visibleNodes) {
                        final  ProductModel pm = (ProductModel) mWords.getValue();
                        if (pm.getTypeDescription().toLowerCase().equals(selectedType)) {
                            filteredList.add(mWords);
                        }
                    }

                    filteredItems = filteredList;

                } else {
                    for (final TreeNode mWords : visibleNodes) {
                        final  ProductModel pm = (ProductModel) mWords.getValue();
                        if ( ( pm.getProductDescription().toLowerCase().contains(filterPattern) ||
                                pm.getIdentityValue().toLowerCase().contains(filterPattern) ||
                                pm.getProductAttributesString().toLowerCase().contains(filterPattern))
                                && pm.getTypeDescription().toLowerCase().equals(selectedType)) {  // 1 1
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


    public int collapseNode(TreeNode node) {
        int position = PRODUCTS_TREE_LIST.indexOf(node);
        if (position != -1 && node.isExpanded()) {
            node.setExpanded(false);
            LinkedList<TreeNode> deletedParents = new LinkedList<>(node.getChildren());
            //rootsNodes.removeAll(node.getChildren());
            for (int i = position + 1; i < PRODUCTS_TREE_LIST.size(); i++) {
                TreeNode iNode = PRODUCTS_TREE_LIST.get(i);
                if (deletedParents.contains(iNode.getParent())) {
                    deletedParents.add(iNode);
                    deletedParents.addAll(iNode.getChildren());
                }
            }
            for (TreeNode tn : PRODUCTS_TREE_LIST){
                if(deletedParents.contains(tn)){
                    tn.setSelected(false);
                }
            }
        }
        return position;
    }

    public int expandNode(TreeNode node) {
        int position = PRODUCTS_TREE_LIST.indexOf(node);
        if (position != -1 && !node.isExpanded()) {
            node.setExpanded(true);
            //rootsNodes.addAll(position + 1, node.getChildren());
            for (TreeNode child : node.getChildren()) {
                child.setSelected(true);
                if (child.isExpanded()) updateExpandedNodeChildren(child);
            }
        }
        return position;
    }

    private void updateExpandedNodeChildren(TreeNode node) {
        int position = PRODUCTS_TREE_LIST.indexOf(node);
        if (position != -1 && node.isExpanded()) {

            //rootsNodes.addAll(position + 1, node.getChildren());
            for (TreeNode child : node.getChildren()) {
                child.setSelected(true);
                if (child.isExpanded()) updateExpandedNodeChildren(child);
            }
        }
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
