package com.example.budgetkitaapp.transaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.dashboard.DashboardFragment;
import com.example.budgetkitaapp.databinding.ActivityEditTransactionBinding;
import com.example.budgetkitaapp.databinding.ActivityHomeBinding;
import com.example.budgetkitaapp.debt.DebtFragment;
import com.example.budgetkitaapp.other.OtherFragment;
import com.example.budgetkitaapp.transaction.expense.ExpenseFragment;
import com.example.budgetkitaapp.transaction.fragment.TransactionFragment;
import com.example.budgetkitaapp.transaction.income.IncomeFragment;

public class EditTransaction extends AppCompatActivity {

    ActivityEditTransactionBinding binding;
    private boolean isFragmentTransactionInProgress = false;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTransactionBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_edit_transaction);

        String entry = getIntent().getStringExtra("entry");
        String incomeId = getIntent().getStringExtra("incomeId");
        String expenseId = getIntent().getStringExtra("expenseId");

        if (entry.equals("Income")) {
            disableExpenseMenuItem();
            openIncomeFragment(entry, incomeId);
        }else{
            disableIncomeMenuItem();
            openExpenseFragment(entry, expenseId);
        }
    }

    private void openIncomeFragment(String entry, String incomeId) {
        IncomeFragment incomeFragment = IncomeFragment.newInstance(entry, incomeId);
        replaceFragment(incomeFragment);
    }

    private void openExpenseFragment(String entry, String expenseId) {
        ExpenseFragment expenseFragment = ExpenseFragment.newInstance(entry, expenseId);
        replaceFragment(expenseFragment);
    }

    private void disableIncomeMenuItem() {
        MenuItem incomeMenuItem = binding.topTransactionView.getMenu().findItem(R.id.goIncome);
        incomeMenuItem.setEnabled(false);
    }

    private void disableExpenseMenuItem() {
        MenuItem expenseMenuItem = binding.topTransactionView.getMenu().findItem(R.id.goExpense);
        expenseMenuItem.setEnabled(false);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_transaction, fragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
        currentFragment = fragment;
        isFragmentTransactionInProgress = false;
    }


    @Override
    public void onBackPressed() {
        // Handle back press by finishing the current activity
        finish();
    }
}

