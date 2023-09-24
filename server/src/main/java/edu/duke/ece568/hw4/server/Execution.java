package edu.duke.ece568.hw4.server;

import javax.persistence.*;
import java.util.Date;

@javax.persistence.Entity
@javax.persistence.Table(name = "executions")
public class Execution {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private BuySellOrder order;
    @Basic
    private double amount;
    @Basic
    private String type;
    @Basic
    private double price;
    @Basic
    private Date date;

    public Execution() {
    }

    public Execution(String type, double price, double amount, BuySellOrder order) {
        this.order = order;
        this.amount = amount;
        this.price = price;
        this.type = type;
        Date date = new Date();
        long longDate = date.getTime();
        this.date = new Date(longDate);
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

}
