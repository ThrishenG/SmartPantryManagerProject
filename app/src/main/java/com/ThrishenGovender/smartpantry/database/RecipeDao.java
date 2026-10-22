package com.ThrishenGovender.smartpantry.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import com.yourname.smartpantry.models.Recipe;

@Dao
public interface RecipeDao {

    @Insert
    void insert(Recipe recipe);

    @Insert
    void insertAll(List<Recipe> recipes);

    @Update
    void update(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM recipes")
    List<Recipe> getAllRecipes();
}
