package com.example.budgetkitaapp.debt.listDebt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.adapter.DebtAdapter;
import com.example.budgetkitaapp.debt.addDebt.AddDebt;
import com.example.budgetkitaapp.debt.debtClass.Debt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DebtFragment extends Fragment {

    private Button addDebt;
    private TextView dAmount;
    private RecyclerView recyclerView;
    double totalDebt = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference debtRef;
    private List<Debt> debtList = new ArrayList<>(); // Initialize the list
    private DebtAdapter adapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_debt, container, false);
        requireActivity().setTitle("Debt");

        // Firebase authentication to identify the current user
        mAuth = FirebaseAuth.getInstance();

        //Set the variable in xml
        assignVariable(view);

        // Initialize the adapter using the fragment's context and debtList
        adapter = new DebtAdapter(getContext(), debtList); // Initialize the adapter
        recyclerView.setAdapter(adapter);

        // Firebase database reference for the current user's debt
        debtRef = FirebaseDatabase.getInstance()
                .getReference("Accounts")
                .child(mAuth.getCurrentUser().getUid())
                .child("Debt");

        //Retrieve the total debt from firebase
        getDebtAmount();

        //Retrieve list of debt from Firebase
        showListDebt();

        addDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goAdd = new Intent(getActivity(), AddDebt.class);
                startActivity(goAdd);
            }
        });
        return view;
    }

    private void assignVariable(View view){

        addDebt = view.findViewById(R.id.buttonAddDebt);
        dAmount =view.findViewById(R.id.tvCalculate);
        recyclerView = view.findViewById(R.id.recyclerView);

        setRecyclerView();

    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }
    private void getDebtAmount() {
        debtRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalDebt = 0; // Initialize the total debt
                for (DataSnapshot debtSnapshot : dataSnapshot.getChildren()) {
                    String debtStatus = debtSnapshot.child("debtStatus").getValue(String.class);

                    if (debtStatus != null && debtStatus.equals("Not Paid")) { // Check debtStatus
                        String debtTotalStr = debtSnapshot.child("debtTotal").getValue(String.class);
                        double debtTotal = 0;

                        try {
                            debtTotal = Double.parseDouble(debtTotalStr);
                        } catch (NumberFormatException e) {
                            // Handle parsing error
                        }

                        totalDebt += debtTotal;
                    }
                }

                // Set the total debt to amount
                dAmount.setText("RM " + String.valueOf(totalDebt));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void showListDebt(){

        debtList.clear();
        debtRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing data before adding new items
                debtList.clear();

                // Iterate through the debt entries and extract data
                for (DataSnapshot debtSnapshot : snapshot.getChildren()) {
                    String debtName = debtSnapshot.child("debtName").getValue(String.class);
                    String debtDate = debtSnapshot.child("debtDate").getValue(String.class);
                    String debtTotal = debtSnapshot.child("debtTotal").getValue(String.class);
                    String debtStatus = debtSnapshot.child("debtStatus").getValue(String.class);

                    // Create a Debt object or data structure to hold this data
                    Debt debt = new Debt(debtName, debtTotal, debtDate, debtStatus);

                    // Add the Debt object to a List or data structure for sorting later
                    debtList.add(debt);
                }

                // Sort the debtList in descending order based on debtDate
                Collections.sort(debtList, new Comparator<Debt>() {
                    @Override
                    public int compare(Debt debt1, Debt debt2) {
                        // Parse the date strings to Date objects for comparison
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                        try {
                            Date date1 = sdf.parse(debt1.getDebtDate());
                            Date date2 = sdf.parse(debt2.getDebtDate());

                            // Compare in descending order
                            return date2.compareTo(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

                // Update RecyclerView adapter with the new sorted data
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getDebtAmount(); // Fetch and update totalDebt value
    }

}