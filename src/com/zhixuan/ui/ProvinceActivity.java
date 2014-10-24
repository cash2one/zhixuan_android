package com.zhixuan.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.zhixuan.R;
import com.zhixuan.utils.Consts;
import com.zhixuan.utils.ZXSharedPreferences;

public class ProvinceActivity extends Activity {

	private ListView mProvinceListView;
	private ZXSharedPreferences mZxSharedPreferences;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ProvinceActivity.this.finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_province);
		setTitle("选择省份");
		initView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在当前的activity中注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Consts.CHOOSE_CITY_SIGNAL);
		this.registerReceiver(this.broadcastReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.broadcastReceiver);
	}

	private void initView() {

		mProvinceListView = (ListView) findViewById(R.id.lv_all_provinces);
		mProvinceListView.setAdapter(new ProvinceListAdapter(this));

		mProvinceListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> province = (HashMap<String, String>) parent
						.getAdapter().getItem(position);
				Intent intent = new Intent(getApplicationContext(),
						CityActivity.class);
				intent.putExtra("provinceName", province.get("name"));
				intent.putExtra("provinceId", province.get("id"));
				startActivity(intent);
			}
		});

	}

	public class ProvinceListAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, String>> provinces;
		private Context context;

		public ProvinceListAdapter(Context context) {
			this.context = context;

			getProvinces();
		}

		private void getProvinces() {
			provinces = new ArrayList<HashMap<String, String>>();

			mZxSharedPreferences = new ZXSharedPreferences(ProvinceActivity.this);

			String dataString = mZxSharedPreferences.getProvinceAndCityFromLocal();
			try {
				JSONObject root = new JSONObject(dataString);
				JSONArray jsonProvinces = root.getJSONArray("data");
				for (int i = 0; i < jsonProvinces.length(); i++) {
					JSONObject jsonProvince = jsonProvinces.getJSONObject(i);

					HashMap<String, String> temp = new HashMap<String, String>();
					temp.put("id", jsonProvince.getInt("id") + "");
					temp.put("name", jsonProvince.getString("name"));

					provinces.add(temp);
				}
			} catch (JSONException e) {

			}
		}

		public int getCount() {
			return provinces.size();
		}

		public View getGenericView(HashMap<String, String> province) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.item_province, null);
			TextView temp = (TextView) itemView
					.findViewById(R.id.tv_province_name);
			temp.setText(province.get("name"));
			return itemView;
		}

		public HashMap<String, String> getItem(int position) {
			return provinces.get(position);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return getGenericView(provinces.get(position));
		}

		public long getItemId(int position) {
			return position;
		}
	}
}
