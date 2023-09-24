package edu.duke.ece568.hw4.server;

import javax.persistence.*;
//import javax.persistence.Basic;

@javax.persistence.Entity
@javax.persistence.Table(name = "orders")
public class BuySellOrder {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int orderId;
    @Basic
    private double limitPrice;
    @Basic
    private double amount;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "accountId", referencedColumnName = "accountId")
    private Account account;
    @Basic
    private String symbol;
    @Basic
    private boolean isBuyOrder;
//    private String status;
//    //OPEN/CANCEL/CLOSE

    public BuySellOrder() {
    }

    public BuySellOrder(double limitPrice, double amount, Account account, String symbol, boolean isBuyOrder) {
//        this.orderId = orderId;
        this.limitPrice = limitPrice;
        this.amount = amount;
        this.account = account;
        this.symbol = symbol;
        this.isBuyOrder = isBuyOrder;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    public boolean isBuy() {
        return isBuyOrder;
    }

    public int getOrderId() {
        return orderId;
    }

    public Account getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSymbol() {
        return symbol;
    }
}
