package com.example.budgetkitaapp.map.editLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.map.saveLocation.userLocation;
import com.example.budgetkitaapp.map.viewLocation.viewLocation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditLocation extends AppCompatActivity {

    Button saveLocation;
    EditText locationName, locationDetail;
    private FirebaseAuth mAuth;
    private String receivedLocationName, receivedLocationDetail, locationID;
    private double receivedLatitude, receivedLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);
        getSupportActionBar().setTitle("Edit Map");
        Fragment fragment = new EditLocationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapView2, fragment).commit();

        // Firebase authentication to identify the current user
        mAuth = FirebaseAuth.getInstance();

        // Retrieve the values from the Intent
        receivedLocationName = getIntent().getStringExtra("locationName");
        receivedLocationDetail = getIntent().getStringExtra("locationDetail");
        locationID = getIntent().getStringExtra("locationID");
        receivedLatitude = getIntent().getDoubleExtra("latitude", 0.0);
        receivedLongitude = getIntent().getDoubleExtra("longitude", 0.0);

        initializeVariables();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeVariables() {
        saveLocation = findViewById(R.id.saveLocation);
        locationDetail = findViewById(R.id.locationDetail);
        locationName = findViewById(R.id.locationName);

        // Set the received location name and detail to the EditText fields
        locationName.setText(receivedLocationName);
        locationDetail.setText(receivedLocationDetail);

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

                EditLocationFragment mapFragment = (EditLocationFragment) getSupportFragmentManager().findFragmentById(R.id.mapView2);
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

                        // Update the location in Firebase using the locationID
                        String finalLocationNameValue = locationNameValue;
                        String finalLocationDetailValue = locationDetailValue;
                        locationRef.child(locationID).setValue(location)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(EditLocation.this, "Location saved successfully", Toast.LENGTH_SHORT).show();

                                        // Inside saveLocation button's onClick() method in EditLocation activity
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("updatedLocationName", finalLocationNameValue);
                                        resultIntent.putExtra("updatedLocationDetail", finalLocationDetailValue);

                                        setResult(RESULT_OK, resultIntent);
                                        finish(); // Finish the EditLocation activity and return to viewLocation activity

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditLocation.this, "Failed to save location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(EditLocation.this, viewLocation.class);
        intent.putExtra("updatedLocationName", receivedLocationName);
        intent.putExtra("updatedLocationDetail", receivedLocationDetail);
        intent.putExtra("latitude", receivedLatitude);
        intent.putExtra("locationID", locationID);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}




