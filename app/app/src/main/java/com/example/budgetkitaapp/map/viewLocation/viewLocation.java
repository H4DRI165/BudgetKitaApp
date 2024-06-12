package com.example.budgetkitaapp.map.viewLocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.map.editLocation.EditLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class viewLocation extends AppCompatActivity {

    TextView locationName, locationDetail;
    Button goLocation, selectLocation;
    private FirebaseAuth mAuth;
    private double receivedLatitude, receivedLongitude;
    private DatabaseReference locationRef;
    private String receivedLocationName, receivedLocationDetail, locationID, source;
    private static final int EDIT_LOCATION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);
        getSupportActionBar().setTitle("View Location");

        // Firebase authentication to identify the current user
        mAuth = FirebaseAuth.getInstance();

        // Retrieve location details from the intent
        receivedLocationName = getIntent().getStringExtra("locationName");
        receivedLocationDetail = getIntent().getStringExtra("locationDetail");
        receivedLatitude = getIntent().getDoubleExtra("latitude", 0.0);
        receivedLongitude = getIntent().getDoubleExtra("longitude", 0.0);
        locationID = getIntent().getStringExtra("locationID");
        source = getIntent().getStringExtra("source");

        // Initialize variables and set location details
        initializeVariables();

        if (source.equals("expenseFragment")) {
            goLocation.setVisibility(View.GONE);
            selectLocation.setVisibility(View.VISIBLE);

        }else if(source.equals("otherFragment")){
            goLocation.setVisibility(View.VISIBLE);
            selectLocation.setVisibility(View.GONE);
        }

        locationName.setText(capitalizeFirstWord(receivedLocationName));
        locationDetail.setText("Detail: " + capitalizeFirstWord(receivedLocationDetail));

        // Create a bundle to pass latitude and longitude to the fragment
        Bundle args = new Bundle();
        args.putDouble("latitude", receivedLatitude);
        args.putDouble("longitude", receivedLongitude);

        // Create an instance of MapDetailFragment and set the arguments
        MapDetailFragment fragment = new MapDetailFragment();
        fragment.setArguments(args);

        // Replace the fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.mapView, fragment).commit();

        goLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleMap();
            }
        });

        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation(receivedLocationName);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();
        locationRef = FirebaseDatabase.getInstance().getReference()
                .child("Accounts")
                .child(userID)
                .child("Location");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crud_location, menu);

        MenuItem editItem = menu.findItem(R.id.editLocation);
        MenuItem deleteItem = menu.findItem(R.id.deleteLocation);

        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editLocation();
                return true;
            }
        });

        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle delete item click here
                deleteLocation();
                return true;
            }
        });
        return true;
    }

    private void editLocation() {

        // Create an intent to start the EditLocation activity
        Intent intent = new Intent(viewLocation.this, EditLocation.class);

        // Add the receivedLocationName, receivedLocationDetail, and locationID as extras
        intent.putExtra("locationName", receivedLocationName);
        intent.putExtra("locationDetail", receivedLocationDetail);
        intent.putExtra("locationID", locationID);
        intent.putExtra("longitude", receivedLongitude);
        intent.putExtra("latitude", receivedLatitude);

        // Start the EditLocation activity
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                receivedLocationName = data.getStringExtra("updatedLocationName");
                receivedLocationDetail = data.getStringExtra("updatedLocationDetail");
                receivedLatitude = data.getDoubleExtra("latitude", 0.0);
                receivedLongitude = data.getDoubleExtra("longitude", 0.0);

                // Update the views only if changes were made
                locationName.setText(capitalizeFirstWord(receivedLocationName));
                locationDetail.setText("Detail: " + capitalizeFirstWord(receivedLocationDetail));
            }
        }
    }

    private void deleteLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Location");
        builder.setMessage("Are you sure you want to delete this location?");

        // Add positive button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the Delete button, proceed with deletion
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null && locationID != null) {
                    DatabaseReference locationToDeleteRef = locationRef.child(locationID);
                    locationToDeleteRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Location deleted successfully
                            Toast.makeText(viewLocation.this, "Location deleted from Firebase", Toast.LENGTH_SHORT).show();
                            finish(); // Optionally, close the activity after deletion
                        } else {
                            // Failed to delete location
                            Toast.makeText(viewLocation.this, "Failed to delete location from Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Add negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the Cancel button, do nothing
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectLocation(String receivedLocationName) {

        String returnLocationName = locationName.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("locationName", returnLocationName);
        setResult(Activity.RESULT_OK, intent);

        // Finish the current activity
        finish();

    }


    private void openGoogleMap() {
        String uri = "geo:" + receivedLatitude + "," + receivedLongitude + "?q=" + receivedLatitude + "," + receivedLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Google Maps app is not installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private String capitalizeFirstWord(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                char firstChar = word.charAt(0);
                if (Character.isLowerCase(firstChar)) {
                    sb.append(Character.toUpperCase(firstChar)).append(word.substring(1));
                } else {
                    sb.append(word);
                }
            }
            sb.append(" ");
        }

        return sb.toString().trim();
    }


    private void initializeVariables() {
        locationDetail = findViewById(R.id.locationDetail);
        locationName = findViewById(R.id.locationName);
        goLocation = findViewById(R.id.goLocation);
        selectLocation = findViewById(R.id.selectLocation);
        locationDetail.setFocusable(false);
        locationName.setFocusable(false);
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
}
