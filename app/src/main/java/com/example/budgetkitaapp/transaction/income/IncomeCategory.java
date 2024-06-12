package com.example.budgetkitaapp.transaction.income;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.budgetkitaapp.R;

public class IncomeCategory extends AppCompatActivity {

    CardView iLoan,iSalary,iSale,iGrant,iOther,iCoupon;
    String iCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_category);
        setTitle("Income Category");

        //To assign variable
        variableAssign();

        //Set onclick listener to CardView
        iLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iCategory = "Loan";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        iSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iCategory = "Salary";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        iSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iCategory = "Sale";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        iGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iCategory = "Grant";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        iCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iCategory = "Coupon";
                returnCategory();
            }
        });

        //Set onclick listener to CardView
        iOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iCategory = "Other Income";
                returnCategory();
            }
        });

        //Enable arrow icon at top to go back to previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void variableAssign() {

        iLoan = findViewById(R.id.loan);
        iSalary = findViewById(R.id.salary);
        iSale = findViewById(R.id.sale);
        iGrant = findViewById(R.id.grant);
        iOther = findViewById(R.id.other);
        iCoupon = findViewById(R.id.coupon);

    }

    private void returnCategory() {
        Intent intent = new Intent();
        intent.putExtra("category", iCategory);
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