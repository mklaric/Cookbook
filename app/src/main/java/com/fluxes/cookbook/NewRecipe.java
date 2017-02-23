package com.fluxes.cookbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fluxes.cookbook.database.Database;
import com.fluxes.cookbook.database.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewRecipe extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    private ImageView imageView1, imageView2, imageView3, imageView4,imageView5;
    public static final String MYPREFS= "token_prefs";
    EditText title,preptime, cooktime, description;
    TableLayout ingredient, step;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_new_recipe);

        SharedPreferences mySharedPreferences=getSharedPreferences(MYPREFS,MODE_PRIVATE);
        token=mySharedPreferences.getString("access_token", "");

        /*imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);*/

        title= (EditText) findViewById(R.id.TitleText);
        preptime= (EditText) findViewById(R.id.enterPrepTime);
        cooktime= (EditText) findViewById(R.id.enterCookTime);
        description=(EditText) findViewById(R.id.DescriptionText);
        ingredient= (TableLayout) findViewById(R.id.ingredientsTable);
        step= (TableLayout) findViewById(R.id.stepsTable);

        View ingrow = getLayoutInflater().inflate(R.layout.ingredients_row, null,false);
        ingredient.addView(ingrow);

        View strow = getLayoutInflater().inflate(R.layout.steps_row, null,false);
        step.addView(strow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_recipe, menu);
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

    public void openImageGallery(View view){
        int id = view.getId();            // get integer id of view
        Resources res = view.getResources();     // get resources
        String idString = res.getResourceEntryName(id);


        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");

        if(idString.equals("imageView1"))
            startActivityForResult(photoPickerIntent,1 );
        else if(idString.equals("imageView2"))
            startActivityForResult(photoPickerIntent,2 );
        else if(idString.equals("imageView3"))
            startActivityForResult(photoPickerIntent,3 );
        else if(idString.equals("imageView4"))
            startActivityForResult(photoPickerIntent,4 );
        else if(idString.equals("imageView5"))
            startActivityForResult(photoPickerIntent,5 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        if(resultCode== RESULT_OK)
        {
            Uri selectedImage = imageReturnedIntent.getData();
            if(requestCode==1)
                    imageView1.setImageURI(selectedImage);
            else if(requestCode==2)
                imageView2.setImageURI(selectedImage);
            else if(requestCode==3)
                imageView3.setImageURI(selectedImage);
            else if(requestCode==4)
                imageView4.setImageURI(selectedImage);
            else if(requestCode==5)
                imageView5.setImageURI(selectedImage);

        }
    }

    // to manifest added android:configChanges="keyboardHidden|orientation" which tells system what configuration changes i am gonna handle by myself
    public void   addIngRow (View V){

        TableLayout table= (TableLayout) findViewById(R.id.ingredientsTable);
        View tbrow = getLayoutInflater().inflate(R.layout.ingredients_row, null,false);
        table.addView(tbrow);
    }

    //za pristupanje elementima će se koristiti table.getChildCount() i getChildAt(int value)

    public void deleteIngRow(View view)
    {
        TableLayout table= (TableLayout) findViewById(R.id.ingredientsTable);
        table.removeView((View) view.getParent());
    }

    public void   addStepRow (View V){

        TableLayout table= (TableLayout) findViewById(R.id.stepsTable);
        View tbrow = getLayoutInflater().inflate(R.layout.steps_row, null,false);
        table.addView(tbrow);
    }

    //za pristupanje elementima će se koristiti table.getChildCount() i getChildAt(int value)

    public void deleteStepRow(View view)
    {
        TableLayout table= (TableLayout) findViewById(R.id.stepsTable);
        table.removeView((View) view.getParent());
    }

    private class AddIngredientRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            Log.d("FILTER", result.Response().toString());

        }
    }

    private class AddStepRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            Log.d("FILTER", result.Response().toString());

        }
    }

    private class OpenRecipeRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            Database db = new Database(NewRecipe.this);
            db.open();

            try {
                Recipe recipe = (new Recipe(db.db)).fromJSONObject(result.Response().getJSONObject(0));
                recipe.save();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("COOKBOOK", e.toString());
            }

            db.close();
            kill_activity();
        }
    }

    private class NewRecipeRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {
            try{
                int id= result.Response().getJSONObject(0).getInt("id");
                for(int i=0; i< ingredient.getChildCount(); ++i)
                {
                    View v=ingredient.getChildAt(i);
                    EditText oneIngredient = (EditText) v.findViewById(R.id.ingredientEditText);
                    String ingredientString = oneIngredient.getText().toString();
                    new AddIngredientRequest().execute(ServerAPI.AddIngredient(token, id,ingredientString));
                }
                for(int i=0; i< step.getChildCount(); ++i)
                {
                    View v=step.getChildAt(i);
                    EditText oneStep = (EditText) v.findViewById(R.id.stepEditText);
                    String stepString = oneStep.getText().toString();
                    new AddStepRequest().execute(ServerAPI.AddStep(token, id, stepString));
                }

                new OpenRecipeRequest().execute(ServerAPI.GetRecipe(id));
            }
            catch (JSONException e){
                Log.d("COOKBOOK", e.toString());
            }

        }
    }

    public void SaveRecipe(View view){


        String name=title.getText().toString();
        String desc=description.getText().toString();
        int preparationTime= Integer.valueOf(preptime.getText().toString());
        int cookingTime= Integer.valueOf(cooktime.getText().toString());


        new NewRecipeRequest().execute(ServerAPI.AddRecipe(token, name, desc, false, preparationTime, cookingTime));

    }

    public void kill_activity()
    {
        finish();
    }


}