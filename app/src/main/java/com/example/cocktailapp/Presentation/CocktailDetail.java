package com.example.cocktailapp.Presentation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Object;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.cocktailapp.Database.ViewModel;
import com.example.cocktailapp.Domain.Cocktail;
import com.example.cocktailapp.Domain.Favorite;
import com.example.cocktailapp.R;
import com.squareup.picasso.Picasso;

public class CocktailDetail extends AppCompatActivity {
    private Cocktail cocktail;
    private ImageView image;
    private List<String> measures;
    private List<String> ingredients;
    private ViewModel viewModel;
    private LiveData<Boolean> favoriteStatusLiveData;
    private final String LOG_TAG = "CocktailDetail";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cocktail_detail_page);

        Intent intent = getIntent();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        else{
            Log.e(LOG_TAG, "actionBar is not found");
        }
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        cocktail = (Cocktail) intent.getSerializableExtra("Cocktail");
        assert cocktail != null;
        Log.v("CocktailDetail", "cocktail detailpage on ID: " + cocktail.getId());
        TextView textViewName = findViewById(R.id.name);
        TextView textViewCategory = findViewById(R.id.category);
        TextView textViewIngredients = findViewById(R.id.ingredients);
        TextView textViewInstruction = findViewById(R.id.instruction);
        TextView textViewAlcoholic = findViewById(R.id.alcoholic);
        TextView textViewLastModified = findViewById(R.id.lastModified);
        ImageView imageView = findViewById(R.id.imageView);
        if (cocktail != null) {
            textViewName.setText(cocktail.getName());
            textViewCategory.setText(cocktail.getCategory());
            textViewInstruction.setText(cocktail.getInstruction());
            textViewAlcoholic.setText(cocktail.getAlcoholic());
            textViewLastModified.setText(cocktail.getLastModified());
            String imageUrl = cocktail.getImage(); // Assuming getImage() returns the image URL
            Picasso.get().load(imageUrl).into(imageView);
            ingredients = clearedIngredientsList(cocktail);
            measures = clearedMeasuresList(cocktail);
            textViewIngredients.setText(getTextViewIngredients(ingredients, measures));

        } else{
            Log.e(LOG_TAG, "cocktail is null");
        }


    }

    public List<String> clearedIngredientsList(Cocktail cocktail) {
        List<String> ingredients = new ArrayList<>();
            ingredients.add(cocktail.getStrIngredient1());
            ingredients.add(cocktail.getStrIngredient2());
            ingredients.add(cocktail.getStrIngredient3());
            ingredients.add(cocktail.getStrIngredient4());
            ingredients.add(cocktail.getStrIngredient5());
            ingredients.add(cocktail.getStrIngredient6());
        Iterator<String> iterator = ingredients.iterator();
        while (iterator.hasNext()) {
            String ingredient = iterator.next();
            Log.v("detail", ingredient != null ? ingredient : "Ingredient is null");
            if (ingredient == null) {
                iterator.remove();
            }
        }
        return ingredients;
    }

    public List<String> clearedMeasuresList(Cocktail cocktail) {
        List<String> measures = new ArrayList<>();
        measures.add(cocktail.getStrMeasure1());
        measures.add(cocktail.getStrMeasure2());
        measures.add(cocktail.getStrMeasure3());
        measures.add(cocktail.getStrMeasure4());
        measures.add(cocktail.getStrMeasure5());
        measures.add(cocktail.getStrMeasure6());
        Iterator<String> iterator = measures.iterator();
        while (iterator.hasNext()) {
            String measure = iterator.next();
            Log.v("detail", measure != null ? measure : "Measure is null");
            if (measure == null) {
                iterator.remove();
            }
        }
        return measures;
    }

    public String getTextViewIngredients(List<String> measures, List<String> ingredients) {
        StringBuilder stringBuilder = new StringBuilder();
        // Iterate over both lists and concatenate the strings
        for (int i = 0; i < Math.min(measures.size(), ingredients.size()); i++) {
            String measure = measures.get(i);
            Log.v("CocktailDetail", "measure: " + measure);
            String ingredient = ingredients.get(i);
            Log.v("CocktailDetail", "ingredient: " + ingredient);
            // Append the measure and ingredient to the StringBuilder
            stringBuilder.append(measure).append(" ").append(ingredient);
            // Append a newline character to separate each measure-ingredient pair
            stringBuilder.append("\n");
        }

        // Set the concatenated string as the text of the TextView
        return stringBuilder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public String createShareText(Cocktail cocktail) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cocktail.getAlcoholic()).append(" ").append(cocktail.getCategory()).append(" '").append(cocktail.getName()).append("', with ID: ").append(cocktail.getId()).append("\n").append("Ingredients: ").append(getTextViewIngredients(ingredients, measures)).append("\n").append(cocktail.getInstruction());
        Log.v("CocktailDetail", "Created share text with value: " + stringBuilder);
        return stringBuilder.toString();
    }

    @Override
    protected void onDestroy() {
        // Remove the observer when the activity is destroyed
        if (favoriteStatusLiveData != null) {
            favoriteStatusLiveData.removeObservers(this);
        }
        else {
            Log.e(LOG_TAG, "favoriteStatusLiveData is null");
        }
        super.onDestroy();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem favoriteItem = menu.findItem(R.id.favorite);
        favoriteStatusLiveData = viewModel.isCocktailFavorite(cocktail.getId());
        favoriteStatusLiveData.observe(this, isFavorite -> {
            favoriteStatusLiveData.removeObservers(this);
        if (isFavorite) {
            favoriteItem.setIcon(R.drawable.baseline_star_24);
        } else {
            favoriteItem.setIcon(R.drawable.baseline_star_border_24);
        }
    });
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            // Handle share action
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = createShareText(cocktail);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Share App Locker"));
            return true;
        } else if (item.getItemId() == R.id.favorite) {
            // Check the favorite status only once
            favoriteStatusLiveData = viewModel.isCocktailFavorite(cocktail.getId());
            favoriteStatusLiveData.observe(this, isFavorite -> {
                favoriteStatusLiveData.removeObservers(this);
                Favorite favorite = new Favorite(cocktail.getId());
                if (isFavorite) {
                    // Remove from favorites
                    viewModel.removeFromFavorites(favorite);
                    Toast.makeText(this, "Removed " + cocktail.getName() + " from favorites!", Toast.LENGTH_SHORT).show();

                    item.setIcon(R.drawable.baseline_star_border_24); // Change icon to empty star
                } else {
                    // Add to favorites
                    viewModel.addToFavorites(favorite);
                    item.setIcon(R.drawable.baseline_star_24);
                    Toast.makeText(this, "Added " + cocktail.getName() + " to favorites!", Toast.LENGTH_SHORT).show();
                    // Change icon to filled star
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}