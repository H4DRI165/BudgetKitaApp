package com.example.budgetkitaapp.transaction.income;

public class Income {

    private String incomeName, incomeCat ,incomeTotal, incomeDate;

    public Income(){
        //empty constructor
    }

    public Income(String incomeName, String incomeCat , String incomeTotal, String incomeDate) {

        this.incomeName = incomeName;
        this.incomeTotal = incomeTotal;
        this.incomeDate = incomeDate;
        this.incomeCat = incomeCat;

    }

    public String getIncomeName() {
        return incomeName;
    }

    public void setIncomeName(String incomeName) {
        this.incomeName = incomeName;
    }

    public String getIncomeCat() {
        return incomeCat;
    }

    public void setIncomeCat(String incomeCat) {
        this.incomeCat = incomeCat;
    }

    public String getIncomeTotal() {
        return incomeTotal;
    }

    public void setIncomeTotal(String incomeTotal) {
        this.incomeTotal = incomeTotal;
    }

    public String getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(String incomeDate) {
        this.incomeDate = incomeDate;
    }


}
