package com.example.fa_saikiranreddy_c0836167_android;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private Context context;
    private static DatabaseClient mInstance;

    private AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "MyFavoritePlaces").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(context);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}