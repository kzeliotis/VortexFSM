package dc.gtest.vortex.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.ServiceModel;

import static dc.gtest.vortex.support.MyGlobals.NEW_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_FOR_NEW_ASSIGNMENT_LIST;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_LIST;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_LIST_FILTERED;

public class ServicesRvAdapter extends RecyclerView.Adapter<ServicesRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<ServiceModel> mValues;
    private final CustomFilter mFilter;
    private final boolean isForNewAssignment;

    public ServicesRvAdapter(List<ServiceModel> items, Context ctx, boolean isForNewAssignment) {
        this.ctx = ctx;
        this.isForNewAssignment = isForNewAssignment;
        mValues = items;
        mFilter = new CustomFilter(ServicesRvAdapter.this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_chevron, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvItemName.setText(holder.mItem.getServiceDescription());

        holder.mView.setOnClickListener(v -> {
            if (isForNewAssignment) {
                NEW_ASSIGNMENT.setServiceId(holder.mItem.getServiceId());
                NEW_ASSIGNMENT.setServiceDescription(holder.mItem.getServiceDescription());
                ((AppCompatActivity) ctx).finish(); // finish activity to go back to new assignment
            }
        });
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
        public final TextView tvItemName;
        public ServiceModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvItemName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvItemName.getText() + "'";
        }
    }

    public class CustomFilter extends Filter {
        private final ServicesRvAdapter mAdapter;

        private CustomFilter(ServicesRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            SERVICES_LIST_FILTERED.clear();
            SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED.clear();
            final FilterResults results = new FilterResults();

            if(isForNewAssignment){
                if (constraint.length() == 0) {
                    SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED.addAll(SERVICES_FOR_NEW_ASSIGNMENT_LIST);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final ServiceModel mWords : SERVICES_FOR_NEW_ASSIGNMENT_LIST) {
                        if (mWords.toString().toLowerCase().contains(filterPattern)) {
                            SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED.add(mWords);
                        }
                    }
                }

                results.values = SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED;
                results.count = SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED.size();
            }else{
                if (constraint.length() == 0) {
                    SERVICES_LIST_FILTERED.addAll(SERVICES_LIST);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final ServiceModel mWords : SERVICES_LIST) {
                        if (mWords.toString().toLowerCase().contains(filterPattern)) {
                            SERVICES_LIST_FILTERED.add(mWords);
                        }
                    }
                }

                results.values = SERVICES_LIST_FILTERED;
                results.count = SERVICES_LIST_FILTERED.size();
            }




            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
