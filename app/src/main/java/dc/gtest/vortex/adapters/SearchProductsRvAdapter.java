package dc.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.SearchAttributesActivity;
import dc.gtest.vortex.api.GetServices;
import dc.gtest.vortex.models.ProductModel;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;
import static dc.gtest.vortex.support.MyLocalization.localized_installation;
import static dc.gtest.vortex.support.MyLocalization.localized_zone;

public class SearchProductsRvAdapter extends RecyclerView.Adapter<SearchProductsRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;

    private final List<ProductModel> allItems;
    private List<ProductModel> filteredItems;

    private final boolean isForNewAssignment;
    private final Spinner spZones;
    private final Spinner spInstallations;


    public SearchProductsRvAdapter(List<ProductModel> allItems, Context ctx, boolean isForNewAssignment, Spinner spZones, Spinner spInstallations) {
        this.allItems = allItems;
        filteredItems = allItems;
        this.ctx = ctx;
        this.isForNewAssignment = isForNewAssignment;
        this.spZones = spZones;
        this.spInstallations = spInstallations;
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

        String productDescription = holder.mItem.getProductDescription();
        String zoneDescription = holder.mItem.getProjectZoneDescription();
        String installationDescription = holder.mItem.getProjectInstallationDescription();
        if (!zoneDescription.isEmpty()) {
            productDescription = productDescription + "\n" + localized_zone + ": " + zoneDescription;
        }
        if (!installationDescription.isEmpty()) {
            productDescription += "\n" + localized_installation + ": " + installationDescription;
        }

        holder.tvItemName.setText(productDescription);

        holder.mView.setOnClickListener(v -> {
            if (isForNewAssignment) {
                NEW_ASSIGNMENT.setProductDescription(holder.mItem.getProductDescription());
                NEW_ASSIGNMENT.setProjectProductId(holder.mItem.getProjectProductId());  // ProjectProductId is ID of project product
                NEW_ASSIGNMENT.setProductId("");
                String customerid = "0";
                if(!NEW_ASSIGNMENT.getCustomerId().isEmpty()){
                    customerid = NEW_ASSIGNMENT.getCustomerId();
                }
                if (MyUtils.isNetworkAvailable()) {
                    GetServices getServices = new GetServices("0", holder.mItem.getProjectProductId(), "0",
                                                                            customerid, false, ctx, false);
                    getServices.execute();
                }
                ((AppCompatActivity) ctx).finish();                                      // finish activity to go back to new assignment
            } else {
                SELECTED_PRODUCT = holder.mItem;

                Intent intent = new Intent(ctx, SearchAttributesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvItemName;
        public ProductModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvItemName);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<ProductModel> filteredList = new ArrayList<>();
                String constraintStr = constraint.toString();
                boolean isZoneFilter = constraintStr.startsWith("SPINNER:");
                boolean isInstallationFilter = constraintStr.startsWith("SPINNER_INSTALLATION:");
                final String filterPattern = isZoneFilter || isInstallationFilter ? "" : constraintStr.toLowerCase().trim();

                List<ProductModel> spinnerFilteredItems = new ArrayList<>(allItems);

                if(spZones.getSelectedItemPosition() > 0){
                    String zone = spZones.getSelectedItem().toString().toLowerCase();
                    List<ProductModel> zoneFilteredList = new ArrayList<>();
                    for (final ProductModel mWords : spinnerFilteredItems) {
                        if (mWords.getProjectZoneDescription() != null &&
                                mWords.getProjectZoneDescription().toLowerCase().equals(zone)) {
                            zoneFilteredList.add(mWords);
                        }
                    }
                    spinnerFilteredItems = zoneFilteredList;
                }

                if(spInstallations.getSelectedItemPosition() > 0){
                    String installation = spInstallations.getSelectedItem().toString().toLowerCase();
                    List<ProductModel> installationFilteredList = new ArrayList<>();
                    for (final ProductModel mWords : spinnerFilteredItems) {
                        if (mWords.getProjectInstallationDescription() != null &&
                                mWords.getProjectInstallationDescription().toLowerCase().equals(installation)) {
                            installationFilteredList.add(mWords);
                        }
                    }
                    spinnerFilteredItems = installationFilteredList;
                }

                if (constraint.length() == 0) {
                    filteredItems = spinnerFilteredItems;
                } else {

                    for (final ProductModel mWords : spinnerFilteredItems) {
//                        if (isZoneFilter) {
//                            // Exact match for spinner
//                            if (mWords.getProjectZoneDescription() != null &&
//                                    mWords.getProjectZoneDescription().toLowerCase().equals(filterPattern)) {
//                                filteredList.add(mWords);
//                            }
//                        }
//                        else if(isInstallationFilter){
//                            if (mWords.getProjectInstallationDescription() != null &&
//                                    mWords.getProjectInstallationDescription().toLowerCase().equals(filterPattern)) {
//                                filteredList.add(mWords);
//                            }
//                        }
//                        else {
//                            // Contains match for search
                            if (mWords.toString().toLowerCase().contains(filterPattern)) {
                                filteredList.add(mWords);
                            }
//                        }
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
}