package com.example.classhelp.activity;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.adapter.SignTasksAdapter;
import com.example.classhelp.entity.SignTask;
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

public class SignTasksActivity extends AppCompatActivity {

    private List<SignTask> signTasks = new ArrayList<>();
    private SignTasksAdapter tasksAdapter;

    private RecyclerView rvTaskList;
    private TitleBar titleBar;

    private SmartRefreshLayout mRefreshLayout;
    private MaterialHeader mMaterialHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_tasks);

        bindView();

        mRefreshLayout.autoRefresh();
        mMaterialHeader.setShowBezierWave(false);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadSignTasksList();
            }
        });

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tasksAdapter = new SignTasksAdapter(this, signTasks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvTaskList.setLayoutManager(linearLayoutManager);
        rvTaskList.setAdapter(tasksAdapter);
    }

    private void bindView() {
        rvTaskList = findViewById(R.id.rv_task_list);
        titleBar = findViewById(R.id.tb_title_bar);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mMaterialHeader = (MaterialHeader) mRefreshLayout.getRefreshHeader();
    }


    private void loadSignTasksList() {
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(SignTasksActivity.this));
        map.put(Contract.Class.ID, getIntent().getStringExtra(Contract.Class.ID));
        new LoadSignsInfo().execute(map);
    }

    class LoadSignsInfo extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/task/classTasks";
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
                        String token = data.getAsJsonObject(Contract.JSONKEY.NEWTOKEN)
                                .get(Contract.Token.TOKEN).getAsString();
                        SPUtils.saveToken(SignTasksActivity.this, token);

                        Type type = new TypeToken<ArrayList<SignTask>>() {}.getType();
                        JsonArray classes = data.getAsJsonArray(Contract.JSONKEY.DATA);
                        List<SignTask> taskList =
                                OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(classes), type);

                        signTasks.clear();
                        signTasks.addAll(taskList);
                        tasksAdapter.notifyDataSetChanged();
                    } else {
                        XToast.error(SignTasksActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                }else{
                    XToast.error(SignTasksActivity.this,
                            "no response").show();
                }
                mRefreshLayout.finishRefresh();
            } catch (Exception e) {
                mRefreshLayout.finishRefresh();
                e.printStackTrace();
                XToast.error(SignTasksActivity.this, "发生未知错误！").show();
            }

        }
    }
}
