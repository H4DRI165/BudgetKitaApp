package com.example.budgetkitaapp.transaction.hostActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.databinding.FragmentTransactionBinding;
import com.example.budgetkitaapp.transaction.expense.ExpenseFragment;
import com.example.budgetkitaapp.transaction.income.IncomeFragment;
import com.example.budgetkitaapp.transaction.listTransaction.ViewTransactionFragment;

public class TransactionFragment extends Fragment {

    FragmentTransactionBinding binding;
    private Fragment currentFragment;

    public TransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle("Transaction");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTransactionBinding.inflate(inflater, container, false);
        replaceFragment(new IncomeFragment());

        binding.topTransactionView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            switch (item.getItemId()) {
                case R.id.income:
                    selectedFragment = new IncomeFragment();
                    break;
                case R.id.expense:
                    selectedFragment = new ExpenseFragment();
                    break;
                case R.id.view_transaction:
                    selectedFragment = new ViewTransactionFragment();
                    break;
                default:
                    return false;
            }
            if (currentFragment != selectedFragment) {
                replaceFragment(selectedFragment);
            }
            return true;
        });

        return binding.getRoot();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_transaction, fragment);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    // Method to handle the transaction bottom navigation item click when already in the TransactionFragment
    public void onTransactionBottomNavigationClicked() {
        // Perform any necessary actions when the transaction bottom navigation item is clicked in the same fragment
        if (currentFragment instanceof IncomeFragment) {
            // Handle IncomeFragment
        } else if (currentFragment instanceof ExpenseFragment) {
            // Handle ExpenseFragment
        } else if (currentFragment instanceof ViewTransactionFragment) {
            // Handle ViewTransactionFragment
        }
    }
}


