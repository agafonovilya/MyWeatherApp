package ru.geekbrains.myweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class CitySelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);
    }

    @Override
    public void onBackPressed() {
        Spinner city = findViewById(R.id.spinner);
        Intent intent = new Intent();
        intent.putExtra("RESULT", city.getSelectedItem().toString());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
    
    public void clickOnBack(View view) {
        onBackPressed();
    }
}
