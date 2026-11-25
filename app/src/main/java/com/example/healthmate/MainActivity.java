package com.example.healthmate;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView txtSteps, txtCalories, txtWater;
    Button btnAddRecord, btnViewHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Connect dashboard text fields
        txtSteps = findViewById(R.id.txtSteps);
        txtCalories = findViewById(R.id.txtCalories);
        txtWater = findViewById(R.id.txtWater);

        // Connect dashboard buttons
        btnAddRecord = findViewById(R.id.btnAddRecord);
        btnViewHistory = findViewById(R.id.btnViewHistory);

        // Dashboard health data will be loaded from SQLite

        // Add Health Record button

        btnAddRecord.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddRecordActivity.class
            );
            startActivity(intent);
        });


        // View Health History button

        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

    }
    private void loadHealthData() {

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        try (Cursor cursor = databaseHelper.getTodayHealthTotals()) {

            if (cursor.moveToFirst()) {

                long steps = cursor.getLong(
                        cursor.getColumnIndexOrThrow("steps"));

                double calories = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("calories"));

                long water = cursor.getLong(
                        cursor.getColumnIndexOrThrow("water"));

                txtSteps.setText("Steps Walked: " + steps);
                txtCalories.setText("Calories Burned: " + calories + " kcal");
                txtWater.setText("Water Intake: " + water + " ml");

            } else {
                txtSteps.setText("Steps Walked: 0");
                txtCalories.setText("Calories Burned: 0 kcal");
                txtWater.setText("Water Intake: 0 ml");
            }
        }

        databaseHelper.close();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadHealthData();
    }
}
