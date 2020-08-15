package ru.geekbrains.myweatherapp.broadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import androidx.core.app.NotificationCompat;

import ru.geekbrains.myweatherapp.R;

public class ConnectivityChange extends BroadcastReceiver {
    private static final String TAG = "WEATHER";
    private int messageId = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ConnectivityChange");

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities network = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if (network != null) {
            if(network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                builder.setContentText("Получен доступ в сеть Интернет");
            }
            if (network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && !network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                builder.setContentText("Отсутствует доступ в сеть Интернет");
            }
            if (!network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                builder.setContentText("Передача через сотовую сеть");
            }
            if (network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && !network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                builder.setContentText("Передача через вай фай");
            }
        } else {
            builder.setContentText("Отсутствует доступ в сеть Интернет");
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());
    }
}
