package com.fluxes.cookbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;

public class Search extends AppCompatActivity {

    public static final String MYPREFS= "token_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_search);

        SharedPreferences mySharedPreferences=getSharedPreferences(MYPREFS,MODE_PRIVATE);
        String token=mySharedPreferences.getString("access_token", "");
        new AllRecipesRequest().execute(ServerAPI.RecipeQuery(token));
    }

    private class AllRecipesRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {

            try {
                String Title = null;
                int numberOfRecipes= result.Response().length();

                View rows[]=new View[numberOfRecipes];
                TableLayout recipes= (TableLayout) findViewById(R.id.searchTable);

                for (int i = 0; i < numberOfRecipes; ++i) {

                    Title = result.Response().getJSONObject(i).getString("name");
                    String Description = result.Response().getJSONObject(i).getString("description");
                    float Grade=(float) result.Response().getJSONObject(i).getDouble("grade");
                    int id=result.Response().getJSONObject(i).getInt("id");


                    rows[i]=getLayoutInflater().inflate(R.layout.recipe_info_row, null,false);
                    TextView title= (TextView) rows[i].findViewById(R.id.recInfoTitle);
                    TextView description = (TextView) rows[i].findViewById(R.id.recInfoDescription);
                    RatingBar rating = (RatingBar) rows[i].findViewById(R.id.recInfoRating);


                    title.setText(Title);
                    title.setTag(id);
                    description.setText(Description);
                    rating.setRating(Grade);

                    recipes.addView(rows[i]);
                }
                Log.d("FILTER", result.Url().toString());
            }
            catch (JSONException e){
                Log.d("COOKBOOK", e.toString());

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }

    public void openRecipeActivity(View view)
    {
        int id= (Integer) view.getTag();
        Intent intent = new Intent(this, OpenRecipe.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
