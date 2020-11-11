/*
 * Copyright (c) 2016. Developed by GTest Development
 */

package am.gtest.vortex.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.HMeasurementModel;

import static am.gtest.vortex.support.MyLocalization.localized_product;

public class HistoryProductsRvAdapter {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    public void setupRecyclerView(@NonNull final RecyclerView recyclerView, List<HMeasurementModel> hMeasurements) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(hMeasurements));
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<HMeasurementModel> mValues;

        public SimpleItemRecyclerViewAdapter(List<HMeasurementModel> items) {
            mValues = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_product, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);

            String productTitleText = localized_product + ": ";
            holder.tvProductTitle.setText(productTitleText);

            holder.tvProduct.setText(mValues.get(position).getProduct());

            HistoryAttributesRvAdapter historyAttributesRvAdapter = new HistoryAttributesRvAdapter();
            historyAttributesRvAdapter.setupRecyclerView(holder.rvAttributes, mValues.get(position).getHMeasurementValues());

            holder.mView.setOnClickListener(v -> {

            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tvProductTitle;
            public final TextView tvProduct;
            public final RecyclerView rvAttributes;

            public HMeasurementModel mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvProductTitle = view.findViewById(R.id.tvProductTitle);
                tvProduct = view.findViewById(R.id.tvProduct);
                rvAttributes = view.findViewById(R.id.rvAttributes);
            }
        }
    }
}
