package com.example.budgetkitaapp.transaction.listTransaction;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.TransactionHistory;
import com.example.budgetkitaapp.adapter.TransactionHistoryAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewTransactionFragment extends Fragment {

    private RecyclerView rv1;
    private TransactionHistoryAdapter adapter;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String selectedDuration;
    private TextView startDate, endDate;
    private Button btnFilter;
    private Spinner durationSpinner;
    private String selectedStartDate, selectedEndDate;
    PieChart pieChart;
    List<PieEntry> pieEntryList;
    double totalIncome = 0;
    double totalExpense = 0;
    private Context mContext;
    private boolean isChartClickable = true;

    boolean isStartDatePickerShown = false;
    boolean isEndDatePickerShown = false;

    private static final int DATE_RANGE_OPTION_POSITION = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_transaction, container, false);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Set up Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts");

        //Initialize variable
        assignVariable(v);

        //Create array
        pieEntryList = new ArrayList<>();
        setUpChart();

        //Get data from Firebase
        getListTransaction(v);

        return v;
    }

    private void getListTransaction(View v){

        // Check if user is authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            userRef = databaseRef.child(userID);

            setDurationSpinner(v);
            setRecyclerView();

            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isStartDatePickerShown) {
                        return; // Dialog is already shown, do nothing
                    }

                    isStartDatePickerShown = true; // Set the flag to true

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

                                    // Validate the end date
                                    String endDateText = endDate.getText().toString().replace("End: ", "");
                                    if (!endDateText.isEmpty()) {
                                        if (compareDates(date, endDateText) > 0) {
                                            Toast.makeText(getContext(), "Start date should be before end date", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }

                                    // Set the text of the startDate TextView to the selected date
                                    startDate.setText("Start: " + date);
                                }
                            },
                            year, month, day);

                    // Show the DatePickerDialog
                    datePickerDialog.show();

                    datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            isStartDatePickerShown = false; // Reset the flag when the dialog is dismissed
                        }
                    });
                }
            });

            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEndDatePickerShown) {
                        return; // Dialog is already shown, do nothing
                    }

                    isEndDatePickerShown = true; // Set the flag to true

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

                                    // Validate the start date
                                    String startDateText = startDate.getText().toString().replace("Start: ", "");
                                    if (!startDateText.isEmpty()) {
                                        if (compareDates(startDateText, date) > 0) {
                                            Toast.makeText(getContext(), "End date should be after start date", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }

                                    // Set the text of the endDate TextView to the selected date
                                    endDate.setText("End: " + date);
                                }
                            },
                            year, month, day);

                    // Show the DatePickerDialog
                    datePickerDialog.show();

                    datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            isEndDatePickerShown = false; // Reset the flag when the dialog is dismissed
                        }
                    });
                }
            });

            btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selectedStartDate = startDate.getText().toString().replace("Start: ", "");
                    selectedEndDate = endDate.getText().toString().replace("End: ", "");

                    // Check if both start and end dates are selected
                    if (selectedStartDate.equals("Start Date") && selectedEndDate.equals("End Date")) {
                        Toast.makeText(getContext(), "Please select start and end dates", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Check if start date is not selected
                    if (selectedStartDate.equals("Start Date")) {
                        Toast.makeText(getContext(), "Please select a start date", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Check if end date is not selected
                    if (selectedEndDate.equals("End Date")) {
                        Toast.makeText(getContext(), "Please select a end date", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Fetch data from Firebase based on the selected start and end dates
                    getList(selectedStartDate, selectedEndDate, new TransactionHistoryCallback() {
                        @Override
                        public void onTransactionHistoryLoaded(List<TransactionHistory> transactionList) {
                            updateRecyclerView(transactionList);
                            durationSpinner.setSelection(DATE_RANGE_OPTION_POSITION); // Select "Date Range" option in the spinner
                        }
                    });
                }
            });
        }
    }

    private void assignVariable(View v){
        rv1 = v.findViewById(R.id.recycler_view);
        startDate = v.findViewById(R.id.startDate);
        endDate = v.findViewById(R.id.endDate);
        btnFilter = v.findViewById(R.id.filterButton);
        durationSpinner = v.findViewById(R.id.durationSpinner);
        pieChart = v.findViewById(R.id.pieChart);
    }

    private void setUpChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT); // Set the hole color to transparent for a better look
        pieChart.setTransparentCircleRadius(0f); // Remove the transparent circle around the hole
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setDrawEntryLabels(true); // Enable entry labels for a better visualization
        pieChart.setDrawMarkers(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setNoDataText("");

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }

    private int compareDates(String date1, String date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        try {
            Date d1 = dateFormat.parse(date1);
            Date d2 = dateFormat.parse(date2);
            return d1.compareTo(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void setDurationSpinner(View v) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.duration_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(adapter);

        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDuration = parent.getItemAtPosition(position).toString();
                fetchDataBasedOnDuration(selectedDuration);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    private void fetchDataBasedOnDuration(String duration) {

        if (duration.equals("Date Range")) {

            selectedStartDate = startDate.getText().toString().replace("Start: ", "");
            selectedEndDate = endDate.getText().toString().replace("End: ", "");

            if(selectedStartDate.equals("Start Date") && selectedEndDate.equals("End Date") ){
                // Toast message to user
                Toast.makeText(getContext(), "Please select start date and end date then press filter", Toast.LENGTH_SHORT).show();
                // Clear the RecyclerView and return
                updateRecyclerView(Collections.emptyList());
                pieChart.setVisibility(View.GONE);
            }

            if(!selectedStartDate.equals("Start Date") && !selectedEndDate.equals("End Date") ){
                // Fetch data from Firebase based on the selected start and end dates
                getList(selectedStartDate, selectedEndDate, new TransactionHistoryCallback() {
                    @Override
                    public void onTransactionHistoryLoaded(List<TransactionHistory> transactionList) {
                        updateRecyclerView(transactionList);
                    }
                });
            }
        } else {

            startDate.setText("Start Date");
            endDate.setText("End Date");
            // Get the current date
            String today = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

            // Determine the start and end dates based on the selected duration
            String startDate;
            String endDate;

            switch (duration) {
                case "Today":
                    startDate = today;
                    endDate = today;
                    break;
                case "All":
                    startDate = null; // Retrieve all data, no start date
                    endDate = null; // Retrieve all data, no end date
                    break;
                case "Last Week":
                    // Retrieve data from the last 7 days
                    startDate = calculateDate(-7);
                    endDate = today;
                    break;
                case "Last Month":
                    // Retrieve data from the last 30 days
                    startDate = calculateDate(-30);
                    endDate = today;
                    break;
                default:
                    startDate = today;
                    endDate = today;
                    break;
            }

            // Fetch data from Firebase based on the start and end dates
            getList(startDate, endDate, new TransactionHistoryCallback() {
                @Override
                public void onTransactionHistoryLoaded(List<TransactionHistory> transactionList) {
                    updateRecyclerView(transactionList);
                }
            });
        }
    }

    private String calculateDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.getTime());
    }

    private void getList(String startDate, String endDate, final TransactionHistoryCallback callback) {
        Query incomeQuery = userRef.child("Income");

        if (startDate != null && endDate != null) {
            incomeQuery = incomeQuery.orderByChild("incomeDate").startAt(startDate).endAt(endDate);
        }

        incomeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TransactionHistory> transactionList = new ArrayList<>();
                totalIncome = 0; // Reset totalIncome before updating

                for (DataSnapshot incomeSnapshot : dataSnapshot.getChildren()) {
                    String incomeTotal = incomeSnapshot.child("incomeTotal").getValue(String.class);
                    String incomeDate = incomeSnapshot.child("incomeDate").getValue(String.class);
                    String incomeId = incomeSnapshot.getKey(); // Get the key of the current snapshot as incomeID
                    transactionList.add(new TransactionHistory("Income", incomeDate, incomeTotal, "", incomeId, ""));

                    // Calculate the sum of income values
                    double incomeAmount = Double.parseDouble(incomeTotal);
                    totalIncome += incomeAmount;
                }

                Query expenseQuery = userRef.child("Expense");

                if (startDate != null && endDate != null) {
                    expenseQuery = expenseQuery.orderByChild("expenseDate").startAt(startDate).endAt(endDate);
                }

                expenseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<TransactionHistory> expenseList = new ArrayList<>();
                        totalExpense = 0; // Reset totalExpense before updating

                        for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                            String expenseTotal = expenseSnapshot.child("expenseTotal").getValue(String.class);
                            String expenseDate = expenseSnapshot.child("expenseDate").getValue(String.class);
                            String expenseId = expenseSnapshot.getKey(); // Get the key of the current snapshot as incomeID
                            expenseList.add(new TransactionHistory("Expense", expenseDate,"", expenseTotal, "", expenseId));

                            // Calculate the sum of expense values
                            double expenseAmount = Double.parseDouble(expenseTotal);
                            totalExpense += expenseAmount;

                        }

                        // Combine income and expense lists into a single list
                        transactionList.addAll(expenseList);

                        Collections.sort(transactionList, new Comparator<TransactionHistory>() {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

                            @Override
                            public int compare(TransactionHistory t1, TransactionHistory t2) {
                                try {
                                    Date date1 = dateFormat.parse(t1.getDate());
                                    Date date2 = dateFormat.parse(t2.getDate());

                                    // Compare the dates in descending order
                                    int dateComparison = date2.compareTo(date1);
                                    if (dateComparison != 0) {
                                        return dateComparison;
                                    }

                                    // If the dates are the same, sort by entry (income before expense)
                                    return t1.getEntry().compareTo(t2.getEntry());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        });
                        callback.onTransactionHistoryLoaded(transactionList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
                // Update the values in the PieChart
                setValues(totalIncome, totalExpense);

                // Hide the pie chart if both eToday and iToday values are 0
                if (totalExpense == 0 && totalIncome == 0) {
                    pieChart.setVisibility(View.GONE);
                } else {
                    pieChart.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv1.setLayoutManager(layoutManager);
    }

    private void updateRecyclerView(List<TransactionHistory> transactionList) {
        adapter = new TransactionHistoryAdapter(getContext(), transactionList);
        rv1.setAdapter(adapter);

        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (TransactionHistory transaction : transactionList) {
            if (transaction.getEntry().equals("Income")) {
                double incomeAmount = Double.parseDouble(transaction.getIncomeValue());
                totalIncome += incomeAmount;
            } else if (transaction.getEntry().equals("Expense")) {
                double expenseAmount = Double.parseDouble(transaction.getExpenseValue());
                totalExpense += expenseAmount;
            }
        }

        // Update the pie chart with the calculated totals
        setValues(totalIncome, totalExpense);

        // Add OnChartValueSelectedListener to the pieChart
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Check if chart is clickable
                if (!isChartClickable) {
                    return; // Ignore click if chart is not clickable
                }

                // Set chart as not clickable temporarily to prevent spam-clicking
                isChartClickable = false;

                // Get the selected entry
                PieEntry selectedEntry = (PieEntry) e;

                // Get the label and value of the selected entry
                String label = selectedEntry.getLabel();
                float value = selectedEntry.getValue();

                // Show the label and value in a dialog or any other desired way
                showDialog(label, value);

                // Re-enable chart clickability after a short delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isChartClickable = true;
                    }
                }, 1000); // Adjust the delay time (in milliseconds) according to your needs
            }

            @Override
            public void onNothingSelected() {
                // Handle the case when nothing is selected (optional)
            }
        });

    }

    // Method to show the selected label and value in a dialog
    private void showDialog(String label, float value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Selected Entry");

        String valueText = "Value: ";
        String colourText = "RM" + value;

        // Set the color of the value text based on the label
        int valueColor;
        if (label.equals("Income")) {
            valueColor = Color.GREEN;
        } else if (label.equals("Expense")) {
            valueColor = Color.RED;
        } else {
            valueColor = Color.BLACK;
        }

        SpannableString spannableValueText = new SpannableString(colourText);
        spannableValueText.setSpan(new ForegroundColorSpan(valueColor), 0, spannableValueText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Concatenate the label and the value text
        CharSequence message = TextUtils.concat(valueText, spannableValueText);

        // Inflate a custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_selected_entry, null);

        // Find the TextView in the custom layout and set the formatted message
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        builder.setView(dialogView);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void setValues(double incomeTotal, double expenseTotal) {
        pieEntryList.clear();

        if (incomeTotal > 0) {
            pieEntryList.add(new PieEntry((float) incomeTotal, "Income"));
        }

        if (expenseTotal > 0) {
            pieEntryList.add(new PieEntry((float) expenseTotal, "Expense"));
        }

        if (pieEntryList.isEmpty()) {
            // Add a dummy entry to show the chart with no data
            pieEntryList.add(new PieEntry(1, ""));
            pieChart.setNoDataText("No Data");
        } else {
            pieChart.setNoDataText("");
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieDataSet.setColors(getColorsArray()); // Set custom colors based on entry type
        pieDataSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.white));
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setSliceSpace(3f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextColor(Color.BLACK);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private int[] getColorsArray() {
        int[] colors = new int[pieEntryList.size()];

        for (int i = 0; i < pieEntryList.size(); i++) {
            PieEntry entry = pieEntryList.get(i);

            // Set color based on entry type
            if (entry.getLabel().equals("Income")) {
                // Use different shades of green for income
                colors[i] = Color.rgb(100, 200, 100); // Light green color for income
            } else if (entry.getLabel().equals("Expense")) {
                // Use different shades of red for expense
                colors[i] = Color.rgb(200, 100, 100); // Light red color for expense
            }
        }

        return colors;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private interface TransactionHistoryCallback {
        void onTransactionHistoryLoaded(List<TransactionHistory> transactionList);
    }

    @Override
    public void onResume() {
        super.onResume();
        getListTransaction(getView()); // Fetch and update the value in case there update
    }
}