package dc.gtest.vortex.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.HAssignmentModel;

import static dc.gtest.vortex.support.MyLocalization.localized_attachments;
import static dc.gtest.vortex.support.MyLocalization.localized_consumables;
import static dc.gtest.vortex.support.MyLocalization.localized_customer_project;
import static dc.gtest.vortex.support.MyLocalization.localized_date;
import static dc.gtest.vortex.support.MyLocalization.localized_measurements;
import static dc.gtest.vortex.support.MyLocalization.localized_problem;
import static dc.gtest.vortex.support.MyLocalization.localized_product;
import static dc.gtest.vortex.support.MyLocalization.localized_resource;
import static dc.gtest.vortex.support.MyLocalization.localized_service;
import static dc.gtest.vortex.support.MyLocalization.localized_solution;

public class HistoryRvAdapter extends RecyclerView.Adapter<HistoryRvAdapter.ViewHolder> implements Filterable {

    private final List<HAssignmentModel> allItems;
    private List<HAssignmentModel> filteredItems;
    private final boolean subAssignments;
    private final Context ctx;

    public HistoryRvAdapter(List<HAssignmentModel> allItems, boolean subAssignments, Context Ctx) {
        this.allItems = allItems;
        filteredItems = allItems;
        this.subAssignments = subAssignments;
        this.ctx = Ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = filteredItems.get(position);

        String fullDate = localized_date + ": " + holder.mItem.getDateStart();
        holder.tvDate.setText(fullDate);

        String fullAssignmentId = "ID: " + " " + holder.mItem.getHAssignmentId();
        holder.tvAssignmentId.setText(fullAssignmentId);

        holder.tvResource.setText(holder.mItem.getResourceName());
        holder.tvProductMain.setText(holder.mItem.getProduct());
        holder.tvService.setText(holder.mItem.getService());
        holder.tvProblem.setText(holder.mItem.getProblem());
        holder.tvSolution.setText(holder.mItem.getSolution());

        // TODO
        HistoryProductsRvAdapter historyProductsRvAdapter = new HistoryProductsRvAdapter();
        historyProductsRvAdapter.setupRecyclerView(holder.rvHistoryMeasurements, holder.mItem.getHMeasurements());

        HistoryConsumablesRvAdapter historyConsumablesRvAdapter = new HistoryConsumablesRvAdapter();
        historyConsumablesRvAdapter.setupRecyclerView(holder.rvConsumables, holder.mItem.getHConsumables());

        HistoryAttachmentsRvAdapter historyAttachmentsRvAdapter = new HistoryAttachmentsRvAdapter();
        historyAttachmentsRvAdapter.setupRecyclerView(holder.rvHAttachments, holder.mItem.getHAttachments(), ctx);

        String resourceTitleText = localized_resource + ":";
        holder.tvResourceTitle.setText(resourceTitleText);

        boolean ShowProjectHistory = holder.mItem.getProjectHistory();

        String productTitleText;
        if (ShowProjectHistory || subAssignments) {
            productTitleText = localized_product + ":";
        }else{
            productTitleText = localized_customer_project + ":";
        }
        holder.tvProductMainTitle.setText(productTitleText);


        String serviceTitleText = localized_service + ":";
        holder.tvServiceTitle.setText(serviceTitleText);

        String problemTitleText = localized_problem + ":";
        holder.tvProblemTitle.setText(problemTitleText);

        String solutionTitleText = localized_solution + ":";
        holder.tvSolutionTitle.setText(solutionTitleText);

        holder.tvMeasurementsTitle.setText(localized_measurements);
        holder.tvConsumablesTitle.setText(localized_consumables);
        holder.tvHAttachments.setText(localized_attachments);
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvDate;
        public final TextView tvAssignmentId;
        public final TextView tvResourceTitle;
        public final TextView tvResource;
        public final TextView tvProductMainTitle;
        public final TextView tvProductMain;
        public final TextView tvServiceTitle;
        public final TextView tvService;
        public final TextView tvProblemTitle;
        public final TextView tvProblem;
        public final TextView tvSolutionTitle;
        public final TextView tvSolution;
        public final TextView tvMeasurementsTitle;
        public final RecyclerView rvHistoryMeasurements;
        public final TextView tvConsumablesTitle;
        public final RecyclerView rvConsumables;
        public final TextView tvHAttachments;
        public final RecyclerView rvHAttachments;
        public HAssignmentModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvDate = view.findViewById(R.id.tvDate);
            tvAssignmentId = view.findViewById(R.id.tvAssignmentId);
            tvResourceTitle = view.findViewById(R.id.tvResourceTitle);
            tvResource = view.findViewById(R.id.tvResource);
            tvProductMainTitle = view.findViewById(R.id.tvProductMainTitle);
            tvProductMain = view.findViewById(R.id.tvProductMain);
            tvServiceTitle = view.findViewById(R.id.tvServiceTitle);
            tvService = view.findViewById(R.id.tvService);
            tvProblemTitle = view.findViewById(R.id.tvProblemTitle);
            tvProblem = view.findViewById(R.id.tvProblem);
            tvSolutionTitle = view.findViewById(R.id.tvSolutionTitle);
            tvSolution = view.findViewById(R.id.tvSolution);
            tvMeasurementsTitle = view.findViewById(R.id.tvMeasurementsTitle);
            rvHistoryMeasurements = view.findViewById(R.id.rvHistoryMeasurements);
            tvConsumablesTitle = view.findViewById(R.id.tvConsumablesTitle);
            rvConsumables = view.findViewById(R.id.rvConsumables);
            tvHAttachments = view.findViewById(R.id.tvHAttachments);
            rvHAttachments = view.findViewById(R.id.rvHAttachments);

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
                    List<HAssignmentModel> filteredList = new ArrayList<>();

                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (HAssignmentModel mWords : allItems) {
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
