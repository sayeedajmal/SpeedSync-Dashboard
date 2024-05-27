package com.strong.speedsyncdashboard.LocalDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ChallanDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CHALLAN = "challan";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CAR_NAME = "car_name";
    private static final String COLUMN_CAR_EMAIL = "email";

    private static final String COLUMN_HIGHWAY = "highway";
    private static final String COLUMN_CURRENT_SPEED = "current_speed";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_INFO = "info";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CHALLAN_TABLE = "CREATE TABLE " + TABLE_CHALLAN + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_CAR_NAME + " TEXT," + COLUMN_HIGHWAY + " TEXT," + COLUMN_CURRENT_SPEED + " REAL," + COLUMN_LATITUDE + " REAL," + COLUMN_LONGITUDE + " REAL," + COLUMN_INFO + " TEXT" + ")";
        db.execSQL(CREATE_CHALLAN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHALLAN);
        onCreate(db);
    }

    public void insertData(String carNumber, String highway, float currentSpeed, double latitude, double longitude, Map<String, Object> info, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAR_NAME, carNumber);
        values.put(COLUMN_CAR_EMAIL, email);
        values.put(COLUMN_HIGHWAY, highway);
        values.put(COLUMN_CURRENT_SPEED, currentSpeed);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_INFO, mapToJsonString(info));
        db.insert(TABLE_CHALLAN, null, values);
        db.close();
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CHALLAN, null);
    }

    public void deleteData(String carNumber, String highway, float currentSpeed, double latitude, double longitude, String infoJson) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHALLAN, COLUMN_CAR_NAME + "=? AND " + COLUMN_HIGHWAY + "=? AND " + COLUMN_CURRENT_SPEED + "=? AND " + COLUMN_LATITUDE + "=? AND " + COLUMN_LONGITUDE + "=? AND " + COLUMN_INFO + "=?", new String[]{carNumber, highway, String.valueOf(currentSpeed), String.valueOf(latitude), String.valueOf(longitude), infoJson});
        db.close();
    }

    private String mapToJsonString(Map<String, Object> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }
}
