package com.lightdog1.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.lightdog1.mobilesafe.db.dao.BlackNumberDao;
import com.android.internal.telephony.ITelephony;
import java.lang.reflect.Method;
import android.net.Uri;
import android.database.ContentObserver;
import android.os.Handler;
import com.lightdog1.mobilesafe.service.BlackNumberService.MyContentObserver;

public class BlackNumberService extends Service {
	private InnerReceiver receiver;
	private BlackNumberDao mDao;

	private TelephonyManager mTM;

	private BlackNumberService.MyPhoneStateListener mPhoneStateListener;

	private BlackNumberService.MyContentObserver myContentObserver;
	
	
	@Override
	public void onCreate() {
		mDao=BlackNumberDao.getInstance(getApplicationContext());
		IntentFilter filter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		//设置优先级
		filter.setPriority(1000);
		receiver=new InnerReceiver();
		registerReceiver(receiver,filter);
		
		//电话状态的监听(服务开启的时候,需要去做监听,关闭的时候电话状态就不需要监听)
		//1,电话管理者对象
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//2,监听电话状态
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
    
	
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(receiver!=null)
		{
			unregisterReceiver(receiver);
		}
		if(myContentObserver!=null)
		{
			getContentResolver().unregisterContentObserver(myContentObserver);
		}
		//取消对电话状态的监听
		if(mPhoneStateListener!=null)
		{
			mTM.listen(mPhoneStateListener,PhoneStateListener.LISTEN_NONE);
		}
	}
    class InnerReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context p1, Intent p2) {
			Object[] objects=(Object[]) p2.getExtras().get("pdus");
			for(Object object:objects)
			{
				SmsMessage sms=SmsMessage.createFromPdu((byte[])object);
				String smsAddress=sms.getOriginatingAddress();
				//String messageBody=sms.getMessageBody();
				
				int mode=mDao.getMode(smsAddress);
				if(mode==1||mode==3)
				{
					abortBroadcast();
				}
			}
		}
	}
	class MyPhoneStateListener extends PhoneStateListener{
		//3,手动重写,电话状态发生改变会触发的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					//摘机状态，至少有个电话活动。该活动或是拨打（dialing）或是通话
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					//挂断电话
					endCall(incomingNumber);
					break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}
	private void endCall(String phone)
	{
//		ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
		int mode=mDao.getMode(phone);
		if(mode==2||mode==3)
		{
			try {
				//1获取类对象的字节码文件
				Class<?> clazz=Class.forName("android.os.ServiceManager");
				//2获取方法
				Method method= clazz.getMethod("getService", String.class);
				//3反射调用此方法
				IBinder iBinder=(IBinder) method.invoke(null,Context.TELEPHONY_SERVICE);
				//调用获取aidl文件对象方法
				ITelephony iTelephony=ITelephony.Stub.asInterface(iBinder);
				//5调用aidl中影藏的endcall方法
				iTelephony.endCall();
			} catch (Exception e) 
			{
				e.printStackTrace();
			} 
			//6删除被拦截的通话记录 (权限)
//			String where = "number = ?";
//			String[] selectionArgs = new String[]{phone};
//			getContentResolver().delete(Uri.parse("content://call_log/calls"), where, selectionArgs);
			//6通过内容观察者观察数据库变化,delsect通话记录
			myContentObserver = new MyContentObserver(new Handler(), phone);
			getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, myContentObserver);
		}
	}
	class MyContentObserver extends ContentObserver
	{

		private String phone;
		public MyContentObserver(Handler handler,String phone)
		{
			super(handler);
			this.phone=phone;
		}

		@Override
		public void onChange(boolean selfChange) {
			getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});
		}
		
	}
}
