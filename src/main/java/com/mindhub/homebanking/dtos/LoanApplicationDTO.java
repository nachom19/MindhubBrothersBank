package com.mindhub.homebanking.dtos;

public class LoanApplicationDTO {
    private long loanId;
    private Double amount;
    private int payments;
    private String toAccountNumber;

    //contructor

    public LoanApplicationDTO(long loanId, Double amount, int payments, String toAccountNumber) {
        this.loanId = loanId;
        this.amount = amount;
        this.payments = payments;
        this.toAccountNumber = toAccountNumber;
    }

    //getters

    public long getLoanId() {
        return loanId;
    }

    public Double getAmount() {
        return amount;
    }

    public int getPayments() {
        return payments;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }
}
