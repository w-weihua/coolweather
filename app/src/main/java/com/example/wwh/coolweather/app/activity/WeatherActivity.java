package com.example.wwh.coolweather.app.activity;
/*
    Created by Joe on 2016/7/14.
    Email: wwh.cto@foxmail.com
*/

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wwh.coolweather.app.R;
import com.example.wwh.coolweather.app.service.AutoUpdateService;
import com.example.wwh.coolweather.app.util.HttpCallbackListener;
import com.example.wwh.coolweather.app.util.HttpUtil;
import com.example.wwh.coolweather.app.util.Utility;

public class WeatherActivity extends Activity implements View.OnClickListener{

    //天气信息的布局
    private LinearLayout weatherInfoLayout;
    //用于显示城市名字
    private TextView cityNameText;
    //用于显示发布时间
    private TextView publicText;
    //用于显示天气描述
    private TextView weatherDespText;
    //用于显示最低气温
    private TextView temp1Text;
    //用于显示最高气温
    private TextView temp2Text;
    //用于显示当前日期
    private TextView currentDateText;
    //切换城市按钮
    private Button switchCity;
    //更新天气按钮
    private Button refreshWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        //初始化各控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publicText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.fresh_info);

        String countyCode = getIntent().getStringExtra("county_code");

        if(!TextUtils.isEmpty(countyCode)){
            //有县级代号就去查询天气
            publicText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            //没有县级代号时就直接显示本地天气
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                //启动选择城市那个活动，并传达一些信息过去，最后finish掉当前活动
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                //以下这句话，好像没有起到作用，难道是课本的错了？
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.fresh_info:
                publicText.setText("同步中...");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                //这里的weather_code其实就是cityid
                String weatherCode = preferences.getString("weather_code", "");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);  //注意这个方法有别于上面的queryWeatherCode
                }
                break;
            default:
                break;
        }
    }


    /**
     *  查询县级代号所对应的天气代号
     */
    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }


    /**
     *  根据传入的地址和类型去向服务器查询天气代号或者天气信息
     */
    private void queryFromServer(final String address, final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if(array != null && array.length ==2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)){
                    //处理服务器返回的天气信息。
                    //正是handleWeatherResponce里还调用了一个saveWeatherInfo()的方法
                    //使得SharedPreferences文件里有了数据信息
                    Utility.handleWeatherResponce(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publicText.setText("同步失败！");
                    }
                });
            }
        });
    }


    /**
     *  从SharedPrefences文件中读取存储的天气信息，并显示到界面上
     */
    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publicText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        //激活AutoUpdateService
        //只要选择了某个城市病成功更新天气之后，AutoUpdateService就会一直运行
        //并保证每8小时更新一次天气
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}
