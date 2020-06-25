/*
 * Copyright (c) 2016. Developed by GTest Development
 */

package am.gtest.vortex.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.HConsumableModel;

public class HistoryConsumablesRvAdapter {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    public void setupRecyclerView(@NonNull final RecyclerView recyclerView, List<HConsumableModel> hConsumables) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(hConsumables));
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<HConsumableModel> mValues;

        public SimpleItemRecyclerViewAdapter(List<HConsumableModel> items) {
            mValues = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_consumable, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.tvConsumableName.setText(mValues.get(position).getProduct());
            holder.tvConsumableQuantity.setText(mValues.get(position).getQuantity());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tvConsumableName;
            public final TextView tvConsumableQuantity;
            public HConsumableModel mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvConsumableName = view.findViewById(R.id.tvConsumableName);
                tvConsumableQuantity = view.findViewById(R.id.tvConsumableQuantity);
            }
        }
    }
}
