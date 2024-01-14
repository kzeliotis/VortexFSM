package dc.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.GetReportPreview;
import dc.gtest.vortex.models.AttachmentModel;


public class AttachmentsRvAdapter extends RecyclerView.Adapter<AttachmentsRvAdapter.ViewHolder> {

    private final Context ctx;
    private final List<AttachmentModel> mValues;

    public AttachmentsRvAdapter(List<AttachmentModel> items, Context ctx) {
        this.ctx = ctx;
        mValues = items;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attachment, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.tvItemName.setText(holder.mItem.getFilename());

        holder.mView.setOnClickListener(v -> {
            String CloudFileURL = holder.mItem.getCloudFileURL();
            String AttachmentId = holder.mItem.getAttachmentId();
            String ObjectType = holder.mItem.getObjectType();
            String assignmentId = holder.mItem.getObjectId();
            String blobAttachmentId = holder.mItem.getBlobAttachmentId();
            String filename = holder.mItem.getFilename();
            if(blobAttachmentId.length()==0){blobAttachmentId = "0";}
            if(CloudFileURL.length()>0) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(CloudFileURL));
                ctx.startActivity(i);
            } else {
                GetReportPreview getReportPreview = new GetReportPreview(ctx, assignmentId, blobAttachmentId, filename, AttachmentId, ObjectType);
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
        public final TextView tvItemName;
        public AttachmentModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemName = view.findViewById(R.id.tvAttachmentFile);

        }
    }
}

