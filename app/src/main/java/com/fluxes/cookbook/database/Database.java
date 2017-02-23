package com.fluxes.cookbook.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Database {
    static final String DATABASE_NAME = "cookbook_database";
    static final int DATABASE_VERSION = 1;
    static final String TAG = "COOKBOOK";
    private Context context;
    private DatabaseHelper DH;
    public SQLiteDatabase db;

    public Database(Context context) {
        this.context = context;
        this.DH = new DatabaseHelper(context);
    }

    public Database open() throws SQLException
    {
        this.db = this.DH.getWritableDatabase();
        return this;
    }

    public void close()
    {
        this.DH.close();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                (new Step(db)).createTable();
                (new Ingredient(db)).createTable();
                (new Recipe(db)).createTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            (new Recipe(db)).dropTable();
            (new Ingredient(db)).dropTable();
            (new Step(db)).dropTable();
        }
    }
}
