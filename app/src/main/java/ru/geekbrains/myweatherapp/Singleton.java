package ru.geekbrains.myweatherapp;

import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Singleton {
    private static Singleton instance;
    String time;

    private Singleton() {
        Date date = new Date();
        SimpleDateFormat formatOfDate = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
        this.time = formatOfDate.format(date);
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public String getTime(){
        return time;
    }
}
