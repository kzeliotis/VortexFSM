package am.gtest.vortex.support;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import am.gtest.vortex.activities.MainActivity;

import static am.gtest.vortex.support.MyGlobals.globalExternalFileDir;

public class MyLogs {

    public static void showFullLog(String LOG_TAG, String apiUrl, String postBody, int responseCode, String responseMessage, String responseBody) {

        Log.e(LOG_TAG, ".\napiUrl: " + apiUrl);
        Log.e(LOG_TAG, "postBody: \n" + postBody);
        Log.e(LOG_TAG, "responseCode: " + responseCode);
        Log.e(LOG_TAG, "responseMessage: " + responseMessage);

        showResponseLog(LOG_TAG, responseBody);

        writeFile_FullLog( LOG_TAG, apiUrl, postBody, responseCode, responseMessage, responseBody);
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

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());

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

}
