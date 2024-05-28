package com.example.cocktailapp.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.cocktailapp.Domain.Cocktail;
import com.example.cocktailapp.Domain.Favorite;

import java.util.List;

public class CocktailRepository {
    private CocktailDao cocktailDao;
    private FavoriteDao favoriteDao;
    private Database database;
    private LiveData<List<Cocktail>> cocktailList;

    public CocktailRepository(Application application){
        database = Database.getDatabase(application);
        cocktailDao = database.cocktailDao();
        cocktailList = cocktailDao.getAllCocktails();
        favoriteDao = database.favoriteDao();
    }
    public LiveData<List<Cocktail>> getAllCocktails(){
        return cocktailDao.getAllCocktails();
    }
    public void insertCocktail(final Cocktail cocktail){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.cocktailDao().insertCocktails(cocktail);
                return null;
            }
        }.execute();
    }
    public LiveData<Integer> getCocktailCount() {
        return cocktailDao.getCocktailCount();
    }
    public LiveData<List<Integer>> getFavoriteCocktailIds() {
        return database.favoriteDao().getAllFavoriteCocktailIds();
    }
    public LiveData<Boolean> isCocktailFavorite(int cocktailId) {
        return database.favoriteDao().isCocktailFavorite(cocktailId);
    }
    public void addToFavorite(Favorite favorite) {
        new InsertFavoriteAsyncTask(favoriteDao).execute(favorite);
    }
    public void deleteFavorite(Favorite favorite) {
        new DeleteFavoriteAsyncTask(favoriteDao).execute(favorite);
    }

    public LiveData<List<Integer>> getAlcoholicCocktailIds() {
        return database.cocktailDao().getAllAlcoholicIds();
    }
    public LiveData<List<Integer>> getNonAlcoholicCocktailIds() {
        return database.cocktailDao().getAllNonAlcoholicIds();
    }

    private static class DeleteFavoriteAsyncTask extends AsyncTask<Favorite, Void, Void> {
        private FavoriteDao favoriteDAO;

        DeleteFavoriteAsyncTask(FavoriteDao favoriteDAO) {
            this.favoriteDAO = favoriteDAO;
        }

        @Override
        protected Void doInBackground(Favorite... favorites) {
            favoriteDAO.deleteFavorite(favorites[0]);
            return null;
        }
    }
    private static class InsertFavoriteAsyncTask extends AsyncTask<Favorite, Void, Void> {
        private FavoriteDao favoriteDAO;

        InsertFavoriteAsyncTask(FavoriteDao favoriteDAO) {
            this.favoriteDAO = favoriteDAO;
        }

        @Override
        protected Void doInBackground(Favorite... favorites) {
            favoriteDAO.insertFavorite(favorites[0]);
            return null;
        }
    }




}
