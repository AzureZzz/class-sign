package com.example.classhelp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.example.classhelp.entity.ClassDetail;
import com.example.classhelp.entity.SignTask;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.alpha.XUIAlphaLinearLayout;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class StudentClassActivity extends AppCompatActivity implements View.OnClickListener {

    private XUIAlphaLinearLayout xllMySign;
    private XUIAlphaLinearLayout xllStudents;
    private RelativeLayout rlNewsestTask;
    private LinearLayout llNoTask;
    private LinearLayout llOutClass;
    private ImageView ivOutClass;

    private RadiusImageView rvTeacherHeadImg;
    private TextView tvTeacherName;
    private TextView tvClassCode;
    private TextView tvStuNumber;
    private MiniLoadingDialog miniLoadingDialog;

    private ImageView mShowQRCode;
    private TitleBar titlebar;

    private TextView tvTaskName;
    private TextView tvTaskStart;
    private TextView tvTaskType;

    private ClassDetail classDetail;
    private StatefulLayout slStateful;

    private SignTask signTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_class);

        loadClassInfo();

        bindView();
        slStateful.showLoading();

        titlebar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        xllMySign.setOnClickListener(this);
        xllStudents.setOnClickListener(this);
        llOutClass.setOnClickListener(this);
        ivOutClass.setOnClickListener(this);
        mShowQRCode.setOnClickListener(this);
        rlNewsestTask.setOnClickListener(this);
    }

    private void bindView() {
        xllMySign = findViewById(R.id.xll_my_sign);
        xllStudents = findViewById(R.id.xll_students);
        mShowQRCode = findViewById(R.id.tv_show_qr_code);
        llOutClass = findViewById(R.id.ll_out_class);
        ivOutClass = findViewById(R.id.iv_out_class);
        titlebar = findViewById(R.id.tb_title_bar);
        rlNewsestTask = findViewById(R.id.rl_newest_task);
        llNoTask = findViewById(R.id.ll_no_task);
        rvTeacherHeadImg = findViewById(R.id.rv_teacher_head_img);
        tvClassCode = findViewById(R.id.tv_class_code);
        tvStuNumber = findViewById(R.id.tv_stu_number);
        tvTeacherName = findViewById(R.id.tv_teacher_name);
        slStateful = findViewById(R.id.sl_stateful);

        tvTaskName = findViewById(R.id.tv_task_name);
        tvTaskStart = findViewById(R.id.tv_task_start);
        tvTaskType = findViewById(R.id.tv_task_type);

        miniLoadingDialog = WidgetUtils.getMiniLoadingDialog(StudentClassActivity.this);
        miniLoadingDialog.updateMessage("加载中");
    }

    private void loadClassInfo() {
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(StudentClassActivity.this));
        map.put(Contract.Class.ID, getIntent().getStringExtra(Contract.Class.ID));
        new LoadClassInfoTask().execute(map);   //加载课堂信息
        new LoadNewestTask().execute(map);      //加载最新任务
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.xll_my_sign:      //我的历史签到任务
                intent = new Intent(StudentClassActivity.this, StudentSignListActivity.class);
                intent.putExtra(Contract.Class.ID, classDetail.getClassId());
                startActivity(intent);
                break;
            case R.id.xll_students:     //课堂成员
                intent = new Intent(StudentClassActivity.this, StudentsActivity.class);
                intent.putExtra(Contract.Class.ID, classDetail.getClassId());
                startActivity(intent);
                break;
            case R.id.tv_show_qr_code:  //课堂二维码
                intent = new Intent(StudentClassActivity.this, QRCodeActivity.class);
                intent.putExtra(Contract.Class.ID, classDetail.getClassId());
                intent.putExtra(Contract.Class.NAME, classDetail.getClassName());
                intent.putExtra(Contract.Class.CODE, classDetail.getClassCode());
                intent.putExtra(Contract.Class.QRCODESRC, classDetail.getClassQrcodeSrc());
                startActivity(intent);
                break;
            case R.id.rl_newest_task:   //最新签到任务
                if (signTask.getTaskType() == 1) {
                    intent = new Intent(StudentClassActivity.this, CodeSignInActivity.class);
                } else {
                    intent = new Intent(StudentClassActivity.this, ScanSignInActivity.class);
                }
                intent.putExtra(Contract.SignTask.ID, signTask.getTaskId() + "");
                intent.putExtra(Contract.SignTask.LATITUDE, signTask.getTaskLatitude());
                intent.putExtra(Contract.SignTask.LONGITUDE, signTask.getTaskLongitude());
                startActivity(intent);
                break;
            case R.id.ll_out_class:
                showConfirmOut();
                break;
            case R.id.iv_out_class:
                showConfirmOut();
                break;
        }
    }

    private void showConfirmOut() {
        new MaterialDialog.Builder(this)
                .content(R.string.tip_out_class_confirm)
                .positiveText(R.string.lab_yes)
                .negativeText(R.string.lab_no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        miniLoadingDialog.show();
                        Map<String, String> map = new HashMap<>();
                        map.put(Contract.Class.ID, classDetail.getClassId());
                        map.put(Contract.Token.TOKEN, SPUtils.getToken(StudentClassActivity.this));
                        new OutClassTask().execute(map);
                    }
                })
                .show();
    }

    class LoadClassInfoTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/class/myClassInfo";
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
            try {
                if (s != null) {
                    JsonObject res = OkHttpUtils.GSON.fromJson(s, JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        JsonObject newToken = res.getAsJsonObject(Contract.JSONKEY.DATA)
                                .getAsJsonObject(Contract.JSONKEY.NEWTOKEN);
                        SPUtils.saveToken(StudentClassActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());
                        if (res.getAsJsonObject(Contract.JSONKEY.DATA)
                                .get(Contract.JSONKEY.DATA) != JsonNull.INSTANCE) {
                            JsonObject data = res.getAsJsonObject(Contract.JSONKEY.DATA)
                                    .getAsJsonObject(Contract.JSONKEY.DATA);
                            classDetail =
                                    OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(data),
                                            ClassDetail.class);
                            classDetail.setClassId(getIntent().getStringExtra(Contract.Class.ID));

                            tvClassCode.setText("加课码:" + classDetail.getClassCode());
                            tvTeacherName.setText(classDetail.getUserName());
                            tvStuNumber.setText("学生" + classDetail.getStuNumber() + "人");
                            titlebar.setTitle(classDetail.getClassName());
                            if (classDetail.getUserHeadSrc() != null) {
                                Glide.with(StudentClassActivity.this)
                                        .load(classDetail.getUserHeadSrc())
                                        .into(rvTeacherHeadImg);
                            }
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    slStateful.showContent();
                                }
                            }, 1000);
                        } else {
                            slStateful.showError(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadClassInfo();
                                }
                            });
                        }
                    } else {
                        slStateful.showError(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadClassInfo();
                            }
                        });
                        XToast.error(StudentClassActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                } else {
                    slStateful.showEmpty();
                    XToast.error(StudentClassActivity.this, "未找到课堂数据！").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(StudentClassActivity.this, "发生未知错误！").show();
            }
        }
    }


    class LoadNewestTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/task/recently";
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
            try {
                if (s != null) {
                    JsonObject res = OkHttpUtils.GSON.fromJson(s, JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        JsonObject newToken = res.getAsJsonObject(Contract.JSONKEY.DATA)
                                .getAsJsonObject(Contract.JSONKEY.NEWTOKEN);
                        SPUtils.saveToken(StudentClassActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());

                        if (res.getAsJsonObject(Contract.JSONKEY.DATA).get(Contract.JSONKEY.DATA) != JsonNull.INSTANCE) {
                            JsonObject data = res.getAsJsonObject(Contract.JSONKEY.DATA)
                                    .getAsJsonObject(Contract.JSONKEY.DATA);
                            signTask = OkHttpUtils.GSON.fromJson(data, SignTask.class);
                            if (signTask != null) {
                                llNoTask.setVisibility(View.GONE);
                                rlNewsestTask.setVisibility(View.VISIBLE);
                                tvTaskName.setText(signTask.getTaskName());
                                tvTaskStart.setText(signTask.getTaskStart());
                                if (signTask.getTaskType() == 1) {
                                    tvTaskType.setText("数字考勤");
                                } else {
                                    tvTaskType.setText("扫码考勤");
                                }
                            }
                        }
                    } else {
                        XToast.error(StudentClassActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                } else {
                    XToast.error(StudentClassActivity.this, "未找到课堂数据！").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(StudentClassActivity.this, "发生未知错误！").show();
            }
        }
    }

    class OutClassTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/class/exit";
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
            try {
                if (s != null) {
                    JsonObject res = OkHttpUtils.GSON.fromJson(s, JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        JsonObject newToken = res.getAsJsonObject(Contract.JSONKEY.DATA)
                                .getAsJsonObject(Contract.JSONKEY.NEWTOKEN);
                        SPUtils.saveToken(StudentClassActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());
                        XToast.success(StudentClassActivity.this,
                                "退出成功！").show();
                        finish();
                    } else {
                        XToast.error(StudentClassActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                } else {
                    XToast.error(StudentClassActivity.this, "退出失败！").show();
                }
                miniLoadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(StudentClassActivity.this, "发生未知错误！").show();
            }
        }
    }
}
