package com.example.cocktailapp.Domain;

import com.example.cocktailapp.Application.CocktailResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CocktailAPI {

    @GET("api/json/v1/1/search.php?f=a")
    Call<CocktailResponse> getCocktails();

}
