package com.zhixuan.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhixuan.R;
import com.zhixuan.utils.Consts;
import com.zhixuan.utils.ZXSharedPreferences;

public class CityActivity extends BaseChildActivity {

    private ListView mCityListView;
    private String provinceName = "";
    private String provinceId = "";
    private ZXSharedPreferences mZXSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        
        provinceName = getIntent().getStringExtra("provinceName");
        provinceId = getIntent().getStringExtra("provinceId");
        initView();
        super.mTitleString = "选择城市";
    }

    private void initView() {
        mZXSharedPreferences = new ZXSharedPreferences(CityActivity.this);

        mCityListView = (ListView) findViewById(R.id.lv_all_citys);
        mCityListView.setAdapter(new CityListAdapter(this, provinceName));

        mCityListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                HashMap<String, String> city = (HashMap<String, String>) parent
                        .getAdapter().getItem(position);

                mZXSharedPreferences.setCityId(city.get("id"));
                mZXSharedPreferences.setCityName(city.get("name"));

                Intent intent = new Intent();
                intent.setAction(Consts.CHOOSE_CITY_SIGNAL);
                CityActivity.this.sendBroadcast(intent);
                CityActivity.this.finish();
            }
        });
    }

    public class CityListAdapter extends BaseAdapter {

        private ArrayList<HashMap<String, String>> cities;
        private Context context;

        public CityListAdapter(Context context, String provinceName) {
            this.context = context;
            getCities();
        }

        private void getCities() {
            cities = new ArrayList<HashMap<String, String>>();

            String dataString = mZXSharedPreferences
                    .getProvinceAndCityFromLocal();
            try {
                JSONObject root = new JSONObject(dataString);
                JSONArray jsonProvinces = root.getJSONArray("data");

                // 循环省份
                for (int i = 0; i < jsonProvinces.length(); i++) {
                    JSONObject jsonProvince = jsonProvinces.getJSONObject(i);

                    // 找到匹配的省份
                    if (provinceId.equals(jsonProvince.getInt("id") + "")) {
                        JSONArray jsonCities = jsonProvince
                                .getJSONArray("cities");
                        // 循环城市
                        for (int j = 0; j < jsonCities.length(); j++) {
                            JSONObject jsonCity = jsonCities.getJSONObject(j);

                            HashMap<String, String> temp = new HashMap<String, String>();
                            temp.put("id", jsonCity.getInt("id") + "");
                            temp.put("name", jsonCity.getString("name"));

                            cities.add(temp);
                        }
                        break;
                    }
                }
            } catch (JSONException e) {

            }
        }

        public int getCount() {
            return cities.size();
        }

        public View getGenericView(HashMap<String, String> city) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.item_city, null);
            TextView temp = (TextView) itemView.findViewById(R.id.tv_city_name);
            temp.setText(city.get("name"));
            return itemView;
        }

        public HashMap<String, String> getItem(int position) {
            return cities.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return getGenericView(cities.get(position));
        }

        public long getItemId(int position) {
            return position;
        }
    }
}
