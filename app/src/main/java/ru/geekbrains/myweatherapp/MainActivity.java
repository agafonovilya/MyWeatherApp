package ru.geekbrains.myweatherapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyApp";
    private static final int REQUEST_CITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton startSettingsActivity = findViewById(R.id.imageButton);
        startSettingsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    public void clickOnCitySelection(View view) {
        Intent intent = new Intent(MainActivity.this, CitySelectionActivity.class);
                startActivityForResult(intent, REQUEST_CITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode== REQUEST_CITY){
            if(resultCode==RESULT_OK){
                TextView textView = findViewById(R.id.cityName);
                textView.setText(data.getStringExtra("RESULT"));
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void openDoubleGIS(View view) {
        Uri uri = Uri.parse("dgis://2gis.ru/routeSearch/rsType/car/to/30.315753,59.939069"); //Строим маршрут до александровской колонны
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        // Проверяем, установлено ли хотя бы одно приложение, способное выполнить это действие.

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if (!isIntentSafe) {// Если приложение не установлено — переходим в Google Play.
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=ru.dublgis.dgismobile"));
        }
        startActivity(intent);
    }


}