package com.example.muttaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

@SuppressWarnings("FieldCanBeLocal")
public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String selectedColor;
    private String selectedtextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner spinnerselectcolor=findViewById(R.id.spinner_select_color);
        ArrayAdapter<CharSequence>adapter= ArrayAdapter.createFromResource(this, R.array.colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerselectcolor.setAdapter(adapter);
        spinnerselectcolor.setOnItemSelectedListener(this);

        Spinner spinnerselecttextcolor=findViewById(R.id.spinner_select_text_color);
        ArrayAdapter<CharSequence>adapter2= ArrayAdapter.createFromResource(this, R.array.text_colors, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerselecttextcolor.setAdapter(adapter2);
        spinnerselecttextcolor.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView <?> parent, View view, int position, long id){
        int spinnerid = parent.getId();
        if(spinnerid == R.id.spinner_select_color){
            selectedColor = parent.getItemAtPosition(position).toString();
            SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("selected_color", selectedColor);
            editor.apply();
        }else if (spinnerid == R.id.spinner_select_text_color){
            selectedtextColor = parent.getItemAtPosition(position).toString();
            SharedPreferences sharedPref2 = getSharedPreferences("user_text_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sharedPref2.edit();
            editor2.putString("select_text_color", selectedtextColor);
            editor2.apply();
        }

    }

    @Override
    public void onNothingSelected(AdapterView <?> parent){

    }

}