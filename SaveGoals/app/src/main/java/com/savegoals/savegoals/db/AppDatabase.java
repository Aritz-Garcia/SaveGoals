package com.savegoals.savegoals.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.savegoals.savegoals.data.dao.EntradasDao;
import com.savegoals.savegoals.data.dao.ObjetivosDao;
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;

@Database(entities = {Objetivos.class, Entradas.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "savegoalsDB")
                            //  .createFromAsset("databases/savegoalsDB.db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ObjetivosDao objetivosDao();
    public abstract EntradasDao entradasDao();
}
