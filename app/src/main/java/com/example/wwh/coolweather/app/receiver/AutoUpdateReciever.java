package com.example.wwh.coolweather.app.receiver;
/*
    Created by Joe on 2016/7/14.
    Email: wwh.cto@foxmail.com
*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.wwh.coolweather.app.service.AutoUpdateService;

public class AutoUpdateReciever extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //每8个小时就执行这个onReceive方法中的内容，即重新启动更新天气的服务
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
