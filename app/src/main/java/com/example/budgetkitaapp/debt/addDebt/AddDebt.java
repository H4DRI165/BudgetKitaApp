package com.example.budgetkitaapp.debt.addDebt;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.debt.debtClass.Debt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AddDebt extends AppCompatActivity {

    private Button dZero, dOne, dTwo, dThree, dFour, dFive, dSix, dSeven, dEight, dNine;
    private Button dAdd, dMinus, dMultiply, dDivide, dEqual, dDecimal,dSave;
    private TextView dTotal, dName,dDate;
    private ImageView dDelete;

    private final char ADDITION = '+';
    private final char SUBTRACTION = '-';
    private final char MULTIPLICATION = '*';
    private final char DIVISION = '/';
    private final char EQU = 0;

    private double val1 = Double.NaN;
    private double val2;
    private char ACTION;

    private FirebaseAuth mAuth;
    private int decimal = 0;

    DatabaseReference debtReference, transactionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debt);
        setTitle("Debt");

        //Authenticate user and create unique key for incomeId
        authenticateUser();

        // Call assignVariable() and pass v as a parameter
        assignVariable();

        //Check if there intent from debtDetail.class
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("entry")) {

            String entry = intent.getStringExtra("entry");
            String date = intent.getStringExtra("date");
            String amount = intent.getStringExtra("amount");

            dName.setText(entry);
            dTotal.setText(amount);
            dDate.setText("Date: " + date);

        }else{
            //Call setCurrentDate() to get current date and set it to iDate
            setCurrentDate();

        }

        // Set click listeners for number buttons
        clickedNumber(dZero);
        clickedNumber(dOne);
        clickedNumber(dTwo);
        clickedNumber(dThree);
        clickedNumber(dFour);
        clickedNumber(dFive);
        clickedNumber(dSix);
        clickedNumber(dSeven);
        clickedNumber(dEight);
        clickedNumber(dNine);


        // Set click listeners for decimal buttons
        clickedDecimal(dDecimal);
        
        dAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOperation();
            }
        });

        dMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusOperation();
            }
        });

        dMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiplyOperation();
            }
        });

        dDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                divideOperation();
            }
        });
        
        dEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                equalOperation();
            }
        });
        
        dDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOperation();
            }
        });
        

        // Set an OnClickListener on the iDate TextView to launch the DatePickerDialog
        dDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open datepicker function for user to select their preferred date
                openDatePickerFunction();
            }
        });
        
        //To save income transaction
        dSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To save income into Firebase
                insertDebtData();
            }
        });
        
        // Enable arrow icon at top to go back to the previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void authenticateUser(){

        //Firebase authentication to identify user
        mAuth = FirebaseAuth.getInstance();

        //Only login user can access to their data
        debtReference = FirebaseDatabase.getInstance().getReference("Accounts").child(mAuth.getCurrentUser().getUid())
                .child("Debt");

        // Generate a new, unique transaction ID
        transactionReference = FirebaseDatabase.getInstance().getReference("Accounts").child(mAuth.getCurrentUser().getUid())
                .child("Debt").push();

        // Generate a new, unique transaction ID
        String transactionId = transactionReference.getKey();
    }

    private void assignVariable() {

        //Assign Variable
        dZero = findViewById(R.id.debt_0);
        dOne = findViewById(R.id.debt_1);
        dTwo = findViewById(R.id.debt_2);
        dThree = findViewById(R.id.debt_3);
        dFour = findViewById(R.id.debt_4);
        dFive = findViewById(R.id.debt_5);
        dSix = findViewById(R.id.debt_6);
        dSeven = findViewById(R.id.debt_7);
        dEight = findViewById(R.id.debt_8);
        dNine = findViewById(R.id.debt_9);
        dAdd = findViewById(R.id.debt_plus);
        dMinus = findViewById(R.id.debt_minus);
        dMultiply = findViewById(R.id.debt_multiply);
        dDivide = findViewById(R.id.debt_divide);
        dEqual = findViewById(R.id.debt_equal);
        dDecimal = findViewById(R.id.debt_decimal);
        dDelete = findViewById(R.id.ivDelete);
        dTotal = findViewById(R.id.debtText);
        dName = findViewById(R.id.debt_name);
        dDate = findViewById(R.id.debtDate);
        dSave = findViewById(R.id.debtSave);

    }

    private void setCurrentDate(){
        //Get current date and set it to iDate and format it as "yyyy/MM/dd"
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = dateFormat.format(currentDate);

        // Set the text of the iDate TextView to today's date
        dDate.setText("Date: " + dateString);
    }

    //Add the number into the iTotal based on xml value assigned in IncomeFragment.xml
    private void clickedNumber(Button button) {
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Button numberButton = (Button) view;
                //Get the digit value from income XML
                String digit = numberButton.getText().toString();
                dTotal.append(digit);
            }
        });
    }

    //Add the decimal into iTotal
    private void clickedDecimal(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = dTotal.getText().toString();

                if (decimal == 0 && input.isEmpty()) {
                    dTotal.setText("0.");
                    decimal = 1;
                    button.setEnabled(false);
                } else if (decimal == 0 && !input.isEmpty()) {
                    dTotal.setText(input + ".");
                    decimal = 1;
                    button.setEnabled(false);
                } else {
                    dTotal.setText(input + ".");
                }
            }
        });
    }
    
    private void addOperation(){

        String input = dTotal.getText().toString();
        if (input.equals("")) {
            Toast.makeText(AddDebt.this, "Cannot start with operation symbol", Toast.LENGTH_SHORT).show();
        } else {
            compute();
            ACTION = ADDITION;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            dTotal.setText(decimalFormat.format(val1) + "+");

            //set decimal to 0 and enable the decimal button
            decimal = 0;
            dDecimal.setEnabled(true);
        }
    }

    private void minusOperation(){

        String input = dTotal.getText().toString();
        if (input.equals("")) {
            Toast.makeText(AddDebt.this, "Cannot start with negative number", Toast.LENGTH_SHORT).show();
        } else {
            compute();
            ACTION = SUBTRACTION;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            dTotal.setText(decimalFormat.format(val1) + "-");

            //set decimal to 0 and enable the decimal button
            decimal = 0;
            dDecimal.setEnabled(true);
        }

    }

    private void multiplyOperation(){

        String input = dTotal.getText().toString();
        if (input.equals("")) {
            Toast.makeText(AddDebt.this, "Cannot start with operation symbol", Toast.LENGTH_SHORT).show();
        } else {
            compute();
            ACTION = MULTIPLICATION;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            dTotal.setText(decimalFormat.format(val1) + "x");

            //set decimal to 0 and enable the decimal button
            decimal = 0;
            dDecimal.setEnabled(true);
        }
    }

    private void divideOperation(){

        String input = dTotal.getText().toString();
        if (input.equals("")) {
            Toast.makeText(AddDebt.this, "Cannot start with operation symbol", Toast.LENGTH_SHORT).show();
        } else {
            compute();
            ACTION = DIVISION;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            dTotal.setText(decimalFormat.format(val1) + "÷");

            // Set decimal to 0 and enable the decimal button
            decimal = 0;
            dDecimal.setEnabled(true);
        }
    }
    
    private void equalOperation(){

        String input = dTotal.getText().toString();
        if (input.equals("")) {
            Toast.makeText(AddDebt.this, "Please enter a numbers", Toast.LENGTH_SHORT).show();
        } else {
            // Check if the input contains an operator (+, -, x, ÷)
            if (input.contains("+") || input.contains("-") || input.contains("x") || input.contains("÷")) {
                double result = 0;
                String operator = "";

                // Split the input into operands and operator
                String[] tokens;
                if (input.contains("+")) {
                    tokens = input.split("\\+");
                    operator = "+";
                } else if (input.contains("-")) {
                    tokens = input.split("-");
                    operator = "-";
                } else if (input.contains("x")) {
                    tokens = input.split("x");
                    operator = "x";
                } else {
                    tokens = input.split("÷");
                    operator = "÷";
                }

                if (tokens.length == 2) {
                    double operand1 = Double.parseDouble(tokens[0]);
                    double operand2 = Double.parseDouble(tokens[1]);
                    switch (operator) {
                        case "+":
                            result = operand1 + operand2;
                            break;
                        case "-":
                            result = operand1 - operand2;
                            break;
                        case "x":
                            result = operand1 * operand2;
                            break;
                        case "÷":
                            if (operand2 != 0) {
                                result = operand1 / operand2;
                            } else {
                                handleCalculationError();
                                return;
                            }
                            break;
                    }
                }
                // Set the result in the input field
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                dTotal.setText(decimalFormat.format(result));

                // Update val1 and val2 for subsequent calculations
                val1 = result;
                val2 = 0;

                //check if decimal exist
                if (dTotal.getText().toString().contains(".")) {
                    //Disable decimal button
                    decimal = 1;
                    dDecimal.setEnabled(false);
                } else {
                    //enable decimal button
                    decimal = 0;
                    dDecimal.setEnabled(true);
                }

                // Display the operator used in the calculation
                Toast.makeText(AddDebt.this, operator, Toast.LENGTH_SHORT).show();
                return; // Exit the onClick() method after performing the calculation
            }

            // If a valid operator is not found, continue with the existing compute() logic
            compute();
            ACTION = EQU;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            dTotal.setText(decimalFormat.format(val1));

            // Check if decimal exists
            if (dTotal.getText().toString().contains(".")) {
                // Disable decimal button
                decimal = 1;
                dDecimal.setEnabled(false);
            } else {
                // Enable decimal button
                decimal = 0;
                dDecimal.setEnabled(true);
            }
        }
    }
    
    private void deleteOperation(){

        // Handle error if field empty
        if (dTotal.getText() == null || dTotal.getText().length() == 0) {
            dTotal.setText(null); // Set the value to null
            return;
        }

        if (dTotal.getText().toString().contains(".")) {
            // Disable decimal button
            decimal = 1;
            dDecimal.setEnabled(false);

            // Perform decimal deletion logic
            if (dTotal.getText().length() > 1) {
                CharSequence number = dTotal.getText().toString();
                dTotal.setText(number.subSequence(0, number.length() - 1));
            } else {
                // Reset values if decimal flag is not set
                val1 = Double.NaN;
                val2 = Double.NaN;
                dTotal.setText(null);
            }

            if (!dTotal.getText().toString().contains(".")) {
                // Disable decimal button
                decimal = 0;
                dDecimal.setEnabled(true);
            }
        } else {
            // Perform decimal deletion logic
            if (dTotal.getText().length() > 1) {
                CharSequence number = dTotal.getText().toString();
                dTotal.setText(number.subSequence(0, number.length() - 1));
            } else {
                // Reset values if decimal flag is not set
                val1 = Double.NaN;
                val2 = Double.NaN;
                dTotal.setText(null);
            }
        }
    }
    
    // Open the date picker if user want to select their own date
    private void openDatePickerFunction(){

        // Create a Calendar instance with the current date
        final Calendar c = Calendar.getInstance();

        // Get the current year, month, and day from the Calendar instance
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog with the current date as the default date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddDebt.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Format the selected date as "yyyy/MM/dd"
                        String date = String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth);

                        // Set the text of the iDate TextView to the selected date
                        dDate.setText("Date: " + date);
                    }
                },
                year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
    
    
    //To do calculation
    private void compute() {
        val2 = 0; // Reset val2 to zero

        if (!Double.isNaN(val1)) {
            String input = dTotal.getText().toString();

            // Split the input into tokens using regex
            String[] tokens = input.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");

            if (tokens.length >= 3) {
                // Get the operator
                String operator = tokens[1];

                // Check if the operator is '-' and adjust the tokens accordingly
                if (operator.equals("-")) {
                    if (tokens.length == 3) {
                        tokens[2] = "-" + tokens[2];
                    } else if (tokens.length == 4 && tokens[2].equals("")) {
                        tokens[2] = "-" + tokens[3];
                        tokens = Arrays.copyOf(tokens, 3);
                    }
                }

                try {
                    // Check if the tokens array contains valid operands
                    if (tokens.length >= 3) {
                        double operand1 = Double.parseDouble(tokens[0]);
                        double operand2 = Double.parseDouble(tokens[2]);

                        // Perform the calculation based on the operator
                        switch (operator) {
                            case "+":
                                val1 = operand1 + operand2;
                                break;
                            case "-":
                                val1 = operand1 - operand2;
                                break;
                            case "x":
                                val1 = operand1 * operand2;
                                break;
                            case "÷":
                                if (operand2 != 0) {
                                    val1 = operand1 / operand2;
                                } else {
                                    handleCalculationError();
                                    return;
                                }
                                break;
                            default:
                                handleCalculationError();
                                return;
                        }
                    } else {
                        handleCalculationError();
                        return;
                    }
                } catch (NumberFormatException e) {
                    handleCalculationError();
                    return;
                }
            }
        } else {
            try {
                val1 = Double.parseDouble(dTotal.getText().toString());
            } catch (NumberFormatException e) {
                dTotal.setText("");
            }
        }
        dTotal.setText("");
    }


    //Handle calculation error
    private void handleCalculationError() {
        Toast.makeText(AddDebt.this, "Error: Division by zero", Toast.LENGTH_SHORT).show();
        dTotal.setText(""); // Clear the input field
    }


    private void insertDebtData() {

        //Get data from the field
        String debtName = dName.getText().toString().trim();
        String debtAmount = dTotal.getText().toString().trim();
        String debtDate = dDate.getText().toString().trim().substring(6);
        String debtStatus = "Not Paid";
        String debtDatePaid = "-";

        //Check if the field have value
        //If not it will point to the empty value
        if (debtName.isEmpty()) {
            dName.setError("Please enter debt name");
            dName.requestFocus();
            return;
        }

        // Check if debtAmount is empty
        if (debtAmount.isEmpty()) {
            Toast.makeText(AddDebt.this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if debtAmount is zero
        if (debtAmount.equals("0")) {
            Toast.makeText(AddDebt.this, "Debt amount cannot be zero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if debtAmount contains +, /, or *
        if (debtAmount.contains("+") || debtAmount.contains("÷") || debtAmount.contains("*") || debtAmount.contains("-")) {
            Toast.makeText(AddDebt.this, "Debt amount cannot contain +, -, ÷, or *", Toast.LENGTH_SHORT).show();
            return;
        }


        //Check if there intent from debtDetail.class
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("debtId")) {
            // Update based on debtId
            String debtId = intent.getStringExtra("debtId");

            if (debtId != null) {
                DatabaseReference debtToUpdateReference = debtReference.child(debtId);

                // Update debtName, debtAmount, and debtDate
                debtToUpdateReference.child("debtName").setValue(debtName);
                debtToUpdateReference.child("debtTotal").setValue(debtAmount);
                debtToUpdateReference.child("debtDate").setValue(debtDate);

                Toast.makeText(AddDebt.this, "Debt updated successfully!", Toast.LENGTH_SHORT).show();

                // Create an intent to send the updated data back to debtDetail.java
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedDebtName", debtName);
                resultIntent.putExtra("updatedDebtAmount", debtAmount);
                resultIntent.putExtra("updatedDebtDate", debtDate);

                // Set the result code and data
                setResult(Activity.RESULT_OK, resultIntent);

                // Finish the AddDebt activity
                finish();

            }
        }else {
            //Create a debt object with the data
            Debt debt = new Debt(debtName, debtAmount, debtDate, debtStatus, debtDatePaid);
            debtReference.push().setValue(debt).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddDebt.this, "Successfully add debt!", Toast.LENGTH_SHORT).show();

                        dName.setText(null);
                        dTotal.setText(null);
                    } else {
                        Toast.makeText(AddDebt.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    //Display the arrow on top to go back to previous activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // Call onBackPressed() to navigate back
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}