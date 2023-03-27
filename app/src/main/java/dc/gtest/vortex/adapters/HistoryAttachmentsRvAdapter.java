package dc.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AssignmentActionsActivity;
import dc.gtest.vortex.api.GetCustomers;
import dc.gtest.vortex.api.GetReportPreview;
import dc.gtest.vortex.models.AttachmentModel;
import dc.gtest.vortex.models.HConsumableModel;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.SELECTED_COMPANY;

public class HistoryAttachmentsRvAdapter {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();


    public void setupRecyclerView(@NonNull final RecyclerView recyclerView, List<AttachmentModel> hattachments, Context ctx) {

        recyclerView.setAdapter(new HistoryAttachmentsRvAdapter.SimpleItemRecyclerViewAdapter(hattachments, ctx));
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<HistoryAttachmentsRvAdapter.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<AttachmentModel> mValues;
        private final Context ctx;

        public SimpleItemRecyclerViewAdapter(List<AttachmentModel> items, Context Ctx) {
            mValues = items;
            ctx = Ctx;
        }

        @NonNull
        @Override
        public HistoryAttachmentsRvAdapter.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_attachment, parent, false);
            return new HistoryAttachmentsRvAdapter.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HistoryAttachmentsRvAdapter.SimpleItemRecyclerViewAdapter.ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            String filename = mValues.get(position).getFilename();
            SpannableString content = new SpannableString(filename);
            content.setSpan(new UnderlineSpan(), 0, filename.length(), 0);
            holder.tvHAttachment.setText(content);


            holder.mView.setOnClickListener(v -> {

                String CloudFileURL = holder.mItem.getCloudFileURL();
                String AttachmentId = holder.mItem.getAttachmentId();
                String ObjectType = holder.mItem.getObjectType();
                String assignmentId = holder.mItem.getObjectId();
                String blobAttachmentId = holder.mItem.getBlobAttachmentId();
                if(CloudFileURL.length()>0) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(CloudFileURL));
                    ctx.startActivity(i);
                } else {
                    GetReportPreview getReportPreview = new GetReportPreview(ctx, assignmentId, blobAttachmentId, "", AttachmentId, ObjectType);
                    getReportPreview.execute();
                }


            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tvHAttachment;
            public AttachmentModel mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvHAttachment = view.findViewById(R.id.tvHAttachment);
            }
        }
    }

}
