package com.example.bandungzoochatbot;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.bandungzoochatbot.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Button btnChat;
    List<Fasilitas> fasilitasList;
    List<Koleksi> koleksiList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefFasilitas = database.getReference("Fasilitas");
    DatabaseReference mtRefKoleksi = database.getReference("Koleksi");

    //maps
    double latitude;
    double longitude;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnChat = findViewById(R.id.btnChat);

        fasilitasList = new ArrayList<>();
        koleksiList = new ArrayList<>();

        myRefFasilitas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fasilitasList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Fasilitas fasilitas = data.getValue(Fasilitas.class);
                    fasilitasList.add(fasilitas);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d("message", "Failed to read value.", error.toException());
            }
        });

        mtRefKoleksi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Koleksi fasilitas = data.getValue(Koleksi.class);
                    String namafasilitas = data.child("nama").getValue(String.class);
                    fasilitasList.add(fasilitas);

                    Log.i("nama_fasilitas", namafasilitas);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d("message", "Failed to read value.", error.toException());
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });


    }

   /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng facility_coor = null;

        //Add marker to the map
        myRefFasilitas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fasilitasList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Fasilitas fasilitas = data.getValue(Fasilitas.class);
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .position(new LatLng(fasilitas.getLatitude(), fasilitas.getLongitude())))
                            .setTitle(fasilitas.getNama());
                    fasilitasList.add(fasilitas);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d("message", "Failed to read value.", error.toException());
            }
        });

        mtRefKoleksi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fasilitasList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Koleksi fasilitas = data.getValue(Koleksi.class);
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .position(new LatLng(fasilitas.getLatitude(), fasilitas.getLongitude())))
                            .setTitle(fasilitas.getNama());
                    koleksiList.add(fasilitas);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d("message", "Failed to read value.", error.toException());
            }
        });
        // If triggered by chat


        if(getIntent().hasExtra("ID_FACILITY")){
            String facility = getIntent().getStringExtra("ID_FASILITY");
            //TODO: get latitude and longitude from the facility
            for(int i=0; i< fasilitasList.size() ;i++){
                if (String.valueOf(fasilitasList.get(i).getId()) == facility){
                    facility_coor = new LatLng(fasilitasList.get(i).getLatitude(), fasilitasList.get(i).getLongitude());
                    break;
                }
            }
            Log.i("ID_FACILITY", facility);
        }
        else{
            //get user coordinate to move camera
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                });
                facility_coor = new LatLng(latitude, longitude);
                Log.i("POSISI_USER", "PAKAI GPS");
            }
            else{
                facility_coor = new LatLng(fasilitasList.get(0).getLatitude(), fasilitasList.get(0).getLongitude());
                Log.i("POSISI_USER", "DEFAULT");

            }
        }

        // TODO: delete later : Facility : Add a marker in Bandung Zoo
//        for(int i=0; i< fasilitasList.size() ;i++){
//            Log.i("fasilitas map", fasilitasList.get(i).getNama());
//            mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//                    .position(new LatLng(fasilitasList.get(i).getLatitude(), fasilitasList.get(i).getLongitude())))
//                    .setTitle(fasilitasList.get(i).getNama());
//            Log.i("nama_fasilitas", String.valueOf(fasilitasList.get(i).getLatitude()));
//        }

        // Collections : Add a marker in Bandung Zoo
        for(int i=0; i< koleksiList.size() ;i++){
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .position(new LatLng(koleksiList.get(i).getLatitude(), koleksiList.get(i).getLongitude())))
                    .setTitle(koleksiList.get(i).getNama());
            Log.i("nama_fasilitas", String.valueOf(koleksiList.get(i).getLatitude()));
        }

        Log.i("MAP_CAMERA_POSITION", String.valueOf(facility_coor));

        UiSettings uiSettings = googleMap.getUiSettings();
        facility_coor = new LatLng(fasilitasList.get(0).getLatitude(), fasilitasList.get(0).getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(facility_coor));
        mMap.setMinZoomPreference(15.0f);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

    }

}