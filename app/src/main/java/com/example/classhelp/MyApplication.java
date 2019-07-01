package com.example.classhelp;

import android.app.Application;
import android.content.Intent;

import com.example.classhelp.activity.LoginActivity;
import com.example.classhelp.activity.MainActivity;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonObject;
import com.xuexiang.xui.XUI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
        super.onCreate();
    }

}
