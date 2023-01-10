package dc.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
//import androidx.cardview.widget.CardView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AssignmentDetailActivity;
import dc.gtest.vortex.models.AssignmentModel;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.activities.AssignmentsActivity.selectedDate;
import static dc.gtest.vortex.activities.AssignmentsActivity.selectedStatus;
import static dc.gtest.vortex.activities.AssignmentsActivity.sortedBy;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.CONST_SORTED_BY_DATE;
import static dc.gtest.vortex.support.MyGlobals.CONST_SORTED_BY_DISTANCE;
import static dc.gtest.vortex.support.MyGlobals.STATUSES_LIST;
import static dc.gtest.vortex.support.MyGlobals.codeScanned;
import static dc.gtest.vortex.support.MyGlobals.singleAssignmentResult;
import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_TRAVEL_STARTED;
import static dc.gtest.vortex.support.MyPrefs.PREF_PROCESS_ASSIGNMENT_ON_SCAN;
import static dc.gtest.vortex.support.MyPrefs.PREF_PROJECT_ID;

public class AssignmentsRvAdapter extends RecyclerView.Adapter<AssignmentsRvAdapter.ViewHolder> implements Filterable {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;

    private final List<AssignmentModel> allItems;
    private List<AssignmentModel> filteredItems;

    public AssignmentsRvAdapter(List<AssignmentModel> allItems, Context ctx) {
        this.allItems = allItems;
        filteredItems = allItems;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
     public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = filteredItems.get(position);

        holder.tvProjectDescription.setText(holder.mItem.getProjectDescription());
        holder.tvCustomerName.setText(holder.mItem.getCustomerName());
        holder.tvServiceDescription.setText(holder.mItem.getServiceDescription() + "\r\n" + holder.mItem.getProductDescription());
        holder.tvAssignmentTime.setText(holder.mItem.getAssignmentTime());
        holder.tvTimeTo.setText(holder.mItem.getTimeTo());
        holder.tvAddress.setText(holder.mItem.getAddress());
        holder.tvAssignmentsStatus.setText(holder.mItem.getStatusName());

        if (holder.mItem.getAssignmentId().contains("-")) {
            holder.cvAssignment.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.pink_50));
        } else {
            holder.cvAssignment.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.light_blue_50));
        }

        if (holder.mItem.getInstallationWarning().length() > 0){
            holder.tvWarning.setVisibility(View.VISIBLE);
            holder.tvWarning.setText(holder.mItem.getInstallationWarning());
        } else {
            holder.tvWarning.setVisibility(View.GONE);
        }

        if (holder.mItem.getStatusColor().length()> 0) {
            holder.tvAssignmentsStatus.setBackgroundColor(Integer.valueOf(holder.mItem.getStatusColor()));
        }

        try {

            if (holder.mItem.getDistance() != -1) {
                NumberFormat formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(0);
                formatter.setMaximumFractionDigits(1);
                String formattedDistance = formatter.format(holder.mItem.getDistance());

                String distanceText = formattedDistance + " km";
                holder.tvDistance.setText(distanceText);
            } else {
                holder.tvDistance.setText(R.string.n_a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.mView.setOnClickListener(v -> {


            for (int i = 0; i < STATUSES_LIST.size(); i++) {
                if (STATUSES_LIST.get(i).getStatusId().equals(holder.mItem.getStatusId())) {
                    if (STATUSES_LIST.get(i).getIsPending() != 1) {
                        MyPrefs.setBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, holder.mItem.getAssignmentId(), true);
                        MyPrefs.setBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, holder.mItem.getAssignmentId(), true);
                        MyPrefs.setBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, holder.mItem.getAssignmentId(), true);
                    }

                    if (STATUSES_LIST.get(i).getIsRollback() == 1) {
                        MyPrefs.setBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, holder.mItem.getAssignmentId(), false);
                    }
                    break;
                }
            }

            MyPrefs.setString(PREF_PROJECT_ID, holder.mItem.getProjectId());
            MyPrefs.setString(PREF_ASSIGNMENT_ID, holder.mItem.getAssignmentId());

            SELECTED_ASSIGNMENT = holder.mItem;

            Intent intent = new Intent(ctx, AssignmentDetailActivity.class);
            ctx.startActivity(intent);
        });

        if (singleAssignmentResult){
            holder.mView.performClick();
        }

        codeScanned = false;
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        final CardView cvAssignment;
        final TextView tvProjectDescription;
        final TextView tvCustomerName;
        final TextView tvServiceDescription;
        final TextView tvAssignmentTime;
        final TextView tvTimeTo;
        final TextView tvAddress;
        final TextView tvDistance;
        final TextView tvAssignmentsStatus;
        final TextView tvWarning;
        public AssignmentModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            cvAssignment = view.findViewById(R.id.cvAssignment);
            tvProjectDescription = view.findViewById(R.id.tvProjectDescription);
            tvCustomerName = view.findViewById(R.id.tvCustomerName);
            tvServiceDescription = view.findViewById(R.id.tvServiceDescription);
            tvAssignmentTime = view.findViewById(R.id.tvAssignmentTime);
            tvTimeTo = view.findViewById(R.id.tvTimeTo);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvDistance = view.findViewById(R.id.tvDistance);
            tvAssignmentsStatus = view.findViewById(R.id.tvAssignmentsStatus);
            tvWarning = view.findViewById(R.id.tvWarning);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

