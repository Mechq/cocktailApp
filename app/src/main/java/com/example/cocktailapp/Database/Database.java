package com.example.cocktailapp.Database;


import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.cocktailapp.Domain.Cocktail;
import com.example.cocktailapp.Domain.Favorite;
import com.example.cocktailapp.Domain.StringListConverter;

@TypeConverters({StringListConverter.class})
@androidx.room.Database(entities = {Cocktail.class, Favorite.class}, version = 3, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract CocktailDao cocktailDao();
    public abstract FavoriteDao favoriteDao();

    public static volatile Database INSTANCE;

    public static Database getDatabase(Context context){
        if(INSTANCE==null){
            synchronized (Database.class){
                if (INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Database.class, "cocktails.db")
                            .fallbackToDestructiveMigration()
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}

