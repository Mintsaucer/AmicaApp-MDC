package com.lab.lauri.amicaapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Luokan on luonut tuomo päivämäärällä 28.10.2017.
 */

class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    private DatePicker datePicker;
    private Activity c;
    private Button buttonCancel, buttonOk;

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
        //datePicker.getBackground().setAlpha(20);
    }

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

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:

                break;
            case R.id.btn_ok:

                break;
            case R.id.date_picker:

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
