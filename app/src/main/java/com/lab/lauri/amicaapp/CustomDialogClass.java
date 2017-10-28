package com.lab.lauri.amicaapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Luokan on luonut tuomo päivämäärällä 28.10.2017.
 */

class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    private DatePicker datePicker;
    private Activity c;
    public Dialog d;
    private Button buttonCancel, buttonOk;


    private int dayOfMonth;
    private int month;
    private int year;
    private String pickedDate;

    EditText editText;

    CustomDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_layout);
        datePicker = (DatePicker) findViewById(R.id.date_picker);
        buttonCancel = (Button) findViewById(R.id.btn_cancel);
        buttonCancel.setOnClickListener(this);

        setDatePickerLimits();
        datePicker.setBackgroundColor(Color.WHITE);
        //datePicker.getBackground().setAlpha(20);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDatePickerLimits()
    {
        Calendar calendar = Calendar.getInstance();
        long minTime;
        long maxtime;

        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
        {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            minTime = calendar.getTimeInMillis();
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            maxtime = calendar.getTimeInMillis();
        }
        else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
        {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            minTime = calendar.getTimeInMillis();
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            maxtime = calendar.getTimeInMillis();
        }
        else
        {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            minTime = calendar.getTimeInMillis();
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            maxtime = calendar.getTimeInMillis();
        }

        datePicker.setMinDate(minTime);
        datePicker.setMaxDate(maxtime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                //Toast.makeText(c.getApplicationContext(),c.getResources().getString(R.string.support_text), Toast.LENGTH_LONG).show();
            case R.id.date_picker:
                dayOfMonth = datePicker.getDayOfMonth();
                month = datePicker.getMonth() + 1;
                year = datePicker.getYear();
                pickedDate = String.format("%04d-%02d-%02d",year, month, dayOfMonth);
                setSharedPreference("sharedPreferences", "pickedDate", pickedDate);
                Log.d("CDC Date", pickedDate);
                break;
            default:
                break;
        }
        dismiss();
    }

    int getDayOfMonth()
    {
        return datePicker.getDayOfMonth();
    }

    int getMonth()
    {
        return datePicker.getMonth() + 1;
    }

    int getYear()
    {
        return datePicker.getYear();
    }

    //Metodi, jolla voi lisätä jaetun muuttujan
    private void setSharedPreference(String sharedPrefTag, String sharedVariableTag, String sharedVariable)
    {
        //Lähetetään tiedot Fragmenttiin SharedPreferencen avulla
        SharedPreferences pref = c.getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(sharedVariableTag, sharedVariable);
        editor.apply();
        Log.d("Shared variable", sharedVariable + " w/ tag: " + sharedVariableTag);
    }

    //Metodi, jolla voi hakea jaetun String -muuttujan
    private String getSharedPreferences(String sharedPrefTag, String sharedVariableTag)
    {
        SharedPreferences pref = c.getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        return pref.getString(sharedVariableTag, null);
    }
}
