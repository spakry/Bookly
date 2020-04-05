package com.example.bookly;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "client_table")
class Client implements Serializable {

    private String name;
    private long balance;
    private long rate;
    private long lastPaid;
    private String lastPaidDate;
    @Ignore
    private int sessionsLeft;


    @PrimaryKey (autoGenerate = true)
    private int id;

    public Client(String name, long rate, long balance) {
        this.name = name;
        this.rate = rate;
        this.balance=balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public int getSessionsLeft() {
        return sessionsLeft;
    }

    public void setSessionsLeft(int sessionsLeft) {
        this.sessionsLeft = sessionsLeft;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public long getLastPaid() {
        return lastPaid;
    }

    public void setLastPaid(long lastPaid) {
        this.lastPaid = lastPaid;
    }

    public String getLastPaidDate() {
        return lastPaidDate;
    }

    public void setLastPaidDate(String lastPaidDate) {
        this.lastPaidDate = lastPaidDate;
    }
}
