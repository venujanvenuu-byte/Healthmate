package com.example.healthmate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AddRecordActivity extends AppCompatActivity {

    Button btnSelectDate, btnSaveRecord;
    EditText edtSteps, edtCalories, edtWater;

    String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_record);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSaveRecord = findViewById(R.id.btnSaveRecord);

        edtSteps = findViewById(R.id.edtSteps);
        edtCalories = findViewById(R.id.edtCalories);
        edtWater = findViewById(R.id.edtWater);

        btnSelectDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    AddRecordActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {

                        selectedDate = String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                selectedYear,
                                selectedMonth + 1,
                                selectedDay
                        );

                        btnSelectDate.setText(selectedDate);
                    },
                    year,
                    month,
                    day
            );

            datePicker.show();
        });

        btnSaveRecord.setOnClickListener(v -> {

            String steps = edtSteps.getText().toString().trim();
            String calories = edtCalories.getText().toString().trim();
            String water = edtWater.getText().toString().trim();

            if (selectedDate.isEmpty()) {
                Toast.makeText(this,
                        "Please select a date",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (steps.isEmpty() ||
                    calories.isEmpty() ||
                    water.isEmpty()) {

                Toast.makeText(this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long stepsValue = Long.parseLong(steps);
                double caloriesValue = Double.parseDouble(calories);
                long waterValue = Long.parseLong(water);

                if (stepsValue < 0 ||
                        caloriesValue < 0 ||
                        !Double.isFinite(caloriesValue) ||
                        waterValue < 0) {

                    Toast.makeText(this,
                            "Enter valid non-negative values",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                DatabaseHelper databaseHelper = new DatabaseHelper(this);

                boolean isSaved = databaseHelper.insertHealthRecord(
                        selectedDate,
                        stepsValue,
                        caloriesValue,
                        waterValue
                );

                if (isSaved) {
                    Toast.makeText(this,
                            "Health record saved successfully!",
                            Toast.LENGTH_LONG).show();

                    finish();
                } else {
                    Toast.makeText(this,
                            "Failed to save health record!",
                            Toast.LENGTH_LONG).show();
                }


            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "Please enter valid numbers",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
