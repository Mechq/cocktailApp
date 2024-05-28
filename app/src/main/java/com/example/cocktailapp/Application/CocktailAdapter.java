package com.example.cocktailapp.Application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailapp.Domain.Cocktail;
import com.example.cocktailapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CocktailAdapter extends RecyclerView.Adapter<CocktailViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;


    private Context context;
    private List<Cocktail> cocktails ;

    public CocktailAdapter(Context context, List<Cocktail> cocktails, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.cocktails = cocktails;
        this.recyclerViewInterface = recyclerViewInterface;
    }
    public void setData(List<Cocktail> cocktails){
        this.cocktails = cocktails;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CocktailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CocktailViewHolder(LayoutInflater.from(context).inflate(R.layout.cocktail_item_view,parent,false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailViewHolder holder, int position) {
        Cocktail cocktail = cocktails.get(holder.getAdapterPosition());
        holder.nameView.setText(cocktails.get(position).getName());
        holder.detailsView.setText(cocktails.get(position).getId() + " | " + cocktails.get(position).getCategory() + " | " + cocktails.get(position).getAlcoholic());
        String imageUrl = cocktails.get(position).getImage(); // Assuming getImage() returns the image URL
        Picasso.get().load(imageUrl).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewInterface.onItemClick(cocktail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cocktails.size();
    }
}
