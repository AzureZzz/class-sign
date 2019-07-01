package com.example.classhelp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.classhelp.entity.ClassInfo;
import com.example.classhelp.entity.SignTask;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.alpha.XUIAlphaLinearLayout;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TeacherClassActivity extends AppCompatActivity implements View.OnClickListener {

    private XUIAlphaLinearLayout mLayNewSign;
    private XUIAlphaLinearLayout mLaySignTasks;
    private XUIAlphaLinearLayout mLayTeachers;
    private RelativeLayout rlNewsestTask;
    private LinearLayout llNoTask;
    private LinearLayout llDeleteClass;
    private ImageView ivDeleteClass;

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
        setContentView(R.layout.activity_teacher_class);

        bindView();
        loadClassInfo();
        slStateful.showLoading();

        titlebar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLayNewSign.setOnClickListener(this);
        mLaySignTasks.setOnClickListener(this);
        mLayTeachers.setOnClickListener(this);
        mShowQRCode.setOnClickListener(this);
        llDeleteClass.setOnClickListener(this);
        ivDeleteClass.setOnClickListener(this);
        rlNewsestTask.setOnClickListener(this);
    }

    private void bindView() {
        mLayNewSign = findViewById(R.id.lay_new_sign);
        mLaySignTasks = findViewById(R.id.lay_sign_tasks);
        mLayTeachers = findViewById(R.id.lay_students);
        mShowQRCode = findViewById(R.id.tv_show_qr_code);
        llDeleteClass = findViewById(R.id.ll_delete_class);
        ivDeleteClass = findViewById(R.id.iv_delete_class);
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

        miniLoadingDialog = WidgetUtils.getMiniLoadingDialog(TeacherClassActivity.this);
        miniLoadingDialog.updateMessage("加载中");
    }

    private void loadClassInfo() {
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(TeacherClassActivity.this));
        map.put(Contract.Class.ID, getIntent().getStringExtra(Contract.Class.ID));
        new LoadClassInfoTask().execute(map);
        new LoadNewestTask().execute(map);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.lay_new_sign:     //新建签到任务
                intent = new Intent(TeacherClassActivity.this, NewSignActivity.class);
                intent.putExtra(Contract.Class.ID, classDetail.getClassId());
                intent.putExtra(Contract.Class.NAME, classDetail.getClassName());
                startActivity(intent);
                break;
            case R.id.lay_sign_tasks:   //历史签到任务
                intent = new Intent(TeacherClassActivity.this, SignTasksActivity.class);
                intent.putExtra(Contract.Class.ID, classDetail.getClassId());
                startActivity(intent);
                break;
            case R.id.lay_students:     //学生列表
                intent = new Intent(TeacherClassActivity.this, StudentsActivity.class);
                intent.putExtra(Contract.Class.ID, classDetail.getClassId());
                startActivity(intent);
                break;
            case R.id.tv_show_qr_code:  //课堂二维码
                intent = new Intent(TeacherClassActivity.this, QRCodeActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra(Contract.Class.ID, classDetail.getClassId());
                intent.putExtra(Contract.Class.NAME, classDetail.getClassName());
                intent.putExtra(Contract.Class.CODE, classDetail.getClassCode());
                intent.putExtra(Contract.Class.QRCODESRC, classDetail.getClassQrcodeSrc());
                startActivity(intent);
                break;
            case R.id.rl_newest_task:   //最新签到
                intent = new Intent(TeacherClassActivity.this, TaskDetailActivity.class);
                intent.putExtra(Contract.SignTask.ID,signTask.getTaskId()+"");
                intent.putExtra(Contract.SignTask.QRCODESRC,signTask.getTaskQrSrc());
                intent.putExtra(Contract.SignTask.TASKCODE,signTask.getTaskCode());
                intent.putExtra(Contract.SignTask.NAME,signTask.getTaskName());
                intent.putExtra(Contract.SignTask.TYPE,String.valueOf(signTask.getTaskType()));
                intent.putExtra(Contract.SignTask.ID, signTask.getTaskId());
                startActivity(intent);
                break;
            case R.id.ll_delete_class:
                showConfirmDelete();
                break;
            case R.id.iv_delete_class:
                showConfirmDelete();
                break;
        }
    }

    private void showConfirmDelete() {
        new MaterialDialog.Builder(this)
                .content(R.string.tip_delete_class_confirm)
                .positiveText(R.string.lab_yes)
                .negativeText(R.string.lab_no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        miniLoadingDialog.show();
                        Map<String, String> map = new HashMap<>();
                        map.put(Contract.Class.ID, classDetail.getClassId());
                        map.put(Contract.Token.TOKEN, SPUtils.getToken(TeacherClassActivity.this));
                        new DeleteClassTask().execute(map);
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
                        SPUtils.saveToken(TeacherClassActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());
                        if (res.getAsJsonObject(Contract.JSONKEY.DATA).get(Contract.JSONKEY.DATA) != JsonNull.INSTANCE) {
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
                                Glide.with(TeacherClassActivity.this).load(classDetail.getUserHeadSrc())
                                        .into(rvTeacherHeadImg);
                            }
                            Timer timer = new Timer();
                            TimerTask timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    slStateful.showContent();
                                }
                            };
                            timer.schedule(timerTask,0,1000);
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
                        XToast.error(TeacherClassActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }

                } else {
                    slStateful.showEmpty();
                    XToast.error(TeacherClassActivity.this, "未找到课堂数据！").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(TeacherClassActivity.this, "发生未知错误！").show();
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
                        SPUtils.saveToken(TeacherClassActivity.this,
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
                        XToast.error(TeacherClassActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                } else {
                    XToast.error(TeacherClassActivity.this, "未找到课堂数据！").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(TeacherClassActivity.this, "发生未知错误！").show();
            }
        }
    }

    class DeleteClassTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/class/disMiss";
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
                        SPUtils.saveToken(TeacherClassActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());
                        XToast.success(TeacherClassActivity.this,
                                "解散成功！").show();
                        finish();
                    } else {
                        XToast.error(TeacherClassActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                } else {
                    slStateful.showEmpty();
                    XToast.error(TeacherClassActivity.this, "解散失败！").show();
                }
                miniLoadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(TeacherClassActivity.this, "发生未知错误！").show();
            }
        }
    }
}
