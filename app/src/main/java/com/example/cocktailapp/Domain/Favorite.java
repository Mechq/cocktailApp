package com.example.cocktailapp.Domain;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Favorites")
public class Favorite {

    @PrimaryKey(autoGenerate = true)
    private int id;

    public Favorite(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
