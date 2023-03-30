package dc.gtest.vortex.support;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import dc.gtest.vortex.services.SendLocationSRV;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENTS_CTX;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_CHECK_LOCATION_SETTINGS;
import static dc.gtest.vortex.support.MyGlobals.PERMISSIONS_BACKGROUND_LOCATION;
import static dc.gtest.vortex.support.MyGlobals.PERMISSIONS_FINE_LOCATION;
import static dc.gtest.vortex.support.MyLocalization.localized_access_to_location_for_features;
import static dc.gtest.vortex.support.MyLocalization.localized_access_to_location_for_features2;
import static dc.gtest.vortex.support.MyLocalization.localized_some_features;
import static dc.gtest.vortex.support.MyLocalization.localized_turn_on_location;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_ENABLE_LOCATION_SERVICE;
import static dc.gtest.vortex.support.MyPrefs.PREF_GPS_PRIORITY;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEEP_GPS_LOG;
import static dc.gtest.vortex.support.MyPrefs.PREF_LOCATION_REFRESH_INTERVAL;
import static dc.gtest.vortex.support.MyPrefs.PREF_LOCATION_SENDING_INTERVAL;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class PermGetLocation {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;

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

    public PermGetLocation(Context ctx) {
        this.ctx = ctx;
    }

    public void myRequestPermission(int requestCode) {

        switch (requestCode) {

            case PERMISSIONS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT > 23) {
                    if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        TextView tv  = new TextView(this);
//                        tv.setMovementMethod(LinkMovementMethod.getInstance());
//                        tv.setText(randomString +"/n"+ Html.fromHtml("<a href=https://play.google.com/store/apps/details?id=com.xxxxxxxxx>Click Here</a>"));

                        new AlertDialog.Builder(ctx)
                                .setMessage(localized_access_to_location_for_features)
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions((AppCompatActivity) ctx, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, requestCode);
                                })
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                    } else {
                        myRequestPermission(PERMISSIONS_BACKGROUND_LOCATION);
                        setupLocationUpdates(ctx);
                    }
                } else {
                    setupLocationUpdates(ctx);
                }
                break;
            case PERMISSIONS_BACKGROUND_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && MyPrefs.getBoolean(PREF_ENABLE_LOCATION_SERVICE, false)) {
                    if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        new AlertDialog.Builder(ctx)
                                .setMessage(localized_access_to_location_for_features2)
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions((AppCompatActivity) ctx, new String[] { Manifest.permission.ACCESS_BACKGROUND_LOCATION }, requestCode);
                                })
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                    }
                }
        }
    }

    public void myPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.e(LOG_TAG, "PermGetLocation ---------------------------- onRequestPermissionsResult requestCode: " + requestCode);

        switch (requestCode) {

            case PERMISSIONS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                        myRequestPermission(PERMISSIONS_BACKGROUND_LOCATION);
                        setupLocationUpdates(ctx);
                    }
                } else {
                    new AlertDialog.Builder(ctx)
                            .setMessage(localized_some_features)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions((AppCompatActivity) ctx, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
                            })
                            .show();
                }
                break;
            case PERMISSIONS_BACKGROUND_LOCATION:
                        setupLocationUpdates(ctx);
                        break;

        }
    }

    public void myActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e(LOG_TAG, "PermGetLocation ------------------------ onActivityResult requestCode: " + requestCode);

        switch (requestCode) {

            // Check for the integer request code originally supplied to startResolutionForResult().
            case OTHER_APP_RESULT_CHECK_LOCATION_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:

                        Log.e(LOG_TAG, "User agreed to make required location settings changes.");

                        break;
                    case RESULT_CANCELED:

                        Log.e(LOG_TAG, "User chose not to make required location settings changes.");

                        new AlertDialog.Builder(ctx)
                                .setMessage(localized_turn_on_location)
                                .setPositiveButton(R.string.ok, (dialog, which) -> {
                                    dialog.dismiss();
                                    ctx.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                })
                                .show();

                        break;
                }
                break;
        }
    }

    private void setupLocationUpdates(Context ctx) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && MyPrefs.getBoolean(PREF_ENABLE_LOCATION_SERVICE, false)) {
            //Context context = getApplicationContext();
            ASSIGNMENTS_CTX = ctx;
            Intent srv = new Intent(ASSIGNMENTS_CTX, SendLocationSRV.class); // Build the intent for the service
            srv.setAction("Start");
            ASSIGNMENTS_CTX.startForegroundService(srv);
            return;
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);

        //Provides access to the Location Settings API.
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(ctx);

        // Kick off the process of building the LocationCallback, LocationRequest, and LocationSettingsRequest objects.
        createLocationCallback(ctx);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && MyPrefs.getBoolean(PREF_ENABLE_LOCATION_SERVICE, false)) {
            //Context context = getApplicationContext();
            ASSIGNMENTS_CTX = ctx;
            Intent srv = new Intent(ASSIGNMENTS_CTX, SendLocationSRV.class); // Build the intent for the service
            srv.setAction("Stop");
            ASSIGNMENTS_CTX.startForegroundService(srv);
            return;
        }

        Log.e(LOG_TAG, "----------------------------------------------------- stopLocationUpdates.");
    }

    public void getCurrentPosition(){
        createLocationRequest();
    }



    // Creates a callback for receiving location events.
    private void createLocationCallback(Context ctx) {

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
}
