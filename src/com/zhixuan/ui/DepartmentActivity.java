package com.zhixuan.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.zhixuan.R;
import com.zhixuan.utils.BitmapCache;
import com.zhixuan.utils.Consts;

public class DepartmentActivity extends BaseChildActivity {

    private RequestQueue mQueue;
    private ImageLoader imageLoader;
    private ListView mListView;
    private CMAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(mQueue, new BitmapCache());

        String departmentId = getIntent().getStringExtra("departmentId");
        String departmentName = getIntent().getStringExtra("departmentName");
        String departmentCMCount = getIntent().getStringExtra(
                "departmentCMCount");
        String departmentImageUrl = getIntent().getStringExtra(
                "departmentImageUrl");
        String departmentTel = getIntent().getStringExtra("departmentTel");
        String departmentDes = getIntent().getStringExtra("departmentDes");
        String departmentCompanyName = getIntent().getStringExtra(
                "departmentCompanyName");
        String departmentAddr = getIntent().getStringExtra("departmentAddr");

        super.mTitleString = "营业部详情";

        setContentView(R.layout.activity_department);

        // 设置控件
        ImageView image = (ImageView) findViewById(R.id.departmentImage);
        ImageListener listener = ImageLoader.getImageListener(image,
                R.drawable.default_person, R.drawable.default_person);
        imageLoader.get(departmentImageUrl, listener);

        TextView name = (TextView) findViewById(R.id.departmentName);
        name.setText(departmentName);

        TextView companyName = (TextView) findViewById(R.id.departmentCompanyName);
        companyName.setText("所属公司     " + departmentCompanyName);

        TextView tel = (TextView) findViewById(R.id.departmentTel);
        tel.setText("联系电话     " + departmentTel);

        TextView addr = (TextView) findViewById(R.id.departmentAddr);
        addr.setText("联系地址     " + departmentAddr);

        TextView des = (TextView) findViewById(R.id.departmentDes);
        des.setText(Html.fromHtml(departmentDes));

        mListView = (ListView) findViewById(R.id.lv_cm_of_department);
        mAdapter = new CMAdapter(departmentId);
        mListView.setAdapter(mAdapter);

    }

    public class CMAdapter extends BaseAdapter {

        ArrayList<HashMap<String, String>> lists;
        String departmentId;

        public CMAdapter(String departmentId) {
            lists = new ArrayList<HashMap<String, String>>();
            this.departmentId = departmentId;
            getData();
        }

        public void getData() {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Consts.MAIN_DOMAIN
                            + "/kaihu/api_get_custom_manager_list_of_department?department_id="
                            + departmentId, null,
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

                                        mAdapter.addItem(
                                                customManagerObj
                                                        .getString("nick"),
                                                customManagerObj
                                                        .getString("img"),
                                                customManagerObj
                                                        .getString("company_name"),
                                                customManagerObj
                                                        .getString("vip_info"),
                                                customManagerObj
                                                        .getString("mobile"),
                                                customManagerObj
                                                        .getString("qq"));
                                    }
                                }
                            } catch (JSONException e) {

                            }

                            mAdapter.notifyDataSetChanged();
                            mAdapter.calculateListViewHeight();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TAG", error.getMessage(), error);
                        }
                    });

            mQueue.add(jsonObjectRequest);
        }

        public void addItem(String name, String img, String companyName,
                String info, String mobile, String qq) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put("name", name);
            temp.put("img", img);
            temp.put("companyName", companyName);
            temp.put("mobile", mobile);
            temp.put("qq", qq);
            temp.put("info", info);
            lists.add(temp);
        }

        public void calculateListViewHeight() {
            int totalHeight = 0;
            for (int i = 0, len = mAdapter.getCount(); i < len; i++) {
                // listAdapter.getCount()返回数据项的数目
                View listItem = mAdapter.getView(i, null, mListView);
                // 计算子项View 的宽高
                listItem.measure(0, 0);
                // 统计所有子项的总高度
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = totalHeight
                    + (mListView.getDividerHeight() * (mListView.getCount() - 1));
            // listView.getDividerHeight()获取子项间分隔符占用的高度
            // params.height最后得到整个ListView完整显示需要的高度
            mListView.setLayoutParams(params);
        }

        public View getGenericView(HashMap<String, String> customManager) {
            LayoutInflater inflater = DepartmentActivity.this
                    .getLayoutInflater();
            View itemView = inflater
                    .inflate(R.layout.item_custom_manager, null);

            TextView name = (TextView) itemView.findViewById(R.id.cmName);
            name.setText(customManager.get("name"));

            ImageView image = (ImageView) itemView.findViewById(R.id.cmImage);
            ImageListener listener = ImageLoader.getImageListener(image,
                    R.drawable.default_person, R.drawable.default_person);

            imageLoader.get(customManager.get("img"), listener);

            TextView info = (TextView) itemView.findViewById(R.id.cmInfo);
            info.setText(customManager.get("companyName") + "     "
                    + customManager.get("info"));
            TextView tel = (TextView) itemView.findViewById(R.id.cmTel);
            tel.setText("电话   " + customManager.get("mobile"));
            TextView QQ = (TextView) itemView.findViewById(R.id.cmQQ);
            QQ.setText("QQ  " + customManager.get("qq"));
            return itemView;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getGenericView(lists.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return lists.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
