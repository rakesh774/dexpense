package com.example.dexpenses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface BalanceDao {
    @Query("SELECT * FROM balance WHERE id = 1")
    Balance getBalance();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Balance balance);

    @Update
    void update(Balance balance);
}
