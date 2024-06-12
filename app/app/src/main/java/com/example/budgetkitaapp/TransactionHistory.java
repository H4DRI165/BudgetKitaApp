package com.example.budgetkitaapp;

import androidx.recyclerview.widget.RecyclerView;

public class TransactionHistory {
    private String entry, date, incomeValue, expenseValue;
    private String incomeId, expenseId;

    public TransactionHistory(String entry, String date, String incomeValue, String expenseValue, String incomeId, String expenseId) {
        this.entry = entry;
        this.date = date;
        this.incomeValue = incomeValue;
        this.expenseValue = expenseValue;
        this.incomeId = incomeId;
        this.expenseId = expenseId;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIncomeValue() {
        return incomeValue;
    }

    public void setIncomeValue(String incomeValue) {
        this.incomeValue = incomeValue;
    }

    public String getExpenseValue() {
        return expenseValue;
    }

    public void setExpenseValue(String expenseValue) {
        this.expenseValue = expenseValue;
    }

    public String getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(String incomeId) {
        this.incomeId = incomeId;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }
}
