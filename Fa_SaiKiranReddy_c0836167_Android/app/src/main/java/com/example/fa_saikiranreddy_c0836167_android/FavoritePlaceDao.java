package com.example.fa_saikiranreddy_c0836167_android;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoritePlaceDao {
    @Query("SELECT * FROM FavoritePlace")
    List<FavoritePlace> getAll();

    @Insert
    void insert(FavoritePlace favoritePlace);

    @Delete
    void delete(FavoritePlace favoritePlace);

    @Update
    void update(FavoritePlace favoritePlace);

    @Query("SELECT COUNT(id) FROM FavoritePlace")
    int getCount();
}
