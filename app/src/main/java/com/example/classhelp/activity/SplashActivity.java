package com.example.classhelp.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonObject;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.activity.BaseSplashActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashActivity extends BaseSplashActivity {

    private boolean isLogin = false;

//    @Override
//    protected long getSplashDurationMillis() {
//        return 2500;
//    }

    @Override
    public void onCreateActivity() {
        initSplashView(R.drawable.splash);
        startSplash(true);
        verifyLogin();
    }

    @Override
    public void onSplashFinished() {
        if(isLogin){
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
        }else{
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
        }
        finish();
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
    }

    private void verifyLogin() {
        String url = getString(R.string.root_url) + "/ljg/verify";
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(SplashActivity.this));
        try {
            OkHttpUtils.post(url, OkHttpUtils.GSON.toJson(map), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JsonObject res = OkHttpUtils.GSON.fromJson(response.body().string(),
                            JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        isLogin =true;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
