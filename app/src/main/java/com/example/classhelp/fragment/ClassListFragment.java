package com.example.classhelp.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.adapter.ClassListAdapter;
import com.example.classhelp.entity.ClassInfo;
import com.example.classhelp.utils.SpacesItemDecoration;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassListFragment extends Fragment {

    private Context context;
    private ClassListAdapter classListAdapter;
    private List<ClassInfo> classList = new ArrayList<>();

    private SmartRefreshLayout mRefreshLayout;
    private MaterialHeader mMaterialHeader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_class_list, null);
        context = view.getContext();

        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mMaterialHeader = (MaterialHeader) mRefreshLayout.getRefreshHeader();

        mRefreshLayout.autoRefresh();
        mMaterialHeader.setShowBezierWave(false);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initClassData();
            }
        });

        //初始化数据
        RecyclerView recyclerView = view.findViewById(R.id.rv_class_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        classListAdapter = new ClassListAdapter(classList);
        recyclerView.setAdapter(classListAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(32));
        classListAdapter.notifyDataSetChanged();
        return view;
    }

    private void initClassData() {
        Map<String, String> map = new HashMap<>();
        map.put(Contract.Token.TOKEN, SPUtils.getToken(context));
        new LoadClassList().execute(map);
    }

    class LoadClassList extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = getString(R.string.root_url) + "/ljg/class/list";
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
                        SPUtils.saveToken(context, token);

                        Type type = new TypeToken<ArrayList<ClassInfo>>() {
                        }.getType();
                        JsonArray classes = data.getAsJsonArray(Contract.JSONKEY.LIST);
                        List<ClassInfo> myClass =
                                OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(classes), type);
                        classList.clear();
                        //老师身份
                        if (SPUtils.getUserType(context) == 2) {
                            JsonArray teacherClasses = data.getAsJsonArray(Contract.JSONKEY.LISTTEA);
                            List<ClassInfo> teacherClass =
                                    OkHttpUtils.GSON.fromJson(OkHttpUtils.GSON.toJson(teacherClasses), type);
                            for (ClassInfo info : teacherClass) {
                                info.setClassType("1");
                            }
                            classList.addAll(teacherClass);
                        }
                        classList.addAll(myClass);
                        classListAdapter.notifyDataSetChanged();
                    }else {
                        XToast.error(context,
                                res.get(Contract.JSONKEY.MESSAGE).getAsString()).show();
                    }
                }else{
                    XToast.error(context, "no response").show();
                }
                mRefreshLayout.finishRefresh();
            }catch (Exception e){
                e.printStackTrace();
                mRefreshLayout.finishRefresh();
                XToast.error(context,"发生未知错误！").show();
            }

        }
    }
}
