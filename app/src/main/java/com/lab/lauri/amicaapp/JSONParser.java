package com.lab.lauri.amicaapp;
// package com.androidhive.jsonparsing;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {

    public JSONParser() {
    }

    // HTTP request
    public JSONObject getJSONObjectFromURL(String urls) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urls);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        //Log.d("JSON STRING: ", jsonString);

        return new JSONObject(jsonString);
    }
}
