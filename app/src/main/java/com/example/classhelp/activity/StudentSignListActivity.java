package com.example.classhelp.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.adapter.StudentSignListAdapter;
import com.example.classhelp.entity.SignInItemInfo;
import com.example.classhelp.entity.SignInfo;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentSignListActivity extends Activity {

    private List<SignInfo> signInfos = new ArrayList<>();

    private TitleBar mTitlebar;
    private RecyclerView recyclerView;
    private TextView tvArriveNumber;
    private TextView tvTruantNumber;
    private TextView tvErrorNumber;
    private TextView tvUnSignNumber;
    private LinearLayout llTotal;

    private SmartRefreshLayout mRefreshLayout;
    private MaterialHeader mMaterialHeader;

    private StudentSignListAdapter studentSignListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_list);

        bindView();

        mTitlebar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //初始化数据 signInItemInfoList

        mRefreshLayout.autoRefresh();
        mMaterialHeader.setShowBezierWave(false);
        //查看全部未签到，隐藏顶部显示
        if (getIntent().getStringExtra("all") != null) {
            llTotal.setVisibility(View.GONE);
            mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    loadUnsignData();
                }
            });
        }else{
            mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    loadData();
                }
            });
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        studentSignListAdapter =
                new StudentSignListAdapter(signInfos, this);
        recyclerView.setAdapter(studentSignListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    private void bindView() {
        recyclerView = findViewById(R.id.rlv_class_check_in);
        mTitlebar = findViewById(R.id.tb_title_bar);
        tvArriveNumber = findViewById(R.id.tv_arrive_number);
        tvTruantNumber = findViewById(R.id.tv_truant_number);
        tvErrorNumber = findViewById(R.id.tv_error_number);
        tvUnSignNumber = findViewById(R.id.tv_unsign_number);
        llTotal = findViewById(R.id.ll_total);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mMaterialHeader = (MaterialHeader) mRefreshLayout.getRefreshHeader();
    }

    private void loadUnsignData(){
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(StudentSignListActivity.this));
//        XToast.success(StudentSignListActivity.this,OkHttpUtils.GSON.toJson(map)).show();
        new LoadUnSignTasks().execute(map);
    }

    private void loadData() {
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(StudentSignListActivity.this));
        map.put(Contract.Class.ID, getIntent().getStringExtra(Contract.Class.ID));
        new LoadSignTasks().execute(map);
    }

    class LoadSignTasks extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/sign/signRec";
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
                        JsonObject data = res.getAsJsonObject(Contract.JSONKEY.DATA);
                        JsonObject newToken = data.getAsJsonObject(Contract.JSONKEY.NEWTOKEN);
                        SPUtils.saveToken(StudentSignListActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());

                        Type type = new TypeToken<ArrayList<SignInfo>>() {
                        }.getType();
                        JsonArray infoArray = data.getAsJsonArray(Contract.JSONKEY.DATA);
                        List<SignInfo> infoList =
                                OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(infoArray), type);
                        signInfos.clear();
                        signInfos.addAll(infoList);
                        updateNumber();
                        studentSignListAdapter.notifyDataSetChanged();
                    } else {
                        XToast.error(StudentSignListActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                }
                mRefreshLayout.finishRefresh();
            } catch (Exception e) {
                mRefreshLayout.finishRefresh();
                e.printStackTrace();
                XToast.error(StudentSignListActivity.this, "发生未知错误！").show();
            }
        }
    }


    class LoadUnSignTasks extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/sign/signWait";
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
//            XToast.success(StudentSignListActivity.this,s).show();
            try {
                if (s != null) {
                    JsonObject res = OkHttpUtils.GSON.fromJson(s, JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        JsonObject data = res.getAsJsonObject(Contract.JSONKEY.DATA);
                        JsonObject newToken = data.getAsJsonObject(Contract.JSONKEY.NEWTOKEN);
                        SPUtils.saveToken(StudentSignListActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());

                        Type type = new TypeToken<ArrayList<SignInfo>>() {
                        }.getType();
                        JsonArray infoArray = data.getAsJsonArray(Contract.JSONKEY.DATA);
                        List<SignInfo> infoList =
                                OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(infoArray), type);
                        signInfos.clear();
                        signInfos.addAll(infoList);
                        studentSignListAdapter.notifyDataSetChanged();
                    } else {
                        XToast.error(StudentSignListActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                }else{
                    XToast.error(StudentSignListActivity.this,
                            "no response").show();
                }
                mRefreshLayout.finishRefresh();
            } catch (Exception e) {
                mRefreshLayout.finishRefresh();
                e.printStackTrace();
                XToast.error(StudentSignListActivity.this, "发生未知错误！").show();
            }
        }
    }

    private void updateNumber() {
        int arrive = 0;
        int truant = 0;
        int error = 0;
        int unsign = 0;
        for (SignInfo s : signInfos) {
            switch (s.getSignState()) {
                case 0:
                    unsign++;
                    break;
                case 1:
                    arrive++;
                    break;
                case 2:
                    error++;
                    break;
                case 3:
                    truant++;
                    break;
            }
        }
        tvUnSignNumber.setText(String.valueOf(unsign));
        tvArriveNumber.setText(String.valueOf(arrive));
        tvTruantNumber.setText(String.valueOf(truant));
        tvErrorNumber.setText(String.valueOf(error));
    }
}
