package com.example.cocktailapp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cocktailapp.Domain.Cocktail;
import com.example.cocktailapp.Domain.Favorite;

import java.util.List;
@Dao
public interface FavoriteDao {
    @Insert
    void insertFavorite(Favorite favorite);

    @Delete
    void deleteFavorite(Favorite favorite);
    @Query("SELECT * FROM Favorites")
    LiveData<List<Favorite>> getAllFavorites();

    @Query("SELECT EXISTS(SELECT 1 FROM Favorites WHERE id = :cocktailId LIMIT 1)")
    LiveData<Boolean> isCocktailFavorite(int cocktailId);

    @Query("SELECT id FROM Favorites")
    LiveData<List<Integer>> getAllFavoriteCocktailIds();
}
