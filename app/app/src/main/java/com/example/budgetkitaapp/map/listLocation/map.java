package com.example.budgetkitaapp.map.listLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.budgetkitaapp.HomeActivity;
import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.adapter.UserLocationAdapter;
import com.example.budgetkitaapp.map.AddLocation.AddLocation;
import com.example.budgetkitaapp.map.AddLocation.userLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class map extends AppCompatActivity implements UserLocationAdapter.LocationClickListener {

    Button addLocation;
    RecyclerView rvLocation;
    private FirebaseAuth mAuth;
    private DatabaseReference locationRef;
    private UserLocationAdapter locationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle("Saved Location");

        // Firebase authentication to identify the current user
        mAuth = FirebaseAuth.getInstance();

        addLocation = findViewById(R.id.btnLocation);
        rvLocation = findViewById(R.id.rvLocation);

        String source = getIntent().getStringExtra("source");

        locationAdapter = new UserLocationAdapter(this);
        locationAdapter.setSource(source);
        locationAdapter.setLocationClickListener(this);
        rvLocation.setAdapter(locationAdapter);

        // Firebase database reference for the current user's locations
        locationRef = FirebaseDatabase.getInstance()
                .getReference("Accounts")
                .child(mAuth.getCurrentUser().getUid())
                .child("Location");

        // Configure RecyclerView and its adapter
        rvLocation.setLayoutManager(new LinearLayoutManager(this));

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent location = new Intent(map.this, AddLocation.class);
                startActivity(location);
            }
        });

        // Enable arrow icon at top to go back to the previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fetch locations from Firebase
        fetchLocations();
    }

    private void fetchLocations() {
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<userLocation> locationList = new ArrayList<>();
                for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                    userLocation location = locationSnapshot.getValue(userLocation.class);
                    if (location != null) {
                        // Set the locationID manually since it's not fetched automatically
                        location.setLocationID(locationSnapshot.getKey());
                        locationList.add(location);
                    }
                }
                locationAdapter.setLocationList(locationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(map.this, "Failed to fetch locations: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationClick(String locationName) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("locationName", locationName);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


    //Display the arrow on top to go back to previous activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        String source = getIntent().getStringExtra("source");
        if (source != null) {
            if (source.equals("expenseFragment")) {
                // Navigate back to ExpenseFragment
                finish();
            } else if (source.equals("otherFragment")) {
                // Navigate back to MapFragment
                finish();
            } else {
                // Navigate back to the activity that hosts OtherFragment
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
        } else {
            // Navigate back to the activity that hosts OtherFragment
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        //super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchLocations(); // Fetch and update the value in case there update
    }

}
