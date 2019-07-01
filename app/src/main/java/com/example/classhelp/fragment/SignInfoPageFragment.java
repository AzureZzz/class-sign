package com.example.classhelp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.classhelp.R;
import com.example.classhelp.adapter.TeacherSignListAdapter;
import com.example.classhelp.entity.SignInfo;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.ArrayList;
import java.util.List;

public class SignInfoPageFragment extends Fragment {

    private String content;
    private View view = null;
    private Context context;

    private List<SignInfo> signInfos;
    private RecyclerView rvSignInfoList;
    private TeacherSignListAdapter signInfoListAdapter;


    public static SignInfoPageFragment newInstance(String content, Context context, List<SignInfo> signInfos) {
        return new SignInfoPageFragment().setContent(content,context,signInfos);
}

    public SignInfoPageFragment setContent(String content,Context context,List<SignInfo> signInfos) {
        this.context = context;
        this.content = content;
        this.signInfos = signInfos;
        return this;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        switch (content) {
            case "出勤":
                view = inflater.inflate(R.layout.fragment_arrive_list,container,false);
                rvSignInfoList = view.findViewById(R.id.rv_arrive_list);
                initList();
                break;
            case "旷课":
                view = inflater.inflate(R.layout.fragment_truant_list,container,false);
                rvSignInfoList = view.findViewById(R.id.rv_truant_list);
                initList();
                break;
            case "异常":
                view = inflater.inflate(R.layout.fragment_error_list,container,false);
                rvSignInfoList = view.findViewById(R.id.rv_error_list);
                initList();
                break;
        }
        return view;
    }

    private void initList(){
        signInfoListAdapter = new TeacherSignListAdapter(signInfos,context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        rvSignInfoList.setLayoutManager(linearLayoutManager);
        rvSignInfoList.setAdapter(signInfoListAdapter);
    }

    public void notifyDataSetChanged(){
        signInfoListAdapter.notifyDataSetChanged();
    }
}
