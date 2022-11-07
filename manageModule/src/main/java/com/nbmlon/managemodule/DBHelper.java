package com.nbmlon.managemodule;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "TodoList.db";

    private static final int CASE_ADD = 100;
    private static final int CASE_CHK = 101;
    private static final int CASE_DEL = 102;

    private Context mContext;

    private String mTableName;
    private DateTime mDstDate;
    private int DST_MARKER;

    private Boolean mTableExist;


    public DBHelper(@Nullable Context context, DateTime dstDate) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;

        mTableName = dstDate.toString("yyyyMMdd");
        mDstDate = dstDate;
        int mDstDay = Integer.parseInt(dstDate.toString("dd"));

        mTableExist = CheckTableExist();
        DST_MARKER = 0b1 << (mDstDay - 1);
    }


    /** DB Helper for Searching **/
    public DBHelper(@Nullable Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_ver, int new_ver) {
        onCreate(db);
    }



    //Create 문
    private void CreateTable(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS '" + mTableName + "' (" +
                "Title TEXT Primary Key," +
                "Content TEXT," +
                "EndTime String default NULL," +
                "PhotoLen Integer default 0," +
                "Done Integet default 0);" );

        mTableExist = true;
    }


    //select문 체크 안된것만 골라오기 (메인) -> Title만 필요
    public ArrayList<Todoitem> GetLeftTodoList(){
        ArrayList<Todoitem> Todoitems = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        if(mTableExist){
            Cursor cursor = db.rawQuery("Select Title from '" + mTableName + "' Where Done = 0 Order by rowid ASC;", null);
            while(cursor.moveToNext()){
                String title = cursor.getString(cursor.getColumnIndexOrThrow("Title"));
                Todoitem todoitem = new Todoitem(title);

                Todoitems.add(todoitem);
            }
            cursor.close();
        }
        return Todoitems;
    }

    //select문 체크 된것만 골라오기
    public ArrayList<Todoitem> GetDoneTodoList(){
        ArrayList<Todoitem> Todoitems = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        //tablename 있는지 확인
        if(mTableExist){
            Cursor cursor = db.rawQuery("Select Title, Content, PhotoLen, EndTime from '" + mTableName + "' Where Done = 1 Order by rowid ASC;", null);
            //조회된 데이터가 있음
            while(cursor.moveToNext()){
                String title = cursor.getString(cursor.getColumnIndexOrThrow("Title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("Content"));
                int photolen = cursor.getInt(cursor.getColumnIndexOrThrow("PhotoLen"));
                String end_time = cursor.getString(cursor.getColumnIndexOrThrow("EndTime"));

                Todoitem todoitem = new Todoitem(title);
                todoitem.setContent(content);
                todoitem.setPhotoLen(photolen);
                todoitem.setDone(true);
                todoitem.setEnd_time(end_time);

                Todoitems.add(todoitem);
            }

            cursor.close();
        }
        return Todoitems;
    }


    /** Insert New Item with no Attribute using Title
     * @return if addable add and return ture
     * **/
    public boolean InsertTodo(String title){
        if (!mTableExist)
            CreateTable();

        if(CheckTitleAddable(title)){

            SQLiteDatabase db = getWritableDatabase();

            db.execSQL("Insert into '" + mTableName + "' ('Title') " +
                    "Values ('" + title.replace("\'","\'\'") + "');");

            ((Runnable) () -> UpdateMarker(CASE_ADD)).run();

            return true;
        }

        else
            return false;
    }

    /** Update Item Attribute Using Target_title
     *  To Rename Title, Essitinally call checktitleaddable before
     * **/
    public void UpdateTodo(String target_title, Todoitem todoitem){
        String title = todoitem.getTitle().replace("\'","\'\'");
        String content = todoitem.getContent().replace("\'","\'\'");
        String end_time = todoitem.getEnd_time().replace("\'","\'\'");
        int photolen = todoitem.getPhotoLen();
        int done = todoitem.getDone() ? 1 : 0;

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("update '" + mTableName +
                "' Set Title = '" + title + "', " +
                "Content = '" + content + "', " +
                "EndTime = '" + end_time + "', " +
                "PhotoLen = " + photolen + ", " +
                "Done = " + done +
                " Where Title = '" + target_title.replace("\'","\'\'") + "'; ");


        new Runnable() {
            @Override
            public void run() {
                if (done == 1){
                    UpdateMarker(CASE_CHK);
                }
            }
        }.run();

    }


    /** Delete TD From DB **/
    public void DeleteTodo(Todoitem item){
        if (item.getPhotoLen() >0 ) {
            new Runnable() {
                @Override
                public void run() {
                    //사진부터 지우기
                    try {
                        FileManager.RecordImage fileManager = new FileManager.RecordImage(mContext, mTableName, item.getTitle());
                        fileManager.deleteSavedFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }.run();
        }

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from '" + mTableName + "' " +
                "Where Title = '" + item.getTitle().replace("\'","\'\'") + "';");

        new Runnable() {
            @Override
            public void run() {
                UpdateMarker(CASE_DEL);
            }
        }.run();


    }





    private int GetLeftedTodoCount(){
        int count;
        if(mTableExist){
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("Select * from '" + mTableName + "' Where Done = 0;", null);
            count = cursor.getCount();
            cursor.close();
        }
        else count = 0;

        return count;

    }

    private int GetTodoCount(){
        SQLiteDatabase db = getReadableDatabase();
        int count;
        if(mTableExist){
            Cursor cursor = db.rawQuery("Select * from '" + mTableName + "';", null);
            count = cursor.getCount();
            cursor.close();
        }
        else count = 0;
        return count;
    }


    public Boolean CheckTitleAddable(String title){
        Boolean addable = true;
        if(mTableExist) {
            SQLiteDatabase db = getReadableDatabase();
            onCreate(db);
            Cursor cursor = db.rawQuery("Select * from '" + mTableName +   "' Where Title = '" + title.replace("\'","\'\'") + "'; ", null);
            addable = cursor.getCount() == 0;
            cursor.close();
        }
        return addable;
    }


    public Boolean CheckTableExist(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + mTableName + "'", null);
        mTableExist = cursor.getCount() != 0;
        cursor.close();
        return mTableExist;
    }


    /**
     * Search For Key in every targetCol
     * target Col must be Title or Content or TiCon
     * **/
    public ArrayList<String[]> Search(String Key, String targetCol){

        //{title, content, tbl_name}
        ArrayList<String[]> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name != 'android_metadata';", null);
        while ( cursor.moveToNext() ) {
            String table_name =  cursor.getString( cursor.getColumnIndexOrThrow("tbl_name"));

            if (targetCol.equals("TiCon")){
                Cursor inner_cursor = db.rawQuery("select * from '" + table_name +
                        "' Where Title like '%" + Key.replace("\'","\'\'") + "%'" +
                        " or Content like '%" + Key.replace("\'","\'\'") + "%';" , null);

                while(inner_cursor.moveToNext()){
                    String title = inner_cursor.getString(inner_cursor.getColumnIndexOrThrow("Title"));
                    String content = inner_cursor.getString(inner_cursor.getColumnIndexOrThrow("Content"));

                    String[] tmp = {title, content, table_name };
                    items.add(tmp);
                }
                inner_cursor.close();
            }


            else{
                Cursor inner_cursor = db.rawQuery("select * from '" + table_name +
                        "' Where " + targetCol + " like '%" + Key.replace("\'","\'\'") + "%';" , null);

                while(inner_cursor.moveToNext()){
                    String title = inner_cursor.getString(inner_cursor.getColumnIndexOrThrow("Title"));
                    String content = inner_cursor.getString(inner_cursor.getColumnIndexOrThrow("Content"));

                    String[] tmp = {title, content ,table_name};
                    items.add(tmp);
                }
                inner_cursor.close();
            }
        }
        cursor.close();


        return items;


    }

    /**
     *  It mast be called after update DB
     *
     *  IN CASE ADD, Update TRY &       WIN
     *  IN CASE DEL, Update TRY & DRW & WIN
     *  IN CASE CHK, Update       DRW & WIN
     *  **/
    private void UpdateMarker(int case_){
        SharedPreferenceManager.MarkersLoader markersLoader = new SharedPreferenceManager.MarkersLoader(mContext, Integer.parseInt(mDstDate.toString("yyyyMM")));
        int[] res = markersLoader.getMarkers();
        int WIN_MARKER = res[markersLoader.WIN_MARKER_INDEX];
        int DRW_MARKER = res[markersLoader.DRW_MARKER_INDEX];
        int TRY_MARKER = res[markersLoader.TRY_MARKER_INDEX];

        if (case_ == CASE_ADD){
            TRY_MARKER |= DST_MARKER;
            WIN_MARKER &= (~DST_MARKER);
        }

        //삭제 후 남아있는 리스트가 없으면 DST_MARK제거
        else if (case_ == CASE_DEL){
            int count = GetTodoCount();

            if (count == 0){
                TRY_MARKER &= (~DST_MARKER);
                DRW_MARKER &= (~DST_MARKER);
                WIN_MARKER &= (~DST_MARKER);

            }
            else{
                int lefted_count = GetLeftedTodoCount();
                if (lefted_count == 0)
                    WIN_MARKER |= DST_MARKER;
                else if(lefted_count == count)
                    DRW_MARKER &= (~DST_MARKER);
            }
        }

        else if (case_ == CASE_CHK){
            DRW_MARKER |= DST_MARKER;
            if(GetLeftedTodoCount() == 0)
                WIN_MARKER |= DST_MARKER;
        }

        markersLoader.saveMarkers(WIN_MARKER, DRW_MARKER, TRY_MARKER);
    }


}
