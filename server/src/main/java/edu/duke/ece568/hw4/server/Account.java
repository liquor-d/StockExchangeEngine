package edu.duke.ece568.hw4.server;

import javax.persistence.Basic;

@javax.persistence.Entity
public class Account {
    @javax.persistence.Id
    private int accountId;
    @Basic
    private double balance;

    public Account() {
    }

    public Account(int id, double balance) {
        this.accountId = id;
        this.balance = balance;
    }


    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int id) {
        this.accountId = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
