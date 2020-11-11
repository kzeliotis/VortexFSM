package am.gtest.vortex.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
//import androidx.annotation.Nullable;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static am.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;

public class SendLocationService extends Service {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private double currentLat = 0;
    private double currentLng = 0;


    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(LOG_TAG, "----------------------------------------------------- SendLocationService onCreate.");

        int coordsSendingIntervalMillis = 10000; //MyPrefs.getInt(PREF_LOCATION_SENDING_INTERVAL, 0);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(coordsSendingIntervalMillis);
        mLocationRequest.setFastestInterval(coordsSendingIntervalMillis);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Log.e(LOG_TAG, "----------------------------------------------------- SendLocationService startLocationUpdates.");

                if (locationResult == null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    return;
                }

                currentLat = 0;
                currentLng = 0;

                Location location = locationResult.getLastLocation();
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();

                    Log.e(LOG_TAG, "----------------- current LatLng: " + currentLat + "; " + currentLng);

                    if (currentLat != 0 && currentLng != 0) {

                        MyPrefs.setString(PREF_CURRENT_LAT, String.valueOf(currentLat));
                        MyPrefs.setString(PREF_CURRENT_LNG, String.valueOf(currentLng));


                    }
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);

            }
        };

        startLocationUpdates();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
