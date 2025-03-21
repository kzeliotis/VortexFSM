package dc.gtest.vortex.services;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dc.gtest.vortex.R;
import dc.gtest.vortex.activities.AssignmentsActivity;
import dc.gtest.vortex.adapters.AssignmentsRvAdapter;
import dc.gtest.vortex.api.SendCoords;
import dc.gtest.vortex.data.AssignmentsData;
import dc.gtest.vortex.support.MyLogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MyUtils;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENTS_CTX;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_CHECK_LOCATION_SETTINGS;
import static dc.gtest.vortex.support.MyLocalization.localized_vortex_tracking_running;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_GPS_PRIORITY;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEEP_GPS_LOG;
import static dc.gtest.vortex.support.MyPrefs.PREF_LOCATION_REFRESH_INTERVAL;
import static dc.gtest.vortex.support.MyPrefs.PREF_LOCATION_SENDING_INTERVAL;



public class SendLocationSRV extends Service {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private static Context ctx;

    private double currentLat = 0;
    private double currentLng = 0;

    private int countDown = 0;
    private int refreshInterval = 0;
    private long mLastLocationTime = 0;

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Provides access to the Fused Location Provider API.
    private FusedLocationProviderClient mFusedLocationClient;

    // Stores parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;

    // Stores the types of location services the client is interested in using. Used for checking
    // settings to determine if the device has optimal location settings.
    private LocationSettingsRequest mLocationSettingsRequest;

    // Callback for Location events.
    private LocationCallback mLocationCallback;
    //private LocationCallback m

