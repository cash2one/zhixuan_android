package com.zhixuan.utils;

import android.content.Context;
import android.text.TextUtils;

public class Consts {
    // 服务器地址
    public static final String MAIN_DOMAIN = "http://kaihu.zhixuan.com";

    // 主站
    public static final String WEB_SITE = "http://www.zhixuan.com";

    // 微博地址
    public static final String SINA_WEIBO = "http://m.weibo.cn/u/5083374708";// "http://weibo.com/zhixuancom";

    // 选择城市信号名字
    public static final String CHOOSE_CITY_SIGNAL = "CITY_CHOSEN";

    // 获取设备信息
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(
                        context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
