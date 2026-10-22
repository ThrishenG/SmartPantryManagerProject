package com.ThrishenGovender.smartpantry.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.ThrishenGovender.smartpantry.R;
import com.ThrishenGovender.smartpantry.adapters.IngredientAdapter;
import com.ThrishenGovender.smartpantry.database.AppDatabase;
import com.ThrishenGovender.smartpantry.models.Ingredient;
import com.ThrishenGovender.smartpantry.models.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static AppDatabase database;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);
    private IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewIngredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientAdapter();
        recyclerView.setAdapter(adapter);

        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "smart_pantry_db")
                .fallbackToDestructiveMigration()
                .build();

        FloatingActionButton fabAdd = findViewById(R.id.fabAddIngredient);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });

        seedDatabaseIfEmpty();
        loadIngredients();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {
        databaseWriteExecutor.execute(() -> {
            List<Ingredient> ingredients = database.ingredientDao().getAllIngredients();
            // Yellow warning fixed here:
            runOnUiThread(() -> adapter.setIngredients(ingredients));
        });
    }

    private Recipe createRecipe(String name, String instructions, String... ingredients) {
        Recipe recipe = new Recipe();
        recipe.name = name;
        recipe.instructions = instructions;
        recipe.requiredIngredients = Arrays.asList(ingredients);
        return recipe;
    }

    private void seedDatabaseIfEmpty() {
        databaseWriteExecutor.execute(() -> {
            List<Recipe> existingRecipes = database.recipeDao().getAllRecipes();

            if (existingRecipes.isEmpty()) {
                List<Recipe> initialRecipes = new ArrayList<>();

                initialRecipes.add(createRecipe("Yogurt Pizza Dough", "Mix flour, yogurt, and salt. Knead until smooth.", "flour", "yogurt", "salt"));
                initialRecipes.add(createRecipe("Tandoori Chicken", "Marinate chicken in yogurt, lemon, and spices. Bake at 200C.", "chicken", "yogurt", "spices", "lemon"));
                initialRecipes.add(createRecipe("Simple Mutton Curry", "Brown onions and mutton, add tomatoes and spices, simmer until tender.", "mutton", "onion", "tomato", "spices"));
                initialRecipes.add(createRecipe("Potato Samosas", "Make pastry from flour. Fill with boiled spiced potatoes and fry.", "flour", "potato", "spices", "oil"));
                initialRecipes.add(createRecipe("Tomato Soup", "Saute onions and garlic, add tomatoes, simmer and blend.", "tomato", "onion", "garlic"));
                initialRecipes.add(createRecipe("Pasta Pomodoro", "Boil pasta. Toss with crushed tomatoes, garlic, and olive oil.", "pasta", "tomato", "garlic", "olive oil"));
                initialRecipes.add(createRecipe("Garlic Bread", "Spread butter and minced garlic on bread, bake until crispy.", "bread", "garlic", "butter"));
                initialRecipes.add(createRecipe("French Toast", "Whisk egg and milk. Dip bread and fry in a pan.", "bread", "egg", "milk"));
                initialRecipes.add(createRecipe("Mashed Potatoes", "Boil potatoes until soft. Mash with butter and milk.", "potato", "butter", "milk"));
                initialRecipes.add(createRecipe("Fried Rice", "Fry cooked rice with egg, chopped onions, and soy sauce.", "rice", "egg", "onion", "soy sauce"));
                initialRecipes.add(createRecipe("Cheese Sandwich", "Butter outside of bread, place cheese inside, toast in pan.", "bread", "cheese", "butter"));
                initialRecipes.add(createRecipe("Scrambled Eggs", "Whisk eggs. Cook over medium heat in a buttered pan.", "egg", "butter"));
                initialRecipes.add(createRecipe("Chicken Salad", "Mix shredded chicken, diced tomato, and lettuce with mayonnaise.", "chicken", "tomato", "lettuce", "mayonnaise"));
                initialRecipes.add(createRecipe("Mac and Cheese", "Boil pasta. Drain and mix with milk and cheese over low heat.", "pasta", "cheese", "milk"));
                initialRecipes.add(createRecipe("Guacamole", "Mash avocado with diced tomato and onion.", "avocado", "tomato", "onion"));

                database.recipeDao().insertAll(initialRecipes);
                System.out.println("Database successfully seeded with " + initialRecipes.size() + " recipes.");
            }
        });
    }
}