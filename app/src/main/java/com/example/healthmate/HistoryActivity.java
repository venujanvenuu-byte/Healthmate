package com.example.healthmate;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout layoutRecords;
    TextView txtNoRecords;
    Button btnBack;
    EditText edtSearch;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        layoutRecords = findViewById(R.id.layoutRecords);
        txtNoRecords = findViewById(R.id.txtNoRecords);
        btnBack = findViewById(R.id.btnBack);
        edtSearch = findViewById(R.id.edtSearch);

        databaseHelper = new DatabaseHelper(this);

        btnBack.setOnClickListener(v -> finish());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                loadHealthRecords();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadHealthRecords();
    }

    private void loadHealthRecords() {

        layoutRecords.removeAllViews();

        String searchText = edtSearch.getText()
                .toString().trim().toLowerCase();

        boolean recordFound = false;

        try (Cursor cursor = databaseHelper.getAllHealthRecords()) {

            if (cursor == null || cursor.getCount() == 0) {
                txtNoRecords.setText("No health records found");
                txtNoRecords.setVisibility(View.VISIBLE);
                return;
            }

            int idIndex = cursor.getColumnIndexOrThrow("id");
            int dateIndex = cursor.getColumnIndexOrThrow("record_date");
            int stepsIndex = cursor.getColumnIndexOrThrow("steps");
            int caloriesIndex = cursor.getColumnIndexOrThrow("calories");
            int waterIndex = cursor.getColumnIndexOrThrow("water");

            while (cursor.moveToNext()) {

                long recordId = cursor.getLong(idIndex);
                String date = cursor.getString(dateIndex);

                if (date == null ||
                        !date.toLowerCase().contains(searchText)) {
                    continue;
                }

                recordFound = true;

                long steps = cursor.getLong(stepsIndex);
                double calories = cursor.getDouble(caloriesIndex);
                long water = cursor.getLong(waterIndex);

                LinearLayout recordCard = new LinearLayout(this);
                recordCard.setOrientation(LinearLayout.VERTICAL);
                recordCard.setPadding(20, 20, 20, 20);
                recordCard.setBackgroundColor(
                        Color.rgb(225, 245, 235));

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                cardParams.setMargins(0, 0, 0, 24);

                TextView recordView = new TextView(this);

                String recordText =
                        "Date: " + date +
                                "\nSteps Walked: " + steps +
                                "\nCalories Burned: " + calories + " kcal" +
                                "\nWater Intake: " + water + " ml";

                recordView.setText(recordText);
                recordView.setTextSize(16);
                recordView.setTextColor(Color.DKGRAY);
                recordView.setPadding(10, 10, 10, 10);

                recordCard.addView(recordView);

                Button btnDelete = new Button(this);
                btnDelete.setText("Delete Record");
                btnDelete.setTextColor(Color.WHITE);
                btnDelete.setBackgroundTintList(
                        ColorStateList.valueOf(Color.RED));

                btnDelete.setOnClickListener(v -> {

                    new AlertDialog.Builder(HistoryActivity.this)
                            .setTitle("Delete Health Record")
                            .setMessage(
                                    "Are you sure you want to delete this record?")
                            .setPositiveButton("Delete", (dialog, which) -> {

                                boolean deleted =
                                        databaseHelper.deleteHealthRecord(recordId);

                                if (deleted) {
                                    Toast.makeText(
                                            HistoryActivity.this,
                                            "Health record deleted successfully",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    loadHealthRecords();

                                } else {
                                    Toast.makeText(
                                            HistoryActivity.this,
                                            "Failed to delete health record",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            })
                            .setNegativeButton("Cancel",
                                    (dialog, which) -> dialog.dismiss())
                            .show();
                });

                recordCard.addView(btnDelete);

                Button btnEdit = new Button(this);
                btnEdit.setText("Edit Record");
                btnEdit.setTextColor(Color.WHITE);
                btnEdit.setBackgroundTintList(
                        ColorStateList.valueOf(Color.rgb(22, 132, 81))
                );

                btnEdit.setOnClickListener(v -> {
                    Toast.makeText(
                            HistoryActivity.this,
                            "Edit Record selected",
                            Toast.LENGTH_SHORT
                    ).show();
                });

                recordCard.addView(btnEdit);

                layoutRecords.addView(recordCard, cardParams);
            }
        }

        if (recordFound) {
            txtNoRecords.setVisibility(View.GONE);
        } else {
            txtNoRecords.setText("No matching records found");
            txtNoRecords.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (edtSearch != null) {
            loadHealthRecords();
        }
    }
}
