package com.zhixuan.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.zhixuan.R;
import com.zhixuan.utils.Consts;
import com.zhixuan.utils.UpdateManager;
import com.zhixuan.utils.ZXSharedPreferences;

public class MainActivity extends ActionBarActivity {

    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragments;
    private TextView mCustomManagerTextView;
    private TextView mDepartmentTextView;
    private TextView mAboutTextView;
    private Menu mMenu;
    private ZXSharedPreferences mZXSharedPreferences;

    private ExpandableListView expandableListView;
    private CityExpandableListAdapter cityExpandableListAdapter;
    private Dialog cityDialog;

    // public interface ChooseCityListener {
    // public void onChoose(String cityName);
    // }
    //
    // private ChooseCityListener listener;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MenuItem location = mMenu.findItem(R.id.action_location);
            location.setTitle(mZXSharedPreferences.getCityName());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.CHOOSE_CITY_SIGNAL);
        this.registerReceiver(this.broadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 隐藏title
        // requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    public void initView() {
        mZXSharedPreferences = new ZXSharedPreferences(MainActivity.this);

        // 如果本地没有城市信息，请求服务端加载
        if (!mZXSharedPreferences.hasProvinceAndCity()) {
            mZXSharedPreferences.getProvinceAndCityFromServer();
        }

        mCustomManagerTextView = (TextView) findViewById(R.id.nav_tv_cm);
        mCustomManagerTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0, false);
            }
        });
        mDepartmentTextView = (TextView) findViewById(R.id.nav_tv_department);
        mDepartmentTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1, false);
            }
        });
        mAboutTextView = (TextView) findViewById(R.id.nav_tv_about);
        mAboutTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2, false);
            }
        });

        mFragments = new ArrayList<Fragment>();
        mFragments.add(new CustomManagerFragment());
        mFragments.add(new DepartmentFragment());
        mFragments.add(new AboutFragment());

        mViewPager = (ViewPager) findViewById(R.id.vp_main);
        mViewPager.setAdapter(new FragmentPagerAdapter(
                getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        });
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mCustomManagerTextView.setTextColor(Color.BLACK);
                mDepartmentTextView.setTextColor(Color.BLACK);
                mAboutTextView.setTextColor(Color.BLACK);

                switch (position) {
                case 0:
                    mCustomManagerTextView.setTextColor(getResources()
                            .getColor(R.color.main_color));
                    break;
                case 1:
                    mDepartmentTextView.setTextColor(getResources().getColor(
                            R.color.main_color));
                    break;
                case 2:
                    mAboutTextView.setTextColor(getResources().getColor(
                            R.color.main_color));
                    break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        // cityDialog = chooseCityDialog();

        UpdateManager um = new UpdateManager(MainActivity.this);
        um.checkUpdate(false);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // for (int i = 0; i < 5; i++) {
        // menu.add(1, i, i, "菜单-" + i);
        // }
        mMenu = menu;
        String cityName = mZXSharedPreferences.getCityName();
        mMenu.findItem(R.id.action_location).setTitle(cityName);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_location) {
            // cityDialog.show();
            Intent intent = new Intent(this, ProvinceActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private Dialog chooseCityDialog() {
        Dialog dialog = new Dialog(this, R.style.city_dialog);
        dialog.setTitle("选择城市");
        dialog.setContentView(R.layout.dialog_city);

        expandableListView = (ExpandableListView) dialog
                .findViewById(R.id.expandableListView);

        cityExpandableListAdapter = new CityExpandableListAdapter(this);
        expandableListView.setAdapter(cityExpandableListAdapter);

        // 事件监听
        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {

                String cityName = cityExpandableListAdapter.getChild(
                        groupPosition, childPosition).toString();

                Fragment temp = mFragments.get(mViewPager.getCurrentItem());
                // if (temp instanceof ChooseCityListener) {
                // listener = (ChooseCityListener) temp;
                // listener.onChoose(cityName);
                // }

                cityDialog.hide();

                return false;
            }
        });

        return dialog;
    }

    public class CityExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<String> groups;
        private ArrayList<ArrayList<String>> children;
        private Context context;

        public CityExpandableListAdapter(Context context) {
            this.context = context;

            groups = new ArrayList<String>();
            groups.add("湖北省");
            groups.add("湖南省");
            groups.add("四川省");
            groups.add("广州省");

            children = new ArrayList<ArrayList<String>>();
            ArrayList<String> child = new ArrayList<String>();
            child.add("武汉");
            child.add("荆州");
            child.add("宜昌");
            child.add("天门");
            children.add(child);

            child = new ArrayList<String>();
            child.add("长沙");
            child.add("益阳");
            child.add("怀化");
            children.add(child);

            child = new ArrayList<String>();
            child.add("成都");
            child.add("德阳");
            child.add("绵阳");
            child.add("雅安");
            children.add(child);

            child = new ArrayList<String>();
            child.add("广州");
            child.add("深圳");
            child.add("汕头");
            children.add(child);
        }

        public View getGenericView() {
            TextView view = new TextView(context);
            view.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64));
            view.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            view.setPadding(80, 0, 0, 0);
            view.setTextSize(20);
            return view;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getGenericView();
            }

            TextView temp = (TextView) convertView;
            temp.setText(getGroup(groupPosition).toString());
            return temp;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getGenericView();
            }

            TextView temp = (TextView) convertView;
            temp.setText(getChild(groupPosition, childPosition).toString());
            return temp;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
