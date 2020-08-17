package ru.geekbrains.myweatherapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ru.geekbrains.myweatherapp.broadcastReceivers.ConnectivityChange;
import ru.geekbrains.myweatherapp.broadcastReceivers.PowerConnected;
import ru.geekbrains.myweatherapp.screen.mainScreen.MainScreenFragment;

public class MainActivity extends AppCompatActivity implements StartFragment {
    private ConnectivityChange wiFiStateChange = new ConnectivityChange();
    private PowerConnected powerConnected = new PowerConnected();

    private static final String TAG = "WEATHER";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_by_fragment);

        registerBroadcastReceivers();
        initNotificationChannel();
        startFragment(new MainScreenFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wiFiStateChange);
        unregisterReceiver(powerConnected);
    }

    @Override
    public void onBackPressed() {
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.container_for_fragment);
        // Проверяем, что активный фрагмент является фрагментов класса MainScreenFragment
        if (activeFragment!= null && activeFragment.getClass() == MainScreenFragment.class) {
            // Вызываем у активного фрагмента метод onBackPressed
            if (!((MainScreenFragment) activeFragment).onBackPressed()) {
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void startFragment(Fragment fragment) {
        /*if (fragment.getClass() == MainScreenFragment.class) {

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry first = getSupportFragmentManager().getBackStackEntryAt(0);
                getSupportFragmentManager().popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Log.d(TAG, "startFragment: ++++++++++++++++++++++++++++");

            }
        }*/
        /*if (fragment.getClass() == MainScreenFragment.class) {
            while (getSupportFragmentManager().getBackStackEntryCount() > 1){
                Log.d(TAG, "startFragment: ++++++++++++++++++++++++++++");
                getSupportFragmentManager().popBackStackImmediate();
            }
        }*/

       /* FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (fragment.getClass() != MainScreenFragment.class) {
            ft.addToBackStack();
        }

        ft.replace(R.id.container_for_fragment, fragment);
        ft.commit();*/

        String backStateName = fragment.getClass().getName();

        if (fragment.getClass().equals(MainScreenFragment.class)) {
            getSupportFragmentManager().popBackStackImmediate(MainScreenFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_for_fragment, fragment);
        fragmentTransaction.addToBackStack(backStateName);
        fragmentTransaction.commit();
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void registerBroadcastReceivers() {
        registerReceiver(wiFiStateChange, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        registerReceiver(powerConnected, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
    }

}
