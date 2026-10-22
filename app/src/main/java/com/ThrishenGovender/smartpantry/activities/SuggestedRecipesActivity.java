package com.ThrishenGovender.smartpantry.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ThrishenGovender.smartpantry.R;
import com.ThrishenGovender.smartpantry.adapters.RecipeAdapter;
import com.ThrishenGovender.smartpantry.models.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecipeAdapter adapter;
    private TextView textEmptyFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        textEmptyFeedback = findViewById(R.id.textEmptyFeedback);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSuggested);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter();
        recyclerView.setAdapter(adapter);

        runStrictMatchingAlgorithm();
    }

    private void runStrictMatchingAlgorithm() {
        MainActivity.databaseWriteExecutor.execute(() -> {
            // 1. Get all pantry ingredient names and recipes
            List<String> pantryItems = MainActivity.database.ingredientDao().getAllIngredientNames();
            List<Recipe> allRecipes = MainActivity.database.recipeDao().getAllRecipes();

            // 2. Convert pantry list to a lowercase Set for fast, accurate comparison
            Set<String> pantrySet = new HashSet<>();
            for (String item : pantryItems) {
                if (item != null) pantrySet.add(item.toLowerCase().trim());
            }

            List<Recipe> suggestedRecipes = new ArrayList<>();

            // 3. The Strict-Matching Logic
            for (Recipe recipe : allRecipes) {
                Set<String> requiredSet = new HashSet<>();
                for (String req : recipe.requiredIngredients) {
                    requiredSet.add(req.toLowerCase().trim());
                }

                // containsAll perfectly satisfies the assignment brief:
                // If even one ingredient is missing, it returns false.
                if (!requiredSet.isEmpty() && pantrySet.containsAll(requiredSet)) {
                    suggestedRecipes.add(recipe);
                }
            }

            // 4. Update UI
            runOnUiThread(() -> {
                if (suggestedRecipes.isEmpty()) {
                    textEmptyFeedback.setVisibility(View.VISIBLE);
                    adapter.setRecipes(new ArrayList<>());
                } else {
                    textEmptyFeedback.setVisibility(View.GONE);
                    adapter.setRecipes(suggestedRecipes);
                }
            });
        });
    }
}