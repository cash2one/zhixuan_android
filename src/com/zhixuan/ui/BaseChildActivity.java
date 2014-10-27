package com.zhixuan.ui;

import com.zhixuan.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseChildActivity extends Activity {
    
    private Activity me;
    public String mTitleString;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        
        getActionBar().hide();
        
        me = this;
        
        TextView titleTextView = (TextView)findViewById(R.id.tv_top_title);
        titleTextView.setText(mTitleString);
        
        ImageView backImageView = (ImageView)findViewById(R.id.iv_top_back);
        
        backImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                me.finish();
            }
        });
        
    }
}
