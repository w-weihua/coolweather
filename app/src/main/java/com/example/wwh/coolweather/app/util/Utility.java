package com.example.wwh.coolweather.app.util;
/*
    Created by Joe on 2016/7/13.
    Email: wwh.cto@foxmail.com
*/

import android.text.TextUtils;

import com.example.wwh.coolweather.app.model.City;
import com.example.wwh.coolweather.app.model.CoolWeatherDB;
import com.example.wwh.coolweather.app.model.County;
import com.example.wwh.coolweather.app.model.Province;

public class Utility {

    /**
     *  解析和处理服务器返回的省级数据
     *  注意到服务器返回的数据是： “代号|城市，代号|城市”这种格式
     *  所以可以分别按照“，”和“|”来分割和提取数据
     *  然后再调用CoolWeatherDB的三个save()方法将数据存储到相应的表中
     */
    public synchronized static boolean handleProvincesResponse(
            CoolWeatherDB coolWeatherDB, String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0){
                for (String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     *  解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(
            CoolWeatherDB coolWeatherDB, String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities != null && allCities.length > 0){
                for(String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     *  解析和处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountiesResponse(
            CoolWeatherDB coolWeatherDB, String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length > 0){
                for(String c : allCounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setConutyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

}
