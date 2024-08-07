package dc.gtest.vortex.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AssignmentActionsActivity;
import dc.gtest.vortex.activities.AssignmentsActivity;
import dc.gtest.vortex.api.SendCheckIn;
import dc.gtest.vortex.api.SendCheckOut;
import dc.gtest.vortex.models.CheckInCheckOutModel;
import dc.gtest.vortex.models.HAssignmentModel;
import dc.gtest.vortex.support.MyDateTime;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_SHOW_PROGRESS_AND_TOAST;
import static dc.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_attachments;
import static dc.gtest.vortex.support.MyLocalization.localized_consumables;
import static dc.gtest.vortex.support.MyLocalization.localized_customer_project;
import static dc.gtest.vortex.support.MyLocalization.localized_date;
import static dc.gtest.vortex.support.MyLocalization.localized_measurements;
import static dc.gtest.vortex.support.MyLocalization.localized_no_internet_data_saved;
import static dc.gtest.vortex.support.MyLocalization.localized_problem;
import static dc.gtest.vortex.support.MyLocalization.localized_product;
import static dc.gtest.vortex.support.MyLocalization.localized_resource;
import static dc.gtest.vortex.support.MyLocalization.localized_service;
import static dc.gtest.vortex.support.MyLocalization.localized_solution;
import static dc.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_TIME;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_IN_TIME_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_CHECK_OUT_TIME_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CHECK_IN_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CHECK_OUT_DATA_TO_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_TRAVEL_STARTED;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_TRAVEL_TIME;
import static dc.gtest.vortex.support.MyPrefs.PREF_START_WORK_TIME;
import static dc.gtest.vortex.support.MyPrefs.PREF_USERID;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class HistoryRvAdapter extends RecyclerView.Adapter<HistoryRvAdapter.ViewHolder> implements Filterable {

    private final List<HAssignmentModel> allItems;
    private List<HAssignmentModel> filteredItems;
    private final boolean subAssignments;
    private final boolean isMaster;
    private final Context ctx;

    public HistoryRvAdapter(List<HAssignmentModel> allItems, boolean subAssignments, boolean isMaster, Context Ctx) {
        this.allItems = allItems;
        filteredItems = allItems;
        this.subAssignments = subAssignments;
        this.isMaster = isMaster;
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

        String assignmentId = holder.mItem.getHAssignmentId();
        if(isMaster){
            holder.llCheckInOut.setVisibility(View.VISIBLE);

            if (!MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false)) {
                holder.btnCheckIn.setEnabled(true);
            }

            if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false)){
                String chkIn = MyPrefs.getStringWithFileName(PREF_CHECK_IN_TIME_FOR_SHOW, assignmentId, "");
                if (!chkIn.isEmpty()){
                    holder.btnCheckIn.setText(chkIn);
                }
            }

            if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)){
                String chkOut = MyPrefs.getStringWithFileName(PREF_CHECK_OUT_TIME_FOR_SHOW, assignmentId, "");
                if (!chkOut.isEmpty()){
                    holder.btnCheckOut.setText(chkOut);
                }
            }

            if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                    !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {
                holder.btnCheckOut.setEnabled(true);
            }

            holder.btnCheckIn.setOnClickListener(v -> {

                SendCheckIn(holder);

            });

            holder.btnCheckOut.setOnClickListener(v -> {

                SendCheckout(holder);

            });;

        }

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
        public final LinearLayout llCheckInOut;
        public final Button btnCheckIn;
        public final Button btnCheckOut;
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
            llCheckInOut = view.findViewById(R.id.llCheckInOut);
            btnCheckIn = view.findViewById(R.id.btnCheckIn);
            btnCheckOut = view.findViewById(R.id.btnCheckOut);

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

    private void SendCheckIn(HistoryRvAdapter.ViewHolder holder){

        String assignmentId = holder.mItem.getHAssignmentId();
        String MasterDet = SELECTED_ASSIGNMENT.getAssignmentId();
        //START TRAVEL
        MyPrefs.setBooleanWithFileName(PREF_FILE_IS_TRAVEL_STARTED, assignmentId, true);
        MyPrefs.setStringWithFileName(assignmentId, PREF_START_TRAVEL_TIME, MyPrefs.getStringWithFileName(MasterDet, PREF_START_TRAVEL_TIME, ""));
        MyPrefs.setStringWithFileName(assignmentId, PREF_START_LAT, MyPrefs.getStringWithFileName(MasterDet, PREF_START_LAT, ""));
        MyPrefs.setStringWithFileName(assignmentId, PREF_START_LNG, MyPrefs.getStringWithFileName(MasterDet, PREF_START_LNG, ""));

        MyPrefs.setBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, true);

        holder.btnCheckIn.setEnabled(false);

        String chkIn = "CHECK IN" + System.getProperty("line.separator") + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(new Date());
        MyPrefs.setStringWithFileName(PREF_CHECK_IN_TIME_FOR_SHOW, assignmentId, chkIn);
        holder.btnCheckIn.setText(chkIn);

        MyPrefs.setStringWithFileName(assignmentId, PREF_CHECK_IN_TIME, MyDateTime.getCurrentTime());
        MyPrefs.setStringWithFileName(assignmentId, PREF_CHECK_IN_LAT, MyPrefs.getString(PREF_CURRENT_LAT, ""));
        MyPrefs.setStringWithFileName(assignmentId, PREF_CHECK_IN_LNG, MyPrefs.getString(PREF_CURRENT_LNG, ""));

        CheckInCheckOutModel checkInCheckOutModel = new CheckInCheckOutModel();

        checkInCheckOutModel.setAssignmentId(assignmentId);
        checkInCheckOutModel.setStartTravelTime(MyPrefs.getStringWithFileName(assignmentId, PREF_START_TRAVEL_TIME, ""));
        checkInCheckOutModel.setCheckInTime(MyDateTime.getCurrentTime());
        checkInCheckOutModel.setStartLat(MyPrefs.getStringWithFileName(assignmentId, PREF_START_LAT, ""));
        checkInCheckOutModel.setStartLng(MyPrefs.getStringWithFileName(assignmentId, PREF_START_LNG, ""));
        checkInCheckOutModel.setCheckInLat(MyPrefs.getString(PREF_CURRENT_LAT, ""));
        checkInCheckOutModel.setCheckInLng(MyPrefs.getString(PREF_CURRENT_LNG, ""));
        checkInCheckOutModel.setUserId(MyPrefs.getString(PREF_USERID, ""));

