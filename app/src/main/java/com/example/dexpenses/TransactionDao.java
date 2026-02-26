package com.example.dexpenses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    List<Transaction> getAllTransactions();

    @Query("SELECT SUM(amount) FROM transactions WHERE accountType = 'Air' AND transactionType = 'Spent' AND isLending = 0")
    double getAirCashSpent();

    @Query("SELECT SUM(amount) FROM transactions WHERE accountType = 'Air' AND transactionType = 'Received'")
    double getAirCashReceived();

    @Query("SELECT SUM(amount) FROM transactions WHERE accountType = 'Solid' AND transactionType = 'Spent' AND isLending = 0")
    double getSolidCashSpent();

    @Query("SELECT SUM(amount) FROM transactions WHERE accountType = 'Solid' AND transactionType = 'Received'")
    double getSolidCashReceived();

    @Query("SELECT SUM(amount) FROM transactions WHERE isLending = 1")
    double getTotalLent();
}
