package am.gtest.vortex.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import am.gtest.vortex.R;

import static am.gtest.vortex.support.MyGlobals.globalCurrentPhotoPath;

public class TakeUploadPhoto {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Activity activity;
    private final Context ctx;

    public TakeUploadPhoto(Context ctx) {
        activity = (Activity) ctx;
        this.ctx = ctx;
    }

    public void requestPermission(int requestCode, final int requestCodePermission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    ctx, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new androidx.appcompat.app.AlertDialog.Builder(ctx)
                            .setMessage(ctx.getString(R.string.provide_permission_to_upload_image))
                            .setPositiveButton(R.string.ok, (dialog, which) -> {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(activity,
                                        new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE },
                                        requestCodePermission);
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE },
                            requestCodePermission);
                }
            } else {
                selectImage(requestCode);
            }
        } else {
            selectImage(requestCode);
        }
    }

    public void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public void dispatchTakePictureIntent(int requestCode, String subFolder) {
        globalCurrentPhotoPath = null;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(ctx.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile(subFolder);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI;

                if (Build.VERSION.SDK_INT >= 24) {
                    photoURI = FileProvider.getUriForFile(ctx, ctx.getPackageName(), photoFile);
                } else {
                    photoURI = Uri.fromFile(photoFile);
                }


                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    activity.startActivityForResult(takePictureIntent, requestCode);




            }
        }
    }

    private File createImageFile(String subFolder) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = timeStamp + ".jpg";
//        File storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(ctx.getExternalFilesDir(null) + File.separator + subFolder);

        MyUtils.checkMakeDir(storageDir);

        File photoFile = new File(storageDir, imageFileName);

//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );

        // Save a file: path for use with ACTION_VIEW intents
        globalCurrentPhotoPath = photoFile.getAbsolutePath();
        return photoFile;
    }

    public static void galleryAddPic(Activity activity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(globalCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }


}
