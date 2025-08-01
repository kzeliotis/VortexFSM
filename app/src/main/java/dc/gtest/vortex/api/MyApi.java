package dc.gtest.vortex.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import dc.gtest.vortex.BuildConfig;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyPrefs.PREF_API_CONNECTION_TIMEOUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_DEVICE_ID;
import static dc.gtest.vortex.support.MyPrefs.PREF_DEV_LOGIN;
import static dc.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

class MyApi {

    static final String MY_API_RESPONSE_CODE = "responseCode";
    static final String MY_API_RESPONSE_MESSAGE = "responseMessage";
    static final String MY_API_RESPONSE_BODY = "responseBody";
    static final String Version = BuildConfig.VERSION_NAME;
    // APIs URLs
    static final String API_VERIFY_IMEI_EXTERNAL_URL = "http://support.dataconsulting.gr/CompanyService/CompanyService.svc/VerifyImei?imei=";
    static final String API_VERIFY_IMEI_INTERNAL_URL = "http://192.168.1.199/CompanyService/CompanyService.svc/VerifyImei?imei=";
    static final String API_GET_ASSIGNMENTS = "/Vortex.svc/GetAssignments?UserName=";
    static final String API_GET_STATUSES = "/Vortex.svc/GetStatus";
    static final String API_GET_MOBILE_SETTINGS = "/Vortex.svc/GetMobileSettings?UserId=";
    static final String API_SEARCH_CUSTOMERS = "/Vortex.svc/GetLiveCustomers";
    static final String API_GET_ALL_PRODUCTS = "/Vortex.svc/GetProducts?userId=";
    static final String API_GET_WAREHOUSE_PRODUCTS = "/Vortex.svc/GetWarehouseProducts?WarehouseId=";
    static final String API_GET_PICKING_LIST = "/Vortex.svc/GetDetPicking?AssignmentId=";
    static final String API_GET_PRODUCT_TYPES = "/Vortex.svc/GetProductTypes";
    static final String API_GET_ALL_ATTRIBUTES = "/Vortex.svc/GetAttributes";
    static final String API_GET_ALL_CONSUMABLES = "/Vortex.svc/GetConsumables?AssignmentId=";
    static final String API_UPDATE_ASSIGNMENT = "/Vortex.svc/UpdateAssignment";
    static final String API_UPDATE_RETURN_TO_BASE = "/Vortex.svc/UpdateReturnToBase";
    static final String API_SET_ASSIGNMENT_PHOTO = "/Vortex.svc/SetAssignmentPhoto";
    static final String API_SET_ASSIGNMENT_ATTACHMENT = "/Vortex.svc/SetAssignmentAttachment";
    static final String API_SET_VORTEX_ATTACHMENT = "/Vortex.svc/SetVortexAttachment";
    static final String API_GET_ZONES = "/Vortex.svc/GetProjectZones?ProjectId=";
    static final String API_GET_ZONE_PRODUCTS = "/Vortex.svc/GetZoneProducts?ZoneId=";
    static final String API_SEND_ZONE_PRODUCTS_MEASUREMENTS = "/Vortex.svc/SetProductMeasurements";
    static final String API_SEND_CONSUMABLES = "/Vortex.svc/InsertConsumables";
    static final String API_SEND_MANDATORY_TASKS = "/Vortex.svc/InsertMandatorySteps";
    static final String API_RESEND_MANDATORY_TASKS = "/Vortex.svc/ResendMandatorySteps";
    static final String API_SEND_USE_PT_OVERNIGHT = "/Vortex.svc/SetAssignmentTransit";
    static final String API_GET_SHOW_START_WORK_BUTTON = "/Vortex.svc/GetShowStartWork";
    static final String API_GET_SHOW_USE_PT_OVERNIGHT_BUTTONS = "/Vortex.svc/GetShowMobileTravelButtons";
    static final String API_GET_SHOW_ZONE_PRODUCTS_BUTTON = "/Vortex.svc/GetShowProductZones";
    static final String API_GET_LOCATION_SENDING_INTERVAL_IN_MINUTES = "/Vortex.svc/GetSendLocationEvery";
    static final String API_SEND_USER_COORDINATES = "/Vortex.svc/SetResourcePosition";
    static final String API_SEND_SET_CUSTOMER = "/Vortex.svc/SetCustomer";
    static final String API_SEND_SET_ASSIGNMENT = "/Vortex.svc/SetAssignment";
    static final String API_GET_CUSTOMER_PROJECTS = "/Vortex.svc/GetCustomerProjects?CustomerId=";
    static final String API_GET_USER_PARTNER_RESOURCES = "/Vortex.svc/GetUserPartnerResources?Username=";
    static final String API_GET_SERVICES = "/Vortex.svc/GetServices?AssignmentId=";
    static final String API_GET_SERVICES_FROM_PICKING = "/Vortex.svc/GetServicesFromPicking?AssignmentId=";
    static final String API_GET_SERVICES_FOR_ASSIGNMENT = "/Vortex.svc/GetServicesForAssignment?CustomerId=";
    static final String API_GET_HISTORY_ASSIGNMENTS = "/Vortex.svc/GetHistoryAssignments?AssignmentId=";
    static final String API_GET_ASSIGNMENT_PRODUCTS = "/Vortex.svc/GetProjectProductsByAssignmentId?AssignmentId=";
    static final String API_DELETE_PRODUCT = "/Vortex.svc/DeleteProduct?ProjectProductId=";
    static final String API_DELETE_ATTRIBUTE = "/Vortex.svc/DeleteAttribute?ProjectProductId=";
    static final String API_SEND_NEW_ATTRIBUTE = "/Vortex.svc/InsertNewAttribute";
    static final String API_SEND_UPDATE_ATTRIBUTE_VALUES = "/Vortex.svc/UpdateAttributeValues?";
    static final String API_SEND_NEW_PRODUCT = "/Vortex.svc/InsertNewProduct";
    static final String API_SEND_CHANGE_RESOURCE = "/Vortex.svc/ChangeResource";
    static final String API_GET_SUB_ASSIGNMENTS = "/Vortex.svc/GetSubAssignments?AssignmentId=";
    static final String API_GET_ASSIGNMENT_COST = "/Vortex.svc/GetAssignmentCost?AssignmentId=";
    static final String API_GET_TECH_ACTIONS = "/Vortex.svc/GetTechActions";
    static final String API_GET_ASSIGNMENT_ACTIONS = "/Vortex.svc/GetAssignmentActions";
    static final String API_GET_ASSIGNMENT_TYPES = "/Vortex.svc/GetAssignmentTypes";
    static final String API_GET_PROJECT_INSTALLATIONS = "/Vortex.svc/GetProjectInstallations?ProjectId=";
    static final String API_SEND_INSTALLATION_ZONE = "/Vortex.svc/SetProjectZone";
    static final String API_DELETE_ZONE = "/Vortex.svc/DeleteProjectZone?ProjectZoneId=";
    static final String API_GET_VORTEX_TABLE_CUSTOM_FIELDS = "/Vortex.svc/GetVortexTableCustomFields?VortexTable=";
    static final String API_SEND_CUSTOM_FIELDS = "/Vortex.svc/SetCustomFields";
    static final String API_GET_REPORT_PREVIEW = "/Vortex.svc/GetReportFile?AssignmentId=";
    static final String API_GET_MANUAL_FILE = "/Vortex.svc/GetManualFromAzure?BlobAttachmentId=";
    static final String API_SEND_ZONES_WITH_NO_MEASUREMENTS = "/Vortex.svc/SetZonesWithNoMeasurements?AssignmentId=";
    static final String API_GET_ATTACHMENT_URL = "/Vortex.svc/GetAttachment?ObjectId=";
    static final String API_GET_ASSIGNMENT_INDICATORS = "/Vortex.svc/GetAssignmentIndicators";
    static final String API_SET_PRODUCTS_TO_INSTALLATION = "/Vortex.svc/SetProjectProductsToProjectInstallation?ProjectInstallationId=";
    static final String API_GET_AES_KEY = "/Vortex.svc/GetAESKey?username=";
    static final String API_GET_MASTER_PROJECTS = "/Vortex.svc/GetMasterProjectsForNewAssignment?CustomerId=";
    static final String API_GET_PROJECT_PRODUCT_BY_IDENTITY = "/Vortex.svc/GetProjectProductByIdentity?idValue=";
    static final String API_SEND_DET_CHILDREN = "/Vortex.svc/SetDetChildren";

