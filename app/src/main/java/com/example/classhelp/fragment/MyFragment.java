package com.example.classhelp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.activity.MyAccountActivity;
import com.example.classhelp.activity.MyInfoActivity;
import com.example.classhelp.activity.StudentSignListActivity;
import com.xuexiang.xui.widget.toast.XToast;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout rlAccount;
    private RelativeLayout rlMyInfo;
    private TextView tvUserType;
    private TextView tvName;
    private TextView tvSchool;
    private CircleImageView cvHeadImg;
    private RelativeLayout rlAllUnsign;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_my, null);

        context = view.getContext();
        rlAccount = view.findViewById(R.id.rl_account);
        rlMyInfo = view.findViewById(R.id.rl_my_info);
        tvUserType = view.findViewById(R.id.tv_user_type);
        tvName = view.findViewById(R.id.tv_name);
        tvSchool = view.findViewById(R.id.tv_school);
        cvHeadImg = view.findViewById(R.id.cv_head_img);
        rlAllUnsign = view.findViewById(R.id.rl_all_unsign);

        loadUserInfo();
        rlAccount.setOnClickListener(this);
        rlMyInfo.setOnClickListener(this);
        rlAllUnsign.setOnClickListener(this);
        return view;
    }

    private void loadUserInfo() {
        SharedPreferences sp = context.getSharedPreferences(Contract.SP.USER_FILE_NAME,
                Context.MODE_PRIVATE);
        tvName.setText(sp.getString(Contract.User.NAME,"未知"));
        tvSchool.setText(sp.getString(Contract.User.SCHOOL,"未设置"));
        String src = sp.getString(Contract.User.HEADSRC, null);
        Glide.with(context).load(src).into(cvHeadImg);
        int type = sp.getInt(Contract.User.TYPE,0);
        if(type == 1){
            tvUserType.setText("(学生)");
        } else if (type == 2) {
            tvUserType.setText("(老师)");
        }else{
            tvUserType.setText("(未知身份)");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_account:
                startActivity(new Intent(getActivity(), MyAccountActivity.class));
                break;
            case R.id.rl_my_info:
                startActivity(new Intent(getActivity(), MyInfoActivity.class));
                break;
            case R.id.rl_all_unsign:
                Intent intent = new Intent(getActivity(), StudentSignListActivity.class);
                intent.putExtra("all","1");
                startActivity(intent);
                break;
        }

    }
}
