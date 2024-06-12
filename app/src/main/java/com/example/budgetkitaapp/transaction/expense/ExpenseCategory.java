package com.example.budgetkitaapp.transaction.expense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.budgetkitaapp.R;

public class ExpenseCategory extends AppCompatActivity {

    CardView eDebt,eDonation,eOperation,ePersonal,eSaving, eOther, eFood, eFuel, eHome, eShopping, eEducation;
    CardView eTransport, ePhone, eBill, eCar, eMoto, eBaby, eTax;
    String eCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_category);
        setTitle("Expense Category");

        //To assign variable
        variableAssign();

        //Set onclick listener to CardView
        eDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Debt Payment";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        eDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Donation";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        eOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Operation Cost";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        ePersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Personal";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        eSaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Saving";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        eOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Other Expense";
                returnCategory();
            }
        });








        //Set onclick listener to CardView
        eFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Food";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Fuel";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Home";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Shopping";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Transportation";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        ePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Phone";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Bill";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Education";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Car";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eMoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Motorcycle";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Baby";
                returnCategory();
            }
        });
        //Set onclick listener to CardView
        eTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eCategory = "Tax";
                returnCategory();
            }
        });


        //Enable arrow icon at top to go back to previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void variableAssign() {

        eDebt = findViewById(R.id.debtPayment);
        eDonation = findViewById(R.id.donation);
        eOperation = findViewById(R.id.operation);
        ePersonal = findViewById(R.id.personal);
        eSaving = findViewById(R.id.saving);
        eOther = findViewById(R.id.otherExpense);
        eFood = findViewById(R.id.food);
        eFuel = findViewById(R.id.fuel);
        eHome = findViewById(R.id.home);
        eShopping = findViewById(R.id.shopping);
        eTransport = findViewById(R.id.transportation);
        ePhone = findViewById(R.id.phone);
        eBill = findViewById(R.id.bill);
        eEducation = findViewById(R.id.education);
        eCar = findViewById(R.id.car);
        eMoto = findViewById(R.id.moto);
        eBaby = findViewById(R.id.baby);
        eTax = findViewById(R.id.tax);

    }

    private void returnCategory() {
        Intent intent = new Intent();
        intent.putExtra("category", eCategory);
        setResult(Activity.RESULT_OK, intent);
        finish();
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

    public boolean onCreateOptionsMenu (Menu menu){
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}