    // Represents a geographical location.
    private Location mCurrentLocation;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent.getAction().equals("Start")){

            try {
                String NOTIFICATION_CHANNEL_ID = createNotificationChannel("dc.gtest.vortex", "Vortex Location service");

                Intent notificationIntent = new Intent(this, ASSIGNMENTS_CTX.getClass());
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                int requestID = (int) System.currentTimeMillis();
                PendingIntent pendingIntent = PendingIntent.getActivity(this,requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
                Notification notification = notificationBuilder.setOngoing(true)
                        .setContentTitle("Vortex")
                        .setContentText(localized_vortex_tracking_running)
                        //.setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                        .setCategory(Notification.CATEGORY_NAVIGATION)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //.setOngoing(true)
                        .build();
                startForeground(666, notification);
            }catch (Exception ex){
                ex.printStackTrace();
                MyLogs.showFullLog("myLogs: " + "SendLocationSRV", "", "Start", 0, ex.getMessage(), "");
            }



//            NotificationChannel nc = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Vortex Location Service", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager nm = getSystemService(NotificationManager.class);
//            nm.createNotificationChannel(nc);

        }
        else if (intent.getAction().equals("Stop")){
            try {
                stopLocationUpdates();
                stopForeground(true);
                stopSelfResult(startId);
            }catch (Exception ex){
                ex.printStackTrace();
                MyLogs.showFullLog("myLogs: " + "SendLocationSRV", "", "Stop", 0, ex.getMessage(), "");
            }

        }


        return START_STICKY;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName){
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_DEFAULT);
        chan.setDescription(channelName);
        chan.enableLights(true);
        chan.setLightColor(Color.BLUE);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(chan);

        return channelId;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        ctx = ASSIGNMENTS_CTX;
        if(ctx == null){
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);

        //Provides access to the Location Settings API.
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(ctx);

        // Kick off the process of building the LocationCallback, LocationRequest, and LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> Log.e(LOG_TAG, "All location settings are satisfied."))

                .addOnFailureListener(e -> {

                    int statusCode = ((ApiException) e).getStatusCode();

                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            Log.e(LOG_TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");

                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult((Activity) ctx, OTHER_APP_RESULT_CHECK_LOCATION_SETTINGS);
                            } catch (Exception sie) {
                                sie.printStackTrace();

                                Log.e(LOG_TAG, "PendingIntent unable to execute request.");
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";

                            Log.e(LOG_TAG, errorMessage);

                            Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        startLocationUpdates();
    }

    public void onDestroy() {
        super.onDestroy();
        stopForeground(true); // Remove notification
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    public void stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationClient
                    .removeLocationUpdates(mLocationCallback);
        }

        Log.e(LOG_TAG, "----------------------------------------------------- stopLocationUpdates.");
    }

    public void getCurrentPosition(){
        createLocationRequest();
    }



    // Creates a callback for receiving location events.
    private void createLocationCallback() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                String logText = "-------------------- \n";
                logText += MyUtils.isNetworkAvailable() ? "NetworkAvailable\n" : "NetworkNotAvailable\n";
                logText += MyUtils.checkGPSStatus() ? "GPS Enabled\n" : "GPS Disabled\n";
                logText += "Refresh Interval:" + refreshInterval +"\n";

                countDown = countDown - refreshInterval;
                boolean sendToService = false;
                if (countDown <= 0){
                    sendToService = true;
                    countDown = MyPrefs.getInt(PREF_LOCATION_SENDING_INTERVAL, 0);
                }

                logText += "Countdown: " + countDown + "\n";
                logText += sendToService ? "SendToService = True\n" : "SendToService = False\n";


                if (locationResult == null) {
                    logText += " LocactionResult == null";
                    if(MyPrefs.getBoolean(PREF_KEEP_GPS_LOG,  false)){
                        MyLogs.appendFile_FullLog("GPS_LOG", logText, "gps_log");
                    }
                    return;
                }

                currentLat = 0;
                currentLng = 0;

                mCurrentLocation = locationResult.getLastLocation();


                if (mCurrentLocation != null) {
                    currentLat = mCurrentLocation.getLatitude();
                    currentLng = mCurrentLocation.getLongitude();

                    long locationTime = mCurrentLocation.getTime();
                    Date lastdate = new Date(mLastLocationTime);
                    Date currentDate = new Date(locationTime);
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    logText += "LatLng: " + currentLat + "; " + currentLng + "\n";
                    logText += "LastLocationTime:" + df.format(lastdate) + "\nCurrLocationTime:" + df.format(currentDate) + "\n";


                    mLastLocationTime = locationTime;

                    if (currentLat != 0 && currentLng != 0) {

                        Log.e(LOG_TAG, "----------------- current LatLng: " + currentLat + "; " + currentLng);

                        MyPrefs.setString(PREF_CURRENT_LAT, String.valueOf(currentLat));
                        MyPrefs.setString(PREF_CURRENT_LNG, String.valueOf(currentLng));

                        if (sendToService) {
                            AssignmentsData.generate(ctx);
                            RecyclerView rvAssignments = ((AppCompatActivity)ctx).findViewById(R.id.rvAssignments);
                            if (rvAssignments != null) {
                                AssignmentsRvAdapter assignmentsRvAdapter = (AssignmentsRvAdapter) rvAssignments.getAdapter();
                                if (assignmentsRvAdapter != null) {
                                    assignmentsRvAdapter.notifyDataSetChanged();
                                    assignmentsRvAdapter.getFilter().filter(AssignmentsActivity.searchedText);
                                }
                            }

                            if (MyPrefs.getInt(PREF_LOCATION_SENDING_INTERVAL, 0) != 0) {
                                SendCoords sendCoords = new SendCoords();
                                sendCoords.execute();
                            }
                        }

                    }

                    if(MyPrefs.getBoolean(PREF_KEEP_GPS_LOG,  false)){
                        MyLogs.appendFile_FullLog("GPS_LOG", logText, "gps_log");
                    }

                } else {
                    logText += "LastLocation == null\n";
                    if(MyPrefs.getBoolean(PREF_KEEP_GPS_LOG,  false)){
                        MyLogs.appendFile_FullLog("GPS_LOG", logText, "gps_log");
                    }
                }
            }
        };
    }

    private void createLocationRequest() {

        refreshInterval = MyPrefs.getInt(PREF_LOCATION_REFRESH_INTERVAL, 30);

        if (refreshInterval == 0) {
            refreshInterval = 30;
        }

        refreshInterval = refreshInterval * 1000;


        countDown = MyPrefs.getInt(PREF_LOCATION_SENDING_INTERVAL, 0);

        if (countDown == 0) {
            countDown = 2 * 60 * 1000;
        }

        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(refreshInterval);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(refreshInterval/2);
        //mLocationRequest.setSmallestDisplacement(20);

        // mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        int Priority = MyPrefs.getInt(PREF_GPS_PRIORITY, 3);

        if (Priority == 0) {
            Priority = 3;
        }

        switch (Priority) {
            case 1:
                mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
                break;
            case 2:
                mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                break;
            case 3:
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                break;
            case 4:
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                break;
        }

    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
