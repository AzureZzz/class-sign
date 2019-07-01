package com.example.classhelp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.adapter.StudentSignListAdapter;
import com.example.classhelp.adapter.TeacherSignListAdapter;
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
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDetailActivity extends AppCompatActivity implements View.OnClickListener {
    
    private List<SignInfo> signInfos = new ArrayList<>();
    
    private RecyclerView recyclerView;
    private TextView tvArriveNumber;
    private TextView tvTruantNumber;
    private TextView tvErrorNumber;
    private TextView tvUnSignNumber;
    
    private ImageView ivBack;
    private ImageView ivCode;

    private SmartRefreshLayout mRefreshLayout;
    private MaterialHeader mMaterialHeader;

    private TeacherSignListAdapter signInfoListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        
        bindView();
        
        mRefreshLayout.autoRefresh();
        mMaterialHeader.setShowBezierWave(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadSignInfo();
            }
        });
        ivBack.setOnClickListener(this);
        ivCode.setOnClickListener(this);

        signInfoListAdapter = new TeacherSignListAdapter(signInfos,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(signInfoListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }
    
    private void bindView() {
        recyclerView = findViewById(R.id.rlv_sign_info);
        tvArriveNumber = findViewById(R.id.tv_arrive_number);
        tvTruantNumber = findViewById(R.id.tv_truant_number);
        tvErrorNumber = findViewById(R.id.tv_error_number);
        tvUnSignNumber = findViewById(R.id.tv_unsign_number);
        ivBack = findViewById(R.id.iv_back);
        ivCode = findViewById(R.id.iv_code);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mMaterialHeader = (MaterialHeader) mRefreshLayout.getRefreshHeader();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_code:
                Intent intent = new Intent(TaskDetailActivity.this, QRCodeActivity.class);
                intent.putExtra(Contract.SignTask.NAME,
                        getIntent().getStringExtra(Contract.SignTask.NAME));
                String type = getIntent().getStringExtra(Contract.SignTask.TYPE);
                if (type.equals("1")) {
                    intent.putExtra("type", 1);
                    intent.putExtra(Contract.SignTask.CODE,
                            getIntent().getStringExtra(Contract.SignTask.TASKCODE));
                } else {
                    intent.putExtra("type", 2);
                    intent.putExtra(Contract.SignTask.QRCODESRC,
                            getIntent().getStringExtra(Contract.SignTask.QRCODESRC));
                }
                startActivity(intent);
                break;
        }
    }

    private void loadSignInfo() {
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(TaskDetailActivity.this));
        map.put(Contract.SignTask.ID, getIntent().getStringExtra(Contract.SignTask.ID));
        new LoadSignInfoTask().execute(map);
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

    class LoadSignInfoTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/sign/getSignRecByTec";
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
//            XToast.success(TaskDetailActivity.this,s).show();
            try {
                if (s != null) {
                    JsonObject res = OkHttpUtils.GSON.fromJson(s, JsonObject.class);
                    if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
                        JsonObject data = res.getAsJsonObject(Contract.JSONKEY.DATA);
                        JsonObject newToken = data.getAsJsonObject(Contract.JSONKEY.NEWTOKEN);
                        SPUtils.saveToken(TaskDetailActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());

                        Type type = new TypeToken<ArrayList<SignInfo>>() {
                        }.getType();
                        JsonArray signInfoArray = data.getAsJsonArray(Contract.JSONKEY.DATA);
                        List<SignInfo> signInfoList =
                                OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(signInfoArray),
                                        type);
                        signInfos.clear();
                        signInfos.addAll(signInfoList);
                        signInfoListAdapter.notifyDataSetChanged();
                        updateNumber();
                        mRefreshLayout.finishRefresh();
                    } else {
                        XToast.error(TaskDetailActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                XToast.error(TaskDetailActivity.this, "发生未知错误！").show();
            }
        }
    }
}
