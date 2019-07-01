package com.example.classhelp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.toast.XToast;

public class MyAccountActivity extends Activity implements View.OnClickListener {

    private TitleBar titleBar;
    private RelativeLayout rlLogout;
    private Button btnLogout;
    private TextView tvPhone;
    private TextView tvEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        bindView();
        loadAccount();
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlLogout.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    private void bindView() {
        titleBar = findViewById(R.id.tb_title_bar);
        rlLogout = findViewById(R.id.rl_logout);
        btnLogout = findViewById(R.id.btn_logout);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);
    }

    private void loadAccount() {
        SharedPreferences sp = getSharedPreferences(Contract.SP.USER_FILE_NAME,
                Context.MODE_PRIVATE);
        tvPhone.setText(sp.getString(Contract.User.PHONE, "未知"));
        tvEmail.setText(sp.getString(Contract.User.EMAIL, "未设置"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_logout:
                showConfirmLogout();
                break;
            case R.id.btn_logout:
                showConfirmLogout();
                break;
        }
    }

    private void showConfirmLogout() {
        new MaterialDialog.Builder(this)
                .content(R.string.tip_logout_confirm)
                .positiveText(R.string.lab_yes)
                .negativeText(R.string.lab_no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        clearUserInfo();
                        Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void clearUserInfo() {
        SharedPreferences sp = getSharedPreferences(Contract.SP.USER_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(Contract.User.PHONE);
        editor.remove(Contract.User.EMAIL);
        editor.remove(Contract.User.SEX);
        editor.remove(Contract.User.HEADSRC);
        editor.remove(Contract.User.GRADE);
        editor.remove(Contract.User.MAJOR);
        editor.remove(Contract.User.TYPE);
        editor.remove(Contract.User.SCHOOL);
        editor.remove(Contract.User.STUNO);
        editor.remove(Contract.User.NAME);
        editor.remove(Contract.User.NICKNAME);
        editor.remove(Contract.User.ID);
        editor.apply();
        sp = getSharedPreferences(Contract.SP.TOKEN_FILE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.remove(Contract.Token.TOKEN);
        editor.apply();
    }
}
