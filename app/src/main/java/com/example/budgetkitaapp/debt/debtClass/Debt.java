package com.example.budgetkitaapp.debt.debtClass;

public class Debt {

    private String debtName, debtTotal, debtDate, debtStatus, debtDatePaid;
    private String debtId;


    public Debt(String debtName, String debtTotal, String debtDate, String debtStatus, String debtDatePaid,String debtId) {
        this.debtName = debtName;
        this.debtTotal = debtTotal;
        this.debtDate = debtDate;
        this.debtStatus = debtStatus;
        this.debtDatePaid = debtDatePaid;
        this.debtId = debtId;
    }

    public Debt(String debtName, String debtTotal, String debtDate, String debtStatus, String debtDatePaid) {
        this.debtName = debtName;
        this.debtTotal = debtTotal;
        this.debtDate = debtDate;
        this.debtStatus = debtStatus;
        this.debtDatePaid = debtDatePaid;
    }

    public String getDebtDatePaid() {
        return debtDatePaid;
    }

    public void setDebtDatePaid(String debtDatePaid) {
        this.debtDatePaid = debtDatePaid;
    }

    public String getDebtId() {
        return debtId;
    }

    public void setDebtId(String debtId) {
        this.debtId = debtId;
    }

    public String getDebtName() {
        return debtName;
    }

    public void setDebtName(String debtName) {
        this.debtName = debtName;
    }

    public String getDebtTotal() {
        return debtTotal;
    }

    public void setDebtTotal(String debtTotal) {
        this.debtTotal = debtTotal;
    }

    public String getDebtDate() {
        return debtDate;
    }

    public void setDebtDate(String debtDate) {
        this.debtDate = debtDate;
    }

    public String getDebtStatus() {
        return debtStatus;
    }

    public void setDebtStatus(String debtStatus) {
        this.debtStatus = debtStatus;
    }
}
