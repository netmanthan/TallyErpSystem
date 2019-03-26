package com.example.prashantgajera.tallyerpsystem.CommenUtilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.prashantgajera.tallyerpsystem.Orderpojo;

import java.util.ArrayList;

/**
 * Created by Kelvin on admin on 13-Mar-18.
 */

public class dbHelper extends SQLiteOpenHelper {



    String TABLE_NAME1="Orders";
    public dbHelper(Context context)
    {
        super(context,"mydata",null,1);

    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String sql1="create table "+TABLE_NAME1+" (id integer primary key autoincrement, title varchar(30), Orderstatus text, times varchar(40),status varchar(10))";
        db.execSQL(sql1);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




    public void insertorderhistory(Orderpojo m) {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("title",m.getTitle());
        cv.put("Orderstatus",m.getOrderdata());
        cv.put("times",m.getTime());
        cv.put("status",m.getStatus());
        db.insert(TABLE_NAME1,null,cv);
    }
    public ArrayList<Orderpojo> fetchorders() {

        ArrayList<Orderpojo> arrayList=new ArrayList<>();

        String countQuery = "SELECT  * FROM "+TABLE_NAME1;
        Log.d("query",countQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        while (cursor.moveToNext()) {

            Orderpojo m=new Orderpojo();

            m.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            m.setOrderdata(cursor.getString(cursor.getColumnIndex("Orderstatus")));
            m.setTime(cursor.getString(cursor.getColumnIndex("times")));
            m.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            arrayList.add(m);


        }
        cursor.close();
        return arrayList;
    }


}
