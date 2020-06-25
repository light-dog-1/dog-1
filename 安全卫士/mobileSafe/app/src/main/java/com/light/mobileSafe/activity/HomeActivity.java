package com.light.mobileSafe.activity;

import android.app.Activity;
import android.os.Bundle;
import com.light.mobileSafe.R;
import android.widget.GridView;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

public class HomeActivity extends Activity {
    
    private String[] mTitles={
        "手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"
    };
    private int[] mIds={
        R.drawable.home_safe,R.drawable.home_callmsgsafe,
        R.drawable.home_apps,R.drawable.home_taskmanager,
        R.drawable.home_netmanager,R.drawable.home_trojan,
        R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings
    };
    private GridView gv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        initData();
    }

    private void initUI() {
        gv=findViewById(R.id.activityhome_gv);
        
    }

    private void initData() {
        gv.setAdapter(new MyAdapter());
        gv.setOnItemClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                    switch(p3)
                    {
                        case 0:
                            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                            break;
                        case 3:
                            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                            break;
                        case 4:
                            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                            break;
                        case 5:
                            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                            break;
                        case 6:
                            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                            break;
                        case 7:
                            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                            break;
                        case 8:
                            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                            break;
                    }
                }
            });
    }
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mIds.length;
        }

        @Override
        public Object getItem(int p1) {
            return null;
        }

        @Override
        public long getItemId(int p1) {
            return p1;
        }

        @Override
        public View getView(int p1, View p2, ViewGroup p3) {
            View view=null;
            if(p2==null)
            {
                view=View.inflate(getApplicationContext(),R.layout.view_iems_home,null);
            }else{
                view=p2;
            }
            ImageView iv_item=view.findViewById(R.id.viewiemshome_iv);
            TextView tv_item=view.findViewById(R.id.viewiemshome_tv);
            
            iv_item.setImageResource(mIds[p1]);
            tv_item.setText(mTitles[p1]);
            return view;
        }
    }
}
