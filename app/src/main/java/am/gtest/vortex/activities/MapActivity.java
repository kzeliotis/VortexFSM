package am.gtest.vortex.activities;

import android.os.Bundle;
//import androidx.fragment.app.FragmentActivity;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import am.gtest.vortex.R;

import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frAssignmentMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            double cabLat = Double.parseDouble(SELECTED_ASSIGNMENT.getCabLat());
            double cabLng = Double.parseDouble(SELECTED_ASSIGNMENT.getCabLng());
            LatLng cabinetLatLng = new LatLng(cabLat, cabLng);
            googleMap.addMarker(new MarkerOptions().position(cabinetLatLng).title(SELECTED_ASSIGNMENT.getAssignmentId()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cabinetLatLng, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}