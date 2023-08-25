package com.example.budgetkitaapp.transaction.income;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.databinding.FragmentIncomeBinding;
import com.example.budgetkitaapp.map.editLocation.EditLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class IncomeFragment extends Fragment {

    private Button iZero, iOne, iTwo, iThree, iFour, iFive, iSix, iSeven, iEight, iNine;
    private Button iAdd, iMinus, iMultiply, iDivide, iEqual, iDecimal,iSave;
    private TextView iTotal, iName,iDate,iCategory;
    private ImageView iDelete;
    private final char ADDITION = '+';
    private final char SUBTRACTION = '-';
    private final char MULTIPLICATION = '*';
    private final char DIVISION = '/';
    private final char EQU = 0;
    private double val1 = Double.NaN;
    private double val2;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE_CATEGORY = 101;
    private int decimal = 0; //initialize the decimal to 0
    private final int NONE = 0;
    private int ACTION = NONE; // Initialize ACTION to some default value (e.g., NONE)
    DatabaseReference incomeReference, transactionReference;

    public static IncomeFragment newInstance(String entry, String incomeId) {
        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        args.putString("entry", entry);
        args.putString("incomeId", incomeId);
        fragment.setArguments(args);
        return fragment;
    } //Receive argument for TransactionDetail.class
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CATEGORY) {
            if (resultCode == Activity.RESULT_OK) {
                // Handle the result from the IncomeCategory activity
                if (data != null) {
                    // Extract the selected category from the Intent data
                    String selectedCategory = data.getStringExtra("category");

                    // Set the selected category to the iCategory TextView
                    iCategory.setText("Category: " +selectedCategory);
                }
            }
        }
    } //Get category for incomeCategory using onActivityResult

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_income, container, false);

        //Authenticate user and create unique key for incomeId
        authenticateUser();

        // Call assignVariable() and pass v as a parameter
        assignVariable(v);

        //Call setCurrentDate() to get current date and set it to iDate
        setCurrentDate();

        //Check for args to indicate if user navigate from Dashboard or TransactionDetail.class
        Bundle args = getArguments();
        if(args != null){
            getDataFromFirebase();
        }

        // Set click listeners for number buttons
        setNumberButtonClickListener(iZero);
        setNumberButtonClickListener(iOne);
        setNumberButtonClickListener(iTwo);
        setNumberButtonClickListener(iThree);
        setNumberButtonClickListener(iFour);
        setNumberButtonClickListener(iFive);
        setNumberButtonClickListener(iSix);
        setNumberButtonClickListener(iSeven);
        setNumberButtonClickListener(iEight);
        setNumberButtonClickListener(iNine);
        setDecimalButtonClickListener(iDecimal);

        // Set click listeners for operator buttons
        setOperatorButtonClickListener(iAdd, ADDITION, "+");
        setOperatorButtonClickListener(iMultiply, MULTIPLICATION, "x");
        setOperatorButtonClickListener(iDivide, DIVISION, "÷");

        //Click listener for operation subtraction, equal and delete
        setButtonClickListener(iMinus, minusClickListener);
        setButtonClickListener(iEqual, equalClickListener);
        setImageViewClickListener(iDelete, deleteClickListener);

        // Set an OnClickListener on the iDate TextView to launch the DatePickerDialog
        iDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open datepicker function for user to select their preferred date
                openDatePickerFunction();
        }
        });

        //To save income transaction
        iSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To save income into Firebase
                insertIncomeData();
            }
        });

        iCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), IncomeCategory.class);
                startActivityForResult(intent, REQUEST_CODE_CATEGORY);
            }
        });
        return v;
    }

    private void authenticateUser(){

        //Firebase authentication to identify user
        mAuth = FirebaseAuth.getInstance();

        //Only login user can access to their data
        incomeReference = FirebaseDatabase.getInstance().getReference("Accounts").child(mAuth.getCurrentUser().getUid())
                .child("Income");

        // Generate a new, unique transaction ID
        transactionReference = FirebaseDatabase.getInstance().getReference("Accounts").child(mAuth.getCurrentUser().getUid())
                .child("Income").push();

        // Generate a new, unique transaction ID
        String transactionId = transactionReference.getKey();
    }
    private void assignVariable(View v) {

        //Assign Variable
        iZero = v.findViewById(R.id.income_0);
        iOne = v.findViewById(R.id.income_1);
        iTwo = v.findViewById(R.id.income_2);
        iThree = v.findViewById(R.id.income_3);
        iFour = v.findViewById(R.id.income_4);
        iFive = v.findViewById(R.id.income_5);
        iSix = v.findViewById(R.id.income_6);
        iSeven = v.findViewById(R.id.income_7);
        iEight = v.findViewById(R.id.income_8);
        iNine = v.findViewById(R.id.income_9);
        iAdd = v.findViewById(R.id.income_plus);
        iMinus = v.findViewById(R.id.income_minus);
        iMultiply = v.findViewById(R.id.income_multiply);
        iDivide = v.findViewById(R.id.income_divide);
        iEqual = v.findViewById(R.id.income_equal);
        iDecimal = v.findViewById(R.id.income_decimal);
        iDelete = v.findViewById(R.id.ivDelete);
        iTotal = v.findViewById(R.id.incomeText);
        iName = v.findViewById(R.id.income_name);
        iDate = v.findViewById(R.id.incomeDate);
        iSave = v.findViewById(R.id.incomeSave);
        iCategory = v.findViewById(R.id.income_category);
    }
    private void setCurrentDate(){
        //Get current date and set it to iDate and format it as "yyyy/MM/dd"
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = dateFormat.format(currentDate);

        // Set the text of the iDate TextView to today's date
        iDate.setText("Date: " + dateString);
    }
    private void setNumberButtonClickListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {

            //Add the number into the iTotal based on xml value assigned in IncomeFragment.xml
            @Override
            public void onClick(View view) {
                Button numberButton = (Button) view;
                //Get the digit value from income XML
                String digit = numberButton.getText().toString();
                iTotal.append(digit);
            }
        });
    }
    private void setDecimalButtonClickListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = iTotal.getText().toString();

                if (decimal == 0 && input.isEmpty()) {
                    iTotal.setText("0.");
                    decimal = 1;
                    button.setEnabled(false);
                } else if (decimal == 0 && !input.isEmpty()) {
                    iTotal.setText(input + ".");
                    decimal = 1;
                    button.setEnabled(false);
                } else {
                    iTotal.setText(input + ".");
                }
            }
        });
    } //Add the number into the iTotal based on xml value assigned in IncomeFragment.xml
    private void setOperatorButtonClickListener(Button button, final int action, final String operator) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = iTotal.getText().toString();
                if (input.equals("")) {
                    Toast.makeText(getActivity(), "Please enter a number", Toast.LENGTH_SHORT).show();
                } else {
                    compute();
                    ACTION = action; // Set the ACTION to the specified action (ADDITION, SUBTRACTION, MULTIPLICATION, or DIVISION)
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    iTotal.setText(decimalFormat.format(val1) + operator);

                    // Set decimal to 0 and enable the decimal button
                    decimal = 0;
                    iDecimal.setEnabled(true);
                }
            }
        });
    }
    private void setButtonClickListener(Button button, View.OnClickListener clickListener) {
        button.setOnClickListener(clickListener);
    } //For button minus and equal
    private void setImageViewClickListener(ImageView imageView, View.OnClickListener clickListener) {
        imageView.setOnClickListener(clickListener);
    } //For imageview delete
    private View.OnClickListener minusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String input = iTotal.getText().toString();

            // Check if the input field is empty or ends with an operator
            if (input.isEmpty() || "+-x÷".contains(input.substring(input.length() - 1))) {
                // Add a negative sign to start a negative number
                iTotal.setText(input + "-");
            } else {
                // Check if two consecutive minus signs are present, indicating subtraction
                int lastIndex = input.length() - 1;
                if (lastIndex >= 0 && input.charAt(lastIndex) == '-') {
                    iTotal.setText(input + "-");
                } else {
                    // Compute the previous operation and set up for subtraction
                    compute();
                    ACTION = SUBTRACTION;
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    iTotal.setText(decimalFormat.format(val1) + "-");
                    // Set decimal to 0 and enable the decimal button
                    decimal = 0;
                    iDecimal.setEnabled(true);
                }
            }
        }
    }; //To handle the subtraction operation and handle negative numbers and consecutive minus signs
    private View.OnClickListener equalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String input = iTotal.getText().toString();
            if (input.equals("")) {
                Toast.makeText(getActivity(), "Please enter a numbers", Toast.LENGTH_SHORT).show();
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
                    iTotal.setText(decimalFormat.format(result));

                    // Update val1 and val2 for subsequent calculations
                    val1 = result;
                    val2 = 0;

                    //check if decimal exist
                    if (iTotal.getText().toString().contains(".")) {
                        //Disable decimal button
                        decimal = 1;
                        iDecimal.setEnabled(false);
                    } else {
                        //enable decimal button
                        decimal = 0;
                        iDecimal.setEnabled(true);
                    }

                    // Display the operator used in the calculation
                    Toast.makeText(getActivity(), operator, Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick() method after performing the calculation
                }

                // If a valid operator is not found, continue with the existing compute() logic
                compute();
                ACTION = EQU;
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                iTotal.setText(decimalFormat.format(val1));

                // Check if decimal exists
                if (iTotal.getText().toString().contains(".")) {
                    // Disable decimal button
                    decimal = 1;
                    iDecimal.setEnabled(false);
                } else {
                    // Enable decimal button
                    decimal = 0;
                    iDecimal.setEnabled(true);
                }
            }
        }
    }; // To handle equal operation
    private View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Handle error if field empty
            if (iTotal.getText() == null || iTotal.getText().length() == 0) {
                iTotal.setText(null); // Set the value to null
                return;
            }

            if (iTotal.getText().toString().contains(".")) {
                // Disable decimal button
                decimal = 1;
                iDecimal.setEnabled(false);

                // Perform decimal deletion logic
                if (iTotal.getText().length() > 1) {
                    CharSequence number = iTotal.getText().toString();
                    iTotal.setText(number.subSequence(0, number.length() - 1));
                } else {
                    // Reset values if decimal flag is not set
                    val1 = Double.NaN;
                    val2 = Double.NaN;
                    iTotal.setText(null);
                }

                if (!iTotal.getText().toString().contains(".")) {
                    // Disable decimal button
                    decimal = 0;
                    iDecimal.setEnabled(true);
                }
            } else {
                // Perform decimal deletion logic
                if (iTotal.getText().length() > 1) {
                    CharSequence number = iTotal.getText().toString();
                    iTotal.setText(number.subSequence(0, number.length() - 1));
                } else {
                    // Reset values if decimal flag is not set
                    val1 = Double.NaN;
                    val2 = Double.NaN;
                    iTotal.setText(null);
                }
            }
        }
    }; //To handle delete operation
    private void getDataFromFirebase() {

        //Get data passed for TransactionDetail
        Bundle args = getArguments();

        // Retrieve the authenticated user's ID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not authenticated, handle accordingly
            return;
        }
        String userID = currentUser.getUid();

        //Assign incomeId receive for TransactionDetail
        String incomeId = args.getString("incomeId");

        // Retrieve income name, category, amount and date from Firebase
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
                    String incomeDate = dataSnapshot.child("incomeDate").getValue(String.class);
                    String incomeTotal = dataSnapshot.child("incomeTotal").getValue(String.class);

                    iName.setText(incomeName);
                    iCategory.setText("Category: " +incomeCat);
                    iTotal.setText(incomeTotal);
                    iDate.setText("Date: " + incomeDate);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error, if any
            }
        });
    } // To retrieve data from firebase based on incomeId receive for TransactionDetail.class
    private void openDatePickerFunction(){

        // Create a Calendar instance with the current date
        final Calendar c = Calendar.getInstance();

        // Get the current year, month, and day from the Calendar instance
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog with the current date as the default date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Format the selected date as "yyyy/MM/dd"
                        String date = String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth);

                        // Set the text of the iDate TextView to the selected date
                        iDate.setText("Date: " + date);
                    }
                },
                year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    } // Open the date picker if user want to select their own date
    private void compute() {
        val2 = 0; // Reset val2 to zero

        if (!Double.isNaN(val1)) {
            String input = iTotal.getText().toString();

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
                val1 = Double.parseDouble(iTotal.getText().toString());
            } catch (NumberFormatException e) {
                iTotal.setText("");
            }
        }
        iTotal.setText("");
    } //To do calculation
    private void handleCalculationError() {
        Toast.makeText(getActivity(), "Error: Invalid calculation", Toast.LENGTH_SHORT).show();
    } //Handle calculation error
    private void insertIncomeData(){

        //Get data from the field
        String incomeName = iName.getText().toString().trim();
        String incomeAmount = iTotal.getText().toString().trim();
        String incomeDate = iDate.getText().toString().trim().substring(6);
        String incomeCat = iCategory.getText().toString().trim();

        //Check if the field have value
        //If not it will point to the empty value
        if(incomeName.isEmpty()){
            iName.setError("Please enter income name");
            iName.requestFocus();
            return;
        }

        if(incomeAmount.isEmpty()){
            Toast.makeText(getActivity(), "Please enter the number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (incomeCat.equals("Income Category")) {
            Toast.makeText(getActivity(), "Please select category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove "Category: " prefix from incomeCat
        incomeCat = incomeCat.substring("Category: ".length());

        //Get data passed for TransactionDetail
        Bundle args = getArguments();

        if(args != null){

            //Assign incomeId receive for TransactionDetail
            String incomeId = args.getString("incomeId");

            Income income = new Income(incomeName, incomeCat, incomeAmount, incomeDate);

            // Retrieve income name, category, amount and date from Firebase
            DatabaseReference updateIncome = FirebaseDatabase.getInstance().getReference()
                    .child("Accounts")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("Income");
                    //.child(incomeId);

            //Update the income detail using IncomeID
            String finalIncomeCat = incomeCat;
            updateIncome.child(incomeId).setValue(income).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Entry update successfully", Toast.LENGTH_SHORT).show();

                            // Inside saveLocation button's onClick() method in EditLocation activity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("updateName", incomeName);
                            resultIntent.putExtra("updateCategory", finalIncomeCat);
                            resultIntent.putExtra("updateAmount", incomeAmount);
                            resultIntent.putExtra("updateDate", incomeDate);

                            //Finish IncomeFragment activity and go back to EditTransaction activity
                            requireActivity().setResult(Activity.RESULT_OK, resultIntent);
                            requireActivity().finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to save entry: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{

            //Create a income object with the data
            Income income = new Income(incomeName, incomeCat ,incomeAmount, incomeDate);
            incomeReference.push().setValue(income).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();

                        iName.setText(null);
                        iCategory.setText("Please select category");
                        iTotal.setText(null);
                    } else {
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    } //Insert data into Firebase
}


