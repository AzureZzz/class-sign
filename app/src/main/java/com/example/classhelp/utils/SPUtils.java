package com.example.classhelp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.classhelp.Contract;
import com.google.gson.JsonObject;

public final class SPUtils {

    public static String getToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Contract.SP.TOKEN_FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(Contract.Token.TOKEN,null);
    }

    public static int getUserType(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Contract.SP.USER_FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(Contract.User.TYPE,0);
    }

    public static void saveToken(Context context,String token){
        SharedPreferences sp = context.getSharedPreferences(Contract.SP.TOKEN_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Contract.Token.TOKEN,token);
        editor.apply();
    }
}
