package com.example.budgetkitaapp.transaction.viewTransaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.transaction.EditTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransactionDetail extends AppCompatActivity {

    TextView tvEntry, tvNote, tvCategory, tvTotal, tvDate, tvLocation, txtLocation;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        getSupportActionBar().setTitle("Transaction Detail");
        //Enable arrow icon at top to go back to previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase authentication to identify user
        mAuth = FirebaseAuth.getInstance();

        getIntentFromList();

    }


    private void getIntentFromList() {

        tvEntry = findViewById(R.id.getEntry);
        tvNote = findViewById(R.id.getNote);
        tvTotal = findViewById(R.id.getTotal);
        tvDate = findViewById(R.id.getDate);
        tvCategory = findViewById(R.id.getCategory);
        tvLocation = findViewById(R.id.getLocation);
        txtLocation = findViewById(R.id.locationLabelTextView);

        // Retrieve the authenticated user's ID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not authenticated, handle accordingly
            return;
        }
        String userID = currentUser.getUid();

        // Retrieve the transaction details, incomeId, and expenseId from the intent
        Intent intent = getIntent();
        String entry = intent.getStringExtra("entry");
        String date = intent.getStringExtra("date");
        String incomeValue = intent.getStringExtra("incomeValue");
        String expenseValue = intent.getStringExtra("expenseValue");
        String incomeId = intent.getStringExtra("incomeId");
        String expenseId = intent.getStringExtra("expenseId");

        tvEntry.setText(entry);
        tvDate.setText(date);

        if (entry.equals("Income")) {
            tvTotal.setText("RM "+incomeValue);

            // Retrieve incomeCat and incomeName based on incomeID
            DatabaseReference incomeRef = FirebaseDatabase.getInstance().getReference()
                    .child("Accounts")
                    .child(userID) // Use the authenticated user's ID
                    .child("Income")
                    .child(incomeId);

            incomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String incomeCat = dataSnapshot.child("incomeCat").getValue(String.class);
                        String incomeName = dataSnapshot.child("incomeName").getValue(String.class);

                        tvCategory.setText(incomeCat);
                        tvNote.setText(incomeName);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error, if any
                }
            });
        } else {
            tvTotal.setText("RM "+expenseValue);

            tvLocation.setVisibility(View.VISIBLE);
            txtLocation.setVisibility(View.VISIBLE);

            // Retrieve incomeCat and incomeName based on incomeID
            DatabaseReference expenseRef = FirebaseDatabase.getInstance().getReference()
                    .child("Accounts")
                    .child(userID) // Use the authenticated user's ID
                    .child("Expense")
                    .child(expenseId);

            expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String expenseCat = dataSnapshot.child("expenseCat").getValue(String.class);
                        String expenseName = dataSnapshot.child("expenseName").getValue(String.class);
                        String expenseLocation = dataSnapshot.child("expenseLocation").getValue(String.class);

                        tvCategory.setText(expenseCat);
                        tvNote.setText(expenseName);
                        tvLocation.setText(expenseLocation);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error, if any
                }
            });
        }
    }

    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_edit, menu);
        MenuItem editItem = menu.findItem(R.id.edit);
        MenuItem deleteItem = menu.findItem(R.id.delete);

        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editEntry();
                return true;
            }
        });

        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle delete item click here
                deleteEntry();
                return true;
            }
        });
        return true;
    }

    private void deleteEntry() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performDelete();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void performDelete() {
        // Retrieve the authenticated user's ID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not authenticated, handle accordingly
            return;
        }
        String userID = currentUser.getUid();

        // Retrieve the transaction details, incomeId, and expenseId from the intent
        Intent intent = getIntent();
        String entry = intent.getStringExtra("entry");
        String incomeId = intent.getStringExtra("incomeId");
        String expenseId = intent.getStringExtra("expenseId");

        if (entry.equals("Income")) {
            // Delete the income entry from Firebase
            DatabaseReference incomeRef = FirebaseDatabase.getInstance().getReference()
                    .child("Accounts")
                    .child(userID)
                    .child("Income")
                    .child(incomeId);
            incomeRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(TransactionDetail.this, "Income entry deleted successfully", Toast.LENGTH_SHORT).show();
                        // Finish the activity or handle the deletion completion as needed
                    } else {
                        Toast.makeText(TransactionDetail.this, "Failed to delete income entry", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Delete the expense entry from Firebase
            DatabaseReference expenseRef = FirebaseDatabase.getInstance().getReference()
                    .child("Accounts")
                    .child(userID)
                    .child("Expense")
                    .child(expenseId);
            expenseRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(TransactionDetail.this, "Expense entry deleted successfully", Toast.LENGTH_SHORT).show();
                        // Finish the activity or handle the deletion completion as needed
                    } else {
                        Toast.makeText(TransactionDetail.this, "Failed to delete expense entry", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        onBackPressed();
    }

    private void editEntry() {

        //Pass current data in TransactionDetail.java to EditTransactionFragment.java
        Intent intent = getIntent();
        String entry = intent.getStringExtra("entry");
        String incomeId = intent.getStringExtra("incomeId");
        String expenseId = intent.getStringExtra("expenseId");

        Intent edit = new Intent(this, EditTransaction.class);
        edit.putExtra("entry", entry);
        edit.putExtra("incomeId", incomeId);
        edit.putExtra("expenseId", expenseId);
        startActivity(edit);
    }



    //Display the arrow on top to go back to previous activity
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
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
    }
}