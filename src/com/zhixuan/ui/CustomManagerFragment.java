package com.zhixuan.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.AvoidXfermode.Mode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhixuan.R;
import com.zhixuan.utils.BitmapCache;
import com.zhixuan.utils.Consts;
import com.zhixuan.utils.LoadingDialog;
import com.zhixuan.utils.ZXSharedPreferences;

public class CustomManagerFragment extends Fragment {

    private ListView mCustomManagerListView;
    private CustomManagerListViewAdapter myAdapter;
    private RequestQueue mQueue;
    private String cityId;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myAdapter.clearAll();
            GetData(1);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.CHOOSE_CITY_SIGNAL);
        getActivity().registerReceiver(this.broadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(this.broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mQueue = Volley.newRequestQueue(getActivity());

        View view = inflater.inflate(R.layout.fragment_custom_manager,
                container, false);

        mCustomManagerListView = (ListView) view.findViewById(R.id.lv_cm_list);

        myAdapter = new CustomManagerListViewAdapter();
        mCustomManagerListView.setAdapter(myAdapter);
        GetData(1);
        return view;
    }

    private void GetData(int currentPage) {

        ZXSharedPreferences mZxSharedPreferences = new ZXSharedPreferences(
                getActivity());
        cityId = mZxSharedPreferences.getCityId();

        if (cityId == "") {
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Consts.MAIN_DOMAIN + "/kaihu/api_get_custom_manager_list?page="
                        + currentPage + "&city_id=" + cityId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {

                            if (obj.getInt("errcode") == 0) {
                                JSONArray customManagers = obj
                                        .getJSONArray("custom_managers");
                                for (int i = 0; i < customManagers.length(); i++) {
                                    JSONObject customManagerObj = customManagers
                                            .getJSONObject(i);

                                    myAdapter.addItem(customManagerObj
                                            .getString("id"), customManagerObj
                                            .getString("qq"), customManagerObj
                                            .getString("nick"),
                                            customManagerObj.getString("img"),
                                            customManagerObj
                                                    .getString("mobile"),
                                            customManagerObj
                                                    .getString("vip_info"),
                                            customManagerObj
                                                    .getString("company_name"));
                                }
                            }
                        } catch (JSONException e) {

                        }

                        myAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });

        mQueue.add(jsonObjectRequest);
    }

    public class CustomManagerListViewAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> items;

        ImageLoader imageLoader;

        public CustomManagerListViewAdapter() {

            items = new ArrayList<HashMap<String, String>>();

            imageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        public void clearAll() {
            items.clear();
        }

        public int getCount() {
            return items.size();
        }

        public HashMap<String, String> getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        private View makeItemView(String strName, String strImageUrl,
                String strCompanyName, String strInfo, String strTel,
                String strQQ) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 使用View的对象itemView与R.layout.item关联
            View itemView = inflater
                    .inflate(R.layout.item_custom_manager, null);

            TextView name = (TextView) itemView.findViewById(R.id.cmName);
            name.setText(strName);

            ImageView image = (ImageView) itemView.findViewById(R.id.cmImage);
            ImageListener listener = ImageLoader.getImageListener(image,
                    R.drawable.default_person, R.drawable.default_person);

            imageLoader.get(strImageUrl, listener);

            TextView companyName = (TextView) itemView.findViewById(R.id.cmCompanyName);
            companyName.setText(strCompanyName);
            TextView info = (TextView) itemView.findViewById(R.id.cmInfo);
            info.setText(strInfo);
            TextView tel = (TextView) itemView.findViewById(R.id.cmTel);
            tel.setText(strTel);
            TextView QQ = (TextView) itemView.findViewById(R.id.cmQQ);
            QQ.setText(strQQ);

            return itemView;
        }

        public void addItem(String strId, String strQQ, String strName,
                String strImage, String strMobile, String strInfo,
                String strCompanyName) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put("id", strId);
            temp.put("qq", strQQ);
            temp.put("name", strName);
            temp.put("img", strImage);
            temp.put("mobile", strMobile);
            temp.put("info", strInfo);
            temp.put("companyName", strCompanyName);

            items.add(temp);

        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // if (convertView == null)

            // return convertView;

            HashMap<String, String> temp = items.get(position);
            return makeItemView(temp.get("name"), temp.get("img"),
                    temp.get("companyName"), temp.get("info"),
                    temp.get("mobile"), temp.get("qq"));
        }
    }
}
