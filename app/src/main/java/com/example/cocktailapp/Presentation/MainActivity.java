package com.example.cocktailapp.Presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailapp.Application.CocktailAdapter;
import com.example.cocktailapp.Application.CocktailResponse;
import com.example.cocktailapp.Application.RecyclerViewInterface;
import com.example.cocktailapp.Database.ViewModel;
import com.example.cocktailapp.Domain.Cocktail;
import com.example.cocktailapp.Domain.CocktailAPI;
import com.example.cocktailapp.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface, Serializable {
    ViewModel cocktailViewModel;
    private List<Cocktail> cocktails;
    private MainActivity mainActivityInstance;
    private RecyclerView recyclerView;
    private final String LOG_TAG = "MainActivity";
    String filterSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String savedLanguage;
        String selectedFilter = "";
        if (savedInstanceState != null) {
            savedLanguage = savedInstanceState.getString("language", "en");
            selectedFilter = savedInstanceState.getString("filter", "");
        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            savedLanguage = prefs.getString("language", "en");
            Log.e(LOG_TAG , "The savedInstanceState is empty");
        }
        setLocale(savedLanguage);
        mainActivityInstance = this;
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cocktails = new ArrayList<>();

        cocktailViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        recyclerView.setAdapter(new CocktailAdapter(getApplicationContext(), cocktails, mainActivityInstance));

        switch (selectedFilter) {
            case "alcoholic":
                cocktailViewModel.getAlcoholicCocktailIds().observe(this, this::setFilter);
                break;
            case "non_alcoholic":
                cocktailViewModel.getNonAlcoholicCocktailIds().observe(this, this::setFilter);
                break;
            case "favorites":
                cocktailViewModel.getFavoriteCocktailIds().observe(this, this::setFilter);
                break;
        }


        if (!databaseExists()) {

            Log.v(LOG_TAG, "Database does not exist, fetching cocktails from API");
            fetchAndInsertCocktails();
        } else {

            Log.v(LOG_TAG, "Database exists, observing changes in cocktail list");
        }
        observeCocktailList();

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.home_icon_silhouette);
        } else {
            Log.e(LOG_TAG,  "actionBar is not found");
        }
    }




    private boolean databaseExists() {
        // Check if the database file exists
        File dbFile = getDatabasePath("cocktails.db");
        return dbFile.exists();
    }
    private void observeCocktailList() {
        cocktailViewModel.getCocktailList().observe(this, cocktailsFromDatabase -> {
            cocktails.clear();
            cocktails.addAll(cocktailsFromDatabase);
            recyclerView.getAdapter().notifyDataSetChanged();
            Log.v(LOG_TAG, cocktails.size()+ " cocktails");
        });
    }


    private void fetchAndInsertCocktails() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.thecocktaildb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CocktailAPI cocktailApi = retrofit.create(CocktailAPI.class);

        Call<CocktailResponse> call = cocktailApi.getCocktails();

        call.enqueue(new Callback<CocktailResponse>() {
            @Override
            public void onResponse(Call<CocktailResponse> call, Response<CocktailResponse> response) {
                CocktailResponse cocktailResponse = response.body();
                List<Cocktail> fetchedCocktails = cocktailResponse.getDrinks();


                for (Cocktail cocktail : fetchedCocktails) {
                    checkAndInsertCocktail(cocktail);
                }

                Toast.makeText(MainActivity.this, "API request count: " + fetchedCocktails.size(), Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", "Successful request");
            }

            @Override
            public void onFailure(Call<CocktailResponse> call, Throwable t) {
                Log.e("MainActivity", "Unsuccessful request");
            }
        });
    }

    private void checkAndInsertCocktail(final Cocktail cocktail) {

        cocktailViewModel.isCocktailInDatabase(cocktail.getId()).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean exists) {
                if (!exists) {

                    insertCocktailIntoDatabase(cocktail);
                }
            }
        });
    }

    private void insertCocktailIntoDatabase(final Cocktail cocktail) {
        cocktailViewModel.insertCocktail(cocktail);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedLanguage = prefs.getString("language", "en");
        outState.putString("language", savedLanguage);


        outState.putString("filter", filterSelected);

        outState.putSerializable("cocktails", (Serializable) cocktails);
    }

    @Override
    public void onItemClick(Cocktail cocktail) {
        Intent intent = new Intent(MainActivity.this, CocktailDetail.class);
        intent.putExtra("Cocktail", (Serializable) cocktail);
        startActivity(intent);
        Log.v("MainActivity", "Activity switched on item " + cocktail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.favorite_page) {

            cocktailViewModel.getFavoriteCocktailIds().observe(this, this::setFilter);
            filterSelected = "favorites";
            Log.v("MainActivity", "favorited items filtered");

            return true;
        } else if (itemId == android.R.id.home) {

            recyclerView.setAdapter(new CocktailAdapter(getApplicationContext(), cocktails, mainActivityInstance));
            Log.v("MainActivity", "Main reloaded");
            return true;
        } else if (itemId == R.id.alcoholic) {
            item.setChecked(true);
            cocktailViewModel.getAlcoholicCocktailIds().observe(this, this::setFilter);
            filterSelected = "alcoholic";
            Log.v("MainActivity", "alcoholic items filtered");
        }
        else if (itemId == R.id.non_alcoholic){
            item.setChecked(true);
            cocktailViewModel.getNonAlcoholicCocktailIds().observe(this, this::setFilter);
            filterSelected = "non-alcoholic";
            Log.v("MainActivity", "non_alcoholic items filtered");
        }
        else if (itemId == R.id.dutch){
            Log.v("MainActivity", "changed language to dutch");
            item.setChecked(true);
            setLocale("nl");
            recreateActivity("nl");
            return true;
        }
        else if (itemId == R.id.german){
            Log.v("MainActivity", "changed language to german");
            item.setChecked(true);
            setLocale("de");
            recreateActivity("de");
            return true;
        }
        else if (itemId == R.id.english){
            Log.v("MainActivity", "changed language to english");
            item.setChecked(true);
            setLocale("en");
            recreateActivity("en");
            return true;
        }

            return super.onOptionsItemSelected(item);

    }
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("language", languageCode).apply();
    }

    private void recreateActivity(String languageCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("language", languageCode).apply();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    public void setFilter(List<Integer> itemIds){
        List<Cocktail> itemList = new ArrayList<>();
        if (itemIds != null && !itemIds.isEmpty()) {
            itemList.clear();
            for (Cocktail cocktail : cocktails) {
                if (itemIds.contains(cocktail.getId())) {
                    itemList.add(cocktail);
                }
            }
            recyclerView.setAdapter(new CocktailAdapter(getApplicationContext(), itemList, mainActivityInstance));
        }
        else {
            // If no items found, show a message or handle as needed
            Toast.makeText(MainActivity.this, "No specific cocktails found", Toast.LENGTH_SHORT).show();
        }
    }

}





