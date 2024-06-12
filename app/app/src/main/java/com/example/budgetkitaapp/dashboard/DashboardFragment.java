package com.example.budgetkitaapp.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.budgetkitaapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    TextView iToday, eToday, textCalculate, tvCalculate;
    PieChart pieChart;
    List<PieEntry> pieEntryList;
    private FirebaseAuth mAuth;
    double totalIncome = 0;
    double totalExpense = 0;

    // Flag to track chart clickability
    private boolean isChartClickable = true;

    private Context mContext;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        requireActivity().setTitle("Dashboard");

        // Assign variables
        iToday = v.findViewById(R.id.iTotal);
        eToday = v.findViewById(R.id.eTotal);
        textCalculate = v.findViewById(R.id.tvProfit);
        tvCalculate = v.findViewById(R.id.tvCalculate);
        pieChart = v.findViewById(R.id.chart);

        pieEntryList = new ArrayList<>();
        setUpChart();

        // Firebase authentication to identify the current user
        mAuth = FirebaseAuth.getInstance();
        // Connect to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get income reference
        DatabaseReference incomeRef = database.getReference("Accounts").child(mAuth.getCurrentUser().getUid()).child("Income");
        // Get expenses reference
        DatabaseReference expensesRef = database.getReference("Accounts").child(mAuth.getCurrentUser().getUid()).child("Expense");

        // Date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(new Date());

        // Fetch today income from Firebase
        Query incomeQuery = incomeRef.orderByChild("incomeDate").equalTo(currentDate);
        incomeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIncome = 0; // Reset totalIncome before updating
                for (DataSnapshot incomeSnapshot : snapshot.getChildren()) {
                    String incomeTotalStr = incomeSnapshot.child("incomeTotal").getValue(String.class);
                    double incomeTotal = 0;

                    try {
                        incomeTotal = Double.parseDouble(incomeTotalStr);
                    } catch (NumberFormatException e) {
                        // Handle any errors that occur during the string-to-double conversion
                    }

                    totalIncome += incomeTotal;
                }

                // Set the total income for today to the iToday TextView
                iToday.setText("RM " + String.valueOf(totalIncome));

                // Calculate the income-expense difference
                double incomeExpensesDifference = totalIncome - totalExpense;
                updateIncomeExpensesDifference(incomeExpensesDifference);

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
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur during the database operation
            }
        });

        // Fetch today expenses from Firebase
        Query expenseQuery = expensesRef.orderByChild("expenseDate").equalTo(currentDate);
        expenseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalExpense = 0; // Reset totalExpense before updating
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    String expenseTotalStr = expenseSnapshot.child("expenseTotal").getValue(String.class);
                    double expenseTotal = 0;

                    try {
                        expenseTotal = Double.parseDouble(expenseTotalStr);
                    } catch (NumberFormatException e) {
                        // Handle any errors that occur during the string-to-double conversion
                    }

                    totalExpense += expenseTotal;
                }

                // Set the total expense for today to the eToday TextView
                eToday.setText("RM " + String.valueOf(totalExpense));

                // Calculate the income-expense difference
                double incomeExpensesDifference = totalIncome - totalExpense;
                updateIncomeExpensesDifference(incomeExpensesDifference);

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
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur during the database operation
            }
        });

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

        return v;
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

    private void updateIncomeExpensesDifference(double incomeExpensesDifference) {
        String formattedDifference;

        if (incomeExpensesDifference > 0) {
            formattedDifference = String.format("%.2f", incomeExpensesDifference);
            tvCalculate.setText("RM " + formattedDifference);
            tvCalculate.setTextColor(Color.parseColor("#00ff00")); // Green color for positive difference
            textCalculate.setText("Profit:");
        } else if (incomeExpensesDifference < 0) {
            formattedDifference = String.format("%.2f", Math.abs(incomeExpensesDifference));
            tvCalculate.setText("RM " + formattedDifference);
            tvCalculate.setTextColor(Color.parseColor("#ff0000")); // Red color for negative difference
            textCalculate.setText("Loss:");
        } else {
            tvCalculate.setText("RM 0.00");
            tvCalculate.setTextColor(Color.parseColor("#000000")); // Black color for zero difference
            textCalculate.setText("Profit/Loss:");
        }
    }
}

