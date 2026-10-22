package com.ThrishenGovender.smartpantry.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ThrishenGovender.smartpantry.R;
import com.ThrishenGovender.smartpantry.activities.RecipeDetailActivity;
import com.ThrishenGovender.smartpantry.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes = new ArrayList<>();

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe currentRecipe = recipes.get(position);
        holder.textViewName.setText(currentRecipe.name);

        String ingredientsString = String.join(", ", currentRecipe.requiredIngredients);
        holder.textViewIngredients.setText("Requires: " + ingredientsString);

        // This click listener is the missing piece that launches the Recipe Detail screen
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);
            intent.putExtra("RECIPE_NAME", currentRecipe.name);
            intent.putExtra("RECIPE_INGREDIENTS", ingredientsString);
            intent.putExtra("RECIPE_INSTRUCTIONS", currentRecipe.instructions);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName, textViewIngredients;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textRecipeName);
            textViewIngredients = itemView.findViewById(R.id.textRecipeIngredients);
        }
    }
}