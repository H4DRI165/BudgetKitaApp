package com.example.budgetkitaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.budgetkitaapp.dashboard.DashboardFragment;
import com.example.budgetkitaapp.databinding.ActivityHomeBinding;
import com.example.budgetkitaapp.debt.listDebt.DebtFragment;
import com.example.budgetkitaapp.other.OtherFragment;
import com.example.budgetkitaapp.transaction.hostActivity.TransactionFragment;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    private boolean isFragmentTransactionInProgress = false;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new DashboardFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (isFragmentTransactionInProgress) {
                return false; // Ignore click if fragment transaction is already in progress
            }

            isFragmentTransactionInProgress = true;

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.dashboard:
                    selectedFragment = new DashboardFragment();
                    break;
                case R.id.transaction:
                    selectedFragment = new TransactionFragment();
                    break;
                case R.id.debt:
                    selectedFragment = new DebtFragment();
                    break;
                case R.id.other:
                    selectedFragment = new OtherFragment();
                    break;
            }

            if (selectedFragment != null && selectedFragment.getClass().equals(currentFragment.getClass())) {
                // If the selected fragment is the same as the current fragment, do nothing
                isFragmentTransactionInProgress = false;
                return true;
            }

            replaceFragment(selectedFragment);
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mapView1, fragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
        currentFragment = fragment;
        isFragmentTransactionInProgress = false;
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.mapView1);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); // Minimize the app to the background
    }
}


