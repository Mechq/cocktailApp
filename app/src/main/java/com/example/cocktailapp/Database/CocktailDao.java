package com.example.cocktailapp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cocktailapp.Domain.Cocktail;

import java.util.List;

@Dao
public interface CocktailDao {

    @Insert
    void insertCocktails(Cocktail cocktails);
    @Query("SELECT * FROM cocktails")
    LiveData<List<Cocktail>> getAllCocktails();

    @Query("SELECT * FROM Cocktails WHERE id = :cocktailId LIMIT 1")
    Cocktail getCocktailById(int cocktailId);
    @Query("SELECT COUNT(*) FROM Cocktails")
    LiveData<Integer> getCocktailCount();

    @Query("SELECT id FROM Cocktails WHERE isAlcoholic = 'Alcoholic'")
    LiveData<List<Integer>> getAllAlcoholicIds();

    @Query("SELECT id FROM Cocktails WHERE isAlcoholic = 'Non alcoholic'")
    LiveData<List<Integer>> getAllNonAlcoholicIds();
}
