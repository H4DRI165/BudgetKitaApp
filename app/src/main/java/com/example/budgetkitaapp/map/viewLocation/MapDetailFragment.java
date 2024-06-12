package com.example.budgetkitaapp.map.viewLocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.budgetkitaapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapDetailFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng redMarkerLatLng;
    private Polyline userToMarkerPolyline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_detail, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(this);

        // Initialize the FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Retrieve latitude and longitude from arguments
        Bundle args = getArguments();
        if (args != null) {
            double latitude = args.getDouble("latitude");
            double longitude = args.getDouble("longitude");
            redMarkerLatLng = new LatLng(latitude, longitude);
        }

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

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // Do nothing when the user clicks on the map
            }
        });

        // Check if red marker's LatLng is available
        if (redMarkerLatLng != null) {
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    // Set the red marker at the fixed location
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(redMarkerLatLng);
                    markerOptions.title(redMarkerLatLng.latitude + " KG " + redMarkerLatLng.longitude);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)); // Set marker color to red
                    googleMap.addMarker(markerOptions);

                    // Draw a line from user location to the red marker location
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    drawPolyline(userLatLng, redMarkerLatLng);
                                }
                            }
                        });
                    }

                    // Calculate bounds and zoom level
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(redMarkerLatLng);

                    // Add current user location to the builder
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    builder.include(userLatLng);

                                    LatLngBounds bounds = builder.build();
                                    int padding = 100; // Adjust padding as desired
                                    int zoomLevel = calculateZoomLevel(bounds);
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), zoomLevel - 1);

                                    googleMap.animateCamera(cu);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private int calculateZoomLevel(LatLngBounds bounds) {
        final int mapWidth = getResources().getDisplayMetrics().widthPixels;
        final int mapHeight = getResources().getDisplayMetrics().heightPixels;
        final int padding = (int) (mapWidth * 0.12); // Adjust padding as desired

        int zoomLevel = 1;
        do {
            bounds = new LatLngBounds.Builder()
                    .include(bounds.northeast)
                    .include(bounds.southwest)
                    .build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.moveCamera(cu);
            zoomLevel++;
        } while (googleMap.getProjection().getVisibleRegion().latLngBounds.contains(bounds.northeast) && zoomLevel < 21);

        return zoomLevel;
    }

    private void drawPolyline(LatLng startLatLng, LatLng endLatLng) {
        if (userToMarkerPolyline != null) {
            userToMarkerPolyline.remove();
        }

        userToMarkerPolyline = googleMap.addPolyline(new PolylineOptions()
                .add(startLatLng, endLatLng)
                .width(5)
                .color(Color.RED));
    }

    @Override
    public void onLocationChanged(Location location) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

        if (redMarkerLatLng != null) {
            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            drawPolyline(userLatLng, redMarkerLatLng);
        }
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
}

