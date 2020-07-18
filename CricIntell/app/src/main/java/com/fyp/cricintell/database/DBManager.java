package com.fyp.cricintell.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.fyp.cricintell.models.AnalysisLog;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(int entryType, String team, String teamFlag, String opponent, String opponentFlag, String ground, String location) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TEAM, team);
        contentValue.put(DatabaseHelper.TEAM_FLAG, teamFlag);
        contentValue.put(DatabaseHelper.OPPONENT, opponent);
        contentValue.put(DatabaseHelper.OPPONENT_FLAG, opponentFlag);
        contentValue.put(DatabaseHelper.GROUND, ground);
        contentValue.put(DatabaseHelper.LOCATION, location);
        contentValue.put(DatabaseHelper.ENTRY_TYPE, entryType);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public List<AnalysisLog> fetch() {
        List<AnalysisLog> analysisLogs = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.ENTRY_TYPE, DatabaseHelper.TEAM, DatabaseHelper.TEAM_FLAG, DatabaseHelper.OPPONENT, DatabaseHelper.OPPONENT_FLAG, DatabaseHelper.GROUND, DatabaseHelper.LOCATION};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        while (cursor.moveToNext()) {
            analysisLogs.add(new AnalysisLog(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEAM)), cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEAM_FLAG)), cursor.getString(cursor.getColumnIndex(DatabaseHelper.OPPONENT)), cursor.getString(cursor.getColumnIndex(DatabaseHelper.OPPONENT_FLAG)), cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUND)), cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION)), cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ENTRY_TYPE))));
        }
        return analysisLogs;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}
