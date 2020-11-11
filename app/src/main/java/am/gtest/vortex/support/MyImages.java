/*
 * Copyright (c) 2017. Developed by GTest Development
 */

package am.gtest.vortex.support;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
//import androidx.exifinterface.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import static am.gtest.vortex.support.MyPrefs.PREF_IMAGE_SIZE;

public class MyImages {

    public static Bitmap getNormalOrientedBitmap(String picturePath) {

        try {
            ExifInterface ei = new ExifInterface(picturePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(BitmapFactory.decodeFile(picturePath), 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(BitmapFactory.decodeFile(picturePath), 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(BitmapFactory.decodeFile(picturePath), 270);
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    return BitmapFactory.decodeFile(picturePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return BitmapFactory.decodeFile(picturePath);
        }
    }

    public static Bitmap getNormalOrientedBitmap(String picturePath, Bitmap bitmap) {
        try {
            ExifInterface ei = new ExifInterface(picturePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bitmap, 270);
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private static Bitmap rotateImage(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static String getImageBase64String(Bitmap bitmap) {

        String encodedImage = "";

        if (bitmap != null) {

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            Log.e("myLogs: MyImages:", "------------------------------ bitmapWidth: " + bitmapWidth);
            Log.e("myLogs: MyImages:", "------------------------------ bitmapHeight: " + bitmapHeight);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            Log.e("myLogs: MyImages:", "baos original size --------------------------------: " + baos.size());

            int compressQuality = 64;
            while (baos.size() > 200*1024 && compressQuality > 1 ) {
                try {
                    baos.flush();
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                baos = null;
                baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, baos);

                Log.e("myLogs: MyImages:", "baos compressed size --------" + compressQuality + "-------: " + baos.size());

                compressQuality = compressQuality / 2;
            }

            bitmap = null;

            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            try {
                baos.flush();
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            baos = null;
            imageBytes = null;
        }

        return encodedImage;
    }

    public static class SetImageFromBase64String extends AsyncTask<String, Void, Bitmap> {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

        private final WeakReference<ImageView> imageViewReference;

        public SetImageFromBase64String(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromBase64String(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]),0);
//        return decodeSampledBitmapFromResource(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                ImageView imageView = imageViewReference.get();

                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        }
    }

    public static class SetImageFromPath extends AsyncTask<String, Void, Bitmap> {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

        private final WeakReference<ImageView> imageViewReference;

        public SetImageFromPath(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            return getNormalOrientedBitmap(params[0], getBitmapFromPath(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]), 0));
//        return decodeSampledBitmapFromResource(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                ImageView imageView = imageViewReference.get();

                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        }
    }

    public static Bitmap getBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight,0);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap getBitmapFromPath(String path, int reqWidth, int reqHeight, int smallSide) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, smallSide);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getBitmapFromBase64String(String base64String, int reqWidth, int reqHeight, int smallSide) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        byte[] decodedString = Base64.decode(base64String, Base64.NO_WRAP);
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, smallSide);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, int smallSide) {
        // Raw height and width of image
        final float height = options.outHeight;
        final float width = options.outWidth;
        int inSampleSize = 1;

        if (smallSide != 0){
            if (width > height) {
                reqWidth = Math.round((width / height) * (float)smallSide);
                reqHeight = smallSide;
            } else {
                reqWidth = smallSide;
                reqHeight = Math.round((height / width) * (float)smallSide);
            }
        }

        if (height > reqHeight || width > reqWidth) {

            final float halfHeight = height / 2;
            final float halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String getImageBase64String(Context ctx, String imagePath, boolean fromGallery) {
        String encodedImage = "";

        if (!imagePath.isEmpty()) {

            int smallSideLength = MyPrefs.getInt(PREF_IMAGE_SIZE, 360);
            Bitmap bitmap;

            if(fromGallery){
                bitmap = getBitmapFromPath(imagePath, 0,0, smallSideLength);
            }else{
                bitmap = getNormalOrientedBitmap(imagePath);
            }

            if (bitmap != null) {

                float bitmapWidth = (float)bitmap.getWidth();
                float bitmapHeight =(float)bitmap.getHeight();

                if (smallSideLength != 0 && !fromGallery) {
                    if (bitmapWidth > bitmapHeight) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round((bitmapWidth / bitmapHeight) * (float)smallSideLength), smallSideLength, false);
                    } else {
                        bitmap = Bitmap.createScaledBitmap(bitmap, smallSideLength, Math.round((bitmapHeight / bitmapWidth) * (float)smallSideLength), false);
                    }
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                bitmap = null;

                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                try {
                    baos.flush();
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                baos = null;
                imageBytes = null;
            }
        }

        return encodedImage;
    }

    public static String createPhotosBase64Strings(Context ctx, List<String> photosPathList) {
        StringBuilder encodedImages = new StringBuilder();

        int smallSideLength = MyPrefs.getInt(PREF_IMAGE_SIZE, 360);

        for (int i = 0; i < photosPathList.size(); i++) {

            String photoPath = photosPathList.get(i);

            Bitmap bitmap = getNormalOrientedBitmap(photoPath);

            if (bitmap != null) {

                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();

                if (smallSideLength != 0) {
                    if (bitmapWidth > bitmapHeight) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, (bitmapWidth / bitmapHeight) * smallSideLength, smallSideLength, false);
                    } else {
                        bitmap = Bitmap.createScaledBitmap(bitmap, smallSideLength, (bitmapHeight / bitmapWidth) * smallSideLength, false);
                    }
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

//                Log.e(LOG_TAG, "baos original size --------------------------------: " + baos.size());
//
//                int compressQuality = 64;
//                while (baos.size() > 80*1024 && compressQuality > 1 ) {
//                    try {
//                        baos.flush();
//                        baos.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    baos = null;
//                    baos = new ByteArrayOutputStream();
//
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, baos);
//
//                    Log.e(LOG_TAG, "baos compressed size ------------" + compressQuality + "-----------------------------: " + baos.size());
//
//                    compressQuality = compressQuality / 2;
//                }

                bitmap = null;

                byte[] imageBytes = baos.toByteArray();
                encodedImages.append("\"").append(Base64.encodeToString(imageBytes, Base64.NO_WRAP)).append("\",");

                try {
                    baos.flush();
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                baos = null;
                imageBytes = null;
            }
        }

        return encodedImages.toString();
    }

//    public static void setImageNoCrop(Context ctx, String imageString, ImageView imageView, int width, int height, int placeholderResourceId) {
//        if (imageString.startsWith("http")) {
//            Glide.with(ctx).load(imageString).placeholder(placeholderResourceId).into(imageView);
//        } else if (imageString.isEmpty()) {
//            imageView.setImageResource(placeholderResourceId);
//        } else {
//            Bitmap bitmap = MyImages.getBitmapFromBase64String(imageString, width, height);
//
//            if (bitmap != null) {
//                imageView.setImageBitmap(bitmap);
//            }
//        }
//    }
//
//    public static void setImageWithCrop(Context ctx, String imageString, ImageView imageView, int width, int height, int placeholderResourceId) {
//        if (imageString.startsWith("http")) {
//            Glide.with(ctx).load(imageString).fitCenter().placeholder(placeholderResourceId).into(imageView);
//            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        } else if (imageString.isEmpty()) {
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageResource(placeholderResourceId);
//        } else {
//            Bitmap bitmap = MyImages.getBitmapFromBase64String(imageString, width, height);
//
//            if (bitmap != null) {
//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                imageView.setImageBitmap(bitmap);
//            }
//        }
//    }
//
//    public static void setAsyncImageNoCrop(Context ctx, String imageString, ImageView imageView, String width, String height, int placeholderResourceId) {
//        if (imageString.startsWith("http")) {
//            Glide.with(ctx).load(imageString).placeholder(placeholderResourceId).into(imageView);
//        } else if (imageString.isEmpty()) {
//            imageView.setImageResource(placeholderResourceId);
//        } else {
//            new MyImages.SetImageFromBase64String(imageView).execute(imageString, width, height);
//        }
//    }
//
//    public static void setAsyncImageWithCrop(Context ctx, String imageString, ImageView imageView, String width, String height, int placeholderResourceId) {
//        if (imageString.startsWith("http")) {
////            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            Glide.with(ctx).load(imageString).centerCrop().placeholder(placeholderResourceId).into(imageView);
//        } else if (imageString.isEmpty()) {
//            imageView.setScaleType(ImageView.ScaleType.CENTER);
//            imageView.setImageResource(placeholderResourceId);
//        } else {
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            new MyImages.SetImageFromBase64String(imageView).execute(imageString, width, height);
//        }
//    }
}