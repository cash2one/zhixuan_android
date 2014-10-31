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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zhixuan.R;
import com.zhixuan.utils.Consts;
import com.zhixuan.utils.UpdateManager;
import com.zhixuan.utils.ZXSharedPreferences;

public class MainActivity extends ActionBarActivity {

    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragments;
    private LinearLayout mCustomManagerLinearLayout;
    private TextView mCustomManagerTextView;
    private ImageView mCustomManagerImageView;
    private LinearLayout mDepartmentLinearLayout;
    private TextView mDepartmentTextView;
    private ImageView mDepartmentImageView;
    private LinearLayout mAboutLinearLayout;
    private TextView mAboutTextView;
    private ImageView mAboutImageView;

    // private Menu mMenu;
    private TextView mTopTitleTextView;
    private TextView mLocationTextView;
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
            mLocationTextView.setText(mZXSharedPreferences.getCityName());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // �ڵ�ǰ��activity��ע��㲥
        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.CHOOSE_CITY_SIGNAL);
        this.registerReceiver(this.broadcastReceiver, filter);

        // ����
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(this.broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        // ����ͳ��
        MobclickAgent.updateOnlineConfig(this);
    }

    public void initView() {
        mZXSharedPreferences = new ZXSharedPreferences(MainActivity.this);

        // �������û�г�����Ϣ���������˼���
        if (!mZXSharedPreferences.hasProvinceAndCity()) {
            mZXSharedPreferences.getProvinceAndCityFromServer();
            mZXSharedPreferences.getCurrentCity();
        }

        mTopTitleTextView = (TextView) findViewById(R.id.tv_main_top_title);

        mCustomManagerImageView = (ImageView) findViewById(R.id.nav_iv_cm);
        mCustomManagerTextView = (TextView) findViewById(R.id.nav_tv_cm);
        mCustomManagerLinearLayout = (LinearLayout) findViewById(R.id.nav_ll_cm);
        mCustomManagerLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0, false);
            }
        });
        mDepartmentImageView = (ImageView) findViewById(R.id.nav_iv_department);
        mDepartmentTextView = (TextView) findViewById(R.id.nav_tv_department);
        mDepartmentLinearLayout = (LinearLayout) findViewById(R.id.nav_ll_department);
        mDepartmentLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1, false);
            }
        });
        mAboutImageView = (ImageView) findViewById(R.id.nav_iv_about);
        mAboutTextView = (TextView) findViewById(R.id.nav_tv_about);
        mAboutLinearLayout = (LinearLayout) findViewById(R.id.nav_ll_about);
        mAboutLinearLayout.setOnClickListener(new OnClickListener() {
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
                mCustomManagerImageView
                        .setImageResource(R.drawable.custom_manager1);
                mDepartmentTextView.setTextColor(Color.BLACK);
                mDepartmentImageView.setImageResource(R.drawable.department1);
                mAboutTextView.setTextColor(Color.BLACK);
                mAboutImageView.setImageResource(R.drawable.about1);

                switch (position) {
                case 0:
                    mCustomManagerTextView.setTextColor(getResources()
                            .getColor(R.color.main_color));
                    mCustomManagerImageView
                            .setImageResource(R.drawable.custom_manager2);
                    mTopTitleTextView.setText("�ͻ�����");
                    break;
                case 1:
                    mDepartmentTextView.setTextColor(getResources().getColor(
                            R.color.main_color));
                    mDepartmentImageView
                            .setImageResource(R.drawable.department2);
                    mTopTitleTextView.setText("Ӫҵ��");
                    break;
                case 2:
                    mAboutTextView.setTextColor(getResources().getColor(
                            R.color.main_color));
                    mAboutImageView.setImageResource(R.drawable.about2);
                    mTopTitleTextView.setText("����");
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

        // ������
        UpdateManager um = new UpdateManager(MainActivity.this);
        um.checkUpdate(false);

        // ����Ĭ�ϵ�actionbar
        getActionBar().hide();

        // ����ص��¼�
        mLocationTextView = (TextView) findViewById(R.id.tv_top_location);
        mLocationTextView.setText(mZXSharedPreferences.getCityName());
        mLocationTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this,
                        ProvinceActivity.class);
                startActivity(intent);
            }
        });
    }

    private Dialog chooseCityDialog() {
        Dialog dialog = new Dialog(this, R.style.city_dialog);
        dialog.setTitle("ѡ�����");
        dialog.setContentView(R.layout.dialog_city);

        expandableListView = (ExpandableListView) dialog
                .findViewById(R.id.expandableListView);

        cityExpandableListAdapter = new CityExpandableListAdapter(this);
        expandableListView.setAdapter(cityExpandableListAdapter);

        // �¼�����
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
            groups.add("����ʡ");
            groups.add("����ʡ");
            groups.add("�Ĵ�ʡ");
            groups.add("����ʡ");

            children = new ArrayList<ArrayList<String>>();
            ArrayList<String> child = new ArrayList<String>();
            child.add("�人");
            child.add("����");
            child.add("�˲�");
            child.add("����");
            children.add(child);

            child = new ArrayList<String>();
            child.add("��ɳ");
            child.add("����");
            child.add("����");
            children.add(child);

            child = new ArrayList<String>();
            child.add("�ɶ�");
            child.add("����");
            child.add("����");
            child.add("�Ű�");
            children.add(child);

            child = new ArrayList<String>();
            child.add("����");
            child.add("����");
            child.add("��ͷ");
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
