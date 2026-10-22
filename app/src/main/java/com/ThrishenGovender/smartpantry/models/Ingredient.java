package com.ThrishenGovender.smartpantry.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredients")
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int quantity;
    public String unit;

    // You are missing this line:
    public String category;
}