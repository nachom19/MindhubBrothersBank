package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private TransactionType type;
    private Double amount;
    private String description;
    private LocalDate date;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_id")
    private Account sourceAccount;

    //constructores
    public Transaction() {
    }

    public Transaction(TransactionType type, Double amount, String description, LocalDate date) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    //getters

    public long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    //Setters

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }
}
