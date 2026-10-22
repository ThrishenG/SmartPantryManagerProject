package com.ThrishenGovender.smartpantry.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.ThrishenGovender.smartpantry.R;
import com.ThrishenGovender.smartpantry.models.Ingredient;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText editName, editQuantity, editUnit;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        editName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editIngredientQuantity);
        editUnit = findViewById(R.id.editIngredientUnit);
        btnSave = findViewById(R.id.btnSaveIngredient);

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {
        String name = editName.getText().toString().trim();
        String quantityStr = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();

        // Input Validation
        if (name.isEmpty() || quantityStr.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        Ingredient newIngredient = new Ingredient();
        // Normalizing the name to lowercase helps immensely with strict-matching later
        newIngredient.name = name.toLowerCase();
        newIngredient.quantity = quantity;
        newIngredient.unit = unit;

        // Insert into database on the background thread
        MainActivity.databaseWriteExecutor.execute(() -> {
            MainActivity.database.ingredientDao().insert(newIngredient);

            // Close the activity and return to MainActivity on the main thread
            runOnUiThread(this::finish);
        });
    }
}