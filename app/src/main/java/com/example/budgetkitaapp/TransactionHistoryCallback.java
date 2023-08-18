package com.example.budgetkitaapp;

import java.util.List;

public interface TransactionHistoryCallback {
    void onTransactionHistoryLoaded(List<TransactionHistory> transactionList);
}
