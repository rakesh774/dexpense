package com.example.dexpenses;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Transaction.class, Balance.class, EMI.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract TransactionDao transactionDao();
    public abstract BalanceDao balanceDao();
    public abstract EMIDao emiDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "expense_tracker_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
