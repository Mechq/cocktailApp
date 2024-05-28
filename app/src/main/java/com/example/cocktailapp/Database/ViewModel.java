package com.example.cocktailapp.Database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cocktailapp.Domain.Cocktail;
import com.example.cocktailapp.Domain.Favorite;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewModel extends AndroidViewModel {
    private CocktailRepository cocktailRepository;
    private LiveData<List<Cocktail>> cocktailList;
    private CocktailDao cocktailDao; // Add this variable

    public ViewModel(@NonNull Application application) {
        super(application);
        cocktailRepository = new CocktailRepository(application);
        cocktailList = cocktailRepository.getAllCocktails();
        Database database = Database.getDatabase(application);
        cocktailDao = database.cocktailDao(); // Initialize cocktailDao
    }

    public LiveData<List<Cocktail>> getCocktailList() {
        return cocktailList;
    }
    public LiveData<Boolean> isCocktailFavorite(int cocktailId) {
        return cocktailRepository.isCocktailFavorite(cocktailId);
    }


    public void insertCocktail(Cocktail cocktail) {
        cocktailRepository.insertCocktail(cocktail);
    }

    public void removeFromFavorites(Favorite favorite) {
        cocktailRepository.deleteFavorite(favorite);
    }
    public void addToFavorites(Favorite favorite) {
        cocktailRepository.addToFavorite(favorite);
    }

    public LiveData<Boolean> isCocktailInDatabase(int itemId) {
        MutableLiveData<Boolean> isItemInDatabase = new MutableLiveData<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            boolean exists = false;
            Cocktail cocktail = cocktailDao.getCocktailById(itemId); // Access getCocktailById from CocktailDao
            exists = cocktail != null;
            isItemInDatabase.postValue(exists);
        });
        executorService.shutdown();
        return isItemInDatabase;
    }

    public LiveData<Integer> getCocktailCount() {
        return cocktailRepository.getCocktailCount();
    }
    public LiveData<List<Integer>> getFavoriteCocktailIds() {
        return cocktailRepository.getFavoriteCocktailIds();
    }
    public LiveData<List<Integer>> getAlcoholicCocktailIds() {
        return cocktailRepository.getAlcoholicCocktailIds();
    }
    public LiveData<List<Integer>> getNonAlcoholicCocktailIds() {
        return cocktailRepository.getNonAlcoholicCocktailIds();
    }
}

