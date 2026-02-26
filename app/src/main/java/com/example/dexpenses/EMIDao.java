package com.example.dexpenses;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EMIDao {
    @Insert
    void insert(EMI emi);

    @Update
    void update(EMI emi);

    @Delete
    void delete(EMI emi);

    @Query("SELECT * FROM emis")
    List<EMI> getAllEMIs();

    @Query("SELECT SUM(amount) FROM emis")
    double getTotalEMIAmount();
}