    private static HttpsURLConnection httpsUrlConnection(URL urlDownload) throws Exception {
        HttpsURLConnection connection=null;
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

            }

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        SSLContext sc = SSLContext.getInstance("SSL"); // Add in try catch block if you get error.
        sc.init(null, trustAllCerts, new java.security.SecureRandom()); // Add in try catch block if you get error.
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier usnoHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        SSLSocketFactory sslSocketFactory = sc.getSocketFactory();

        connection = (HttpsURLConnection) urlDownload.openConnection();
        connection.setHostnameVerifier(usnoHostnameVerifier);
        connection.setSSLSocketFactory(sslSocketFactory);

        return connection;
    }

    public static Bundle post(String apiUrl, String postBody, boolean isTextHtml) {

        Bundle bundle = new Bundle();

        postBody = postBody.replace("\\", "\\\\");

        try {

            int  conn_timeout = MyPrefs.getInt(PREF_API_CONNECTION_TIMEOUT, 15);
            conn_timeout *= 1000;

            URL url = new URL(apiUrl);

            if (apiUrl.toUpperCase().contains("HTTPS")){
                HttpsURLConnection httpURLConnection = httpsUrlConnection(url);//(HttpsURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(120000);
                httpURLConnection.setConnectTimeout(conn_timeout);
                httpURLConnection.setRequestMethod("POST");

            //Version = Version.replace(".","");
                if (MyPrefs.getBoolean(PREF_DEV_LOGIN, false)){
                    httpURLConnection.setRequestProperty("Authorization", "consult1ng|" + Version);
                }else{
                    httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, "") + "|" + Version);
                }

                if (isTextHtml) {
                    httpURLConnection.setRequestProperty("Content-type","text/plain; charset=UTF-8");
                } else {
                    httpURLConnection.setRequestProperty("Content-type","application/json; charset=UTF-8");
                }
                httpURLConnection.setRequestProperty("VortexFSM","f035gbl00033eedfpsycrencewe3225fcvf09");
                if (apiUrl.contains("GetUserAuthentication") || apiUrl.contains("GetAESKey")){
                    httpURLConnection.setRequestProperty("UserName", "GetUserAuthentication");
                }

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                bufferedWriter.write(postBody);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                int responseCode = httpURLConnection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 400) {
                    inputStream = httpURLConnection.getErrorStream();
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }

                String response = byteArrayOutputStream.toString("UTF-8");

                bundle.putInt(MY_API_RESPONSE_CODE, responseCode);
                bundle.putString(MY_API_RESPONSE_MESSAGE, httpURLConnection.getResponseMessage());
                bundle.putString(MY_API_RESPONSE_BODY, response);

                byteArrayOutputStream.close();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            } else {
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(120000);
                httpURLConnection.setConnectTimeout(conn_timeout);
                httpURLConnection.setRequestMethod("POST");

                //Version = Version.replace(".","");
                if (MyPrefs.getBoolean(PREF_DEV_LOGIN, false)){
                    httpURLConnection.setRequestProperty("Authorization", "consult1ng|" + Version);
                }else{
                    httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, "") + "|" + Version);
                }

                if (isTextHtml) {
                    httpURLConnection.setRequestProperty("Content-type","text/plain; charset=UTF-8");
                } else {
                    httpURLConnection.setRequestProperty("Content-type","application/json; charset=UTF-8");
                }

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                bufferedWriter.write(postBody);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                int responseCode = httpURLConnection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 400) {
                    inputStream = httpURLConnection.getErrorStream();
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }

                String response = byteArrayOutputStream.toString("UTF-8");

                bundle.putInt(MY_API_RESPONSE_CODE, responseCode);
                bundle.putString(MY_API_RESPONSE_MESSAGE, httpURLConnection.getResponseMessage());
                bundle.putString(MY_API_RESPONSE_BODY, response);

                byteArrayOutputStream.close();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            }


        } catch (Exception e) {
            if (apiUrl.contains("GetUserAuthentication")) {
                postBody = "Login failed attempt " + MyPrefs.getString(PREF_USER_NAME, "-");
            }
            MyLogs.showFullLog("myLogs: " + "MyApi", apiUrl, postBody, 0, e.getMessage(), "");
            e.printStackTrace();
        }

        return bundle;
    }

    public static Bundle get(String apiUrl) {

        Bundle bundle = new Bundle();

        //apiUrl = escapeUrlCharacters(apiUrl);

        try {

            int  conn_timeout = MyPrefs.getInt(PREF_API_CONNECTION_TIMEOUT, 15);
            conn_timeout *= 1000;

            URL url = new URL(apiUrl);
            if(apiUrl.toUpperCase().contains("HTTPS")){
                HttpsURLConnection httpURLConnection = httpsUrlConnection(url);//(HttpsURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(60000);
                httpURLConnection.setConnectTimeout(conn_timeout);
                httpURLConnection.setRequestMethod("GET");
                if (MyPrefs.getBoolean(PREF_DEV_LOGIN, false)){
                    httpURLConnection.setRequestProperty("Authorization", "consult1ng|" + Version);
                }else{
                    httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, "") + "|" + Version);
                }
                httpURLConnection.setRequestProperty("VortexFSM","f035gbl00033eedfpsycrencewe3225fcvf09");
                if (apiUrl.contains("GetUserAuthentication") || apiUrl.contains("GetAESKey")){
                    httpURLConnection.setRequestProperty("UserName", "GetUserAuthentication");
                }
                httpURLConnection.setDoInput(true);

                int responseCode = httpURLConnection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 400) {
                    inputStream = httpURLConnection.getErrorStream();
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }

                String response = byteArrayOutputStream.toString("UTF-8");

                bundle.putInt(MY_API_RESPONSE_CODE, responseCode);
                bundle.putString(MY_API_RESPONSE_MESSAGE, httpURLConnection.getResponseMessage());
                bundle.putString(MY_API_RESPONSE_BODY, response);

                byteArrayOutputStream.close();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            } else {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(60000);
                httpURLConnection.setConnectTimeout(conn_timeout);
                httpURLConnection.setRequestMethod("GET");
                if (MyPrefs.getBoolean(PREF_DEV_LOGIN, false)){
                    httpURLConnection.setRequestProperty("Authorization", "consult1ng|" + Version);
                }else{
                    httpURLConnection.setRequestProperty("Authorization", MyPrefs.getDeviceId(PREF_DEVICE_ID, "") + "|" + Version);
                }
                httpURLConnection.setDoInput(true);

                int responseCode = httpURLConnection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 400) {
                    inputStream = httpURLConnection.getErrorStream();
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }

                String response = byteArrayOutputStream.toString("UTF-8");

                bundle.putInt(MY_API_RESPONSE_CODE, responseCode);
                bundle.putString(MY_API_RESPONSE_MESSAGE, httpURLConnection.getResponseMessage());
                bundle.putString(MY_API_RESPONSE_BODY, response);

                byteArrayOutputStream.close();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return bundle;
    }

    static String escapeUrlCharacters(String textValue) {
        try{
            URL url= new URL(textValue);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            textValue = uri.toASCIIString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return textValue;
    }

//
//     if (apiUrl.toUpperCase().contains("HTTPS")){
//        HttpsURLConnection httpURLConnection = httpsUrlConnection(url);//(HttpsURLConnection)url.openConnection();
//        httpURLConnection.setReadTimeout(60000);
//        httpURLConnection.setConnectTimeout(conn_timeout);
//        httpURLConnection.setRequestMethod("POST");


    public static String downloadFile(String fileUrl, String destinationPath, Context ctx) {
        try {
            URL url = new URL(fileUrl);
            HttpsURLConnection connection = httpsUrlConnection(url);
            File outputFile = new File(ctx.getExternalFilesDir(null) + File.separator + destinationPath);
            // Set up SSL certificate verification bypass if needed
            //connection.setSSLSocketFactory(TrustManagerHelper.getTrustAllSSLSocketFactory());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                int contentLength = connection.getContentLength();

                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                int totalBytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    // Optionally update progress here based on totalBytesRead and contentLength
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }

            connection.disconnect();
            destinationPath = outputFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            destinationPath = e.getMessage();
        }

        return destinationPath;
    }


}
