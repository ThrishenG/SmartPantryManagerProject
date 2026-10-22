package com.ThrishenGovender.smartpantry.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.List;

@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "recipe_name")
    public String name;

    @ColumnInfo(name = "instructions")
    public String instructions;

    // The TypeConverter will handle saving this list behind the scenes
    @ColumnInfo(name = "required_ingredients")
    public List<String> requiredIngredients;
}
