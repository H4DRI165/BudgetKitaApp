package com.example.budgetkitaapp.map.saveLocation;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.budgetkitaapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    LocationManager locationManager;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(this);

        // Initialize the FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Enable location button and set listener
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                getCurrentLocation();
                return false;
            }
        });

        // Get the last known location
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                });

        // Request location updates
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (bestProvider != null && locationManager.isProviderEnabled(bestProvider)) {
            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
        } else {
            // Display dialog prompting the user to enable GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("GPS Required");
            builder.setMessage("Please enable GPS to use this feature.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Open location settings
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Handle cancellation
                    Toast.makeText(getActivity(), "GPS is required to use this feature.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " KG " + latLng.longitude);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                googleMap.addMarker(markerOptions);
            }
        });

        // Call saveLocationFirebase method after the map has been loaded
        saveLocationFirebase();
    }

    @Override
    public void onLocationChanged(Location location) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Get the current location using FusedLocationProviderClient
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to get current location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveLocationFirebase() {
        if (googleMap != null) {
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(latLng.latitude + " KG " + latLng.longitude);
                    googleMap.clear();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                    googleMap.addMarker(markerOptions);
                }
            });
        }
    }
}
