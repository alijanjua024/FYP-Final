package com.fyp.cricintell.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Table Name
    public static final String TABLE_NAME = "ANALYSISLOG";

    // Table columns
    public static final String _ID = "_id";
    public static final String TEAM = "team";
    public static final String OPPONENT = "opponent";
    public static final String TEAM_FLAG = "team_flag";
    public static final String OPPONENT_FLAG = "opponent_flag";
    public static final String GROUND = "ground";
    public static final String LOCATION = "location";
    public static final String ENTRY_TYPE = "entry_type";

    // Database Information
    static final String DB_NAME = "CRIC_INTELL.DB";

    // database version
    static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ENTRY_TYPE + " INTEGER NOT NULL, " + TEAM + " TEXT NOT NULL, " + TEAM_FLAG + " TEXT NOT NULL, " + OPPONENT + " TEXT NOT NULL, " + OPPONENT_FLAG + " TEXT NOT NULL, " + GROUND + " TEXT NOT NULL, " + LOCATION + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
