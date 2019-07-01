package com.example.classhelp.db;

import android.content.Context;

public class DbUtils {

    private static MyDbOpenHelper myDbOpenHelper;

    public static MyDbOpenHelper getInstance(Context context){
        if(myDbOpenHelper == null){
            myDbOpenHelper = new MyDbOpenHelper(context);
        }
        return myDbOpenHelper;
    }
}
