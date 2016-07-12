package com.example.wwh.coolweather.app.util;
/*
    Created by Joe on 2016/7/13.
    Email: wwh.cto@foxmail.com
*/

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
