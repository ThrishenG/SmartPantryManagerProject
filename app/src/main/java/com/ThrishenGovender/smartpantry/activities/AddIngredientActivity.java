package com.ThrishenGovender.smartpantry.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.ThrishenGovender.smartpantry.R;
import com.ThrishenGovender.smartpantry.models.Ingredient;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText editName, editQuantity, editUnit, editCategory;
    private Button btnSave;

    private boolean isEditMode = false;
    private int ingredientIdToEdit = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        editName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editIngredientQuantity);
        editUnit = findViewById(R.id.editIngredientUnit);

        // Make sure you add an EditText with this ID to your XML, otherwise it defaults to null
        editCategory = findViewById(R.id.editCategory);
        btnSave = findViewById(R.id.btnSaveIngredient);

        // Check if we are editing an existing item from the adapter click
        Intent intent = getIntent();
        if (intent.hasExtra("IS_EDIT_MODE")) {
            isEditMode = true;
            ingredientIdToEdit = intent.getIntExtra("EDIT_ID", -1);

            // Pre-fill the fields
            editName.setText(intent.getStringExtra("EDIT_NAME"));
            editQuantity.setText(String.valueOf(intent.getIntExtra("EDIT_QTY", 1)));
            editUnit.setText(intent.getStringExtra("EDIT_UNIT"));
            if (editCategory != null && intent.hasExtra("EDIT_CATEGORY")) {
                editCategory.setText(intent.getStringExtra("EDIT_CATEGORY"));
            }

            btnSave.setText("Update Ingredient");
        }

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {
        String name = editName.getText().toString().trim();
        String quantityStr = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();

        // Default to "Fresh" if the category field isn't added to the XML yet
        String category = (editCategory != null && !editCategory.getText().toString().trim().isEmpty())
                ? editCategory.getText().toString().trim()
                : "Fresh";

        if (name.isEmpty() || quantityStr.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        Ingredient ingredient = new Ingredient();
        ingredient.name = name.toLowerCase(); // Keeps strict-matching consistent
        ingredient.quantity = quantity;
        ingredient.unit = unit;
        ingredient.category = category;

        MainActivity.databaseWriteExecutor.execute(() -> {
            if (isEditMode) {
                ingredient.id = ingredientIdToEdit;
                MainActivity.database.ingredientDao().update(ingredient);
            } else {
                MainActivity.database.ingredientDao().insert(ingredient);
            }

            runOnUiThread(this::finish);
        });
    }
}