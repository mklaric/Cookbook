package com.fluxes.cookbook;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;


class RequestSender {
    public static JSONArray Send(Request request) {
        //The JSON we will get back as a response from the server
        JSONObject jsonResponse = null;
        JSONArray jsonArray= null;

        //Http connections and data streams
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) request.Url().openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            if (!request.Method().equals("GET")) {
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod(request.Method());

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(request.Input());

                writer.flush();
                writer.close();
                os.close();
            }

            //prepare input buffer and get the http response from server
            StringBuilder stringBuilder = new StringBuilder();
            int responseCode = conn.getResponseCode();

            //Check to make sure we got a valid status response from the server,
            //then get the server JSON response if we did.
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //read in each line of the response to the input buffer
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close(); //close out the input stream

                try {
                    Object json = new JSONTokener(stringBuilder.toString()).nextValue();
                    if (json instanceof JSONArray)
                        jsonArray = new JSONArray(stringBuilder.toString());
                    else if (json instanceof JSONObject)
                        jsonResponse = new JSONObject(stringBuilder.toString());
                } catch (JSONException je) {
                    je.printStackTrace();
                }

            } else {
                //read in each line of the response to the input buffer
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(),"utf-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close(); //close out the input stream

                try {
                    Object json = new JSONTokener(stringBuilder.toString()).nextValue();
                    if (json instanceof JSONArray)
                        jsonArray = new JSONArray(stringBuilder.toString());
                    else if (json instanceof JSONObject)
                        jsonResponse = new JSONObject(stringBuilder.toString());
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if(conn != null) {
                conn.disconnect(); //close out our http connection
            }
        }

        if (jsonResponse != null)
            try {
                return new JSONArray("[" + jsonResponse.toString() + "]");
            } catch (JSONException e) {}
        //Return the JSON response from the server.
        return jsonArray;
    }
}
