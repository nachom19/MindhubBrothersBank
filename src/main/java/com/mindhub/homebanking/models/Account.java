package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String number;
    private LocalDate creationDate;
    private Double balance;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_id")
    private Client ownerAccount;
    @OneToMany(mappedBy="sourceAccount", fetch=FetchType.EAGER)
    private Set<Transaction> transactions = new HashSet<>();


    //constructores
    public Account() {
    }

    public Account(String number, Double balance) {
        this.number = number;
        this.balance = balance;
        creationDate =LocalDate.now();
    }

    //getters y setters

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Client getOwnerAccount() {
        return ownerAccount;
    }

    public void setOwnerAccount(Client ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }



    //Agregar transaccion a una cuenta
    public void addTransaction(Transaction transaction) {
        transaction.setSourceAccount(this);
        transactions.add(transaction);
    }
}



