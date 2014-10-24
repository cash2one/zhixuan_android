package com.zhixuan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ZXSharedPreferences {

    private final String SP_NAME = "ZHIXUAN";
    private final String SP_CITY_NAME = "CITY_NAME";
    private final String SP_CITY_ID = "CITY_ID";
    private final String SP_PROVINCE_AND_CITY = "PROVINCE_AND_CITY";
    private final String SP_LAST_CHECK_UPDATE = "LAST_CHECK_UPDATE";
    private final String DEFAULT_LAST_CHECK_UPDATE = "2014-10-23";
    private final String DEFAULT_CITY_NAME = "�ɶ���";
    private final String DEFAULT_CITY_ID = "1974";

    private RequestQueue mQueue;
    private SharedPreferences mSharedPreferences;

    public ZXSharedPreferences(Context context) {
        mQueue = Volley.newRequestQueue(context);
        mSharedPreferences = context.getSharedPreferences(SP_NAME,
                context.MODE_PRIVATE);
    }

    /*
     * ��ȡ��������
     */
    public String getCityName() {
        return mSharedPreferences.getString(SP_CITY_NAME, DEFAULT_CITY_NAME);
    }

    /*
     * ���ó�������
     */
    public void setCityName(String cityName) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(SP_CITY_NAME, cityName);
        editor.commit();
    }

    /*
     * ��ȡ����id
     */
    public String getCityId() {
        return mSharedPreferences.getString(SP_CITY_ID, DEFAULT_CITY_ID);
    }

    /*
     * ���ó���id
     */
    public void setCityId(String cityId) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(SP_CITY_ID, cityId);
        editor.commit();
    }

    /*
     * �����Ƿ��Ѿ���ʡ�ݺͳ��е���Ϣ
     */
    public boolean hasProvinceAndCity() {
        return getProvinceAndCityFromLocal() == "none" ? false : true;
    }

    /*
     * ���ؼ��س���
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
     * �������˼��س�����Ϣ
     */
    public void getProvinceAndCityFromServer() {

        StringRequest stringRequest = new StringRequest(Consts.MAIN_DOMAIN
                + "/kaihu/api_get_province_and_city",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // ��������
                        Editor editor = mSharedPreferences.edit();
                        editor.putString(SP_PROVINCE_AND_CITY, response);
                        editor.putString(SP_CITY_ID, DEFAULT_CITY_ID);
                        editor.putString(SP_CITY_NAME, DEFAULT_CITY_NAME);
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
}
