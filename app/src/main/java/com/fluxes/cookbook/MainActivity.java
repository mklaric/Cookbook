package com.fluxes.cookbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fluxes.cookbook.database.Database;
import com.fluxes.cookbook.database.Recipe;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    public static final String MYPREFS= "token_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(MYPREFS,MODE_PRIVATE);
        if(!preferences.contains("access_token")) {
            Intent loginScreen = new Intent(getApplicationContext(),Login.class);
            startActivity(loginScreen);
        }
        else {
            setContentView(R.layout.activity_main);
            String token=preferences.getString("access_token", "");

            Database db = new Database(this);
            db.open();

            if ((new Recipe(db.db)).isEmpty()) {
                new MyRecipesRequest().execute(ServerAPI.GetRecipes(token));
            } else {
                myRecipesHelper((new Recipe(db.db)).jsonAll());
            }

            db.close();
        }
    }

    private class MyRecipesRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            myRecipesHelper(result.Response());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private class OpenRecipeRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            Database db = new Database(MainActivity.this);
            db.open();

            try {
                Recipe recipe = (new Recipe(db.db)).fromJSONObject(result.Response().getJSONObject(0));
                recipe.save();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("COOKBOOK", e.toString());
            }

            db.close();
        }
    }

    private void myRecipesHelper(JSONArray json) {
        Database db = new Database(this);
        db.open();
        try {
            String Title = null;
            int numberOfRecipes = json.length();

            View rows[]=new View[numberOfRecipes];
            TableLayout recipes= (TableLayout) findViewById(R.id.myRecipesTable);

            for (int i = 0; i < numberOfRecipes; ++i) {
                Title = json.getJSONObject(i).getString("name");
                String Description = json.getJSONObject(i).getString("description");
                float Grade = (float) json.getJSONObject(i).getDouble("grade");
                int id = json.getJSONObject(i).getInt("id");

                if ((new Recipe(db.db)).find(id) == null) {
                    new OpenRecipeRequest().execute(ServerAPI.GetRecipe(id));
                }

                rows[i]=getLayoutInflater().inflate(R.layout.recipe_info_row, null,false);
                TextView title = (TextView) rows[i].findViewById(R.id.recInfoTitle);
                TextView description = (TextView) rows[i].findViewById(R.id.recInfoDescription);
                RatingBar rating = (RatingBar) rows[i].findViewById(R.id.recInfoRating);


                title.setText(Title);
                title.setTag(id);
                description.setText(Description);
                rating.setRating(Grade);

                recipes.addView(rows[i]);
            }
        }
        catch (JSONException e){
            Log.d("COOKBOOK", e.toString());
        }
        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_favorite) {
            Intent intent = new Intent(this, Favorites.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.user_info) {
            Intent intent = new Intent(this, UserInfo.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createNewRecipe(View view) {
        Intent intent = new Intent(this, NewRecipe.class);
        startActivity(intent);
    }

    public void openRecipeActivity(View view)
    {
        int id= (Integer) view.getTag();
        Intent intent = new Intent(this, OpenRecipe.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
