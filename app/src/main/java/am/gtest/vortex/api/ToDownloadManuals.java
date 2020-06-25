package am.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.net.URLEncoder;

import am.gtest.vortex.R;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyLocalization.localized_no_pdf_app;

public class ToDownloadManuals extends AsyncTask<String, Void, String>{

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    @SuppressLint("StaticFieldLeak")
    private ProgressBar mProgressBar;

    private File file;
    private BroadcastReceiver onComplete;

    public ToDownloadManuals(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        mProgressBar = ((Activity) ctx).findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... param) {
        String manualUrl = param[0];

        File path = new File(ctx.getExternalFilesDir(null) + "/manuals");

        if (!path.exists()) {
            MyUtils.checkMakeDir(path);
        }

        int lastIndex = manualUrl.lastIndexOf("/");
        String manualsLocation = manualUrl.substring(0, lastIndex + 1);

        String manualFileNameForDownload = manualUrl.substring(lastIndex + 1);
        String manualFileNameForSave = manualUrl.substring(lastIndex + 1);

        try {
            manualFileNameForDownload = URLEncoder.encode(manualFileNameForDownload, "UTF-8");
            manualUrl = manualsLocation + manualFileNameForDownload;
            manualUrl = manualUrl.replace("+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }

        manualFileNameForSave = manualFileNameForSave.replace(" ", "_");

        file = new File(path + "/" + manualFileNameForSave);

        boolean exists = file.exists();
        if(!exists) {
            downloadFileImages(manualUrl, manualFileNameForSave);
        }

//                File[] cachedFiles = path.listFiles();
//
//                for (File cachedFile : cachedFiles) {
//                    String cachedFilePath = cachedFile.toString();
//                    int lastIndex = cachedFilePath.lastIndexOf("/");
//                    String cachedFileName = cachedFilePath.substring(lastIndex + 1);
//                    int cachedFileNamePosition = imageFileNames.indexOf(cachedFileName);
//                    if (cachedFileNamePosition == -1) {
//                        cachedFile.delete();
//                    }
//                }

        return null;
    }

    private void downloadFileImages(String manualUrl, String manualFileNameForSave) {

        DownloadManager mgr = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);

        String path = "/manuals";

        onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intentOnReceive) {

                ctx.unregisterReceiver(onComplete);

                mProgressBar.setVisibility(View.GONE);

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
            }
        };

        ctx.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        try {
            Uri downloadUri = Uri.parse(manualUrl);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("Manuals")
                    .setDescription("Downloading manuals")
                    .setDestinationInExternalFilesDir(ctx, path, manualFileNameForSave);

            if (mgr != null) {
                mgr.enqueue(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