//            Log.e(LOG_TAG, "---------------------------- selectedDate: " + selectedDate);
//            Log.e(LOG_TAG, "---------------------------- searchedText: " + searchedText);
//            Log.e(LOG_TAG, "-------------------------- selectedStatus: " + selectedStatus);
//            Log.e(LOG_TAG, ". \n");

                final String filterPattern = constraint.toString().toLowerCase().trim();
                List<AssignmentModel> filteredList = new ArrayList<>();

                if (filterPattern.isEmpty() && selectedStatus.isEmpty() && selectedDate.isEmpty()) { // 0 0 0
                    filteredItems = allItems;
                } else if (constraint.length() == 0 && selectedStatus.isEmpty() && !selectedDate.isEmpty()) { // 0 0 1
                    for (final AssignmentModel mWords : allItems) {
                        if (mWords.getStartDate().equals(selectedDate)) {
                            filteredList.add(mWords);
                        }
                    }
                    filteredItems = filteredList;
                } else if (constraint.length() == 0 && !selectedStatus.isEmpty() && selectedDate.isEmpty()) { // 0 1 0
                    for (final AssignmentModel mWords : allItems) {
                        if (mWords.getStatusName().toLowerCase().equals(selectedStatus)) {
                            filteredList.add(mWords);
                        }
                    }
                    filteredItems = filteredList;
                } else if (constraint.length() == 0 && !selectedStatus.isEmpty() && !selectedDate.isEmpty()) { // 0 1 1
                    for (final AssignmentModel mWords : allItems) {
                        if (mWords.getStatusName().toLowerCase().equals(selectedStatus) && mWords.getStartDate().equals(selectedDate)) {
                            filteredList.add(mWords);
                        }
                    }
                    filteredItems = filteredList;
                } else if (constraint.length() != 0 && selectedStatus.isEmpty() && selectedDate.isEmpty()) { // 1 0 0
                    for (final AssignmentModel mWords : allItems) {
                        if (mWords.toString().toLowerCase().contains(filterPattern)) {
                            filteredList.add(mWords);
                        }
                    }
                    filteredItems = filteredList;
                } else if (constraint.length() != 0 && selectedStatus.isEmpty() && !selectedDate.isEmpty()) { // 1 0 1
                    for (final AssignmentModel mWords : allItems) {
                        if (mWords.toString().toLowerCase().contains(filterPattern) && mWords.getStartDate().equals(selectedDate)) {
                            filteredList.add(mWords);
                        }
                    }
                    filteredItems = filteredList;
                } else if (constraint.length() != 0 && !selectedStatus.isEmpty() && selectedDate.isEmpty()) { // 1 1 0
                    for (final AssignmentModel mWords : allItems) {
                        if (mWords.toString().toLowerCase().contains(filterPattern) && mWords.getStatusName().toLowerCase().equals(selectedStatus)) {
                            filteredList.add(mWords);
                        }
                    }
                    filteredItems = filteredList;
                } else { // 1 1 1
                    for (final AssignmentModel mWords : allItems) {
                        if (mWords.toString().toLowerCase().contains(filterPattern) &&
                                mWords.getStatusName().toLowerCase().equals(selectedStatus) && mWords.getStartDate().equals(selectedDate)) {
                            filteredList.add(mWords);
                        }
                    }
                    filteredItems = filteredList;
                }

                switch (sortedBy) {
                    case CONST_SORTED_BY_DATE:
                        Collections.sort(filteredItems, (a, b) -> a.getStartDateTime().compareTo(b.getStartDateTime()));
                        break;
                    case CONST_SORTED_BY_DISTANCE:
                        Collections.sort(filteredItems, (a, b) -> {
                            Double distance = (double) a.getDistance();
                            Double distance1 = (double) b.getDistance();
                            if (distance.compareTo(distance1) < 0) {
                                return -1;
                            } else if (distance.compareTo(distance1) > 0) {
                                return 1;
                            } else {
                                return 0;
                            }
                        });
                        break;
                    default:
                        Collections.sort(filteredItems, (a, b) -> a.getSorting().compareTo(b.getSorting()));
                        break;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItems;
                int result_count = filteredItems.size();
                if (result_count == 1 && MyPrefs.getBoolean(PREF_PROCESS_ASSIGNMENT_ON_SCAN, false) && codeScanned) {singleAssignmentResult = true;} //
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
//                filteredItems = (ArrayList<TopicModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // TODO delete
//    public class CustomFilter extends Filter {
//        private final AssignmentsRvAdapter mAdapter;
//
//        private CustomFilter(AssignmentsRvAdapter mAdapter) {
//            super();
//            this.mAdapter = mAdapter;
//        }
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//
//            ASSIGNMENTS_LIST_FILTERED.clear();
//
//            final FilterResults results = new FilterResults();
//
//            final String filterPattern = constraint.toString().toLowerCase().trim();
//
//            if (constraint.length() == 0 && selectedStatus.isEmpty() && selectedDate.isEmpty()) { // 0 0 0
//                ASSIGNMENTS_LIST_FILTERED.addAll(ASSIGNMENTS_LIST);
//            } else if (constraint.length() == 0 && selectedStatus.isEmpty() && !selectedDate.isEmpty()) { // 0 0 1
//                for (final AssignmentModel mWords : ASSIGNMENTS_LIST) {
//                    if (mWords.getStartDate().equals(selectedDate)) {
//                        ASSIGNMENTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            } else if (constraint.length() == 0 && !selectedStatus.isEmpty() && selectedDate.isEmpty()) { // 0 1 0
//                for (final AssignmentModel mWords : ASSIGNMENTS_LIST) {
//                    if (mWords.getStatusName().toLowerCase().equals(selectedStatus)) {
//                        ASSIGNMENTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            } else if (constraint.length() == 0 && !selectedStatus.isEmpty() && !selectedDate.isEmpty()) { // 0 1 1
//                for (final AssignmentModel mWords : ASSIGNMENTS_LIST) {
//                    if (mWords.getStatusName().toLowerCase().equals(selectedStatus) && mWords.getStartDate().equals(selectedDate)) {
//                        ASSIGNMENTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            } else if (constraint.length() != 0 && selectedStatus.isEmpty() && selectedDate.isEmpty()) { // 1 0 0
//                for (final AssignmentModel mWords : ASSIGNMENTS_LIST) {
//                    if (mWords.toString().toLowerCase().contains(filterPattern)) {
//                        ASSIGNMENTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            } else if (constraint.length() != 0 && selectedStatus.isEmpty() && !selectedDate.isEmpty()) { // 1 0 1
//                for (final AssignmentModel mWords : ASSIGNMENTS_LIST) {
//                    if (mWords.toString().toLowerCase().contains(filterPattern) && mWords.getStartDate().equals(selectedDate)) {
//                        ASSIGNMENTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            } else if (constraint.length() != 0 && !selectedStatus.isEmpty() && selectedDate.isEmpty()) { // 1 1 0
//                for (final AssignmentModel mWords : ASSIGNMENTS_LIST) {
//                    if (mWords.toString().toLowerCase().contains(filterPattern) && mWords.getStatusName().toLowerCase().equals(selectedStatus)) {
//                        ASSIGNMENTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            } else { // 1 1 1
//                for (final AssignmentModel mWords : ASSIGNMENTS_LIST) {
//                    if (mWords.toString().toLowerCase().contains(filterPattern) &&
//                            mWords.getStatusName().toLowerCase().equals(selectedStatus) && mWords.getStartDate().equals(selectedDate)) {
//                        ASSIGNMENTS_LIST_FILTERED.add(mWords);
//                    }
//                }
//            }
//
//            switch (sortedBy) {
//                case CONST_SORTED_BY_DATE:
//                    Collections.sort(ASSIGNMENTS_LIST_FILTERED, (a, b) -> a.getStartDateTime().compareTo(b.getStartDateTime()));
//                    break;
//                case CONST_SORTED_BY_DISTANCE:
//                    Collections.sort(ASSIGNMENTS_LIST_FILTERED, (a, b) -> {
//                        Double distance = (double) a.getDistance();
//                        Double distance1 = (double) b.getDistance();
//                        if (distance.compareTo(distance1) < 0) {
//                            return -1;
//                        } else if (distance.compareTo(distance1) > 0) {
//                            return 1;
//                        } else {
//                            return 0;
//                        }
//                    });
//                    break;
//                default:
//                    Collections.sort(ASSIGNMENTS_LIST_FILTERED, (a, b) -> a.getSorting().compareTo(b.getSorting()));
//                    break;
//            }
//
//            results.values = ASSIGNMENTS_LIST_FILTERED;
//            results.count = ASSIGNMENTS_LIST_FILTERED.size();
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            this.mAdapter.notifyDataSetChanged();
//        }
//    }
}
