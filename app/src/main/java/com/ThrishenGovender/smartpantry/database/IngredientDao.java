package com.ThrishenGovender.smartpantry.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import com.yourname.smartpantry.models.Ingredient;

@Dao
public interface IngredientDao {

    @Insert
    void insert(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);

    @Delete
    void delete(Ingredient ingredient);

    // Reads all ingredients to display in the list
    @Query("SELECT * FROM pantry_ingredients")
    List<Ingredient> getAllIngredients();

    @Query("SELECT name FROM pantry_ingredients")
    List<String> getAllIngredientNames();
}
