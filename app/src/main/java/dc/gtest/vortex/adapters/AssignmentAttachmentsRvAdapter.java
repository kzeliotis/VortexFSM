package dc.gtest.vortex.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ATTACHMENT_FOR_SYNC;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;

public class AssignmentAttachmentsRvAdapter extends RecyclerView.Adapter<AssignmentAttachmentsRvAdapter.ViewHolder> {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final List<String> mValues;
    private final Context ctx;
    private final String assignmentId;

    public AssignmentAttachmentsRvAdapter(List<String> items, Context ctx) {
        mValues = items;
        this.ctx = ctx;
        assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");
    }

    @Override
    public AssignmentAttachmentsRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_det_attachment, parent, false);
        return new AssignmentAttachmentsRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AssignmentAttachmentsRvAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        if (holder.mItem != null) {
            int lastIndex = holder.mItem.lastIndexOf("/");
            String attachmentFileName = holder.mItem.substring(lastIndex + 1);
            holder.ivAttachmentFile.setText(attachmentFileName);
        } else {
            holder.ivAttachmentFile.setText(null);
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {
            holder.ivRemove.setVisibility(View.GONE);
        } else {
            holder.ivRemove.setVisibility(View.VISIBLE);
        }

        holder.ivRemove.setOnClickListener(v -> {
            try {
                int lastIndex = holder.mItem.lastIndexOf("/");
                String attachmentFileName = holder.mItem.substring(lastIndex + 1);
                String prefKey = assignmentId + "_" + attachmentFileName;
                MyPrefs.removeStringWithFileName(PREF_FILE_ATTACHMENT_FOR_SYNC, prefKey);

                Log.e(LOG_TAG, "--------------- prefKey: " + prefKey);

                File file = new File(holder.mItem);

                if (MyUtils.deleteFile(file)) {
                    int position1 = holder.getAdapterPosition();
                    mValues.remove(position1);
                    notifyItemRemoved(position1);
                    notifyItemRangeChanged(position1, mValues.size());
                } else if (!Files.exists(Paths.get(holder.mItem))) {
                    int position1 = holder.getAdapterPosition();
                    mValues.remove(position1);
                    notifyItemRemoved(position1);
                    notifyItemRangeChanged(position1, mValues.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        holder.ivAttachmentFile.setOnClickListener(v -> {
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                File pickedFile = new File(holder.mItem);
                if (pickedFile.exists()){
                    Uri contentUri = FileProvider.getUriForFile(ctx, ctx.getPackageName(), pickedFile);
                    myIntent.setDataAndType(contentUri, "application/pdf");
                    myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent j = Intent.createChooser(myIntent, "Choose an application to open with:");
                    j.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    ctx.startActivity(myIntent);
                }
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView ivAttachmentFile;
        final TextView ivRemove;
        String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivAttachmentFile = view.findViewById(R.id.tvAttachmentFile);
            ivRemove = view.findViewById(R.id.tvRemoveAttachment);
        }

        @Override
        public String toString() {
            return super.toString() + mItem;
        }
    }
}
