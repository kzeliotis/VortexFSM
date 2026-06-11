package dc.gtest.vortex.support;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static dc.gtest.vortex.support.MyGlobals.globalExternalFileDir;

public class MyLogs {

    public static void showFullLog(String LOG_TAG, String apiUrl, String postBody, int responseCode, String responseMessage, String responseBody) {

        Log.e(LOG_TAG, ".\napiUrl: " + apiUrl);
        Log.e(LOG_TAG, "postBody: \n" + postBody);
        Log.e(LOG_TAG, "responseCode: " + responseCode);
        Log.e(LOG_TAG, "responseMessage: " + responseMessage);

        showResponseLog(LOG_TAG, responseBody);

        try{
            writeFile_FullLog( LOG_TAG, apiUrl, postBody, responseCode, responseMessage, responseBody);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void showResponseLog(String LOG_TAG, String responseBody) {

        if (responseBody == null) {
            Log.e(LOG_TAG, "responseBody: " + null + "\n.");
        } else if (responseBody.startsWith("{")) {
            showJsonObjectLog(LOG_TAG, responseBody);
        } else if (responseBody.startsWith("[")) {
            showJsonArrayLog(LOG_TAG, responseBody);
        } else {
            Log.e(LOG_TAG, "responseBody, data size: " + responseBody.length()*2 + " byte, " + responseBody.length() + " 16-bit chars\n" + responseBody + "\n.");
        }
    }

    private static void showJsonObjectLog(String LOG_TAG, String responseBody) {
        if (responseBody != null) {
            try {
                JSONObject jObjectDataFromApi = new JSONObject(responseBody);
                Log.e(LOG_TAG, "jObjectDataFromApi, data size: " + responseBody.length()*2 + " byte, "  + responseBody.length() + " 16-bit chars\n" + jObjectDataFromApi.toString(2) + "\n.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void showJsonArrayLog(String LOG_TAG, String responseBody) {
        if (responseBody != null) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(responseBody);
                Log.e(LOG_TAG, "jArrayDataFromApi, data size: " + responseBody.length()*2 + " byte, "  + responseBody.length() + " 16-bit chars, " +
                        "json array size: " + jArrayDataFromApi.length() + " items\n" + jArrayDataFromApi.toString(2) + "\n.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile_FullLog(String LOG_TAG, String apiUrl, String postBody, int responseCode, String responseMessage, String responseBody) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.ENGLISH).format(new Date());

        String LogText = "";
        LogText += "apiUrl: " + apiUrl +"\n";
        LogText += "postBody: " + postBody +"\n";
        LogText += "responseCode: " + responseCode +"\n";
        LogText += "responseMessage: " + responseMessage +"\n";


        if (responseBody == null) {
            LogText += "responseBody: " + null + "\n.";
        } else if (responseBody.startsWith("{")) {
            try {
                JSONObject jObjectDataFromApi = new JSONObject(responseBody);
                LogText += jObjectDataFromApi.toString(2) + "\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (responseBody.startsWith("[")) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(responseBody);
                LogText += jArrayDataFromApi.toString(2) + "\n.";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogText += "responseBody, data size: " + responseBody.length()*2 + " byte, " + responseBody.length() + " 16-bit chars\n" + responseBody + "\n.";
        }

        try {
            File directory = new File(globalExternalFileDir + "/LOGS/" + LOG_TAG.replace("myLogs: ", ""));
            MyUtils.checkMakeDir(directory);
            File logFile = new File(directory, timeStamp + ".txt");

            FileWriter writer = new FileWriter(logFile);
            writer.append(LogText);
            writer.flush();
            writer.close();

        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }


    }


    public static void appendFile_FullLog(String LOG_TAG, String LogText, String filename) {

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).format(new Date());

        LogText = timeStamp + " - " + LogText + "\n";

        try {
            File directory = new File(globalExternalFileDir + "/LOGS/" + LOG_TAG.replace("myLogs: ", ""));
            MyUtils.checkMakeDir(directory);
            File logFile = new File(directory, filename + ".txt");

            FileWriter writer = new FileWriter(logFile, true);
            writer.write(LogText);
            writer.flush();
            writer.close();

        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }


    }


    /**
     * Collects shared_prefs + LOGS folder and compresses them into a single zip file.
     *
     * @param context  Application context
     * @return         The resulting zip File, or null if something went wrong
     */
    public static File createDiagnosticsZip(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        File zipFile = new File(context.getExternalFilesDir(null), "diagnostics_" + timeStamp + ".zip");

        // Sources to include
        File sharedPrefsDir = new File("/data/data/dc.gtest.vortex/shared_prefs");
        File logsDir        = new File(globalExternalFileDir + "/LOGS");

        // Collect device info
        String diagnosticsText = DiagnosticsInfo.collect(context);

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream  zos = new ZipOutputStream(new BufferedOutputStream(fos))) {

            // Add diagnostics_info.txt as the first entry so it's easy to spot
            ZipEntry infoEntry = new ZipEntry("diagnostics_info.txt");
            zos.putNextEntry(infoEntry);
            zos.write(diagnosticsText.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            if (sharedPrefsDir.exists()) {
                addDirToZip(zos, sharedPrefsDir, "shared_prefs");
            }
            if (logsDir.exists()) {
                addDirToZip(zos, logsDir, "LOGS");
            }

            zos.finish();
            return zipFile;

        } catch (IOException e) {
            Log.e("ZipExport", "Failed to create zip: " + e.getMessage());
            return null;
        }
    }

    /**
     * Recursively adds a directory into the zip, preserving the folder structure
     * under the given zipPath prefix.
     */
    private static void addDirToZip(ZipOutputStream zos, File dir, String zipPath) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            String entryName = zipPath + "/" + file.getName();
            if (file.isDirectory()) {
                // Recurse into sub-directories (e.g. LOGS/SomeTag/)
                addDirToZip(zos, file, entryName);
            } else {
                addFileToZip(zos, file, entryName);
            }
        }
    }

    /**
     * Writes a single file into the zip stream.
     */
    private static void addFileToZip(ZipOutputStream zos, File file, String entryName) throws IOException {
        byte[] buffer = new byte[8192];
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            ZipEntry entry = new ZipEntry(entryName);
            entry.setSize(file.length());
            entry.setTime(file.lastModified());
            zos.putNextEntry(entry);

            int count;
            while ((count = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, count);
            }
            zos.closeEntry();
        }
    }




}
