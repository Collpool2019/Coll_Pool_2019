package com.example.cpgui;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
//import androidx.fragment.app.Fragment;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.model.Marker;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.android.libraries.places.api.model.*;
import java.sql.Driver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FinalSpace extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
   // private LatLng sydney = new LatLng(-8.579892, 116.095239);
    private SupportMapFragment mapFragment;
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 17f;
    private LatLng mLatLng;
    private LatLng CustomerPickup;
    private Location currentLocation;
    private ImageView mGps;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase ;
    private FirebaseDatabase firebaseDatabase_Driver ;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference_1;
    private DatabaseReference databaseReference_Driver;
    private DatabaseReference driverLocationref;
    private FirebaseApp secondary;
    private GeoFire geoFire;
    private String Customeruserid;
    private Button CallCar;
    private int radius=1;
    private boolean driverfound=false;
    private String driverfoundId;
    Marker DriverMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_space);
        Log.d(TAG, "OnCreate");
        mGps =  findViewById((R.id.ic_gps));
        firebaseAuth=FirebaseAuth.getInstance();
        Customeruserid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance("https://collpool2019-2fe22.firebaseio.com/");
        databaseReference=firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
        databaseReference_1=firebaseDatabase.getReference().child("Customer Requests");
        geoFire=new GeoFire(databaseReference);
        CallCar=findViewById(R.id.call_car);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:425559792661:android:4ddd0c33a98426b5b1021c")
                .setApiKey("AIzaSyAf3Af_S7SHEyMJRbdrmh50USH1B3Xomwc")
                .setDatabaseUrl("https://coll-pool-driver.firebaseio.com")
                .build();
        FirebaseApp.initializeApp(this /* Context */, options,"secondary");
         secondary = FirebaseApp.getInstance("secondary");
        firebaseDatabase_Driver = FirebaseDatabase.getInstance(secondary);
        databaseReference_Driver=firebaseDatabase_Driver.getReference();
        driverLocationref=firebaseDatabase_Driver.getReference().child("Driver Working");



        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),"AIzaSyDplZLeZYlKUCYmYY_tLOSAcfz4AauzssU");
        }
        PlacesClient placesClient = Places.createClient(this);
        setupAutoCompleteFragment();
        getLocationPermission();
    }


    public void RequestRide(View v)
    {
        CallCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getDeviceLocation();
               GeoFire geoFire_1=new GeoFire(databaseReference_1);
               geoFire_1.setLocation(Customeruserid,new GeoLocation(currentLocation.getLatitude(),currentLocation.getLongitude()), new GeoFire.CompletionListener() {
                   @Override
                   public void onComplete(String key, DatabaseError error) {
                       if (error != null) {
                           Log.d(TAG, "RequestRide: Logging Location Unsuccessful");
                           System.err.println("There was an error saving the location to GeoFire: " + error);
                       } else {
                           Log.d(TAG, "RequestRide: Logging Location Successful");
                           System.out.println("Location saved on server successfully!");
                       }
                   }
               });
               CustomerPickup=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
               mMap.addMarker(new MarkerOptions().position(CustomerPickup).title("PICKUP LOCATION"));

               CallCar.setText("Getting Your Ride....");
               GetClosestDriver();
            }
        });
    }



private void GetClosestDriver() {
    databaseReference_Driver.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild("Driver Availability")) {
                Toast.makeText(FinalSpace.this, "FOUND", Toast.LENGTH_SHORT).show();
                databaseReference_Driver=firebaseDatabase_Driver.getReference().child("Driver Availability");
                GeoFire geoFire_driver=new GeoFire(databaseReference_Driver);
                GeoQuery geoQuery_driver=geoFire_driver.queryAtLocation(new GeoLocation(currentLocation.getLatitude(),currentLocation.getLongitude()),radius);
                geoQuery_driver.removeAllListeners();
                geoQuery_driver.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        if(!driverfound)
                        {

                            driverfound=true;
                            driverfoundId=key;

                            databaseReference_Driver = firebaseDatabase_Driver.getReference().child("On Going Driver").child(driverfoundId);
                            HashMap driversMap = new HashMap();
                            driversMap.put("CustomerRideID",Customeruserid);
                          //  driversMap.put("Driver Id",driverfoundId);
                            GeoFire geoFire_1=new GeoFire(databaseReference_Driver);
                            geoFire_1.setLocation(Customeruserid,new GeoLocation(currentLocation.getLatitude(),currentLocation.getLongitude()), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    if (error != null) {
                                        Log.d(TAG, " After Mapping Driver RequestRide: Logging Location Unsuccessful");
                                        System.err.println("There was an error saving the location to GeoFire: " + error);
                                    } else {
                                        Log.d(TAG, " After Mapping Driver RequestRide: Logging Location Successful");
                                        System.out.println("Location saved on server successfully!");
                                    }
                                }
                            });
                            databaseReference_Driver.updateChildren(driversMap);
                            GettingDriverLocation();
                            Toast.makeText(FinalSpace.this, driverfoundId, Toast.LENGTH_SHORT).show();
                            CallCar.setText("Looking for Driver Location.....");
                        }

                    }

                    @Override
                    public void onKeyExited(String key) {

                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {
                        if(!driverfound)
                        {
                            radius=radius+1;
                            GetClosestDriver();
                        }

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });

            }
            else
            {
                Toast.makeText(FinalSpace.this, "NOT FOUND", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}




   public void GettingDriverLocation() {
        driverLocationref.child(driverfoundId).child("l")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            List<Object>driverLocationMap=(List<Object>)dataSnapshot.getValue();
                            double LocationLat=0;
                            double LocationLng=0;
                            CallCar.setText("Driver Found");
                            if(driverLocationMap.get(0)!=null)
                            {
                                LocationLat=Double.parseDouble(driverLocationMap.get(0).toString());
                            }
                            if(driverLocationMap.get(1)!=null)
                            {
                                LocationLng=Double.parseDouble(driverLocationMap.get(1).toString());
                            }
                            LatLng DriverLocation=new LatLng(LocationLat,LocationLng);
                            if(DriverMarker!=null)
                            {
                                DriverMarker.remove();
                            }

                            Location location1=new Location("");
                            location1.setLatitude(CustomerPickup.latitude);
                            location1.setLongitude(CustomerPickup.longitude);

                            Location location2=new Location("");
                            location2.setLatitude(DriverLocation.latitude);
                            location2.setLongitude(DriverLocation.longitude);

                            float Distance=location1.distanceTo(location2);
                            CallCar.setText("Driver Found :"+String.valueOf(Distance));

                            DriverMarker=mMap.addMarker(new MarkerOptions().position(DriverLocation).title("YOUR RIVER IS HERE "));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
            public void onError(@NonNull Status status) {
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
                             currentLocation = (Location) task.getResult();
                            mLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            String userid =firebaseAuth.getCurrentUser().getUid();
                            geoFire.setLocation(userid, new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), new GeoFire.CompletionListener() {
                                        @Override
                                        public void onComplete(String key, DatabaseError error) {
                                            if (error != null) {
                                                Log.d(TAG, "onComplete: geo fire location error");
                                                System.err.println("There was an error saving the location to GeoFire: " + error);
                                            } else {
                                                Log.d(TAG, "onComplete: geo fire location successful");
                                                System.out.println("Location saved on server successfully!");
                                            }
                                        }
                                    });
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
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(options);
        if (!title.equals("MY LOCATION")) {
            options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
    }


}
