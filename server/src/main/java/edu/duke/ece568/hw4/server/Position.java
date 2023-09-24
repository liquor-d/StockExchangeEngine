package edu.duke.ece568.hw4.server;

import javax.persistence.*;

@javax.persistence.Entity
@javax.persistence.Table(name = "positions")
public class Position {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int id;
    @Basic
    private String symbol;
    @Basic
    private double amount;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "accountId", referencedColumnName = "accountId")
    private Account account;
//    @JoinColumn(name= "accountID", referencedColumnName = "accountID", nullable = false)
//    private int accountId;

    public Position() {
    }

    public Position(String symbol, double amount, Account account) {
        this.symbol = symbol;
        this.amount = amount;
        this.account = account;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double newAmount) {
        this.amount = newAmount;
    }

    public int getAccountId() {
        return account.getAccountId();
    }

    public int getId() {
        return id;
    }

}
