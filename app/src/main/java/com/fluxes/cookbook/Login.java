package com.fluxes.cookbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class Login extends AppCompatActivity {

    public static final String MYPREFS= "token_prefs";
    TextView content;
    EditText username,  pass;
    String  Username,  Pass;
    /**/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_login);

            content = (TextView) findViewById(R.id.content);
            username = (EditText) findViewById(R.id.username);
            pass = (EditText) findViewById(R.id.password);


            Button saveme = (Button) findViewById(R.id.save);

            saveme.setOnClickListener(new Button.OnClickListener() {

                public void onClick(View v) {
                    try {

                        // CALL GetText method to make post method call
                        new LoginPost().execute(getString(R.string.server) +"/api/users/login");
                    } catch (Exception ex) {
                        content.setText(" url exception! ");
                    }
                }
            });

    }

    private class LoginPost extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls){
            return GetText(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.substring(0,1).equals("1")){

                //add token to preferences
                SharedPreferences preferences = getSharedPreferences(MYPREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("access_token", result.substring(1,result.length()));
                editor.commit();
                // go to main screen
                Intent startScreen = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(startScreen);
            }
            else {
                TextView tekst = (TextView) findViewById(R.id.content);
                tekst.setText(result);
                username.setText("");
                pass.setText("");
            }

        }

    }

    public  String  GetText(String URLString)
    {
        String text = "";

        // Get user defined values
        Username   = username.getText().toString();
        Pass   = pass.getText().toString();

        // Create data variable for sent values to server
        try
        {

            String data = URLEncoder.encode("username", "UTF-8")
                    + "=" + URLEncoder.encode(Username, "UTF-8");

            data += "&" + URLEncoder.encode("password", "UTF-8")
                    + "=" + URLEncoder.encode(Pass, "UTF-8");

            // Defined URL  where to send data
            URL url = new URL(URLString);

            // Send POST data request

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write(data);

            writer.flush();
            writer.close();
            os.close();

            //Response from server
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK ) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    text+=line;
                }
                line=text.split(":")[1];
                text="1"+line.substring(2,line.length()-2);

            }
            else if(responseCode==422){
                String line;
                int brojac=0;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    text+=line;
                }
                line=(text.split(",")[1]).split(":")[1];
                text=line.substring(2,line.length()-2);

            }
            else {
                text="Krivo spajanje";

            }
        } catch (Exception e) {

            e.printStackTrace();
            return "Server not working";
        }

        return text;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
