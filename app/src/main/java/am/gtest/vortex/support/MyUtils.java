package am.gtest.vortex.support;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import am.gtest.vortex.R;
import am.gtest.vortex.application.MyApplication;

public class MyUtils {

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean checkGPSStatus(){
        LocationManager manager = (LocationManager) MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    public static void hideKeypad(Activity activity){
        if (activity.getWindow().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static void hideKeypad(Context ctx, View view){
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void hideKeypad(View view){
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }


    public static String getMimeType(Uri uri, Context ctx) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = ctx.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }


    public static void showKeypad(Context ctx, View view){
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }

    public static boolean isEmailValid(CharSequence emailAddressChars) {
        return Patterns.EMAIL_ADDRESS.matcher(emailAddressChars).matches();
    }

    public static boolean isUrlValid(CharSequence url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public static String doubleToString(double doubleNumber) {
        DecimalFormat formatter  = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
        DecimalFormatSymbols symbols = formatter .getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        return decimalFormat.format(doubleNumber).replace(".00", "");
    }

    public static String doubleToStringNoDecimal(double doubleNumber) {
        DecimalFormat formatter  = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
        DecimalFormatSymbols symbols = formatter .getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#,##0", symbols);
        return decimalFormat.format(doubleNumber);
    }

    public static String doubleToString(String doubleString) {
        DecimalFormat formatter  = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
        DecimalFormatSymbols symbols = formatter .getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00",symbols);
        return decimalFormat.format(Double.parseDouble(doubleString)).replace(".00", "");
    }

    public static String doubleToStringNoSeparator(double doubleNumber) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(doubleNumber).replace(".00", "");
    }

    public static String getWindowWidthString(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return String.valueOf(size.x);
    }

    public static int getWindowWidthInt(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static String getWindowHeightString(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return String.valueOf(size.y);
    }

    public static int getWindowHeightInt(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static boolean checkMakeDir(File path) {
        return !path.exists() && path.mkdirs();
    }

    public static boolean deleteFile(File file) {
        return file.delete();
    }

    public static boolean deleteFile(String file) {
        return new File(file).delete();
    }

    public static boolean createNewFile(File path) {
        try {
            return path.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void underConstructionToast() {
        Toast toast = Toast.makeText(MyApplication.getContext(), R.string.under_construction, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}