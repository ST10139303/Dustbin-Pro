package com.example.dustbinpro.models;

public class Payment {
    private double amount;
    private String status;
    private long timestamp;

    // Required no-argument constructor for Firestore
    public Payment() { }

    public Payment(double amount, String status, long timestamp) {
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
