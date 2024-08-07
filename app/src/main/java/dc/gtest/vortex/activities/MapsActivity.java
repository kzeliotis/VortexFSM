package dc.gtest.vortex.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dc.gtest.vortex.R;
import dc.gtest.vortex.models.AssignmentModel;
import dc.gtest.vortex.support.MyDialogs;
import dc.gtest.vortex.support.MyPrefs;
import dc.gtest.vortex.support.MySwitchLanguage;

import static dc.gtest.vortex.support.MyGlobals.ASSIGNMENTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.CONST_EN;
import static dc.gtest.vortex.support.MyGlobals.FILTERED_ASSIGNMENTS_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LAT;
import static dc.gtest.vortex.support.MyPrefs.PREF_CURRENT_LNG;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ASSIGNMENTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_1 = 5;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_2 = 6;

    private GoogleMap mMap;

    private LatLng centerCamera;
    private boolean didOnce1 = false;
    private boolean didOnce2 = false;

    private GoogleApiClient mGoogleApiClient;
    private double gotLat = 38.0255957;
    private double gotLng = 23.7457171;
    private LocationRequest mLocationRequest;

    private String language;
    private String accessToLocation;
    private String someFeatures;
    private List<LatLng> _markerPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        language = MyPrefs.getString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);

        changeTextLanguage();

        // Create an instance of GoogleAPIClient.
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frAllDataMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(30000);
//        mLocationRequest.setFastestInterval(100);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        try {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onStop() {
//        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onRadioButtonClicked(View view) {
        language = new MySwitchLanguage(this).mySwitchLanguage(view);
        changeTextLanguage();
    }

    private void changeTextLanguage() {
        if (language.equals("gr")) {
            accessToLocation = getString(R.string.access_to_location_for_center_gr);
            someFeatures = getString(R.string.some_features_gr);
        } else {
            accessToLocation = getString(R.string.access_to_location_for_center);
            someFeatures = getString(R.string.some_features);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String assignmentData = MyPrefs.getString(PREF_DATA_ASSIGNMENTS, "");
        List<AssignmentModel> assignmentsList = FILTERED_ASSIGNMENTS_LIST.size() > 0 ? FILTERED_ASSIGNMENTS_LIST : ASSIGNMENTS_LIST;
        _markerPositions = new ArrayList<>();
        String cLat = MyPrefs.getString(PREF_CURRENT_LAT, "");
        String cLon = MyPrefs.getString(PREF_CURRENT_LNG, "");
        if(!cLat.isEmpty() && !cLon.isEmpty()){
            _markerPositions.add(new LatLng(Double.parseDouble(cLat), Double.parseDouble(cLon)));
        }

        if (assignmentsList.size() > 0) {

            try {
                //JSONArray jsonArr = new JSONArray(assignmentData);

                for (AssignmentModel am : assignmentsList) {

                    String projectLat = am.getProjectLat(); // oneObject.getString("ProjectLat");
                    String projectLon = am.getProjectLon(); // oneObject.getString("ProjectLon");

                    if (!projectLat.equals("") && !projectLon.equals("")) {
                        LatLng markerCord = new LatLng(Double.parseDouble(projectLat), Double.parseDouble(projectLon));

                        _markerPositions.add(markerCord);

                        BitmapDescriptor afLogo = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
                        String title = am.getCustomerName(); // oneObject.getString("CustomerName");

                        String address = "Address: " + am.getProjectAddress() + ", " + am.getProjectCity();
                        String phone = "Phone: " + am.getPhone();

                        String dateStart = am.getDateStart();
                        dateStart = dateStart.substring(dateStart.length()-5);
                        String dateEnd = am.getDateEnd();
                        dateEnd = dateEnd.substring(dateEnd.length()-5);
                        String assignmentTime = "Time: " + dateStart + " - " + dateEnd;

                        String assignmentType = "Type: " + am.getAssignmentType();

                        mMap.addMarker(new MarkerOptions()
                                .position(markerCord)
                                .title(title)
                                .icon(afLogo)
                                .snippet(assignmentTime + "\n" + assignmentType + "\n" + address + "\n" + phone)
                        );

                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {

                                LinearLayout info = new LinearLayout(getApplicationContext());
                                info.setOrientation(LinearLayout.VERTICAL);

                                TextView title = new TextView(getApplicationContext());
                                title.setTextColor(Color.parseColor("#339933"));
                                //title.setGravity(Gravity.LEFT);
                                title.setTypeface(null, Typeface.BOLD);
                                title.setText(marker.getTitle());

                                TextView snippet = new TextView(getApplicationContext());
                                snippet.setTextColor(Color.parseColor("#cc3300"));
                                snippet.setText(marker.getSnippet());

                                info.addView(title);
                                info.addView(snippet);

                                return info;
                            }
                        });
                    }
                }

                zoomToFitMarkers();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel(
                            (dialog, which) -> ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_1));
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_1);
                }

            } else {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void zoomToFitMarkers() {
        if (_markerPositions == null || _markerPositions.isEmpty()) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng position : _markerPositions) {
            builder.include(position);
        }

        LatLngBounds bounds = builder.build();
        int padding = 400; // offset from edges of the map in pixels
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }


    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MapsActivity.this)
                .setMessage(accessToLocation)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        didOnce1 = true;
        didOnce2 = true;
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel(
                            (dialog, which) -> ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_2));
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_2);
                }
            } else {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    gotLat = mLastLocation.getLatitude();
                    gotLng = mLastLocation.getLongitude();
                }

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } else {

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                gotLat = mLastLocation.getLatitude();
                gotLng = mLastLocation.getLongitude();
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        if (!didOnce1) {
            didOnce1 = true;
            centerCamera = new LatLng(gotLat, gotLng);
            //centerCamera = new LatLng(37.986628, 23.762729);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerCamera, 11));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        gotLat = location.getLatitude();
        gotLng = location.getLongitude();

        if (!didOnce2) {
            didOnce2 = true;
            centerCamera = new LatLng(gotLat, gotLng);
            //centerCamera = new LatLng(37.986628, 23.762729);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerCamera, 11));
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    MyDialogs.showOK(MapsActivity.this, someFeatures);
                }
                break;

            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        gotLat = mLastLocation.getLatitude();
                        gotLng = mLastLocation.getLongitude();
                    }

                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                } else {
                    MyDialogs.showOK(MapsActivity.this, someFeatures);
                }
                break;
        }
    }
}
