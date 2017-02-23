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

public class Ingredient extends Model {
    public String name = "";
    public int recipe_id = -1;

    public Ingredient(SQLiteDatabase db) {
        super(db, "ingredients");
    }

    private Ingredient(SQLiteDatabase db, int id) {
        super(db, "ingredients");
        this.id = id;
    }

    @Override
    public boolean save() {
        if (this.id < 0 || this.recipe_id < 0)
            return false;

        String command;
        if (this.find(this.id) == null) {
            command = "INSERT INTO " + this.table + " (id, name, recipe_id) VALUES ('";
            command += Integer.toString(this.id) + "','";
            command += this.name + "','";
            command += Integer.toString(this.recipe_id) + "');";
        } else {
            command = "UPDATE " + this.table + " SET ";
            command += "name='" + this.name + "',";
            command += "recipe_id='" + Integer.toString(this.recipe_id) + "' ";
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
        command += "name TEXT unique,";
        command += "recipe_id integer";
        command += ");";
        try {
            this.db.execSQL(command);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Ingredient find(int id) {
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

        Ingredient ingredient = new Ingredient(db, c.getInt(0));
        ingredient.name = c.getString(1);
        ingredient.recipe_id = c.getInt(2);

        c.close();
        return ingredient;
    }

    public List<Ingredient> findByRecipeId(int recipe_id) {
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

        List<Ingredient> is = new ArrayList<>();
        if (!c.moveToFirst()) {
            c.close();
            return is;
        }

        do {
            Ingredient ingredient = new Ingredient(db, c.getInt(0));
            ingredient.name = c.getString(1);
            ingredient.recipe_id = c.getInt(2);
            is.add(ingredient);
        } while (c.moveToNext());

        c.close();
        return is;
    }

    public JSONObject jsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", this.id);
            json.put("name", this.name);
            json.put("recipe_id", this.recipe_id);
        } catch (JSONException e) {
            Log.d("COOKBOOK", e.toString());
        }

        return json;
    }

    public JSONArray jsonByRecipeId(int recipe_id) {
        List<Ingredient> li = this.findByRecipeId(recipe_id);

        JSONArray json = new JSONArray();
        for (Ingredient i: li)
            json.put(i.jsonObject());

        return json;
    }

    public Ingredient fromJSONObject(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.name = json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("COOKBOOK", e.toString());
        }

        return this;
    }
}
