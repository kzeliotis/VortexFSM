package dc.gtest.vortex.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.GetReportPreview;
import dc.gtest.vortex.api.ToDownloadManuals;
import dc.gtest.vortex.models.ManualModel;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.CONST_EN;
import static dc.gtest.vortex.support.MyLocalization.localized_no_pdf_app;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;

public class ManualsRvAdapter extends RecyclerView.Adapter<ManualsRvAdapter.ViewHolder> implements Filterable {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;

    private final List<ManualModel> allItems;
    private List<ManualModel> filteredItems;

    public ManualsRvAdapter(List<ManualModel> allItems, Context ctx) {
        this.allItems = allItems;
        filteredItems = allItems;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manual, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = filteredItems.get(position);

        holder.tvManualName.setText(holder.mItem.getManualName());

        holder.mView.setOnClickListener(v -> {

            int lastIndexUrl = holder.mItem.getManualUrl().lastIndexOf("/");
            String manualFileNameFromUrl = holder.mItem.getfileName();   //holder.mItem.getManualUrl().substring(lastIndexUrl + 1);
            manualFileNameFromUrl = manualFileNameFromUrl.replace(" ", "_");

            File file = new File(ctx.getExternalFilesDir("/manuals") + "/" + manualFileNameFromUrl);

            String blobAttachmentId = holder.mItem.getblobAttachmentId();
            String file_name = holder.mItem.getManualName();
            String procedureId = holder.mItem.getManualId();

            if (file.exists()) {
                try {
                    Uri fileURI;

                    if (Build.VERSION.SDK_INT >= 24) {
                        fileURI = FileProvider.getUriForFile(ctx, ctx.getPackageName(), file);
                    } else {
                        fileURI = Uri.fromFile(file);
                    }

                    Log.e(LOG_TAG, "---------------------- fileURI: " + fileURI);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileURI, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    ctx.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(ctx, localized_no_pdf_app, Toast.LENGTH_LONG).show();
                }
            } else if (procedureId.length() > 0 && !procedureId.equals("0")){
                if(blobAttachmentId.length() == 0){blobAttachmentId = "0";}
                GetReportPreview getReportPreview = new GetReportPreview(ctx, "0", blobAttachmentId, file_name, procedureId, "Procedures");
                getReportPreview.execute();
            } else  {
                if (MyUtils.isNetworkAvailable() ) {
                    ToDownloadManuals toDownloadManuals = new ToDownloadManuals(ctx);
                    toDownloadManuals.execute(holder.mItem.getManualUrl());
                } else {
                    String dialogMessage;
                    String language = MyPrefs.getString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);

                    if (language.equals("gr")) {
                        dialogMessage = ctx.getString(R.string.no_internet_try_later_2_lines_gr);
                    } else {
                        dialogMessage = ctx.getString(R.string.no_internet_try_later_2_lines);
                    }

                    MyDialogs.showOK(ctx, dialogMessage);
                }
            }

//                    if (path != null) {
//                        try {
//                            File[] files = path.listFiles();
//
//                            for (File aFile : files) {
//                                int lastIndexSavedFile = aFile.toString().lastIndexOf("/");
//                                String manualFileNameSavedFile = aFile.toString().substring(lastIndexSavedFile + 1);
//                                manualFileNameSavedFile = manualFileNameSavedFile.replace(" ", "_");
//
//                                if (manualFileNameSavedFile.equals(manualFileNameFromUrl)) {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setDataAndType(Uri.fromFile(aFile), "application/pdf");
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                    startActivity(intent);
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        ToDownloadManuals toDownloadManuals = new ToDownloadManuals(ManualsActivity.this);
//                        toDownloadManuals.execute(holder.mItem.manualUrl);
//                    }

        });

    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView tvManualName;
        ManualModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvManualName = view.findViewById(R.id.tvManualName);
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
                    List<ManualModel> filteredList = new ArrayList<>();

                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (ManualModel mWords : allItems) {
                        if (mWords.getManualName().toLowerCase().contains(filterPattern) ||
                                mWords.getManualKeywords().toLowerCase().contains(filterPattern)) {
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
