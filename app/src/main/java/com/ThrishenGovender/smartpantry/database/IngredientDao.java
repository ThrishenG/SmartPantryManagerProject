package com.ThrishenGovender.smartpantry.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ThrishenGovender.smartpantry.models.Ingredient;

import java.util.List;

@Dao
public interface IngredientDao {
    @Insert
    void insert(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);

    @Delete
    void delete(Ingredient ingredient);

    // Updated table name to 'ingredients' to match your model
    @Query("SELECT * FROM ingredients")
    List<Ingredient> getAllIngredients();

    // Updated table name to 'ingredients' to match your model
    @Query("SELECT name FROM ingredients")
    List<String> getAllIngredientNames();
}