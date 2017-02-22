package com.fluxes.cookbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;

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

            TableLayout recipes= (TableLayout) findViewById(R.id.myRecipesTable);
            View ingrow = getLayoutInflater().inflate(R.layout.recipe_info_row, null,false);
            View ingrow2 = getLayoutInflater().inflate(R.layout.recipe_info_row, null,false);
            View ingrow3 = getLayoutInflater().inflate(R.layout.recipe_info_row, null,false);
            View ingrow4 = getLayoutInflater().inflate(R.layout.recipe_info_row, null,false);

            recipes.addView(ingrow);
            recipes.addView(ingrow2);
            recipes.addView(ingrow3);
            recipes.addView(ingrow4);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        Intent intent = new Intent(this, OpenRecipe.class);
        startActivity(intent);
    }
}
