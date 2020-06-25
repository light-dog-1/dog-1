package com.light.mobileSafe.db;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class BlackNumberOpenHelper extends SQLiteOpenHelper {

    public BlackNumberOpenHelper(Context context)
    {
        super(context,"blacknumber",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase p1) {
        p1.execSQL("create table blacknumber (_id integer primary key autoincrement,phone varchar(20),mode varchar(5));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase p1, int p2, int p3) {
    }
    
    
}
