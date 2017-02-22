package com.fluxes.cookbook;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class Request {
    private JSONObject JSON;
    private String Method;
    private URL Url;
    private JSONArray Response;
    private boolean Valid = true;

    Request(String Url, String Method, JSONObject JSON, JSONArray Response) {
        this.JSON = JSON;
        this.Response = Response;

        try {
            this.Url = new URL(Url);
        } catch (MalformedURLException e) {
            this.Valid = false;
        }

        this.Method = Method;
    }

    Request(String Url, String Method, JSONObject JSON) {
        this(Url, Method, JSON, null);
    }

    public boolean isValid() {
        return this.Valid;
    }

    public JSONObject JSON() {
        return this.JSON;
    }

    public String Method() {
        return this.Method;
    }

    public URL Url() {
        if (this.Method != "GET") {
            return this.Url;
        } else {
            try {
                return new URL(this.Url.toString() + "?" + this.Input());
            } catch (MalformedURLException e) {}
        }
        return null;
    }

    public String Form() {
        if (this.Method == "GET")
            return "";
        return this.Input();
    }

    public String Input() {
        String ret = "";

        if (this.JSON.toString() == "{}")
            return ret;

        for(Iterator iterator = this.JSON.keys(); iterator.hasNext();) {
            String key = (String) iterator.next();
            if (ret != "")
                ret += "&";
            try {
                // Log.d("COOKBOOK", this.JSON.get(key).toString());

                ret += URLEncoder.encode(key, "UTF-8") + "="
                        + URLEncoder.encode(this.JSON.get(key).toString(), "UTF-8");
            } catch (JSONException e) {

            } catch (UnsupportedEncodingException e) {

            }
        }
        return ret;
    }

    public JSONArray Response() {
        return this.Response;
    }
}
