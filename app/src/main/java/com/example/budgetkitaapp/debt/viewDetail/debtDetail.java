package com.example.budgetkitaapp.debt.viewDetail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.debt.addDebt.AddDebt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class debtDetail extends AppCompatActivity {

    TextView debtAmount, debtDate, debtComplete;
    Button doneButton;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE_DONE_UPDATE_DEBT = 1010;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DONE_UPDATE_DEBT && resultCode == Activity.RESULT_OK) {
            // Handle the updated data from the resultIntent
            String updatedDebtName = data.getStringExtra("updatedDebtName");
            String updatedDebtAmount = data.getStringExtra("updatedDebtAmount");
            String updatedDebtDate = data.getStringExtra("updatedDebtDate");


            // Update your UI or perform other actions with the updated data
            setTitle(updatedDebtName);
            debtDate.setText(updatedDebtDate);
            debtAmount.setText(updatedDebtAmount);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_detail);
        setTitle("");


        //Firebase authentication to identify user
        mAuth = FirebaseAuth.getInstance();

        //Assign variable
        assignVariable();

        //Get the intent value from recyclerview in DebtFragment
        getIntentFromList();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void assignVariable(){

        debtAmount = findViewById(R.id.tvDebtValue);
        debtDate = findViewById(R.id.tvDebtDate);
        debtComplete = findViewById(R.id.debtPaidDate);
        doneButton = findViewById(R.id.doneButton);

    }

    private void getIntentFromList() {

        // Retrieve the authenticated user's ID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not authenticated, handle accordingly
            return;
        }

        String userID = currentUser.getUid();

        // Retrieve the entry, date, debtAmount, debtId from intent DebtAdapter
        Intent intent = getIntent();

        String entry = intent.getStringExtra("entry");
        String date = intent.getStringExtra("date");
        String datePaid = intent.getStringExtra("datePaid");
        String amount = intent.getStringExtra("debtAmount");

        setTitle(entry);
        debtAmount.setText("RM" + amount);
        debtDate.setText("Date: " +date);
        debtComplete.setText("Date Paid: " + datePaid);

        if(datePaid.equals("-")){
            // DatePaid is "-", so enable the "Done" button
            doneButton.setEnabled(true);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    debtDone();
                }
            });
        } else {
            // DatePaid is not "-", so disable the "Done" button
            doneButton.setEnabled(false);
            doneButton.setOnClickListener(null);
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
                editDebt();
                return true;
            }
        });

        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle delete item click here
                deleteDebt();
                return true;
            }
        });
        return true;
    }

    private void editDebt() {

        // Retrieve the entry, date, debtAmount, debtId from intent DebtAdapter
        Intent intent = getIntent();
        String entry = intent.getStringExtra("entry");
        String date = intent.getStringExtra("date");
        String amount = intent.getStringExtra("debtAmount");
        String debtId = intent.getStringExtra("debtId");

        Intent edit = new Intent(this, AddDebt.class);
        edit.putExtra("entry", entry);
        edit.putExtra("date", date);
        edit.putExtra("amount", amount);
        edit.putExtra("debtId", debtId);

        startActivityForResult(edit, 1010);
    }

    private void deleteDebt() {
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

        //Perform deletion based on debtId received from DebtAdapter
        String debtId = intent.getStringExtra("debtId");

        DatabaseReference debtRef = FirebaseDatabase.getInstance().getReference()
                .child("Accounts")
                .child(userID)
                .child("Debt")
                .child(debtId);
        debtRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(debtDetail.this, "Debt entry deleted successfully", Toast.LENGTH_SHORT).show();
                    // Finish the activity or handle the deletion completion as needed
                } else {
                    Toast.makeText(debtDetail.this, "Failed to delete debt entry", Toast.LENGTH_SHORT).show();
                }
            }
        });

        onBackPressed();
    }

    //Display the arrow on top to go back to previous activity
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // Call onBackPressed() to navigate back
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void debtDone() {
        // Retrieve the debtId from the intent
        Intent intent = getIntent();
        String debtId = intent.getStringExtra("debtId");

        if (debtId != null) {
            // Create an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to mark this debt as paid?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Get a reference to the Firebase database
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    // Build a reference to the specific debt node based on debtId
                    DatabaseReference debtReference = databaseReference
                            .child("Accounts")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Debt")
                            .child(debtId);

                    // Get the current date and format it as needed
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    String currentDate = dateFormat.format(new Date());

                    // Update debtDatePaid and debtStatus
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("debtDatePaid", currentDate);
                    updateData.put("debtStatus", "Paid");

                    debtReference.updateChildren(updateData, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                // Handle the error, e.g., show a toast message
                                Toast.makeText(getApplicationContext(), "Failed to update debt", Toast.LENGTH_SHORT).show();
                            } else {
                                // Successfully updated the debt
                                Toast.makeText(getApplicationContext(), "Debt marked as paid", Toast.LENGTH_SHORT).show();
                                // Optionally, you can finish this activity or perform any other necessary actions
                                onBackPressed();
                            }
                        }
                    });
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked "No," do nothing or handle accordingly
                    dialog.dismiss();
                }
            });

            // Show the AlertDialog
            builder.create().show();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}