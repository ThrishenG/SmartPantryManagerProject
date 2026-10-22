package com.ThrishenGovender.smartpantry.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ThrishenGovender.smartpantry.models.Ingredient;
import com.ThrishenGovender.smartpantry.models.Recipe;

@Database(entities = {Ingredient.class, Recipe.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract IngredientDao ingredientDao();
    public abstract RecipeDao recipeDao();

}