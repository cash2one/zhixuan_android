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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
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
import com.zhixuan.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhixuan.utils.BitmapCache;
import com.zhixuan.utils.Consts;
import com.zhixuan.utils.LoadingDialog;
import com.zhixuan.utils.ZXSharedPreferences;

public class DepartmentFragment extends Fragment {

    private PullToRefreshListView mPullRefreshListView;
    private DepartmentListViewAdapter myAdapter;
    private ListView actualListView;
    private RequestQueue mQueue;
    private boolean getLastest = false;
    private int currentPageCount = 1;
    private Dialog loadingDialog;
    private String cityId;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myAdapter.clearAll();
            currentPageCount = 1;
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
        super.onDestroy();
        getActivity().unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mQueue = Volley.newRequestQueue(getActivity());
        loadingDialog = LoadingDialog.createLoadingDialog(getActivity(),
                "数据加载中...");
        currentPageCount = 1;

        View view = inflater.inflate(R.layout.fragment_department, container,
                false);

        mPullRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.pull_refresh_department_list);

        mPullRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(getActivity(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);

                        // Update the LastUpdatedLabel
                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);

                        // Do work to refresh the list here.
                        myAdapter.clearAll();
                        currentPageCount = 1;
                        GetData(1);
                    }
                });

        mPullRefreshListView
                .setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

                    @Override
                    public void onLastItemVisible() {
                        currentPageCount += 1;
                        GetData(currentPageCount);
                        getLastest = false;

                    }
                });

        actualListView = mPullRefreshListView.getRefreshableView();

        // Need to use the Actual ListView when registering for Context Menu
        // registerForContextMenu(actualListView);

        // 绑定点击事件
        actualListView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                HashMap<String, String> temp = (HashMap<String, String>) parent
                        .getAdapter().getItem(position);

                Intent intent = new Intent(getActivity(),
                        DepartmentActivity.class);
                intent.putExtra("departmentId", temp.get("id"));
                intent.putExtra("departmentName", temp.get("name"));
                intent.putExtra("departmentCMCount", temp.get("cmCount"));
                intent.putExtra("departmentImageUrl", temp.get("img"));
                intent.putExtra("departmentTel", temp.get("tel"));
                intent.putExtra("departmentDes", temp.get("des"));
                intent.putExtra("departmentCompanyName",
                        temp.get("companyName"));
                intent.putExtra("departmentAddr", temp.get("addr"));
                startActivity(intent);
            }

        });

        myAdapter = new DepartmentListViewAdapter();
        actualListView.setAdapter(myAdapter);
        GetData(1);
        return view;
    }

    private void GetData(int currentPage) {

        ZXSharedPreferences mZxSharedPreferences = new ZXSharedPreferences(
                getActivity());
        cityId = mZxSharedPreferences.getCityId();

        loadingDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Consts.MAIN_DOMAIN + "/kaihu/api_get_department_list?page="
                        + currentPage + "&city_id=" + cityId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        // Log.d("TAG", obj.toString());

                        loadingDialog.hide();

                        try {

                            if (obj.getInt("errcode") == 0) {
                                JSONArray departments = obj
                                        .getJSONArray("departments");
                                for (int i = 0; i < departments.length(); i++) {
                                    JSONObject departmentObj = departments
                                            .getJSONObject(i);

                                    myAdapter.addItem(
                                            departmentObj.getString("id"),
                                            departmentObj
                                                    .getString("short_name"),
                                            departmentObj.getString("img"),
                                            departmentObj.getString("tel"),
                                            departmentObj.getString("addr"),
                                            departmentObj.getString("cm_count"),
                                            departmentObj
                                                    .getString("company_name"),
                                            departmentObj.getString("des"));
                                }
                            }
                        } catch (JSONException e) {

                        }

                        myAdapter.notifyDataSetChanged();

                        // Call onRefreshComplete when the list has been
                        // refreshed.
                        mPullRefreshListView.onRefreshComplete();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                        loadingDialog.hide();
                    }
                });

        mQueue.add(jsonObjectRequest);
    }

    public class DepartmentListViewAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> items;

        ImageLoader imageLoader;

        public DepartmentListViewAdapter() {

            items = new ArrayList<HashMap<String, String>>();

            imageLoader = new ImageLoader(mQueue, new BitmapCache());
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
                String strTel, String strAddr) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 使用View的对象itemView与R.layout.item关联
            View itemView = inflater.inflate(R.layout.item_department, null);

            TextView name = (TextView) itemView
                    .findViewById(R.id.departmentName);
            name.setText(strName);

            ImageView image = (ImageView) itemView
                    .findViewById(R.id.departmentImage);
            ImageListener listener = ImageLoader.getImageListener(image,
                    R.drawable.default_person, R.drawable.default_person);

            imageLoader.get(strImageUrl, listener);

            TextView tel = (TextView) itemView.findViewById(R.id.departmentTel);
            tel.setText(strTel);
            TextView addr = (TextView) itemView
                    .findViewById(R.id.departmentAddr);
            addr.setText(strAddr);

            return itemView;
        }

        public void addItem(String strId, String strName, String strImageUrl,
                String strTel, String strAddr, String strCMCount,
                String strCompanyName, String strDes) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put("id", strId);
            temp.put("name", strName);
            temp.put("img", strImageUrl);
            temp.put("tel", strTel);
            temp.put("addr", strAddr);
            temp.put("cmCount", strCMCount);
            temp.put("companyName", strCompanyName);
            temp.put("des", strDes);

            if (getLastest) {
                items.add(0, temp);
            } else {
                items.add(temp);
            }
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // if (convertView == null)

            // return convertView;

            HashMap<String, String> temp = items.get(position);
            return makeItemView(temp.get("name"), temp.get("img"),
                    temp.get("tel"), temp.get("addr"));
        }

        public void clearAll() {
            items.clear();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle("Item: ");
        menu.add("Item 1");
        menu.add("Item 2");
        menu.add("Item 3");
        menu.add("Item 4");

        super.onCreateContextMenu(menu, v, menuInfo);
    }
}
