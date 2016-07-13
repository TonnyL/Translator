package com.marktony.translator.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lizhaotailang on 2016/7/13.
 */

public class DBUtil {

    public static Boolean queryIfItemExist(NotebookDatabaseHelper dbhelper, String queryString){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.query("notebook",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                String s = cursor.getString(cursor.getColumnIndex("input"));
                if (queryString.equals(s)){
                    return true;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return false;
    }

    public static void insertValue(NotebookDatabaseHelper dbhelper, ContentValues values){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        db.insert("notebook",null,values);
    }

    public static void deleteValue(NotebookDatabaseHelper dbhelper,String deleteString){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        db.delete("notebook","input = ?",new String[]{deleteString});
    }

}
