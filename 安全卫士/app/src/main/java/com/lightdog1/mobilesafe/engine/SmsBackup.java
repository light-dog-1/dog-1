package com.lightdog1.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import java.io.File;
import java.io.FileOutputStream;
import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import com.lidroid.xutils.util.LogUtils;

public class SmsBackup {
	

    public static void backup(Context ctx, String toPath, backStateListener stateListenner) {
		Cursor cursor=null;
		FileOutputStream fos=null;
		try {
			File file=new File(toPath);
			cursor= ctx.getContentResolver().query(Uri.parse("content://sms/"),
														  new String[]{"address","date","type","body"}, null, null, null);
			fos=new FileOutputStream(file);
			
			XmlSerializer newSerializer=Xml.newSerializer();
			newSerializer.setOutput(fos,"utf-8");
			newSerializer.startDocument("utf-8",true);
			newSerializer.startTag(null,"smss");
			
		    stateListenner.onReady(cursor.getCount());
			
			int index=0;
			while(cursor.moveToNext())
			{
				
				newSerializer.startTag(null,"sms");
				
				newSerializer.startTag(null,"address");
				newSerializer.text(cursor.getString(0));
				newSerializer.endTag(null,"address");
				
				newSerializer.startTag(null,"date");
				newSerializer.text(cursor.getString(1));
				newSerializer.endTag(null,"date");
				
				newSerializer.startTag(null,"type");
				newSerializer.text(cursor.getString(2));
				newSerializer.endTag(null,"type");
				
				newSerializer.startTag(null,"body");
				newSerializer.text(cursor.getString(3));
				newSerializer.endTag(null,"body");
				
				newSerializer.endTag(null,"sms");
				
				//newSerializer.flush();
				stateListenner.onOneSucceed(++index);
				Thread.sleep(300);
			}
			
			newSerializer.endTag(null,"smss");
			newSerializer.endDocument();
			stateListenner.onAllFinish();
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			if(cursor!=null&&fos!=null)
			{
				cursor.close();
				try {
					fos.close();
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
    public interface backStateListener {
		void onReady(int allEntry);
		void onOneSucceed(int position);
		void onFailed();
		void onAllFinish();
	}
}
