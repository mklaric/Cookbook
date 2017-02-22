package com.fluxes.cookbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;

public class Favorites extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        TableLayout recipes= (TableLayout) findViewById(R.id.favsTable);
        View ingrow = getLayoutInflater().inflate(R.layout.recipe_info_row, null,false);
        View ingrow2 = getLayoutInflater().inflate(R.layout.recipe_info_row, null,false);
        View ingrow3 = getLayoutInflater().inflate(R.layout.recipe_info_row, null,false);

        recipes.addView(ingrow);
        recipes.addView(ingrow2);
        recipes.addView(ingrow3);

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
        Intent intent = new Intent(this, OpenRecipe.class);
        startActivity(intent);
    }
}
