package com.lab.lauri.amicaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

@EActivity
public class MainActivity extends AppCompatActivity {

    @ViewById
    TextView tv_date;
    @ViewById
    ListView list_view;
    @ViewById
    ProgressBar progressbar;
    @ViewById
    Button search_btn, language_english_button, language_finnish_button;

    String language = "en";
    String defaultDate;

    SharedPreferences sharedPreferences;

    private ArrayList<String> names = new ArrayList<>();
    private ArrayAdapter adapter;

    private final String MyPREFERENCES = "MyPrefs";
    private final String Lang = "langKey";

    //JSON Nodes
    private static final String TAG_LUNCHMENU = "LunchMenu";
    private static final String TAG_DAY = "DayOfWeek";
    private static final String TAG_DATE = "Date";
    private static final String TAG_MENUS = "SetMenus";
    private static final String TAG_MEALS = "Meals";
    private static final String TAG_NAME = "Name";

    private String day, date;
    private String url = "http://amica.fi/api/restaurant/menu/day?date=2017-10-10&language=en&restaurantPageId=66287";
    CustomDialogClass customDialogClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Tekee notification barista läpinäkyvän
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        checkSelectedLanguage();
        getDefaultDate();
        customDialogClass = new CustomDialogClass(this);
        customDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Hakee default ruokalistan sovelluksen avautuessa (nykyinen päivä / viikonloppuna seuraava maanantai)
        getDefaultMealList();
    }

    public void getDefaultMealList()
    {
        url = "http://amica.fi/api/restaurant/menu/day?date=" + defaultDate + "&language="+ language + "&restaurantPageId=66287";
        adapter = new ArrayAdapter<>(this, R.layout.meal_list, names);
        list_view.setAdapter(adapter);
        new ParseTask().execute(url);
    }

    @SuppressLint("DefaultLocale")
    public void getDefaultDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.get(Calendar.DATE);
        //TODO: korjaa kuukauden hakeminen, nykyinen metodi palauttaa kuukauden numeroa liian pienenä (Ilmeisesti koska kuukaudet on ilmoitettu 0-11)
        int month = calendar.get(Calendar.MONTH) + 1 ; //Purkkakorjaus
        int date = calendar.get(Calendar.DATE);

        //Toteutuu jos on viikonloppu -> siirrytään seuraavan viikon maanantaihin
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
        {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Log.d("Nyt on", String.valueOf(calendar.get(Calendar.DAY_OF_WEEK))); //Viikonloppuna amican JSON ei toimi
            date = calendar.get(Calendar.DATE);
        }

        defaultDate = calendar.get(Calendar.YEAR) + "-" + month + "-" + date;
        defaultDate = String.format("%04d-%02d-%02d",calendar.get(Calendar.YEAR), month, date); //Muotoilee päiväyksen Amican Jsoniin sopivaksi
        Log.d("Default Date", defaultDate);
        tv_date.setText(defaultDate);
    }

    public void checkSelectedLanguage()
    {
        SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        language = preferences.getString(Lang, "");

        switch (language){
            case "fi":
                language_english_button.getBackground().setColorFilter(0xe9999999,PorterDuff.Mode.SRC_ATOP);
                language_finnish_button.getBackground().setColorFilter(null);
                break;
            case "en":
                language_finnish_button.getBackground().setColorFilter(0xe9999999,PorterDuff.Mode.SRC_ATOP); //Vaihtaa upean oranssin värin klikattuun nappulaan
                language_english_button.getBackground().setColorFilter(null); //Ottaa filtterin pois toisesta nappulasta
                break;
            default:
                language = "en";
                language_finnish_button.getBackground().setColorFilter(0xe9999999,PorterDuff.Mode.SRC_ATOP); //Vaihtaa upean oranssin värin klikattuun nappulaan
                language_english_button.getBackground().setColorFilter(null); //Ottaa filtterin pois toisesta nappulasta
                break;
        }
    }

    public void searchClicked(View view) {

        customDialogClass.show();
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
        if(day != null && date != null)
        {
            tv_date.setText(day + " " + date);
        }
        else
        {
            tv_date.setText("Error");
        }

        Log.d("listArray size: ", String.valueOf(names.size()));
        adapter.notifyDataSetChanged(); // Adapteria täytyy virkistää muutoksen jälkeen, jotta näkymä päivittyy
        progressbar.setVisibility(View.INVISIBLE);
    }

    //Tsekkaa painetun napin ID:n, jonka mukaan asettaa kielen
    public void setLanguage(View v)
    {
        switch (v.getId()){
            case R.id.language_english_button:
                language = "en";
                language_finnish_button.getBackground().setColorFilter(0xe9999999,PorterDuff.Mode.SRC_ATOP); //Vaihtaa upean oranssin värin klikattuun nappulaan
                language_english_button.getBackground().setColorFilter(null); //Ottaa filtterin pois toisesta nappulasta
                search_btn.setText("Search"); //TODO: vaihtaa values -> EN
                updateResources(this, "en_US");
                Log.d("Kieliasetus", "Englanti");
                break;
            case R.id.language_finnish_button:
                language = "fi";
                language_english_button.getBackground().setColorFilter(0xe9999999,PorterDuff.Mode.SRC_ATOP);
                language_finnish_button.getBackground().setColorFilter(null);
                search_btn.setText("Hae"); //TODO: vaihtaa values -> FI
                updateResources(this, "fi_FI");
                Log.d("Kieliasetus", "Suomi");
                break;
            default:
                language = "en";
        }
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(Lang, language);
        edit.apply(); //commit();
    }

    public void onButtonOkClicked(View v)
    {
        progressbar.setVisibility(View.VISIBLE);
        @SuppressLint("DefaultLocale") String formattedPickedDate = String.format("%04d-%02d-%02d",customDialogClass.getYear(), customDialogClass.getMonth(), customDialogClass.getDayOfMonth());
        names.clear(); //Tyhjennetään lista, jotta sen voi päivittää
        url = "http://amica.fi/api/restaurant/menu/day?date=" + formattedPickedDate + "&language="+ language + "&restaurantPageId=66287";
        new ParseTask().execute(url);
        customDialogClass.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        language = preferences.getString(Lang, "");
    }

    //Metodi, jolla voi hakea jaetun String -muuttujan
    private String getSharedPreferences(String sharedPrefTag, String sharedVariableTag)
    {
        SharedPreferences pref = this.getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        return pref.getString(sharedVariableTag, null);
    }
}