//                Log.e(LOG_TAG, "------- checkInCheckOutModel.toString(): \n" + checkInCheckOutModel.toString());

        MyPrefs.setStringWithFileName(PREF_FILE_CHECK_IN_DATA_TO_SYNC, assignmentId, checkInCheckOutModel.toString());

        holder.btnCheckOut.setEnabled(true);

        if (MyUtils.isNetworkAvailable()) {
            SendCheckIn sendCheckIn = new SendCheckIn(ctx, assignmentId, CONST_SHOW_PROGRESS_AND_TOAST);
            sendCheckIn.execute();
        } else {
            MyDialogs.showOK(ctx, localized_no_internet_data_saved);
        }
    }

    private void SendCheckout(HistoryRvAdapter.ViewHolder holder) {

        String assignmentId = holder.mItem.getHAssignmentId();

        CheckInCheckOutModel checkInCheckOutModel = new CheckInCheckOutModel();
        checkInCheckOutModel.setAssignmentId(assignmentId);
        checkInCheckOutModel.setStartWorkTime(MyPrefs.getStringWithFileName(assignmentId, PREF_START_WORK_TIME, ""));
        checkInCheckOutModel.setStartTravelTime(MyPrefs.getStringWithFileName(assignmentId, PREF_START_TRAVEL_TIME, ""));
        checkInCheckOutModel.setCheckInTime(MyPrefs.getStringWithFileName(assignmentId, PREF_CHECK_IN_TIME, ""));
        checkInCheckOutModel.setCheckOutTime(MyDateTime.getCurrentTime());
        checkInCheckOutModel.setStartLat(MyPrefs.getStringWithFileName(assignmentId, PREF_START_LAT, ""));
        checkInCheckOutModel.setStartLng(MyPrefs.getStringWithFileName(assignmentId, PREF_START_LNG, ""));
        checkInCheckOutModel.setCheckInLat(MyPrefs.getStringWithFileName(assignmentId, PREF_CHECK_IN_LAT, ""));
        checkInCheckOutModel.setCheckInLng(MyPrefs.getStringWithFileName(assignmentId, PREF_CHECK_IN_LNG, ""));
        checkInCheckOutModel.setCheckOutLat(MyPrefs.getString(PREF_CURRENT_LAT, ""));
        checkInCheckOutModel.setCheckOutLng(MyPrefs.getString(PREF_CURRENT_LNG, ""));
        checkInCheckOutModel.setSolution("");
        checkInCheckOutModel.setNotes("");
        checkInCheckOutModel.setChargedAmount("");
        checkInCheckOutModel.setPaidAmount("");
        checkInCheckOutModel.setStatus("-");
        checkInCheckOutModel.setStatusCode(holder.mItem.getProposedCheckOutStatus());
        checkInCheckOutModel.setSignatureName("");
        checkInCheckOutModel.setSignatureEmail("");
        checkInCheckOutModel.setEncodedSignature("");
        checkInCheckOutModel.setUserId(MyPrefs.getString(PREF_USERID, ""));

        holder.btnCheckOut.setEnabled(false);
        String chkOut = "CHECK OUT" + System.getProperty("line.separator") + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(new Date());
        MyPrefs.setStringWithFileName(PREF_CHECK_OUT_TIME_FOR_SHOW, assignmentId, chkOut);
        holder.btnCheckOut.setText(chkOut);

        MyPrefs.setStringWithFileName(PREF_FILE_CHECK_OUT_DATA_TO_SYNC, assignmentId, checkInCheckOutModel.toString());
        MyPrefs.setBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, true);

        if (MyUtils.isNetworkAvailable()) {

            SendCheckOut sendCheckOut = new SendCheckOut(ctx, assignmentId, CONST_SHOW_PROGRESS_AND_TOAST, false);

            if (!sendCheckOut.isCheckingOut()) {
                sendCheckOut.execute();
            }

        } else {
            new androidx.appcompat.app.AlertDialog.Builder(ctx)
                    .setMessage(localized_no_internet_data_saved)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        dialog.dismiss();

                    })
                    .show();
        }
    }


}
