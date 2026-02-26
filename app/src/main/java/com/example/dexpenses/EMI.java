package com.example.dexpenses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "emis")
public class EMI {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;
    private double amount;
    private int dueDate; // Day of month

    public EMI(String name, double amount, int dueDate) {
        this.name = name;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getDueDate() { return dueDate; }
    public void setDueDate(int dueDate) { this.dueDate = dueDate; }
}
