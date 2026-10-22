package com.yourname.smartpantry.database; // Keep your actual package name

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.yourname.smartpantry.models.Ingredient;
import com.yourname.smartpantry.models.Recipe;

// Notice Recipe.class is now in the entities array
@Database(entities = {Ingredient.class, Recipe.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class}) // This activates your TypeConverter
public abstract class AppDatabase extends RoomDatabase {

    public abstract IngredientDao ingredientDao();
    public abstract RecipeDao recipeDao(); // Adds access to recipe queries

}