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


public class Recipe extends Model {
    public String description = "";
    public String name = "";
    public int user_id = -1;
    public boolean Private = false;
    public int preparation_time = -1;
    public int cooking_time = -1;
    public float grade = 0;

    public List<Ingredient> ingredients = new ArrayList<>();
    public List<Step> steps = new ArrayList<>();

    public Recipe(SQLiteDatabase db) {
        super(db, "recipes");
    }

    private Recipe(SQLiteDatabase db, int id) {
        super(db, "recipes");
        this.id = id;
    }

    @Override
    public boolean save() {
        if (this.id < 0 || this.user_id < 0 || this.cooking_time< 0 || this.preparation_time < 0)
            return false;

        String command;
        if (this.find(this.id) == null) {
            command = "INSERT INTO " + this.table;
            command += " (id, description, name, user_id, private, preparation_time, cooking_time, grade) VALUES ('";
            command += Integer.toString(this.id) + "','";
            command += this.description + "','";
            command += this.name + "','";
            command += Integer.toString(this.user_id) + "','";
            command += Boolean.toString(this.Private) + "','";
            command += Integer.toString(this.preparation_time) + "','";
            command += Integer.toString(this.cooking_time) + "','";
            command += Float.toString(this.grade) + "');";
        } else {
            command = "UPDATE " + this.table + " SET ";
            command += "description='" + this.description + "',";
            command += "name='" + this.name + "',";
            command += "user_id='" + Integer.toString(this.user_id) + "',";
            command += "private='" + Integer.toString(this.Private ? 1 : 0) + "',";
            command += "preparation_time='" + Integer.toString(this.preparation_time) + "',";
            command += "cooking_time='" + Integer.toString(this.cooking_time) + "',";
            command += "grade='" + Float.toString(this.grade) + "' ";
            command += "WHERE id=" + Integer.toString(this.id) + ";";
        }
        try {
            this.db.execSQL(command);
            for (Ingredient i: this.ingredients) {
                i.save();
            }

            for (Step s: this.steps) {
                s.save();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("COOKBOOK", e.toString());
            return false;
        }
    }

    @Override
    public boolean delete() {
        boolean success = super.delete();

        for (Ingredient i: this.ingredients) {
            success &= i.delete();
        }

        for (Step s: this.steps) {
            success &= s.delete();
        }

        return success;
    }

    @Override
    public boolean createTable() {
        String command = "CREATE TABLE " + this.table + "(";
        command += "id integer primary key,";
        command += "description TEXT,";
        command += "name TEXT,";
        command += "user_id integer,";
        command += "private bool,";
        command += "preparation_time integer,";
        command += "cooking_time integer,";
        command += "grade float";
        command += ");";
        try {
            this.db.execSQL(command);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Recipe find(int id) {
        Cursor c;
        try {
            String[] whereArgs = new String[] {
                    String.valueOf(id)
            };
            c = db.query(this.table, null, "id = ?", whereArgs, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("COOKBOOK", e.toString());
            return null;
        }

        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Recipe recipe = new Recipe(db, c.getInt(0));
        recipe.description= c.getString(1);
        recipe.name = c.getString(2);
        recipe.user_id = c.getInt(3);
        recipe.Private = c.getInt(4) == 1;
        recipe.preparation_time = c.getInt(5);
        recipe.cooking_time = c.getInt(6);
        recipe.grade = c.getFloat(7);

        recipe.ingredients = (new Ingredient(this.db)).findByRecipeId(recipe.id);
        recipe.steps = (new Step(this.db)).findByRecipeId(recipe.id);

        c.close();
        return recipe;
    }

    public List<Recipe> all() {
        Cursor c;
        try {
            c = db.query(this.table, null, null, null, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

//        Log.d("COOKBOOK", "all");
        List<Recipe> is = new ArrayList<>();
        if (!c.moveToFirst()) {
            c.close();
            return is;
        }

        do {
            Recipe recipe = new Recipe(db, c.getInt(0));
            recipe.description= c.getString(1);
            recipe.name = c.getString(2);
            recipe.user_id = c.getInt(3);
            recipe.Private = c.getInt(4) == 1;
            recipe.preparation_time = c.getInt(5);
            recipe.cooking_time = c.getInt(6);
            recipe.grade = c.getFloat(7);

            recipe.ingredients = (new Ingredient(this.db)).findByRecipeId(recipe.id);
            recipe.steps = (new Step(this.db)).findByRecipeId(recipe.id);

            is.add(recipe);
        } while (c.moveToNext());

        c.close();
        return is;
    }

    public JSONObject jsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", this.id);
            json.put("description", this.description);
            json.put("name", this.name);
            json.put("author", this.user_id);
            json.put("cooking_time", this.cooking_time);
            json.put("preparation_time", this.preparation_time);
            json.put("grade", (double) this.grade);
            // steps
            Step step = new Step(this.db);
            json.put("steps", step.jsonByRecipeId(this.id));
            // ingredients
            Ingredient ingredient = new Ingredient(this.db);
            json.put("ingredients", ingredient.jsonByRecipeId(this.id));
            // tags
        } catch (JSONException e) {
            Log.d("COOKBOOK", e.toString());
            return new JSONObject();
        }

        return json;
    }

    public JSONArray jsonAll() {
        List<Recipe> li = this.all();

        JSONArray json = new JSONArray();
        for (Recipe i: li) {
            json.put(i.jsonObject());
        }

        return json;
    }

    public JSONArray toJSONArray() {
        JSONArray json = new JSONArray();
        JSONObject j = this.jsonObject();
        if (j != null)
            json.put(this.jsonObject());

        return json;
    }

    public Recipe fromJSONObject(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.description = json.getString("description");
            this.name = json.getString("name");
            try {
                this.Private = json.getBoolean("private");
            } catch (JSONException e) {
                this.Private = false;
            }
            this.user_id = json.getInt("author");
            this.preparation_time = json.getInt("preparation_time");
            this.cooking_time = json.getInt("cooking_time");
            this.grade = (float) json.getDouble("grade");

            JSONArray steps = json.getJSONArray("steps");
            for (int i = 0; i < steps.length(); ++i) {
                Step step = new Step(this.db);
                step.fromJSONObject(steps.getJSONObject(i));
                step.recipe_id = this.id;
                this.steps.add(step);
            }
            // ingredients
            JSONArray ingredients = json.getJSONArray("ingredients");
            for (int i = 0; i < ingredients.length(); ++i) {
                Ingredient ingredient = new Ingredient(this.db);
                ingredient.fromJSONObject(ingredients.getJSONObject(i));
                ingredient.recipe_id = this.id;
                this.ingredients.add(ingredient);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("COOKBOOK", e.toString());
        }

        return this;
    }
}
