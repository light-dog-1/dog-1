package com.lightdog1.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import com.lightdog1.mobilesafe.R;
import android.os.StatFs;
import android.os.Environment;
import android.text.format.Formatter;
import android.widget.TextView;

public class AppManagerActivity extends Activity {
    
	private TextView tv_mSize,tv_SDSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
		initTitle();
		initAppList();
    }

	private void initTitle() {
		String memoryPath=Environment.getDataDirectory().getAbsolutePath();
		String SDPath=Environment.getExternalStorageDirectory().getAbsolutePath();
		String mSize=Formatter.formatFileSize(this, getAvailableSize(memoryPath));
		String sSize=Formatter.formatFileSize(this, getAvailableSize(SDPath));
		
		tv_mSize=findViewById(R.id.appmanager_tv_mavail);
		tv_SDSize=findViewById(R.id.appmanager_tv_sdavail);
		
		tv_mSize.setText(mSize);
		tv_SDSize.setText(sSize);
	}

	private long getAvailableSize(String path) {
		StatFs statFs=new StatFs(path);
		long blocks=statFs.getAvailableBlocks();
		long bsize=statFs.getBlockSize();
		
		return blocks*bsize;
	}

	private void initAppList() {
	}
    
}
