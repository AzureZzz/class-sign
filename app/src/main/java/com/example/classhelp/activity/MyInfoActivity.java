package com.example.classhelp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.entity.UserInfo;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.toast.XToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyInfoActivity extends Activity implements View.OnClickListener {

    private TitleBar titleBar;
    private CircleImageView cvHeadImg;
    private TextView tvName;
    private TextView tvSex;
    private TextView tvStuNo;
    private TextView tvSchool;
    private TextView tvType;
    private TextView tvMajor;
    private TextView tvGrade;
    private RelativeLayout rlStuNo;

    private LinearLayout llEdit;
    private ImageView ivEdit;

    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        bindView();
        loadUserInfo();

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llEdit.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
    }

    private void bindView() {
        titleBar = findViewById(R.id.tb_title_bar);
        cvHeadImg = findViewById(R.id.cv_head_img);
        tvName = findViewById(R.id.tv_name);
        tvSex = findViewById(R.id.tv_sex);
        tvStuNo = findViewById(R.id.tv_stu_no);
        tvSchool = findViewById(R.id.tv_school);
        tvType = findViewById(R.id.tv_type);
        tvMajor = findViewById(R.id.tv_major);
        tvGrade = findViewById(R.id.tv_grade);
        rlStuNo = findViewById(R.id.rl_stu_no);
        llEdit = findViewById(R.id.ll_edit);
        ivEdit = findViewById(R.id.iv_edit);
    }

    private void loadUserInfo() {
        userInfo = new UserInfo();
        SharedPreferences sp = getSharedPreferences(Contract.SP.USER_FILE_NAME,
                Context.MODE_PRIVATE);

        userInfo.setUserName(sp.getString(Contract.User.NAME, null));
        userInfo.setUserSex(sp.getInt(Contract.User.SEX, 0));
        userInfo.setUserSchool(sp.getString(Contract.User.SCHOOL, null));
        userInfo.setUserType(sp.getInt(Contract.User.TYPE, 0));
        userInfo.setUserNo(sp.getString(Contract.User.STUNO, null));
        userInfo.setUserMajor(sp.getString(Contract.User.MAJOR, null));
        userInfo.setUserGrade(sp.getString(Contract.User.GRADE, null));
        userInfo.setUserHeadSrc(sp.getString(Contract.User.HEADSRC, null));
        userInfo.setUserEmail(sp.getString(Contract.User.EMAIL, null));

        tvName.setText(userInfo.getUserName());
        int sex = userInfo.getUserSex();
        if (sex == 1) {
            tvSex.setText("男");
        } else if (sex == 2) {
            tvSex.setText("女");
        } else {
            tvSex.setText("未知");
        }
        tvSchool.setText(userInfo.getUserSchool());
        int type = userInfo.getUserType();
        if (type == 1) {
            tvType.setText("学生");
            tvStuNo.setText(userInfo.getUserNo());
        } else if (type == 2) {
            tvType.setText("老师");
            rlStuNo.setVisibility(View.GONE);
        } else {
            tvType.setText("未知身份");
        }
        tvMajor.setText(userInfo.getUserMajor());
        tvGrade.setText(userInfo.getUserGrade());
        String src = userInfo.getUserHeadSrc();
        if (src != null) {
            Glide.with(MyInfoActivity.this).load(src).into(cvHeadImg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_edit:
                toSetting();
                break;
            case R.id.iv_edit:
                toSetting();
                break;
        }
    }

    private void toSetting(){
        Bundle bundle = new Bundle();
        bundle.putString(Contract.User.NAME,userInfo.getUserName());
        bundle.putInt(Contract.User.SEX,userInfo.getUserType());
        bundle.putInt(Contract.User.TYPE,userInfo.getUserType());
        bundle.putString(Contract.User.SCHOOL,userInfo.getUserSchool());
        bundle.putString(Contract.User.STUNO,userInfo.getUserNo());
        bundle.putString(Contract.User.MAJOR,userInfo.getUserMajor());
        bundle.putString(Contract.User.GRADE,userInfo.getUserGrade());
        bundle.putString(Contract.User.HEADSRC,userInfo.getUserHeadSrc());
        bundle.putString(Contract.User.EMAIL,userInfo.getUserEmail());
        if(userInfo.getUserSex()==null){
            bundle.putString(Contract.User.SEX,null);
        }else{
            bundle.putString(Contract.User.SEX,String.valueOf(userInfo.getUserSex()));
        }
        Intent intent;
        intent = new Intent(MyInfoActivity.this,SettingUserInfoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
