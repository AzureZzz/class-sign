package com.example.classhelp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.entity.Token;
import com.example.classhelp.entity.UserInfo;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonObject;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mSignUp;
    private ButtonView bvLogin;
    private MaterialEditText metPhone;
    private MaterialEditText metPassword;
    private CheckBox cbRemember;
    private LinearLayout llLoad;


    private static String[] PERMISSONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        bindView();
        //        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
        //        Manifest
//        .permission.CAMERA)) {
//            permissionList.add(Manifest.permission.CAMERA);
//        } else {
//            requestPermissions(PERMISSONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
//        }
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            //代表权限已经具备了
            //调用A方法
            Log.d("TAG", "已经具备权限。。。。");
        }

        SharedPreferences spRemember = getSharedPreferences(Contract.SP.REMEMBER_FILE_NAME,
                Context.MODE_PRIVATE);
        String phone = spRemember.getString(Contract.SP.LOGIN_PHONE, null);
        String password = spRemember.getString(Contract.SP.LOGIN_PASSWORD, null);
        Boolean rememberPassword = spRemember.getBoolean(Contract.SP.LOGIN_REMEMBER, false);

        if (phone != null && !TextUtils.isEmpty(phone)) {
            metPhone.setText(phone);
        }
        if (password != null && !TextUtils.isEmpty(password)) {
            metPassword.setText(password);
        }
        cbRemember.setChecked(rememberPassword);

        mSignUp.setOnClickListener(this);
        bvLogin.setOnClickListener(this);
    }

    private void bindView() {
        mSignUp = findViewById(R.id.tv_sign_up);
        bvLogin = findViewById(R.id.bv_login);
        metPhone = findViewById(R.id.met_phone);
        metPassword = findViewById(R.id.met_password);
        cbRemember = findViewById(R.id.cb_remember);
        llLoad = findViewById(R.id.ll_load);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    //调用A方法
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用软件", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "出现未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_sign_up:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.bv_login:
                if (metPhone.validate() && metPassword.validate()) {
                    SharedPreferences spRemember =
                            getSharedPreferences(Contract.SP.REMEMBER_FILE_NAME,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = spRemember.edit();
                    if (cbRemember.isChecked()) {
                        String phone = metPhone.getText().toString();
                        String password = metPassword.getText().toString();
                        editor.putString(Contract.SP.LOGIN_PHONE, phone);
                        editor.putString(Contract.SP.LOGIN_PASSWORD, password);
                        editor.putBoolean(Contract.SP.LOGIN_REMEMBER, true);
                        editor.apply();
                    } else {
                        editor.remove(Contract.SP.LOGIN_PHONE);
                        editor.remove(Contract.SP.LOGIN_PASSWORD);
                        editor.remove(Contract.SP.LOGIN_REMEMBER);
                        editor.apply();
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put(Contract.User.PHONE, metPhone.getText().toString());
                    map.put(Contract.User.PASSWORD, metPassword.getText().toString());
                    llLoad.setVisibility(View.VISIBLE);
                    bvLogin.setVisibility(View.GONE);
                    new LoginTask().execute(map);
                }
                break;
        }
    }

    class LoginTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/user/login";
            try {
                return OkHttpUtils.post(url, OkHttpUtils.GSON.toJson(maps[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            llLoad.setVisibility(View.GONE);
            bvLogin.setVisibility(View.VISIBLE);
            try {
                if (s != null) {
                    JsonObject res = OkHttpUtils.GSON.fromJson(s, JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        JsonObject userInfoObj = res.getAsJsonObject(Contract.JSONKEY.DATA)
                                .getAsJsonObject(Contract.JSONKEY.USERINFO);
                        UserInfo userInfo =
                                OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(userInfoObj),
                                        UserInfo.class);
                        JsonObject newTokenObj = res.getAsJsonObject(Contract.JSONKEY.DATA)
                                .getAsJsonObject(Contract.JSONKEY.NEWTOKEN);
                        Token token =
                                OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(newTokenObj),
                                        Token.class);

                        SharedPreferences spUser = getSharedPreferences(Contract.SP.USER_FILE_NAME,
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = spUser.edit();

                        //本地存储用户信息
                        editor.putInt(Contract.User.ID, token.getUserId());
                        if (userInfo.getUserSex() == null) {
                            editor.putInt(Contract.User.SEX, 0);
                        } else {
                            editor.putInt(Contract.User.SEX, userInfo.getUserSex());
                        }

                        editor.putString(Contract.User.NICKNAME, userInfo.getUserNickname());
                        editor.putString(Contract.User.HEADSRC, userInfo.getUserHeadSrc());
                        editor.putString(Contract.User.MAJOR, userInfo.getUserMajor());
                        editor.putString(Contract.User.GRADE, userInfo.getUserGrade());
                        editor.putString(Contract.User.EMAIL, userInfo.getUserEmail());
                        editor.putString(Contract.User.SCHOOL, userInfo.getUserSchool());
                        editor.putString(Contract.User.STUNO, userInfo.getUserNo());

                        editor.putInt(Contract.User.TYPE, userInfo.getUserType());
                        editor.putString(Contract.User.NAME, userInfo.getUserName());
                        editor.putString(Contract.User.PHONE, metPhone.getText().toString());
                        editor.apply();

                        SharedPreferences spToken =
                                getSharedPreferences(Contract.SP.TOKEN_FILE_NAME,
                                        Context.MODE_PRIVATE);
                        editor = spToken.edit();
                        editor.putString(Contract.Token.TOKEN, token.getToken());
                        editor.apply();
                        finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        XToast.error(LoginActivity.this,
                                "账号或密码错误！").show();
                    }
                } else {
                    XToast.error(LoginActivity.this,
                            "no response").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(LoginActivity.this, "发生未知错误！").show();
            }
        }
    }
}
