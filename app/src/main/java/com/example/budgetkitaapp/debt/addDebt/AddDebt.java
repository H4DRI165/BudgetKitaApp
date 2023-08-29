package com.example.budgetkitaapp.debt.addDebt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.debt.debtClass.Debt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddDebt extends AppCompatActivity {

    private Button dZero, dOne, dTwo, dThree, dFour, dFive, dSix, dSeven, dEight, dNine;
    private Button dAdd, dMinus, dMultiply, dDivide, dEqual, dDecimal,dSave;
    private TextView dTotal, dName,dDate,dCategory;
    private ImageView dDelete;

    private final char ADDITION = '+';
    private final char SUBTRACTION = '-';
    private final char MULTIPLICATION = '*';
    private final char DIVISION = '/';
    private final char EQU = 0;
    private double val1 = Double.NaN;
    private double val2;
    private FirebaseAuth mAuth;
    private int decimal = 0; //initialize the decimal to 0
    private final int NONE = 0;
    private int ACTION = NONE; // Initialize ACTION to some default value (e.g., NONE)
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

        //Call setCurrentDate() to get current date and set it to iDate
        setCurrentDate();

        // Set click listeners for number buttons
        setNumberButtonClickListener(dZero);
        setNumberButtonClickListener(dOne);
        setNumberButtonClickListener(dTwo);
        setNumberButtonClickListener(dThree);
        setNumberButtonClickListener(dFour);
        setNumberButtonClickListener(dFive);
        setNumberButtonClickListener(dSix);
        setNumberButtonClickListener(dSeven);
        setNumberButtonClickListener(dEight);
        setNumberButtonClickListener(dNine);
        setDecimalButtonClickListener(dDecimal);


        // Set click listeners for operator buttons
        setOperatorButtonClickListener(dAdd, ADDITION, "+");
        setOperatorButtonClickListener(dMultiply, MULTIPLICATION, "x");
        setOperatorButtonClickListener(dDivide, DIVISION, "÷");


        //Click listener for operation subtraction, equal and delete
        setButtonClickListener(dMinus, minusClickListener);
        setButtonClickListener(dEqual, equalClickListener);
        setImageViewClickListener(dDelete, deleteClickListener);


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
    private void setNumberButtonClickListener(Button button) {
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
    private void setDecimalButtonClickListener(Button button) {
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

    //For add, multiply and division
    private void setOperatorButtonClickListener(Button button, final int action, final String operator) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = dTotal.getText().toString();
                if (input.equals("")) {
                    Toast.makeText(AddDebt.this, "Please enter a number", Toast.LENGTH_SHORT).show();

                } else {
                    compute();
                    ACTION = action; // Set the ACTION to the specified action (ADDITION, MULTIPLICATION, or DIVISION)
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    dTotal.setText(decimalFormat.format(val1) + operator);

                    // Set decimal to 0 and enable the decimal button
                    decimal = 0;
                    dDecimal.setEnabled(true);
                }
            }
        });
    }

    //For button minus and equal
    private void setButtonClickListener(Button button, View.OnClickListener clickListener) {
        button.setOnClickListener(clickListener);
    }

    //For imageview delete
    private void setImageViewClickListener(ImageView imageView, View.OnClickListener clickListener) {
        imageView.setOnClickListener(clickListener);
    }

    //To handle the subtraction operation and handle negative numbers and consecutive minus signs
    private View.OnClickListener minusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String input = dTotal.getText().toString();

            // Check if the input field is empty or ends with an operator
            if (input.isEmpty() || "+-x÷".contains(input.substring(input.length() - 1))) {
                // Add a negative sign to start a negative number
                dTotal.setText(input + "-");
            } else {
                // Check if two consecutive minus signs are present, indicating subtraction
                int lastIndex = input.length() - 1;
                if (lastIndex >= 0 && input.charAt(lastIndex) == '-') {
                    dTotal.setText(input + "-");
                } else {
                    // Compute the previous operation and set up for subtraction
                    compute();
                    ACTION = SUBTRACTION;
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    dTotal.setText(decimalFormat.format(val1) + "-");
                    // Set decimal to 0 and enable the decimal button
                    decimal = 0;
                    dDecimal.setEnabled(true);
                }
            }
        }
    };

    // To handle equal operation
    private View.OnClickListener equalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
    };

    //To handle delete operation
    private View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
    };

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

            // Handle consecutive minus signs and spaces
            input = input.replaceAll("(?<=\\d)-(?=\\d)", "- "); // Add space between consecutive minus signs

            // Split the input into tokens using regex
            String[] tokens = input.split(" ");

            if (tokens.length >= 3) {
                // Get the operator
                String operator = tokens[1];

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
        Toast.makeText(AddDebt.this, "Error: Invalid calculation", Toast.LENGTH_SHORT).show();
    }


    private void insertDebtData(){

        //Get data from the field
        String debtName = dName.getText().toString().trim();
        String debtAmount = dTotal.getText().toString().trim();
        String debtDate = dDate.getText().toString().trim().substring(6);
        String debtStatus = "Not Paid";

        //Check if the field have value
        //If not it will point to the empty value
        if(debtName.isEmpty()){
            dName.setError("Please enter debt name");
            dName.requestFocus();
            return;
        }

        if(debtAmount.isEmpty()){
            Toast.makeText(AddDebt.this, "Please enter the number", Toast.LENGTH_SHORT).show();
            return;
        }

        //Create a debt object with the data
        Debt debt = new Debt(debtName, debtAmount, debtDate, debtStatus);
        debtReference.push().setValue(debt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AddDebt.this, "Success!", Toast.LENGTH_SHORT).show();

                    dName.setText(null);
                    dTotal.setText(null);
                } else {
                    Toast.makeText(AddDebt.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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