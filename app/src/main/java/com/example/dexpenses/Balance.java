package com.example.dexpenses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "balance")
public class Balance {
    @PrimaryKey
    private int id = 1; // Single row for settings
    
    private double initialAir;
    private double initialSolid;
    private double monthlyLimit;

    public Balance(double initialAir, double initialSolid, double monthlyLimit) {
        this.initialAir = initialAir;
        this.initialSolid = initialSolid;
        this.monthlyLimit = monthlyLimit;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getInitialAir() { return initialAir; }
    public void setInitialAir(double initialAir) { this.initialAir = initialAir; }

    public double getInitialSolid() { return initialSolid; }
    public void setInitialSolid(double initialSolid) { this.initialSolid = initialSolid; }

    public double getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(double monthlyLimit) { this.monthlyLimit = monthlyLimit; }
}
