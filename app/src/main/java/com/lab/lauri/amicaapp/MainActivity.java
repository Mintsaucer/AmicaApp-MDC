package com.lab.lauri.amicaapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

@EActivity
public class MainActivity extends AppCompatActivity {

    @ViewById
    TextView tv_date;
    @ViewById
    ListView list_view;
    @ViewById
    ProgressBar progressbar;
    @ViewById
    Button search_btn;
    @ViewById
    EditText input_edit_text;

    String language = "en";

    private ArrayList<String> names = new ArrayList<>();
    private ArrayAdapter adapter;

    //JSON Nodes
    private static final String TAG_LUNCHMENU = "LunchMenu";
    private static final String TAG_DAY = "DayOfWeek";
    private static final String TAG_DATE = "Date";
    private static final String TAG_MENUS = "SetMenus";
    private static final String TAG_MEALS = "Meals";
    private static final String TAG_NAME = "Name";

    private String day, date;
    private String url = "http://amica.fi/api/restaurant/menu/day?date=2017-10-10&language=en&restaurantPageId=66287";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<>(this, R.layout.meal_list, names);
        new ParseTask().execute(url);
    }

    public void searchClicked(View view) {
        String input = input_edit_text.getText().toString();
        url = "http://amica.fi/api/restaurant/menu/day?date=" + input + "&language="+ language + "&restaurantPageId=66287";
        names.clear();
        new ParseTask().execute(url);
    }

    private class ParseTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONParser jParser = new JSONParser();
                JSONObject json = null;

                json = jParser.getJSONObjectFromURL(url);
                JSONObject mainObject = json.getJSONObject(TAG_LUNCHMENU);

                day = String.valueOf(mainObject.getString(TAG_DAY));
                date = String.valueOf(mainObject.getString(TAG_DATE));

                JSONArray menus = mainObject.getJSONArray(TAG_MENUS);
                Log.d("JSON menu", menus.toString());

                for (int i = 0; i < menus.length(); i++) {
                    JSONObject jsonMeals = menus.getJSONObject(i);
                    JSONArray meals = jsonMeals.getJSONArray(TAG_MEALS);
                    Log.d("JSON meals",  meals.toString());

                    for (int j = 0; j < meals.length(); j++) {
                        JSONObject jsonNames = meals.getJSONObject(j);
                        String name = String.valueOf(jsonNames.getString(TAG_NAME));
                        names.add(name);
                        Log.d("JSON names", names.get(j));
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String ab) {
            print();
        }
    }

    @UiThread
    public void print() {
        tv_date.setText(day + " " + date);
        Log.d("listArray size: ", String.valueOf(names.size()));

        for (int i = 0; i < names.size(); i++)
        {
            // set adapter to listView
            list_view.setAdapter(adapter);
        }
        progressbar.setVisibility(View.INVISIBLE);
    }

    //Tsekkaa painetun napin ID:n, jonka mukaan asettaa kielen
    public void setLanguage(View v)
    {
        switch (v.getId()){
            case R.id.language_english_button:
                //url = "http://amica.fi/api/restaurant/menu/day?date=2017-10-10&language=en&restaurantPageId=66287";
                language = "en";
                Log.d("Kieliasetus", "Englanti");
                break;
            case R.id.language_finnish_button:
                //url = "http://amica.fi/api/restaurant/menu/day?date=2017-10-10&language=fi&restaurantPageId=66287";
                language = "fi";
                Log.d("Kieliasetus", "Suomi");
                break;
            default:
                //url = "http://amica.fi/api/restaurant/menu/day?date=2017-10-10&language=en&restaurantPageId=66287";
                language = "en";
        }
    }
}