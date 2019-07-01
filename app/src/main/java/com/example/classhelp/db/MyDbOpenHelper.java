package com.example.classhelp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDbOpenHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_TOKEN_TABLE =
            "CREATE TABLE " + Contract.TokenEntry.TABLE_NAME + " (" +
                    Contract.TokenEntry._ID + " INTEGER PRIMARY KEY, " +
                    Contract.TokenEntry.COLUMN_NAME_TOKEN + " VARCHAR(255)  " +
                    " ) ";

    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + Contract.UserEntry.TABLE_NAME + " (" +
                    Contract.UserEntry._ID + " INTEGER PRIMARY KEY, " +
                    Contract.UserEntry.COLUMN_NAME_PHONE + " VARCHAR(11) , " +
                    Contract.UserEntry.COLUMN_NAME_NAME + " VARCHAR(50) , " +
                    Contract.UserEntry.COLUMN_NAME_STUNO + " VARCHAR(20) , " +
                    Contract.UserEntry.COLUMN_NAME_SCHOOL + " VARCHAR(50) , " +
                    Contract.UserEntry.COLUMN_NAME_TYPE + " INTEGER  " +
                    " ) ";

    private static final String SQL_CREATE_USER_INFO_TABLE =
            "CREATE TABLE " + Contract.UserInfoEntry.TABLE_NAME + " (" +
                    Contract.UserInfoEntry._ID + " INTEGER PRIMARY KEY, " +
                    Contract.UserInfoEntry.COLUMN_NAME_SEX + " INTEGER , " +
                    Contract.UserInfoEntry.COLUMN_NAME_NICKNAME + " VARCHAR(100) , " +
                    Contract.UserInfoEntry.COLUMN_NAME_HEADIMG + " VARCHAR(100) , " +
                    Contract.UserInfoEntry.COLUMN_NAME_MAJOR + " VARCHAR(50) , " +
                    Contract.UserInfoEntry.COLUMN_NAME_GRADE + " VARCHAR(10) , " +
                    Contract.UserInfoEntry.COLUMN_NAME_EMAIL + " VARCHAR(100)  " +
                    " ) ";

    private static final String SQL_DROP_TOKEN_TABLE =
            "DROP TABLE IF EXISTS " + Contract.TokenEntry.TABLE_NAME;
    private static final String SQL_DROP_USER_TABLE =
            "DROP TABLE IF EXISTS " + Contract.UserEntry.TABLE_NAME;
    private static final String SQL_DROP_USER_INFO_TABLE =
            "DROP TABLE IF EXISTS " + Contract.UserInfoEntry.TABLE_NAME;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "user.db";
    private Context context;

    public MyDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TOKEN_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_USER_INFO_TABLE);
        initDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TOKEN_TABLE);
        db.execSQL(SQL_DROP_USER_TABLE);
        db.execSQL(SQL_DROP_USER_INFO_TABLE);
        onCreate(db);
    }

    private void initDb(SQLiteDatabase db) {

    }
}
