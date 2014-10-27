package com.zhixuan.ui;

import com.zhixuan.R;

import android.app.Activity;
import android.os.Bundle;

public class AboutUsActivity extends BaseChildActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        
        super.mTitleString = "关于我们";
    }
}
