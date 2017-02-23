package com.fluxes.cookbook;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo extends AppCompatActivity {

    TextView birthday,email,first_name,last_name,gender,user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        birthday= (TextView) findViewById(R.id.userBirthday);
        email= (TextView) findViewById(R.id.userEmail);
        first_name= (TextView) findViewById(R.id.userFirstName);
        last_name=(TextView) findViewById(R.id.userLastName);
        gender=(TextView) findViewById(R.id.userGender);
        user_name=(TextView) findViewById(R.id.userUsername);


        SharedPreferences mySharedPreferences=getSharedPreferences("token_prefs",MODE_PRIVATE);
        String token=mySharedPreferences.getString("access_token", "");
        new UserInfoRequest().execute(ServerAPI.GetProfile(token));

    }

    private class UserInfoRequest extends SendRequest {
        @Override
        protected void onPostExecute(Request result) {

           // Log.d("FILTER", result.Response().toString());

            try {
                String Email = result.Response().getJSONObject(0).getString("email");
                String First_name = result.Response().getJSONObject(0).getString("first_name");
                String Last_name= result.Response().getJSONObject(0).getString("last_name");
                String User_name= result.Response().getJSONObject(0).getString("username");
                String Gender= result.Response().getJSONObject(0).getString("gender");
                JSONObject date= result.Response().getJSONObject(0).getJSONObject("birthday");

                String Birthday= Integer.toString(date.getInt("day"))+ ". "+
                        Integer.toString(date.getInt("month"))+ ". "+
                        Integer.toString(date.getInt("year"));

                setTitle("Hi " + First_name + " !");
                email.setText(Email);
                first_name.setText(First_name);
                last_name.setText(Last_name);
                user_name.setText(User_name);
                gender.setText(Gender);
                birthday.setText(Birthday);

            }

            catch (JSONException e){
                Log.d("COOKBOOK", e.toString());

            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
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
