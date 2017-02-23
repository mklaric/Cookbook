
package com.fluxes.cookbook;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fluxes.cookbook.database.Database;
import com.fluxes.cookbook.database.Recipe;

import org.json.JSONArray;
import org.json.JSONException;


public class OpenRecipe extends AppCompatActivity {

    TextView title,preptime, cooktime, description, ratingText;
    TableLayout ingredient, step;
    RatingBar rating;
    Button rateButton;
    String token;
    public int idRecepta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_recipe);
        idRecepta= getIntent().getIntExtra("id",0);

        title= (TextView) findViewById(R.id.openRecipeTitle);
        preptime= (TextView) findViewById(R.id.openPrepTime);
        cooktime= (TextView) findViewById(R.id.openCookTime);
        description=(TextView) findViewById(R.id.openDescription);
        ratingText=(TextView) findViewById(R.id.openRatingText);
        ingredient= (TableLayout) findViewById(R.id.openIngredientsTable);
        step= (TableLayout) findViewById(R.id.openStepsTable);
        rating = (RatingBar) findViewById(R.id.openRatingBar);
        rateButton=(Button)findViewById(R.id.rateButton);

        SharedPreferences mySharedPreferences=getSharedPreferences("token_prefs", MODE_PRIVATE);
        token=mySharedPreferences.getString("access_token", "");

        Database db = new Database(this);
        db.open();
        Recipe recipe = (new Recipe(db.db)).find(idRecepta);
        if (recipe == null) {
            new OpenRecipeRequest().execute(ServerAPI.GetRecipe(idRecepta));
        } else {
            openRecipeRequestHelper(recipe.toJSONArray());
        }

        db.close();

    }


    private class OpenRecipeRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            openRecipeRequestHelper(result.Response());
        }
    }

    private void openRecipeRequestHelper(JSONArray json) {
        try {
            String ingredientName,stepName;
            String Title = json.getJSONObject(0).getString("name");
            String Description = json.getJSONObject(0).getString("description");
            int preparationTime = json.getJSONObject(0).getInt("preparation_time");
            int cookingTime = json.getJSONObject(0).getInt("cooking_time");
            JSONArray ingredients = json.getJSONObject(0).getJSONArray("ingredients");
            JSONArray steps = json.getJSONObject(0).getJSONArray("steps");

            setTitle(Title);
            title.setText(Title);
            description.setText(Description);
            preptime.setText(Integer.toString(preparationTime));
            cooktime.setText(Integer.toString(cookingTime));

            View rows[]=new View[ingredients.length()];
            for (int i = 0; i < ingredients.length(); i++) {
                ingredientName= ingredients.getJSONObject(i).getString("name");
                rows[i] = getLayoutInflater().inflate(R.layout.ingredients_row_open_recipe, null,false);
                TextView ingredientText= (TextView) rows[i].findViewById(R.id.ingredientTextView);
                ingredientText.setText(ingredientName);
                ingredient.addView(rows[i]);
            }


            View rowsteps[]=new View[steps.length()];
            for (int i = 0; i < steps.length(); i++) {
                stepName= steps.getJSONObject(i).getString("description");
                int number= steps.getJSONObject(i).getInt("number");
                rowsteps[i] = getLayoutInflater().inflate(R.layout.steps_row_open_recipe, null, false);

                TextView stepNumber= (TextView) rowsteps[i].findViewById(R.id.stepNumber);
                TextView stepText= (TextView) rowsteps[i].findViewById(R.id.stepTextView);
                stepNumber.setText(Integer.toString(number)+".");
                stepText.setText(stepName);
                step.addView(rowsteps[i]);
            }
            new GetGradeRequest().execute(ServerAPI.GetGrade(token,idRecepta));

        }

        catch (JSONException e){
            Log.d("COOKBOOK", e.toString());

        }
    }

    private class GetGradeRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            Log.d("FILTER", result.Response().toString());

            try {
                int grade= result.Response().getJSONObject(0).getInt("grade");
                if (grade > 0) {
                    rating.setIsIndicator(true);
                    rating.setRating((float) grade);
                    rateButton.setVisibility(View.INVISIBLE);
                    ratingText.setText("Your grade");
                }
            }
            catch (JSONException e){
                Log.d("COOKBOOK", e.toString());
            }
        }

    }

    private class StoreRecipeGradeRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            Database db = new Database(OpenRecipe.this);
            db.open();

            try {
                float grade = (float) result.Response().getJSONObject(0).getDouble("grade");
                int id = result.Response().getJSONObject(0).getInt("id");

                Recipe recipe = new Recipe(db.db).find(id);

                recipe.grade = grade;
                recipe.save();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("COOKBOOK", e.toString());
            }

            db.close();
        }
    }

    private class RateRecipeRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            rating.setIsIndicator(true);
            rateButton.setVisibility(View.INVISIBLE);
            ratingText.setText("Your grade");
            try {
                int id = result.Response().getJSONObject(0).getInt("id");

                new StoreRecipeGradeRequest().execute(ServerAPI.GetRecipe(token, id));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("COOKBOOK", e.toString());
            }

        }
    }

    public void rateRecipe (View view){

        int grade= (int) rating.getRating();
        new RateRecipeRequest().execute(ServerAPI.AddGrade(token, idRecepta,grade));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_recipe, menu);
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
}
