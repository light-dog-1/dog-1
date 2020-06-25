package com.light.mobileSafe.db.dao;
import android.content.Context;
import com.light.mobileSafe.db.BlackNumberOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import com.light.mobileSafe.db.domain.BlackNumberInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class BlackNumberDao {
    private BlackNumberOpenHelper BNopenhelper;
    private BlackNumberDao(Context ctx) {
        BNopenhelper = new BlackNumberOpenHelper(ctx);
    }
    private static BlackNumberDao Bnd;

    public static BlackNumberDao newInstance(Context ctx) {
        if (Bnd == null) {
            Bnd = new BlackNumberDao(ctx);
        }
        return Bnd;
    }
    public void insert(String phone, String mode) {
        SQLiteDatabase db=BNopenhelper.getReadableDatabase();
        ContentValues  values = new ContentValues();
//        values.put("phone", phone);
//        values.put("mode", mode);
//        db.insert("blacknumber", null, values);
        
        Random random=new Random();
        for(int i=0;i<100;i++)
        {
            
            values.put("phone", phone+i);
            values.put("mode", 1+random.nextInt(3));
            db.insert("blacknumber", null, values);
        }
        db.close();
    }
    public void delect(String phone) {
        SQLiteDatabase db=BNopenhelper.getReadableDatabase();
        
        db.delete("blacknumber","phone = ?",new String[]{phone});
        
        db.close();
    }
    public void update(String phone, String mode) {
        SQLiteDatabase db=BNopenhelper.getReadableDatabase();
        ContentValues  values = new ContentValues();
        values.put("mode", mode);
        db.update("blacknumber",values,"phone = ?",new String[]{phone});
        db.close();
    }
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db=BNopenhelper.getReadableDatabase();

        Cursor cursor= db.query("blacknumber",new String[]{"phone","mode"},null,null,null,null,"_id desc");
        List<BlackNumberInfo> bnlist=new ArrayList<>();
        while(cursor.moveToNext())
        {
            BlackNumberInfo bninfo=new BlackNumberInfo();
            bninfo.setPhone(cursor.getString(0));
            bninfo.setMode(cursor.getString(1));
            bnlist.add(bninfo);
        }
        cursor.close();
        db.close();
        return bnlist;
    }
    public List<BlackNumberInfo> find(int index) {
        SQLiteDatabase db=BNopenhelper.getReadableDatabase();

        Cursor cursor= db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;",new String[]{index+""});
        List<BlackNumberInfo> bnlist=new ArrayList<>();
        while(cursor.moveToNext())
        {
            BlackNumberInfo bninfo=new BlackNumberInfo();
            bninfo.setPhone(cursor.getString(0));
            bninfo.setMode(cursor.getString(1));
            bnlist.add(bninfo);
        }
        cursor.close();
        db.close();
        return bnlist;
    }
    public int getCount()
    {
        SQLiteDatabase db=BNopenhelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from blacknumber;",null);
        int count=0;
        if(cursor.moveToNext())
        {
            count=cursor.getInt(0);
        }
        
        db.close();
        return count;
    }
}
