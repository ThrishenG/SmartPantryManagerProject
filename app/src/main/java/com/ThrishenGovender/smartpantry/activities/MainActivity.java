package com.ThrishenGovender.smartpantry.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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

    private List<Ingredient> allIngredients = new ArrayList<>();
    private String currentCategory = "All items";
    private String currentSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewIngredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientAdapter();
        recyclerView.setAdapter(adapter);

        // Edit Ingredient Listener
        adapter.setOnItemClickListener(ingredient -> {
            Intent intent = new Intent(MainActivity.this, AddIngredientActivity.class);
            intent.putExtra("IS_EDIT_MODE", true);
            intent.putExtra("EDIT_ID", ingredient.id);
            intent.putExtra("EDIT_NAME", ingredient.name);
            intent.putExtra("EDIT_QTY", ingredient.quantity);
            intent.putExtra("EDIT_UNIT", ingredient.unit);
            intent.putExtra("EDIT_CATEGORY", ingredient.category);
            startActivity(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Ingredient ingredientToDelete = adapter.getIngredientAt(position);

                databaseWriteExecutor.execute(() -> {
                    database.ingredientDao().delete(ingredientToDelete);
                    loadIngredients();
                });
                Toast.makeText(MainActivity.this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "smart_pantry_db")
                .fallbackToDestructiveMigration()
                .build();

        FloatingActionButton fabAdd = findViewById(R.id.fabAddIngredient);
        fabAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddIngredientActivity.class)));

        findViewById(R.id.btnFindRecipes).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        EditText editSearch = findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString();
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        View.OnClickListener filterListener = v -> {
            Button clickedButton = (Button) v;
            currentCategory = clickedButton.getText().toString();
            applyFilters();
        };

        findViewById(R.id.btnFilterAll).setOnClickListener(filterListener);
        findViewById(R.id.btnFilterFresh).setOnClickListener(filterListener);
        findViewById(R.id.btnFilterDairy).setOnClickListener(filterListener);
        findViewById(R.id.btnFilterGrains).setOnClickListener(filterListener);
        findViewById(R.id.btnFilterCans).setOnClickListener(filterListener);

        seedDatabaseIfEmpty();
        seedIngredientsIfEmpty();
        loadIngredients();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {
        databaseWriteExecutor.execute(() -> {
            allIngredients = database.ingredientDao().getAllIngredients();
            runOnUiThread(this::applyFilters);
        });
    }

    private void applyFilters() {
        List<Ingredient> filteredList = new ArrayList<>();
        for (Ingredient item : allIngredients) {
            boolean matchesCategory = currentCategory.equals("All items") ||
                    (item.category != null && item.category.equalsIgnoreCase(currentCategory));
            boolean matchesSearch = currentSearch.isEmpty() ||
                    (item.name != null && item.name.toLowerCase().contains(currentSearch.toLowerCase()));

            if (matchesCategory && matchesSearch) {
                filteredList.add(item);
            }
        }
        adapter.setIngredients(filteredList);
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
            if (database.recipeDao().getAllRecipes().isEmpty()) {
                List<Recipe> initialRecipes = new ArrayList<>();

                initialRecipes.add(createRecipe("Yogurt Pizza Dough",
                        "Prep time: 10 mins\n\n1. In a large bowl, combine flour and salt.\n2. Add yogurt and mix until a shaggy dough forms.\n3. Turn out onto a floured surface and knead for 5-8 minutes until smooth and elastic.\n4. Roll out to your desired thickness and add toppings before baking.",
                        "flour", "yogurt", "salt"));

                initialRecipes.add(createRecipe("Tandoori Chicken",
                        "Prep time: 15 mins | Cook time: 25 mins\n\n1. In a bowl, mix yogurt, spices, and lemon juice to create the marinade.\n2. Coat the chicken thoroughly and let it rest for at least 2 hours.\n3. Preheat your oven to 200°C (400°F).\n4. Place chicken on a baking sheet and bake for 25 minutes or until fully cooked and slightly charred at the edges.",
                        "chicken", "yogurt", "spices", "lemon"));

                initialRecipes.add(createRecipe("Simple Mutton Curry",
                        "Prep time: 15 mins | Cook time: 1.5 hrs\n\n1. Heat oil in a heavy-bottomed pot and brown the finely chopped onions.\n2. Add the mutton pieces and sear until browned on all sides.\n3. Stir in the spices and cook for 2 minutes until fragrant.\n4. Add diced tomatoes and enough water to cover. Simmer on low heat until the mutton is tender.",
                        "mutton", "onion", "tomato", "spices"));

                initialRecipes.add(createRecipe("Potato Samosas",
                        "Prep time: 30 mins | Cook time: 15 mins\n\n1. Boil potatoes until soft, then mash and mix with spices for the filling.\n2. Create a stiff dough using flour, water, and a little oil.\n3. Roll out the dough, cut into semi-circles, and form into cones.\n4. Stuff each cone with the potato filling and seal the edges.\n5. Deep fry in hot oil until golden brown and crispy.",
                        "flour", "potato", "spices", "oil"));

                initialRecipes.add(createRecipe("Tomato Soup",
                        "Prep time: 10 mins | Cook time: 20 mins\n\n1. Finely chop onions and garlic.\n2. Sauté in a pot until the onions are soft and translucent.\n3. Add chopped tomatoes and a cup of water or broth. Simmer for 15 minutes.\n4. Use an immersion blender to puree the soup until smooth. Season to taste.",
                        "tomato", "onion", "garlic"));

                initialRecipes.add(createRecipe("Pasta Pomodoro",
                        "Prep time: 5 mins | Cook time: 15 mins\n\n1. Boil a pot of salted water and cook the pasta until al dente.\n2. In a large pan, gently heat olive oil and sauté minced garlic until fragrant.\n3. Add crushed tomatoes and simmer for 10 minutes to thicken the sauce.\n4. Toss the cooked pasta directly into the sauce. Serve hot.",
                        "pasta", "tomato", "garlic", "olive oil"));

                initialRecipes.add(createRecipe("Garlic Bread",
                        "Prep time: 5 mins | Cook time: 10 mins\n\n1. Soften the butter and mix thoroughly with finely minced garlic.\n2. Slice the bread and spread the garlic butter generously on each piece.\n3. Bake in a preheated oven at 200°C (400°F) for 8-10 minutes until the edges are crispy and golden.",
                        "bread", "garlic", "butter"));

                initialRecipes.add(createRecipe("Golden French Toast",
                        "Prep time: 5 mins | Cook time: 10 mins\n\n1. In a shallow bowl, briskly whisk together the eggs and milk until completely combined.\n2. Heat a skillet over medium heat and melt a generous spoonful of butter.\n3. Dip each slice of bread into the egg mixture, ensuring both sides are fully soaked.\n4. Place the bread into the hot skillet. Cook for 3-4 minutes per side until golden brown and slightly crispy on the edges.\n5. Serve immediately with syrup.",
                        "bread", "egg", "milk"));

                initialRecipes.add(createRecipe("Mashed Potatoes",
                        "Prep time: 10 mins | Cook time: 15 mins\n\n1. Peel and cut potatoes into chunks. Boil in salted water until fork-tender.\n2. Drain the potatoes well and return to the warm pot.\n3. Mash the potatoes while adding butter and splashes of milk until you reach a creamy, smooth consistency.",
                        "potato", "butter", "milk"));

                initialRecipes.add(createRecipe("Fried Rice",
                        "Prep time: 10 mins | Cook time: 10 mins\n\n1. Dice the onion and scramble the egg in a hot wok or large pan.\n2. Add day-old cooked rice to the pan and break up any clumps.\n3. Toss continuously over high heat while adding soy sauce.\n4. Cook until the rice is heated through and slightly toasted.",
                        "rice", "egg", "onion", "soy sauce"));

                initialRecipes.add(createRecipe("Toasted Cheese Sandwich",
                        "Prep time: 2 mins | Cook time: 5 mins\n\n1. Spread a generous layer of butter on one side of each slice of bread.\n2. Place one slice, butter-side down, in a cold non-stick pan.\n3. Layer thick slices of cheese evenly across the bread.\n4. Top with the second slice of bread, butter-side facing up.\n5. Turn the heat to medium-low. Grill for 3 minutes until the bottom is perfectly browned, then carefully flip and grill the other side until the cheese is oozing.",
                        "bread", "cheese", "butter"));

                initialRecipes.add(createRecipe("Creamy Scrambled Eggs",
                        "Prep time: 2 mins | Cook time: 4 mins\n\n1. Crack the eggs into a bowl and whisk vigorously until the yolks and whites are fully blended and slightly frothy.\n2. Melt butter in a non-stick pan over medium-low heat.\n3. Pour in the eggs and let them sit undisturbed for 20 seconds until the edges start to set.\n4. Using a spatula, gently pull the edges toward the center to form soft, fluffy curds.\n5. Remove from heat just before they look fully dry.",
                        "egg", "butter"));

                initialRecipes.add(createRecipe("Chicken Salad",
                        "Prep time: 10 mins\n\n1. Cook and shred the chicken breast.\n2. Wash and roughly chop the lettuce and dice the tomatoes.\n3. In a large bowl, combine the chicken, lettuce, and tomatoes.\n4. Toss with mayonnaise until lightly coated. Serve chilled.",
                        "chicken", "tomato", "lettuce", "mayonnaise"));

                initialRecipes.add(createRecipe("Gourmet Mac and Cheese",
                        "Prep time: 10 mins | Cook time: 15 mins\n\n1. Boil a large pot of salted water. Add pasta and cook until al dente (about 8-10 mins). Drain well.\n2. In the same pot over medium-low heat, pour in the milk and whisk gently until slightly warm.\n3. Gradually stir in the blocks of cheese until completely melted into a rich, smooth sauce.\n4. Fold the cooked pasta back into the cheese sauce until evenly coated. Serve hot.",
                        "pasta", "cheese", "milk"));

                initialRecipes.add(createRecipe("Guacamole",
                        "Prep time: 10 mins\n\n1. Halve the avocados, remove the pit, and scoop the flesh into a bowl.\n2. Mash the avocado gently with a fork.\n3. Finely dice the tomato and onion, and fold them into the mashed avocado.\n4. Mix well and serve immediately.",
                        "avocado", "tomato", "onion"));

                database.recipeDao().insertAll(initialRecipes);
            }
        });
    }

    private void seedIngredientsIfEmpty() {
        databaseWriteExecutor.execute(() -> {
            if (database.ingredientDao().getAllIngredients().isEmpty()) {
                // This combination unlocks 4 recipes: Cheese Sandwich, Scrambled Eggs, French Toast, and Mac and Cheese
                Ingredient i1 = new Ingredient(); i1.name = "bread"; i1.quantity = 1; i1.unit = "loaf"; i1.category = "Grains";
                Ingredient i2 = new Ingredient(); i2.name = "cheese"; i2.quantity = 2; i2.unit = "blocks"; i2.category = "Dairy";
                Ingredient i3 = new Ingredient(); i3.name = "butter"; i3.quantity = 1; i3.unit = "tub"; i3.category = "Dairy";
                Ingredient i4 = new Ingredient(); i4.name = "egg"; i4.quantity = 12; i4.unit = "pieces"; i4.category = "Fresh";
                Ingredient i5 = new Ingredient(); i5.name = "milk"; i5.quantity = 1; i5.unit = "liter"; i5.category = "Dairy";
                Ingredient i6 = new Ingredient(); i6.name = "pasta"; i6.quantity = 1; i6.unit = "box"; i6.category = "Grains";

                database.ingredientDao().insert(i1);
                database.ingredientDao().insert(i2);
                database.ingredientDao().insert(i3);
                database.ingredientDao().insert(i4);
                database.ingredientDao().insert(i5);
                database.ingredientDao().insert(i6);

                loadIngredients();
            }
        });
    }
}