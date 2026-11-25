package com.example.healthmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HealthMate.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "health_records";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "record_date TEXT NOT NULL, " +
                "steps INTEGER NOT NULL, " +
                "calories INTEGER NOT NULL, " +
                "water INTEGER NOT NULL)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean insertHealthRecord(String date,
                                      long steps,
                                      double calories,
                                      long water) {


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("record_date", date);
        values.put("steps", steps);
        values.put("calories", calories);
        values.put("water", water);

        long result = db.insert(TABLE_NAME, null, values);

        return result != -1;
    }

    public Cursor getAllHealthRecords() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                        " ORDER BY id DESC", null);
    }


    public Cursor getLatestHealthRecord() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                "record_date DESC, id DESC",
                "1"
        );
    }
    public boolean deleteHealthRecord(long recordId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME,
                "id = ?",
                new String[]{String.valueOf(recordId)}
        );

        return result > 0;
    }
    public boolean updateHealthRecord(long recordId,
                                      String date,
                                      long steps,
                                      double calories,
                                      long water) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("record_date", date);
        values.put("steps", steps);
        values.put("calories", calories);
        values.put("water", water);

        int result = db.update(
                TABLE_NAME,
                values,
                "id = ?",
                new String[]{String.valueOf(recordId)}
        );

        return result > 0;
    }
// Delete a health record using its ID

    public Cursor getTodayHealthTotals() {

        SQLiteDatabase db = this.getReadableDatabase();

        String today = new java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.US
        ).format(new java.util.Date());

        return db.rawQuery(
                "SELECT " +
                        "COALESCE(SUM(steps), 0) AS steps, " +
                        "COALESCE(SUM(calories), 0) AS calories, " +
                        "COALESCE(SUM(water), 0) AS water " +
                        "FROM " + TABLE_NAME +
                        " WHERE record_date = ?",
                new String[]{today}
        );
    }
}
