package com.example.budgetkitaapp.transaction.expense;

public class Expense {

    private String expenseName, expenseTotal, expenseDate, expenseCat, expenseLocation;

    public Expense(){
        //empty constructor
    }

    public Expense(String expenseName, String expenseCat, String expenseTotal, String expenseDate, String expenseLocation) {

        this.expenseName = expenseName;
        this.expenseTotal = expenseTotal;
        this.expenseDate = expenseDate;
        this.expenseCat = expenseCat;
        this.expenseLocation = expenseLocation;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public String getExpenseTotal() {
        return expenseTotal;
    }

    public void setExpenseTotal(String expenseTotal) {
        this.expenseTotal = expenseTotal;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getExpenseCat() {
        return expenseCat;
    }

    public void setExpenseCat(String expenseCat) {
        this.expenseCat = expenseCat;
    }

    public String getExpenseLocation() {
        return expenseLocation;
    }

    public void setExpenseLocation(String expenseLocation) {
        this.expenseLocation = expenseLocation;
    }
}
