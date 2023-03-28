package dc.gtest.vortex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.StatusModel;

import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;

public class StatusesRvAdapter extends RecyclerView.Adapter<StatusesRvAdapter.ViewHolder> {

    private final Context ctx;
    private final List<StatusModel> mValues;

    public StatusesRvAdapter(List<StatusModel> items, Context ctx) {
        this.ctx = ctx;
        mValues = items;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.tvStatusDescription.setText(holder.mItem.getStatusDescription());

        holder.mView.setOnClickListener(v -> {

            NEW_ASSIGNMENT.setStatusCode(holder.mItem.getStatusId());
            NEW_ASSIGNMENT.setStatusDescription(holder.mItem.getStatusDescription());

            ((AppCompatActivity) ctx).finish();
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvStatusDescription;
        public StatusModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvStatusDescription = view.findViewById(R.id.tvStatusDescription);

        }
    }

}