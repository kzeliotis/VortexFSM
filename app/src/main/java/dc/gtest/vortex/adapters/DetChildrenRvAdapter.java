package dc.gtest.vortex.adapters;

import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_no;
import static dc.gtest.vortex.support.MyLocalization.localized_reset_start_stop_question;
import static dc.gtest.vortex.support.MyLocalization.localized_yes;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_SHOW_DET_CHILDREN_START_STOP;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.GetReportPreview;
import dc.gtest.vortex.models.AttachmentModel;
import dc.gtest.vortex.models.DetChildrenModel;
import dc.gtest.vortex.support.MyDateTime;
import dc.gtest.vortex.support.MyPrefs;

public class DetChildrenRvAdapter extends RecyclerView.Adapter<DetChildrenRvAdapter.ViewHolder> implements Filterable {

    private final Context ctx;
    private final List<DetChildrenModel> mAllValues;
    private List<DetChildrenModel> mFileteredValues;
    private List<DetChildrenModel> mVisibleValues;

    public DetChildrenRvAdapter(List<DetChildrenModel> items, Context ctx) {
        this.ctx = ctx;
        mAllValues = items;
        mFileteredValues = items;
        mVisibleValues = mAllValues.stream().filter(dc -> SELECTED_ASSIGNMENT.getResourceId().equals(dc.getResourceId())).collect(Collectors.toList());
    }

    @NonNull
    @Override

    public DetChildrenRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detchild, parent, false);
        return new DetChildrenRvAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final DetChildrenRvAdapter.ViewHolder holder, int position) {

        holder.mItem = mFileteredValues.get(position);
        holder.tvDescription.setText(holder.mItem.getDescription());

        if(MyPrefs.getBoolean(PREF_SHOW_DET_CHILDREN_START_STOP, false)){
            holder.llDetChildStartStop.setVisibility(View.VISIBLE);
            holder.swDetChildCompleted.setVisibility(View.GONE);
        }else{
            holder.llDetChildStartStop.setVisibility(View.GONE);
            holder.swDetChildCompleted.setVisibility(View.VISIBLE);
        }

        if(!holder.mItem.getDetChildStart().isEmpty()){
            String startTime = "START: " + holder.mItem.getDetChildStart().split(" ")[1];
            holder.btnDetChildStart.setText(startTime);
        }

        if(!holder.mItem.getDetChildStop().isEmpty()){
            String stopTime = "STOP: " + holder.mItem.getDetChildStop().split(" ")[1];
            holder.btnDetChildStop.setText(stopTime);
        }


        holder.btnDetChildStart.setOnClickListener(v -> {
            if(holder.mItem.getDetChildStart().isEmpty()){
                holder.mItem.setDetChildStart(MyDateTime.getCurrentTimeWithSeconds());
                String start = "START: " + new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                holder.btnDetChildStart.setText(start);
            }
        });

        holder.btnDetChildStop.setOnClickListener(v -> {
            if(holder.mItem.getDetChildStop().isEmpty() && !holder.mItem.getDetChildStart().isEmpty()){
                holder.mItem.setDetChildStop(MyDateTime.getCurrentTimeWithSeconds());
                String stop = "STOP: " + new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                holder.btnDetChildStop.setText(stop);
                holder.mItem.setDetChildCompleted("1");
            }
        });

        holder.btnDetChildStart.setOnLongClickListener(v -> {
            if(!holder.mItem.getDetChildStart().isEmpty() && holder.mItem.getDetChildStop().isEmpty()){
                new AlertDialog.Builder(v.getContext())
                        .setMessage(localized_reset_start_stop_question)
                        .setPositiveButton(localized_yes, (dialog, which) -> {
                            // User clicked Yes, perform the reset
                            holder.mItem.setDetChildStart("");
                            holder.btnDetChildStart.setText("START");
                        })
                        .setNegativeButton(localized_no, (dialog, which) -> {
                            // User clicked No, do nothing
                            dialog.dismiss();
                        })
                        .show();
            }
            return false;
        });

        holder.btnDetChildStop.setOnLongClickListener(v -> {
            if(!holder.mItem.getDetChildStop().isEmpty()){
                new AlertDialog.Builder(v.getContext())
                        .setMessage(localized_reset_start_stop_question)
                        .setPositiveButton(localized_yes, (dialog, which) -> {
                            // User clicked Yes, perform the reset
                            holder.mItem.setDetChildStop("");
                            holder.btnDetChildStop.setText("STOP");
                            holder.mItem.setDetChildCompleted("0");
                        })
                        .setNegativeButton(localized_no, (dialog, which) -> {
                            // User clicked No, do nothing
                            dialog.dismiss();
                        })
                        .show();
            }
            return false;
        });

        holder.swDetChildCompleted.setOnCheckedChangeListener(null);
        holder.swDetChildCompleted.setChecked(holder.mItem.getdetChildCompleted().equals("1"));
        holder.swDetChildCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.mItem.setDetChildCompleted("1");
            } else {
                holder.mItem.setDetChildCompleted("0");
            }
        });

        String assignmentId = SELECTED_ASSIGNMENT.getAssignmentId();

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {
            holder.swDetChildCompleted.setEnabled(true);
            holder.btnDetChildStop.setEnabled(true);
            holder.btnDetChildStart.setEnabled(true);
        } else {
            holder.swDetChildCompleted.setEnabled(false);
            holder.btnDetChildStop.setEnabled(false);
            holder.btnDetChildStart.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return mFileteredValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvDescription;
        public DetChildrenModel mItem;
        public SwitchCompat swDetChildCompleted;
        public LinearLayout llDetChildStartStop;
        public Button btnDetChildStart;
        public Button btnDetChildStop;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvDescription = view.findViewById(R.id.tvDescription);
            swDetChildCompleted = view.findViewById(R.id.swDetChildCompleted);
            llDetChildStartStop = view.findViewById(R.id.llDetChildStartStop);
            btnDetChildStart = view.findViewById(R.id.btnDetChildStart);
            btnDetChildStop = view.findViewById(R.id.btnDetChildStop);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String _filterPattern = constraint.toString().toLowerCase().trim();
                List<DetChildrenModel> filteredList = new ArrayList<>();

                if(!_filterPattern.contains("@show_all")){
                    mVisibleValues = mAllValues.stream().filter(dc -> SELECTED_ASSIGNMENT.getResourceId().equals(dc.getResourceId())).collect(Collectors.toList());
                }else{
                    _filterPattern = _filterPattern.replace("@show_all", "");
                    mVisibleValues = mAllValues;
                }

                final String filterPattern = _filterPattern;

                if (filterPattern.isEmpty()) {  // 0 0
                    mFileteredValues = mVisibleValues;
                } else {  // 1 0
                    for (final DetChildrenModel mWords : mVisibleValues) {
                        if (mWords.getDescription().toLowerCase().contains(filterPattern)) {
                            filteredList.add(mWords);
                        }
                    }

                    mFileteredValues = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFileteredValues;
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
