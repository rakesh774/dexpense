package com.example.dexpenses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private double amount;
    private String category;
    private String accountType; // "Air" or "Solid"
    private String transactionType; // "Spent" or "Received"
    private boolean isLending;
    private long timestamp;

    public Transaction(double amount, String category, String accountType, String transactionType, boolean isLending, long timestamp) {
        this.amount = amount;
        this.category = category;
        this.accountType = accountType;
        this.transactionType = transactionType;
        this.isLending = isLending;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public boolean isLending() { return isLending; }
    public void setLending(boolean lending) { isLending = lending; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
