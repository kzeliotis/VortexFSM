package am.gtest.vortex.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.HMeasurementValueModel;

public class HistoryAttributesRvAdapter {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    public void setupRecyclerView(@NonNull final RecyclerView recyclerView, List<HMeasurementValueModel> hMeasurementValues) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(hMeasurementValues));
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<HMeasurementValueModel> mValues;

        public SimpleItemRecyclerViewAdapter(List<HMeasurementValueModel> items) {
            mValues = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_attribute, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            String attributeNameWithColon = mValues.get(position).getMeasurableAttribute() + ": ";
            holder.tvAttributeName.setText(attributeNameWithColon);
            holder.tvAttributeValue.setText(mValues.get(position).getMeasurementValue());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tvAttributeName;
            public final TextView tvAttributeValue;
            public HMeasurementValueModel mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvAttributeName = view.findViewById(R.id.tvAttributeName);
                tvAttributeValue = view.findViewById(R.id.tvAttributeValue);
            }
        }
    }
}
