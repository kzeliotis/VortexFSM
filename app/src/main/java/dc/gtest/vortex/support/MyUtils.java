package dc.gtest.vortex.support;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.amrdeveloper.treeview.TreeNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import dc.gtest.vortex.R;
import dc.gtest.vortex.application.MyApplication;
import dc.gtest.vortex.models.ProductModel;

import static dc.gtest.vortex.support.MyGlobals.CONST_ASSIGNMENT_ATTACHMENTS_FOLDER;


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

    public static String ToJson(String raw) {
        String escaped = raw;
//        escaped = escaped.replace("\\", "\\\\");
//        escaped = escaped.replace("\"", "\\\"");
//        escaped = escaped.replace("\b", "\\b");
//        escaped = escaped.replace("\f", "\\f");
//        escaped = escaped.replace("\n", "\\n");
//        escaped = escaped.replace("\r", "\\r");
//        escaped = escaped.replace("\t", "\\t");
        escaped = escaped.replace("\\", " ");
        escaped = escaped.replace("\"", " ");
        escaped = escaped.replace("\b", " ");
        escaped = escaped.replace("\f", " ");
        escaped = escaped.replace("\n", " ");
        escaped = escaped.replace("\r", " ");
        escaped = escaped.replace("\t", " ");
        // TODO: escape other non-printing characters using uXXXX notation
        return escaped;
    }

    public static String escapeJsonString(String input) {
        String escaped = input;

        if(escaped.contains("\\\\")
          || escaped.contains("\\\"")
          || escaped.contains("\\\"")
          || escaped.contains("\\b")
          || escaped.contains("\\f")
          || escaped.contains("\\n")
          || escaped.contains("\\t")
        ){
            return escaped;
        }

        escaped = escaped.replace("\\", "\\\\");
        escaped = escaped.replace("\"", "\\\"");
        escaped = escaped.replace("\b", "\\b");
        escaped = escaped.replace("\f", "\\f");
        escaped = escaped.replace("\n", "\\n");
        escaped = escaped.replace("\r", "\\r");
        escaped = escaped.replace("\t", "\\t");
        // TODO: escape other non-printing characters using uXXXX notation
        return escaped;

    }

    //private static final String AES_KEY = "brFsF6KjzXn3cOzlTBB0zo9ktziYr+LziPIrxj1Vu7ttac+bJHZ0vp7KqIeeO11qPv1AsGKN3TdUx0J04KoybnBMqQb5frhUqb4cWzFcbP1gD7yjH8mPzLArD83CDfjXpkco53WA2VsvZ+UY/PrWXOoe4acFWKyoV7SV/Jfkk/oipC+oB4vrq2eML9V525byIRBmkr2dkUPB8O5OG1o0/jYniP5TBI2Yk3sG7Ds2dKCHJ7qjb7Z+TgBJJUTMbu6D7hppo2cBmQA2epPDdeVlEDiJIzH8dnTBsBEoG3TeMFvlkQytt6C2mgJqeu+4e+Do35j00QFYI417w3li1aa8dA==";

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String HASH_ALGORITHM = "MD5";

    private static byte[] getKey(String key) throws Exception {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hash = md.digest(key.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = new byte[32];
        System.arraycopy(hash, 0, keyBytes, 0, 16);
        System.arraycopy(hash, 0, keyBytes, 15, 16);
        return keyBytes;
    }

    public static String encrypt(String input) {

        String result = "";

        String AES_KEY = MyPrefs.getString(MyPrefs.PREF_AES_KEY, "");

        if (AES_KEY.length() == 0) {return input;}

        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(getKey(AES_KEY), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(input.getBytes());
            result = new String(java.util.Base64.getEncoder().encode(encryptedBytes), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            result = input;
        }

        return result;

    }

    public static String decrypt(String input) {

        String result = "";
        String AES_KEY = MyPrefs.getString(MyPrefs.PREF_AES_KEY, "");
        if (AES_KEY.length() == 0) {return input;}

        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(getKey(AES_KEY), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(java.util.Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8)));
            result = new String(decryptedBytes, StandardCharsets.UTF_8);
        }catch (Exception ex) {
            ex.printStackTrace();
            result = input;
        }

        return result;

    }


    public static String Base64ToFile(Context ctx, String base64String, String fileName, String assignmentId, String filePath) {
        //String base64String = "yourBase64EncodedString"; // Replace with your actual Base64 string\\\
        if (base64String.length() == 0){
            return "";
        }

        if(!assignmentId.equals("0"))
        {
            filePath = ctx.getExternalFilesDir(null) + File.separator + assignmentId + CONST_ASSIGNMENT_ATTACHMENTS_FOLDER;
        } // Replace with the desired output file path
        else
        {
            filePath = ctx.getExternalFilesDir(null) + "/manuals";
        }

        File path = new File(filePath);

        if (!path.exists()) {
            MyUtils.checkMakeDir(path);
        }

        filePath += "/" + fileName;

        try {
            // Decode Base64 string to bytes
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);

            // Write bytes to file
            writeBytesToFile(decodedBytes, filePath);

            if(assignmentId.equals("0")){
                Uri fileURI;
                File _file = new File(filePath);

                if (Build.VERSION.SDK_INT >= 24) {
                    fileURI = FileProvider.getUriForFile(ctx, ctx.getPackageName(), _file);
                } else {
                    fileURI = Uri.fromFile(_file);
                }

                //Log.e(LOG_TAG, "---------------------- fileURI: " + fileURI);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileURI, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ctx.startActivity(intent);
            }else{
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                File pickedFile = new File(filePath);
                if (pickedFile.exists()){
                    Uri contentUri = FileProvider.getUriForFile(ctx, ctx.getPackageName(), pickedFile);
                    String mimeType = "application/pdf";
                    try{
                        mimeType = ctx.getContentResolver().getType(contentUri);
                    }catch (Exception ex){
                    }
                    myIntent.setDataAndType(contentUri, mimeType);
                    myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent j = Intent.createChooser(myIntent, "Choose an application to open with:");
                    j.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    ctx.startActivity(myIntent);
                }
            }

            //System.out.println("File successfully created at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }



    private static void writeBytesToFile(byte[] bytes, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        }
    }

    public static Object cloneObject(Object original) {
        try {
            // Create a new instance of the original object's class
            Object clone = original.getClass().newInstance();

            // Get all fields of the original object's class, including private ones
            Field[] fields = original.getClass().getDeclaredFields();
            for (Field field : fields) {
                // Set the accessible flag to true to access private fields
                field.setAccessible(true);
                // Copy the value from the original object's field to the clone object's field
                field.set(clone, field.get(original));
            }

            return clone;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static TreeNode cloneTreeNode(TreeNode originalNode) {
        try {

            // Invoke the constructor to create a new instance of TreeNode
            TreeNode clonedNode = new TreeNode(originalNode.getValue(), 0);
            clonedNode.setExpanded(originalNode.isExpanded());
            clonedNode.setSelected(originalNode.isSelected());

            return clonedNode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }


    public static List<String> GetNodeAndParents(TreeNode node, List<String> parents){

        TreeNode nodeparent = node.getParent();
        if (nodeparent != null){
            parents.add(((ProductModel)nodeparent.getValue()).getProjectProductId());
            parents = GetNodeAndParents(nodeparent, parents);
        }

        return parents;
    }

}