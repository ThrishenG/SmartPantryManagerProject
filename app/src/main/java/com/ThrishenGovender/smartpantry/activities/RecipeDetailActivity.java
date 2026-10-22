package com.ThrishenGovender.smartpantry.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.ThrishenGovender.smartpantry.R;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView textName = findViewById(R.id.detailRecipeName);
        TextView textIngredients = findViewById(R.id.detailRecipeIngredients);
        TextView textInstructions = findViewById(R.id.detailRecipeInstructions);

        // Retrieve the data passed from the adapter
        String name = getIntent().getStringExtra("RECIPE_NAME");
        String ingredients = getIntent().getStringExtra("RECIPE_INGREDIENTS");
        String instructions = getIntent().getStringExtra("RECIPE_INSTRUCTIONS");

        // Set the text
        textName.setText(name);
        textIngredients.setText(ingredients);
        textInstructions.setText(instructions);
    }
}