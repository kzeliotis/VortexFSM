package am.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.FullScreenImageActivity;
import am.gtest.vortex.support.MyImages;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyPrefs.PREF_ASSIGNMENT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IMAGE_FOR_SYNC;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;

public class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.ViewHolder> {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final List<String> mValues;
    private final Context ctx;
    private final String assignmentId;

    public PhotosRecyclerViewAdapter(List<String> items, Context ctx) {
        mValues = items;
        this.ctx = ctx;
        assignmentId = MyPrefs.getString(PREF_ASSIGNMENT_ID, "");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_photos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        if (holder.mItem != null) {
            new MyImages.SetImageFromPath(holder.ivProjectPhoto).execute(holder.mItem, "200", "200");
        } else {
            holder.ivProjectPhoto.setImageDrawable(null);
        }

        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {
            holder.ivRemove.setVisibility(View.GONE);
        } else {
            holder.ivRemove.setVisibility(View.VISIBLE);
        }

        holder.ivRemove.setOnClickListener(v -> {
            try {
                int lastIndex = holder.mItem.lastIndexOf("/");
                String photoFileName = holder.mItem.substring(lastIndex + 1);
                String prefKey = assignmentId + "_" + photoFileName;
                MyPrefs.removeStringWithFileName(PREF_FILE_IMAGE_FOR_SYNC, prefKey);

                Log.e(LOG_TAG, "--------------- prefKey: " + prefKey);

                File file = new File(holder.mItem);

                if (MyUtils.deleteFile(file)) {
                    int position1 = holder.getAdapterPosition();
                    mValues.remove(position1);
                    notifyItemRemoved(position1);
                    notifyItemRangeChanged(position1, mValues.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, FullScreenImageActivity.class);
            intent.putExtra("photoPath", holder.mItem);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView ivProjectPhoto;
        final ImageView ivRemove;
        String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivProjectPhoto = view.findViewById(R.id.ivProjectPhoto);
            ivRemove = view.findViewById(R.id.ivRemove);
        }

        @Override
        public String toString() {
            return super.toString() + mItem;
        }
    }
}
