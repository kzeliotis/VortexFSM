package am.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;
import am.gtest.vortex.R;
import am.gtest.vortex.activities.CustomFieldDetailsEditActivity;
import am.gtest.vortex.activities.CustomFieldsActivity;
import am.gtest.vortex.models.CustomFieldDetailModel;

import static am.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST;
import static am.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.KEY_VORTEX_TABLE;
import static am.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD;
import static am.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD_DETAIL;


public class CustomFieldDetailsRvAdapter extends RecyclerView.Adapter<CustomFieldDetailsRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<CustomFieldDetailModel> mValues;
    private final CustomFilter mFilter;
    private final String vortexTable;


    public CustomFieldDetailsRvAdapter(List<CustomFieldDetailModel> items, Context ctx, String vortexTable) {
        this.ctx = ctx;
        this.vortexTable = vortexTable;
        mValues = items;
        mFilter = new CustomFilter(CustomFieldDetailsRvAdapter.this);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_field_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvCustomFieldDetailsString.setText(holder.mItem.getCustomFieldDetailsString());
        holder.tvCustomFieldDetailsString.setMovementMethod(LinkMovementMethod.getInstance());


        if(SELECTED_CUSTOM_FIELD.getEditable()){
            holder.tvEditCustomFieldDetails.setBackgroundResource(R.drawable.ic_chevron_right_blue_24dp);
            holder.tvEditCustomFieldDetails.setOnClickListener(v -> {
                SELECTED_CUSTOM_FIELD_DETAIL = holder.mItem;
                Intent intent = new Intent(ctx, CustomFieldDetailsEditActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_VORTEX_TABLE, vortexTable);
                ctx.startActivity(intent);
            });
        } else {
            holder.tvEditCustomFieldDetails.setBackgroundResource(R.drawable.ic_chevron_right_gray_24dp);
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
        final TextView tvCustomFieldDetailsString;
        final TextView tvEditCustomFieldDetails;
        public CustomFieldDetailModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvCustomFieldDetailsString = view.findViewById(R.id.tvCustomFieldDetailsString);
            tvEditCustomFieldDetails = view.findViewById(R.id.tvEditCustomFieldDetails);
        }
    }

    public class CustomFilter extends Filter {
        private final CustomFieldDetailsRvAdapter mAdapter;

        private CustomFilter(CustomFieldDetailsRvAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();

            CUSTOM_FIELD_DETAILS_LIST_FILTERED.clear();

                if (constraint.length() == 0) {
                    CUSTOM_FIELD_DETAILS_LIST_FILTERED.addAll(CUSTOM_FIELD_DETAILS_LIST);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final CustomFieldDetailModel mWords : CUSTOM_FIELD_DETAILS_LIST) {
                        if (mWords.toString().toLowerCase().contains(filterPattern)) {
                            CUSTOM_FIELD_DETAILS_LIST_FILTERED.add(mWords);
                        }
                    }
                }

                results.values = CUSTOM_FIELD_DETAILS_LIST_FILTERED;
                results.count = CUSTOM_FIELD_DETAILS_LIST_FILTERED.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
