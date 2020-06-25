package com.light.mobileSafe.activity;

import android.app.Activity;
import android.os.Bundle;
import com.light.mobileSafe.R;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;

public class SplashActivity extends Activity {
    
    private static final int NO_UPDATE=-1;
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case NO_UPDATE:
                    enterHome();
                    break;
            }
        }
        
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUI();
        initData();
    }

    private void initData() {
        checkUpdate();
    }

    private void checkUpdate()
    {
        handler.sendEmptyMessageDelayed(NO_UPDATE,4000);
    }

    private void initUI() {
        
    }
    
    private void enterHome()
    {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }
    
}
