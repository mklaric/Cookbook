package com.fluxes.cookbook.database;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Step extends Model {
    public int step_number = -1;
    public int recipe_id = -1;
    public String description = "";

    public Step(SQLiteDatabase db) {
        super(db, "steps");
    }

    private Step(SQLiteDatabase db, int id) {
        super(db, "steps");
        this.id = id;
    }

    @Override
    public boolean save() {
        if (this.id < 0 || this.recipe_id < 0 || this.step_number < 0)
            return false;

        String command;
        if (this.find(this.id) == null) {
            command = "INSERT INTO " + this.table;
            command += " (id, step_number, recipe_id, description) VALUES ('";
            command += Integer.toString(this.id) + "','";
            command += Integer.toString(this.step_number) + "','";
            command += Integer.toString(this.recipe_id) + "','";
            command += this.description + "');";
        } else {
            command = "UPDATE " + this.table + " SET ";
            command += "step_number='" + Integer.toString(this.step_number) + "',";
            command += "recipe_id='" + Integer.toString(this.recipe_id) + "',";
            command += "description='" + this.description + "' ";
            command += "WHERE id=" + Integer.toString(this.id) + ";";
        }
        try {
            this.db.execSQL(command);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createTable() {
        String command = "CREATE TABLE " + this.table + "(";
        command += "id integer primary key,";
        command += "step_number integer,";
        command += "recipe_id integer,";
        command += "description TEXT";
        command += ");";
        try {
            this.db.execSQL(command);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Step find(int id) {
        Cursor c;
        try {
            String[] whereArgs = new String[] {
                    String.valueOf(id)
            };
            c = db.query(this.table, null, "id = ?", whereArgs, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Step step = new Step(db, c.getInt(0));
        step.step_number = c.getInt(1);
        step.recipe_id = c.getInt(2);
        step.description = c.getString(3);

        c.close();
        return step;
    }

    public List<Step> findByRecipeId(int recipe_id) {
        Cursor c;
        try {
            String[] whereArgs = new String[] {
                    String.valueOf(recipe_id)
            };
            c = db.query(this.table, null, "recipe_id = ?", whereArgs, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        List<Step> is = new ArrayList<>();

        if (!c.moveToFirst()) {
            c.close();
            return is;
        }

        do {
            Step step = new Step(db, c.getInt(0));
            step.step_number = c.getInt(1);
            step.recipe_id = c.getInt(2);
            step.description = c.getString(3);
            is.add(step);
        } while (c.moveToNext());

        c.close();
        return is;
    }

    public JSONObject jsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", this.id);
            json.put("number", this.step_number);
            json.put("recipe_id", this.recipe_id);
            json.put("description", this.description);
        } catch (JSONException e) {
            Log.d("COOKBOOK", e.toString());
        }

        return json;
    }

    public JSONArray jsonByRecipeId(int recipe_id) {
        List<Step> li = this.findByRecipeId(recipe_id);

        JSONArray json = new JSONArray();
        for (Step i: li)
            json.put(i.jsonObject());

        return json;
    }

    public Step fromJSONObject(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.step_number = json.getInt("number");
            this.description = json.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("COOKBOOK", e.toString());
        }

        return this;
    }
}
