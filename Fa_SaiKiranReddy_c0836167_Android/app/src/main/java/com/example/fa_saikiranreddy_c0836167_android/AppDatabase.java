package com.example.fa_saikiranreddy_c0836167_android;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FavoritePlace.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavoritePlaceDao placeDao();
}