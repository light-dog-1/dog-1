package com.lightdog1.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.lightdog1.mobilesafe.R;
import com.lightdog1.mobilesafe.engine.SmsBackup;
import com.lightdog1.mobilesafe.engine.SmsBackup.backStateListener;
import java.io.File;
import com.lightdog1.mobilesafe.utils.ToastUtil;

public class AToolActivity extends Activity {
	private TextView tv_query_phone_address;

	private TextView tv_sms_backup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		
		//电话归属地查询方法
		initPhoneAddress();
		//短信备份
		initSmsBackup();
	}

	private void initPhoneAddress() {
		tv_query_phone_address =  findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), QueryAddressActivity.class));
			}
		});
	}
	private void initSmsBackup() {
		tv_sms_backup =  findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showSmsBackupDialog();
				}
			});
	}
	private void showSmsBackupDialog()
	{
		final ProgressDialog progressDialog=new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
		
		//4短信获取,调用方法
		new Thread(new Runnable(){

				@Override
				public void run() {
					SmsBackup.backStateListener listenner = new backStateListener(){

						@Override
						public void onReady(int allEntry) {
							progressDialog.setMax(allEntry);
						}


						@Override
						public void onOneSucceed(int position) {
							progressDialog.setProgress(position);
						}

						@Override
						public void onFailed() {
							ToastUtil.show(getApplicationContext(),"备份出现问题");
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {e.printStackTrace();}
							progressDialog.dismiss();
						}

						@Override
						public void onAllFinish() {
							progressDialog.dismiss();
						}
					};
					String topath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"sms.xml";
					SmsBackup.backup(getApplicationContext(), topath, listenner);
				}
			}).start();
		
	}
}
