package com.example.wwh.coolweather.app.service;
/*
    Created by Joe on 2016/7/14.
    Email: wwh.cto@foxmail.com
*/

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.wwh.coolweather.app.receiver.AutoUpdateReciever;
import com.example.wwh.coolweather.app.util.HttpCallbackListener;
import com.example.wwh.coolweather.app.util.HttpUtil;
import com.example.wwh.coolweather.app.util.Utility;

public class AutoUpdateService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //开了一个子线程来更新天气信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        //设置定时信息
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 *1000; //每8小时更新一次
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReciever.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     *  更新天气信息
     */
    private void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = preferences.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //将服务器返回的天气数据交给handleWeatherResponce方法处理
                //从而可以把最新的天气信息存储到SharedPreferences文件中
                Utility.handleWeatherResponce(AutoUpdateService.this, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

}
