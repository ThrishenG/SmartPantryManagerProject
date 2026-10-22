package com.ThrishenGovender.smartpantry.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

import com.ThrishenGovender.smartpantry.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch switchMetric = findViewById(R.id.switchMetricUnits);

        // Initialize SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SmartPantryPrefs", MODE_PRIVATE);

        // Load the saved preference (defaults to true if it doesn't exist yet)
        boolean isMetric = prefs.getBoolean("USE_METRIC", true);
        switchMetric.setChecked(isMetric);

        // Save the new preference whenever the user toggles the switch
        switchMetric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("USE_METRIC", isChecked);
            editor.apply();
        });
    }
}