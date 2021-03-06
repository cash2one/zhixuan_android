package com.zhixuan.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zhixuan.ui.CityActivity;

public class ZXSharedPreferences {

    private final String SP_NAME = "ZHIXUAN";
    private final String SP_CITY_NAME = "CITY_NAME";
    private final String SP_CITY_ID = "CITY_ID";
    private final String SP_PROVINCE_AND_CITY = "PROVINCE_AND_CITY";
    private final String SP_LAST_CHECK_UPDATE = "LAST_CHECK_UPDATE";
    private final String DEFAULT_LAST_CHECK_UPDATE = "2014-10-23";
    private final String DEFAULT_CITY_NAME = "成都市";
    private final String DEFAULT_CITY_ID = "1974";

    private RequestQueue mQueue;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public ZXSharedPreferences(Context context) {
        mContext = context;
        mQueue = Volley.newRequestQueue(context);
        mSharedPreferences = context.getSharedPreferences(SP_NAME,
                context.MODE_PRIVATE);
    }

    /*
     * 获取城市名字
     */
    public String getCityName() {
        return mSharedPreferences.getString(SP_CITY_NAME, "");
    }

    /*
     * 设置城市名字
     */
    public void setCityName(String cityName) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(SP_CITY_NAME, cityName);
        editor.commit();
    }

    /*
     * 获取城市id
     */
    public String getCityId() {
        return mSharedPreferences.getString(SP_CITY_ID, "");
    }

    /*
     * 设置城市id
     */
    public void setCityId(String cityId) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(SP_CITY_ID, cityId);
        editor.commit();
    }

    /*
     * 本地是否已经有省份和城市的信息
     */
    public boolean hasProvinceAndCity() {
        return getProvinceAndCityFromLocal() == "none" ? false : true;
    }

    /*
     * 本地加载城市
     */
    public String getProvinceAndCityFromLocal() {
        return mSharedPreferences.getString(SP_PROVINCE_AND_CITY, "none");
    }

    public String getLastCheckUpdate() {
        return mSharedPreferences.getString(SP_LAST_CHECK_UPDATE,
                DEFAULT_LAST_CHECK_UPDATE);
    }

    public void setLastCheckUpdate(String strDate) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(SP_LAST_CHECK_UPDATE, strDate);
        editor.commit();
    }

    /*
     * 请求服务端加载城市信息
     */
    public void getProvinceAndCityFromServer() {

        StringRequest stringRequest = new StringRequest(Consts.MAIN_DOMAIN
                + "/kaihu/api_get_province_and_city",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 存入数据
                        Editor editor = mSharedPreferences.edit();
                        editor.putString(SP_PROVINCE_AND_CITY, response);
                        editor.commit();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });

        mQueue.add(stringRequest);
    }

    /*
     * 根据ip定位城市
     */
    public void getCurrentCity() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Consts.MAIN_DOMAIN + "/kaihu/api_get_city_by_ip", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {
                            // 存入数据
                            setCityId(obj.getString("city_id"));
                            setCityName(obj.getString("city_name"));
                            
                            Intent intent = new Intent();
                            intent.setAction(Consts.CHOOSE_CITY_SIGNAL);
                            mContext.sendBroadcast(intent);
                            
                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });

        mQueue.add(jsonObjectRequest);

    }

}
