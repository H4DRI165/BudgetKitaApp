package com.example.budgetkitaapp.map.AddLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.map.listLocation.map;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddLocation extends AppCompatActivity {

    Button saveLocation;
    EditText locationName, locationDetail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        getSupportActionBar().setTitle("Map");
        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapView1, fragment).commit();

        // Firebase authentication to identify the current user
        mAuth = FirebaseAuth.getInstance();

        initializeVariables();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeVariables() {
        saveLocation = findViewById(R.id.saveLocation);
        locationDetail = findViewById(R.id.locationDetail);
        locationName = findViewById(R.id.locationName);

        saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationNameValue = locationName.getText().toString().trim();
                String locationDetailValue = locationDetail.getText().toString().trim();

                // Capitalize the first letter of locationNameValue
                if (!locationNameValue.isEmpty()) {
                    String firstLetter = locationNameValue.substring(0, 1);
                    locationNameValue = firstLetter.toUpperCase() + locationNameValue.substring(1);
                }

                // Capitalize the first letter of locationDetailValue
                if (!locationDetailValue.isEmpty()) {
                    String firstLetter = locationDetailValue.substring(0, 1);
                    locationDetailValue = firstLetter.toUpperCase() + locationDetailValue.substring(1);
                }

                MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView1);
                if (mapFragment != null) {
                    GoogleMap googleMap = mapFragment.getGoogleMap();
                    if (googleMap != null) {
                        LatLng currentMarker = googleMap.getCameraPosition().target;
                        double latitude = currentMarker.latitude;
                        double longitude = currentMarker.longitude;

                        // Create a new Location object with the data
                        userLocation location = new userLocation(locationNameValue, locationDetailValue, latitude, longitude);

                        // Get the Firebase database reference for the current user's location
                        DatabaseReference locationRef = FirebaseDatabase.getInstance()
                                .getReference("Accounts")
                                .child(mAuth.getCurrentUser().getUid())
                                .child("Location");

                        // Generate a new unique key for the location entry
                        String locationKey = locationRef.push().getKey();

                        // Save the location object to Firebase
                        locationRef.child(locationKey).setValue(location)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(AddLocation.this, "Location saved successfully", Toast.LENGTH_SHORT).show();
                                        finish(); // Finish the activity after saving the location
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddLocation.this, "Failed to save location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(AddLocation.this, map.class));
    }
}

