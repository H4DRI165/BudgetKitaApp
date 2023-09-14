package com.example.budgetkitaapp.transaction.expense;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetkitaapp.debt.addDebt.AddDebt;
import com.example.budgetkitaapp.map.listLocation.map;
import com.example.budgetkitaapp.R;
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


public class ExpenseFragment extends Fragment {

    private Button eZero, eOne, eTwo, eThree, eFour, eFive, eSix, eSeven, eEight, eNine;
    private Button eAdd, eMinus, eMultiply, eDivide, eEqual, eDecimal,eSave;
    private TextView eTotal, eName,eDate, eCategory, eLocation;
    private ImageView eDelete;

    private final char ADDITION = '+';
    private final char SUBTRACTION = '-';
    private final char MULTIPLICATION = '*';
    private final char DIVISION = '/';
    private final char EQU = 0;

    private double val1 = Double.NaN;
    private double val2;
    private char ACTION;

    private FirebaseAuth mAuth;
    int decimal = 0;
    private static final int REQUEST_CODE = 5;

    DatabaseReference expenseReference, transactionReference;

    public static ExpenseFragment newInstance(String entry, String expenseId) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putString("entry", entry);
        args.putString("expenseId", expenseId);
        fragment.setArguments(args);
        return fragment;
    }

    //Get location and category
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String locationName = data.getStringExtra("locationName");
            eLocation.setText("Location: "+locationName);

        } else if (requestCode == 102) {
            // Handle the result from the IncomeCategory activity
            if (data != null) {
                // Extract the selected category from the Intent data
                String selectedCategory = data.getStringExtra("category");

                // Set the selected category to the iCategory TextView
                eCategory.setText("Category: " + selectedCategory);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_expense, container, false);

        //Firebase authentication to identify user
        mAuth = FirebaseAuth.getInstance();

        //Only login user can access to their data
        expenseReference = FirebaseDatabase.getInstance().getReference("Accounts").child(mAuth.getCurrentUser().getUid())
                .child("Expense");

        // Generate a new, unique transaction ID
        transactionReference = FirebaseDatabase.getInstance().getReference("Accounts").child(mAuth.getCurrentUser().getUid())
                .child("Expense").push();

        // Generate a new, unique transaction ID
        String transactionId = transactionReference.getKey();

        // Call assignVariable() and pass v as a parameter
        assignVariable(v);

        //Retrieve the arguments
        Bundle args = getArguments();
        if(args != null){
            String entry = args.getString("entry");
            String expenseId = args.getString("expenseId");

            // Set the 'entry' to the iName TextView
            eName.setText(entry);
        }



        Bundle extras = getArguments();
        if (extras != null) {
            String receivedLocation = extras.getString("locationName");
            eLocation.setText(receivedLocation);
        }

        eDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = eTotal.getText().toString();

                if (decimal == 0 && input.isEmpty()) {
                    eTotal.setText("0.");
                    decimal = 1;
                    eDecimal.setEnabled(false);
                } else if (decimal == 0 && !input.isEmpty()) {
                    eTotal.setText(input + ".");
                    decimal = 1;
                    eDecimal.setEnabled(false);
                } else {
                    eTotal.setText(input + ".");
                }
            }
        });


        eZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "0");
            }
        });

        eOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "1");
            }
        });

        eTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "2");
            }
        });

        eThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "3");
            }
        });

        eFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "4");
            }
        });

        eFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "5");
            }
        });

        eSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "6");
            }
        });

        eSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "7");
            }
        });

        eEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "8");
            }
        });

        eNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eTotal.setText(eTotal.getText().toString() + "9");
            }
        });

        eAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = eTotal.getText().toString();
                if (input.equals("")) {
                    Toast.makeText(getActivity(), "Cannot start with operation symbol", Toast.LENGTH_SHORT).show();
                } else {
                    compute();
                    ACTION = ADDITION;
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    eTotal.setText(decimalFormat.format(val1) + "+");

                    //set decimal to 0 and enable the decimal button
                    decimal = 0;
                    eDecimal.setEnabled(true);

                }
            }
        });

        eMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = eTotal.getText().toString();
                if (input.equals("")) {
                    Toast.makeText(getActivity(), "Cannot start with negative number", Toast.LENGTH_SHORT).show();
                } else {
                    compute();
                    ACTION = SUBTRACTION;
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    eTotal.setText(decimalFormat.format(val1) + "-");

                    //set decimal to 0 and enable the decimal button
                    decimal = 0;
                    eDecimal.setEnabled(true);
                }
            }
        });

        eMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = eTotal.getText().toString();
                if (input.equals("")) {
                    Toast.makeText(getActivity(), "Cannot start with operation symbol", Toast.LENGTH_SHORT).show();
                } else {
                    compute();
                    ACTION = MULTIPLICATION;
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    eTotal.setText(decimalFormat.format(val1) + "x");

                    //set decimal to 0 and enable the decimal button
                    decimal = 0;
                    eDecimal.setEnabled(true);
                }
            }
        });

        eDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = eTotal.getText().toString();
                if (input.equals("")) {
                    Toast.makeText(getActivity(), "Cannot start with operation symbol", Toast.LENGTH_SHORT).show();
                } else {
                    compute();
                    ACTION = DIVISION;
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    eTotal.setText(decimalFormat.format(val1) + "÷");

                    // Set decimal to 0 and enable the decimal button
                    decimal = 0;
                    eDecimal.setEnabled(true);
                }
            }
        });


        eEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = eTotal.getText().toString();
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
                        eTotal.setText(decimalFormat.format(result));

                        // Update val1 and val2 for subsequent calculations
                        val1 = result;
                        val2 = 0;

                        //check if decimal exist
                        if (eTotal.getText().toString().contains(".")) {
                            //Disable decimal button
                            decimal = 1;
                            eDecimal.setEnabled(false);
                        } else {
                            //enable decimal button
                            decimal = 0;
                            eDecimal.setEnabled(true);
                        }

                        // Display the operator used in the calculation
                        Toast.makeText(getActivity(), operator, Toast.LENGTH_SHORT).show();
                        return; // Exit the onClick() method after performing the calculation
                    }

                    // If a valid operator is not found, continue with the existing compute() logic
                    compute();
                    ACTION = EQU;
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    eTotal.setText(decimalFormat.format(val1));

                    // Check if decimal exists
                    if (eTotal.getText().toString().contains(".")) {
                        // Disable decimal button
                        decimal = 1;
                        eDecimal.setEnabled(false);
                    } else {
                        // Enable decimal button
                        decimal = 0;
                        eDecimal.setEnabled(true);
                    }
                }
            }
        });

        eDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Handle error if field empty
                if (eTotal.getText() == null || eTotal.getText().length() == 0) {
                    eTotal.setText(null); // Set the value to null
                    return;
                }

                if (eTotal.getText().toString().contains(".")) {
                    // Disable decimal button
                    decimal = 1;
                    eDecimal.setEnabled(false);

                    // Perform decimal deletion logic
                    if (eTotal.getText().length() > 1) {
                        CharSequence number = eTotal.getText().toString();
                        eTotal.setText(number.subSequence(0, number.length() - 1));
                    } else {
                        // Reset values if decimal flag is not set
                        val1 = Double.NaN;
                        val2 = Double.NaN;
                        eTotal.setText(null);
                    }

                    if (!eTotal.getText().toString().contains(".")) {
                        // Disable decimal button
                        decimal = 0;
                        eDecimal.setEnabled(true);
                    }
                } else {
                    // Perform decimal deletion logic
                    if (eTotal.getText().length() > 1) {
                        CharSequence number = eTotal.getText().toString();
                        eTotal.setText(number.subSequence(0, number.length() - 1));
                    } else {
                        // Reset values if decimal flag is not set
                        val1 = Double.NaN;
                        val2 = Double.NaN;
                        eTotal.setText(null);
                    }
                }
            }
        });


        // Get the current date and format it as "yyyy/MM/dd"
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = dateFormat.format(currentDate);

        // Set the text of the iDate TextView to today's date
        eDate.setText("Date: " + dateString);

        // Set an OnClickListener on the iDate TextView to launch the DatePickerDialog
        eDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                eDate.setText("Date: " + date);
                            }
                        },
                        year, month, day);

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

        eSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertExpenseData();
            }
        });

        eCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ExpenseCategory.class);
                startActivityForResult(intent, 102);
            }
        });

        eLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent location = new Intent(getActivity(), map.class);
                location.putExtra("source", "expenseFragment");

                //startActivity(location);
                startActivityForResult(location, REQUEST_CODE);
            }
        });

        return v;
    }

    private void assignVariable(View v) {

        //Assign Variable
        eZero = v.findViewById(R.id.expense_0);
        eOne = v.findViewById(R.id.expense_1);
        eTwo = v.findViewById(R.id.expense_2);
        eThree = v.findViewById(R.id.expense_3);
        eFour = v.findViewById(R.id.expense_4);
        eFive = v.findViewById(R.id.expense_5);
        eSix = v.findViewById(R.id.expense_6);
        eSeven = v.findViewById(R.id.expense_7);
        eEight = v.findViewById(R.id.expense_8);
        eNine = v.findViewById(R.id.expense_9);
        eAdd = v.findViewById(R.id.expense_plus);
        eMinus = v.findViewById(R.id.expense_minus);
        eMultiply = v.findViewById(R.id.expense_multiply);
        eDivide = v.findViewById(R.id.expense_divide);
        eEqual = v.findViewById(R.id.expense_equal);
        eDecimal = v.findViewById(R.id.expense_decimal);
        eDelete = v.findViewById(R.id.ivDelete);
        eTotal = v.findViewById(R.id.expenseText);
        eName = v.findViewById(R.id.expense_name);
        eDate = v.findViewById(R.id.expenseDate);
        eSave = v.findViewById(R.id.expenseSave);
        eCategory = v.findViewById(R.id.expense_category);
        eLocation = v.findViewById(R.id.expense_location);
    }

    private void compute() {
        val2 = 0; // Reset val2 to zero

        if (!Double.isNaN(val1)) {
            String input = eTotal.getText().toString();

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
                val1 = Double.parseDouble(eTotal.getText().toString());
            } catch (NumberFormatException e) {
                eTotal.setText("");
            }
        }
        eTotal.setText("");
    }

    private void handleCalculationError() {
        Toast.makeText(getActivity(), "Error: Division by zero", Toast.LENGTH_SHORT).show();
        eTotal.setText(""); // Clear the input field
    }

    private void insertExpenseData(){

        //Get data from the field
        String expenseName = eName.getText().toString().trim();
        String expenseTotal = eTotal.getText().toString().trim();
        String expenseDate = eDate.getText().toString().trim().substring(6);
        String expenseCat = eCategory.getText().toString().trim();
        String expenseLocation = eLocation.getText().toString().trim();

        //Check if the field have value
        //If not it will point to the empty value
        if(expenseName.isEmpty()){
            eName.setError("Please enter expense name");
            eName.requestFocus();
            return;
        }

        if(expenseTotal.isEmpty()){
            Toast.makeText(getActivity(), "Please enter the number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expenseCat.equals("Income Category")) {
            Toast.makeText(getActivity(), "Please select category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expenseLocation.equals("Location")) {
            Toast.makeText(getActivity(), "Please select location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if debtAmount is zero
        if (expenseTotal.equals("0")) {
            Toast.makeText(getActivity(), "Expense amount cannot be zero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if debtAmount contains +, /, or *
        if (expenseTotal.contains("+") || expenseTotal.contains("÷") || expenseTotal.contains("*") || expenseTotal.contains("-")) {
            Toast.makeText(getActivity(), "Expense amount cannot contain +, -, ÷, or *", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove "Category: " prefix from incomeCat
        expenseCat = expenseCat.substring("Category: ".length());
        expenseLocation = expenseLocation.substring("Location: ".length());

        //Insert the expense transaction to firebase
        Expense expense = new Expense(expenseName, expenseCat, expenseTotal, expenseDate, expenseLocation);
        expenseReference.push().setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Successfully add expense!", Toast.LENGTH_SHORT).show();

                    eName.setText(null);
                    eCategory.setText("Please select category");
                    eLocation.setText("Location");
                    eTotal.setText(null);
                } else {
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

