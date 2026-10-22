package com.ThrishenGovender.smartpantry.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yourname.smartpantry.R;
import com.yourname.smartpantry.database.AppDatabase;

public class MainActivity extends AppCompatActivity {

    // 1. Create a public static database instance and a thread pool
    public static AppDatabase database;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "smart_pantry_db")
                .fallbackToDestructiveMigration()
                .build();

        // Run the seeder on startup
        seedDatabaseIfEmpty();
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
            } else {
                System.out.println("Database already contains recipes. Skipping seed.");
            }
        });
    }
}