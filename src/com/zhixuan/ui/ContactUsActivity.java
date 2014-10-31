package com.zhixuan.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.zhixuan.R;
import com.zhixuan.utils.Consts;

public class ContactUsActivity extends BaseChildActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        super.mTitleString = "联系我们";

        // LinearLayout websiteLayout =
        // (LinearLayout)findViewById(R.id.ll_website);
        // websiteLayout.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // Intent intent = new Intent(Intent.ACTION_VIEW,
        // Uri.parse(Consts.WEB_SITE));
        // startActivity(intent);
        // }
        // });

        LinearLayout weiboLayout = (LinearLayout) findViewById(R.id.ll_weibo);
        weiboLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                        .parse(Consts.SINA_WEIBO));
                startActivity(intent);
            }
        });
    }

}
