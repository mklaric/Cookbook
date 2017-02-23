package com.fluxes.cookbook.database;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public abstract class Model {
    protected SQLiteDatabase db;
    protected String table;
    public int id = -1;

    public Model(SQLiteDatabase db, String table) {
        this.db = db;
        this.table = table;
    }

    public abstract boolean save();

    public boolean delete() {
        try {
            db.execSQL(
                    "DELETE FROM " + this.table + " WHERE id=" + Integer.toString(this.id) + ";"
            );
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("COOKBOOK", e.toString());
            return false;
        }
    }

    public abstract boolean createTable();

    public boolean dropTable() {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + this.table);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEmpty() {
        Cursor c;
        try {
            c = db.query(this.table, null, "1 = 1", null, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

        if (!c.moveToFirst()) {
            c.close();
            return true;
        }

        return false;
    }
}
