package com.example.healthmate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class EditRecordActivity extends AppCompatActivity {

    Button btnEditDate, btnUpdateRecord, btnCancelEdit;
    EditText edtEditSteps, edtEditCalories, edtEditWater;

    DatabaseHelper databaseHelper;

    long recordId;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        btnEditDate = findViewById(R.id.btnEditDate);
        btnUpdateRecord = findViewById(R.id.btnUpdateRecord);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);

        edtEditSteps = findViewById(R.id.edtEditSteps);
        edtEditCalories = findViewById(R.id.edtEditCalories);
        edtEditWater = findViewById(R.id.edtEditWater);

        databaseHelper = new DatabaseHelper(this);

        // Receive existing record details
        recordId = getIntent().getLongExtra("id", -1);

        selectedDate = getIntent().getStringExtra("date");

        long steps = getIntent().getLongExtra("steps", 0);
        double calories = getIntent().getDoubleExtra("calories", 0);
        long water = getIntent().getLongExtra("water", 0);

        if (recordId == -1 || selectedDate == null) {
            Toast.makeText(this,
                    "Invalid health record",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display existing values
        btnEditDate.setText(selectedDate);
        edtEditSteps.setText(String.valueOf(steps));
        edtEditCalories.setText(String.valueOf(calories));
        edtEditWater.setText(String.valueOf(water));

        // Change record date
        btnEditDate.setOnClickListener(v -> showDatePicker());

        // Update record
        btnUpdateRecord.setOnClickListener(v -> updateRecord());

        // Cancel editing
        btnCancelEdit.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        try {
            String[] parts = selectedDate.split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]) - 1;
            day = Integer.parseInt(parts[2]);
        } catch (Exception ignored) {
        }

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {

                            selectedDate = String.format(
                                    Locale.US,
                                    "%04d-%02d-%02d",
                                    selectedYear,
                                    selectedMonth + 1,
                                    selectedDay
                            );

                            btnEditDate.setText(selectedDate);
                        },
                        year,
                        month,
                        day
                );

        datePickerDialog.show();
    }

    private void updateRecord() {

        String stepsText = edtEditSteps.getText().toString().trim();
        String caloriesText = edtEditCalories.getText().toString().trim();
        String waterText = edtEditWater.getText().toString().trim();

        if (stepsText.isEmpty() ||
                caloriesText.isEmpty() ||
                waterText.isEmpty()) {

            Toast.makeText(this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        try {

            long steps = Long.parseLong(stepsText);
            double calories = Double.parseDouble(caloriesText);
            long water = Long.parseLong(waterText);

            if (steps < 0 || calories < 0 || water < 0 ||
                    Double.isNaN(calories) ||
                    Double.isInfinite(calories)) {

                Toast.makeText(this,
                        "Please enter valid positive or zero values",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            boolean updated = databaseHelper.updateHealthRecord(
                    recordId,
                    selectedDate,
                    steps,
                    calories,
                    water
            );

            if (updated) {

                Toast.makeText(this,
                        "Health record updated successfully",
                        Toast.LENGTH_SHORT).show();

                finish();

            } else {

                Toast.makeText(this,
                        "Failed to update health record",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {

            Toast.makeText(this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
