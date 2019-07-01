package com.example.classhelp.activity;

import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.adapter.StudentListAdapter;
import com.example.classhelp.entity.Student;
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

public class StudentsActivity extends AppCompatActivity {

    private List<Student> students = new ArrayList<>();
    private RecyclerView rvStudents;
    private TitleBar titleBar;
    private StudentListAdapter studentAdapter;

    private SmartRefreshLayout mRefreshLayout;
    private MaterialHeader mMaterialHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        bindView();

        mRefreshLayout.autoRefresh();
        mMaterialHeader.setShowBezierWave(false);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadStudentsInfo();
            }
        });

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        studentAdapter = new StudentListAdapter(this, students);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvStudents.setLayoutManager(linearLayoutManager);
        rvStudents.setAdapter(studentAdapter);
        rvStudents.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    private void bindView(){
        rvStudents = findViewById(R.id.rv_student_list);
        titleBar = findViewById(R.id.tb_title_bar);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mMaterialHeader = (MaterialHeader) mRefreshLayout.getRefreshHeader();
    }

    private void loadStudentsInfo(){
        Map<String,String> map = new HashMap<>();
        map.put(Contract.Class.ID,getIntent().getStringExtra(Contract.Class.ID));
        map.put(Contract.Token.TOKEN, SPUtils.getToken(StudentsActivity.this));
        new LoadStudentsTask().execute(map);
    }

    class LoadStudentsTask extends AsyncTask<Map<String,String>,Void,String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url)+"/ljg/class/member";
            try {
                return OkHttpUtils.post(url,OkHttpUtils.GSON.toJson(maps[0]));
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
                        SPUtils.saveToken(StudentsActivity.this,
                                newToken.get(Contract.Token.TOKEN).getAsString());

                        Type type = new TypeToken<ArrayList<Student>>() {}.getType();
                        JsonArray studentArray = data.getAsJsonArray(Contract.JSONKEY.DATA);
                        List<Student> studentList =
                                OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(studentArray), type);
                        students.clear();
                        students.addAll(studentList);
                        studentAdapter.notifyDataSetChanged();
                    }else{
                        XToast.error(StudentsActivity.this,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                }else{
                    XToast.error(StudentsActivity.this, "no response").show();
                }
                mRefreshLayout.finishRefresh();
            }catch (Exception e) {
                e.printStackTrace();
                mRefreshLayout.finishRefresh();
                XToast.error(StudentsActivity.this, "出现未知错误！").show();
            }
        }
    }
}
