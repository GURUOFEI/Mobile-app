package com.example.textapp.notepad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.textapp.notepad.utils.DBUtils;
import com.example.textapp.notepad.bean.NotepadBean;

import java.util.ArrayList;
import java.util.List;


public class SQLiteHelper extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;

    public static final String CREATE_USER = "create table User ("
            + "id integer primary key autoincrement,"
            + "name text,"
            + "password text)";

    //创建数据库
    public SQLiteHelper(Context context){
        super(context, DBUtils.DATABASE_NAME,null,DBUtils.DATABASE_VERION);
        sqLiteDatabase=this.getWritableDatabase();
    }
    //创建表
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+DBUtils.DATABASE_TABLE+"("+DBUtils.NOTEPAD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+DBUtils.NOTEPAD_CONTENT+" text, "+DBUtils.NOTEPAD_TIME+" text, "+DBUtils.NOTEPAD_PHONE+" text)");
        db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    //添加数据
    public boolean insertData(String userContent,String userTime, String phone){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBUtils.NOTEPAD_CONTENT,userContent);
        contentValues.put(DBUtils.NOTEPAD_TIME,userTime);
        contentValues.put(DBUtils.NOTEPAD_PHONE,phone);
        return sqLiteDatabase.insert(DBUtils.DATABASE_TABLE,null,contentValues)>0;
    }
    //删除数据
    public boolean deleteData(String id){
        String sql=DBUtils.NOTEPAD_ID+"=?";
        //连接字符串
        String[] contentValuesArray=new String[]{String.valueOf(id)};
        return sqLiteDatabase.delete(DBUtils.DATABASE_TABLE,sql,contentValuesArray)>0;
    }
    //修改数据
    public boolean updateData(String id,String content,String userYear){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBUtils.NOTEPAD_CONTENT,content);
        contentValues.put(DBUtils.NOTEPAD_TIME,userYear);
        String sql=DBUtils.NOTEPAD_ID+"=?";
        String[] strings=new String[]{id};
        return sqLiteDatabase.update(DBUtils.DATABASE_TABLE,contentValues,sql,strings)>0;
    }
    //查询数据
    public List<NotepadBean> query(String p){
        List<NotepadBean>list=new ArrayList<NotepadBean>();
        Cursor cursor=sqLiteDatabase.query(DBUtils.DATABASE_TABLE,null,"phone=?",new String[]{p},null,null,DBUtils.NOTEPAD_ID+" desc");
        if (cursor!=null){
            while (cursor.moveToNext()){
                NotepadBean noteInfo=new NotepadBean();
                String id=String.valueOf(cursor.getInt(cursor.getColumnIndex(DBUtils.NOTEPAD_ID)));
                String content=cursor.getString(cursor.getColumnIndex(DBUtils.NOTEPAD_CONTENT));
                long time=cursor.getLong(cursor.getColumnIndex(DBUtils.NOTEPAD_TIME));
                String phone=cursor.getString(cursor.getColumnIndex(DBUtils.NOTEPAD_PHONE));
//                noteInfo.setId(id);
                noteInfo.setNotepadContent(content);
                noteInfo.setNotepadTime(time);
                noteInfo.setNotepadPhone(phone);
                list.add(noteInfo);
            }
            cursor.close();
        }
        return list;
    }
}
