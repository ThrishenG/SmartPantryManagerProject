package com.ThrishenGovender.smartpantry.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.yourname.smartpantry.models.Ingredient;

@Database(entities = {Ingredient.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // This allows the rest of the app to access the database queries
    public abstract IngredientDao ingredientDao();

}
