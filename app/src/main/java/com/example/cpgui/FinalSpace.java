package com.example.cpgui;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
//import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.compat.ui.PlaceAutocompleteFragment;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.AutocompleteActivity;
//import com.google.android.libraries.places.api.model.*;
import java.util.Arrays;

public class FinalSpace extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng sydney = new LatLng(-8.579892, 116.095239);
    SupportMapFragment mapFragment;
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 17f;
    private LatLng mLatLng;
    private ImageView mGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_space);
        Log.d(TAG, "OnCreate");
        mGps = (ImageView) findViewById((R.id.ic_gps));
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),"AIzaSyDplZLeZYlKUCYmYY_tLOSAcfz4AauzssU");
        }
        PlacesClient placesClient = Places.createClient(this);
        setupAutoCompleteFragment();
        getLocationPermission();
    }

    private void setupAutoCompleteFragment() {
        Log.d(TAG, "setupAutoCompleteFragment : CALLED");
       final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_support_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG, Place.Field.NAME));

        Log.d(TAG, "setupAutoCompleteFragment : LISTENER CALLED");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.d(TAG, "setupAutoCompleteFragment : ON PLACE SELECTED");
                // TODO: Get info about the selected place.
               // mapFragment.getMapAsync(FinalSpace.this);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                if(place.getName()!=null&&place.getLatLng()!=null) {
                    Log.d(TAG, "setupAutoCompleteFragment : ON PLACE SELECTED IN IF CONDITION");
                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName());
                }
            }

            @Override
            public void onError( Status status) {
                Log.d(TAG, "setupAutoCompleteFragment : ON ERROR");
                // TODO: Handle the error.
                Log.e("Error", status.getStatusMessage());
                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            mMap.clear();
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                Log.d(TAG, "getLocationPermission: permission granted");
                initMap();
            } else {
                Log.d(TAG, "getLocationPermission: else of coarse");
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.d(TAG, "getLocationPermission:else of fine");
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }

    }
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting location of device");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(FinalSpace.this);
        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()&& task.getResult() != null) {
                            Log.d(TAG, "onComplete: found Location");
                            Location currentLocation = (Location) task.getResult();
                            mLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveCamera(mLatLng, DEFAULT_ZOOM, "MY LOCATION");
                        } else {
                            Log.d(TAG, "onComplete: current location not found");
                            Toast.makeText(FinalSpace.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: security exception " + e.getMessage());
        }
    }

    public void currentLocation(View v){
        mGps.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){
                getDeviceLocation();
        }
    });
    }


    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
         mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(FinalSpace.this);
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("MY LOCATION")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
    }


}
