package am.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.SearchAttributesActivity;
import am.gtest.vortex.api.GetServices;
import am.gtest.vortex.models.ProductModel;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.SELECTED_PRODUCT;

public class SearchProductsRvAdapter extends RecyclerView.Adapter<SearchProductsRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;

    private List<ProductModel> allItems;
    private List<ProductModel> filteredItems;

    private final boolean isForNewAssignment;

    public SearchProductsRvAdapter(List<ProductModel> allItems, Context ctx, boolean isForNewAssignment) {
        this.allItems = allItems;
        filteredItems = allItems;
        this.ctx = ctx;
        this.isForNewAssignment = isForNewAssignment;
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

        holder.mView.setOnClickListener(v -> {
            if (isForNewAssignment) {
                NEW_ASSIGNMENT.setProductDescription(holder.mItem.getProductDescription());
                NEW_ASSIGNMENT.setProjectProductId(holder.mItem.getProjectProductId());  // ProjectProductId is ID of project product
                NEW_ASSIGNMENT.setProductId("");
                if (MyUtils.isNetworkAvailable()) {
                    GetServices getServices = new GetServices("0", holder.mItem.getProjectProductId(), "0");
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

                if (constraint.length() == 0) {
                    filteredItems = allItems;
                } else {
                    List<ProductModel> filteredList = new ArrayList<>();
                    final String filterPattern = constraint.toString().toLowerCase().trim();

                    for (final ProductModel mWords : allItems) {
                        if (mWords.toString().toLowerCase().contains(filterPattern)) {
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
}