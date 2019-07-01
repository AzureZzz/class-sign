package com.example.classhelp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.activity.CodeSignInActivity;
import com.example.classhelp.activity.ScanSignInActivity;
import com.example.classhelp.entity.SignInItemInfo;
import com.example.classhelp.entity.SignInfo;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;

import java.util.List;


/**
 * 签到列表
 */
public class StudentSignListAdapter extends RecyclerView.Adapter<StudentSignListAdapter.ViewHolder> {

    private List<SignInfo> signInfos;
    private Context context;

    public StudentSignListAdapter(List<SignInfo> signInfos, Context context) {
        this.signInfos = signInfos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_student_sign_list, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.rbSignCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                SignInfo signInfo = signInfos.get(position);
                Intent intent = new Intent(v.getContext(), CodeSignInActivity.class);
                intent.putExtra(Contract.SignTask.ID,signInfo.getTaskId()+"");
                intent.putExtra(Contract.SignTask.LATITUDE,signInfo.getTaskLatitude());
                intent.putExtra(Contract.SignTask.LONGITUDE,signInfo.getTaskLongitude());
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.rbSignScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                SignInfo signInfo = signInfos.get(position);
                Intent intent = new Intent(v.getContext(), ScanSignInActivity.class);
                intent.putExtra(Contract.SignTask.ID,signInfo.getTaskId()+"");
                intent.putExtra(Contract.SignTask.LATITUDE,signInfo.getTaskLatitude());
                intent.putExtra(Contract.SignTask.LONGITUDE,signInfo.getTaskLongitude());
                v.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SignInfo signInfo = signInfos.get(i);
        viewHolder.tvTaskName.setText(signInfo.getTaskName());
        int signType = 0;
        if(signInfo.getSignType() != null){
            signType = Integer.valueOf(signInfo.getSignType());
        }else{
            signType = Integer.valueOf(signInfo.getTaskType());
        }
        if (signType == 1) {
            viewHolder.rbSignCode.setVisibility(View.VISIBLE);
            viewHolder.tvSignInType.setText("数字考勤");
        } else  {
            viewHolder.rbSignScan.setVisibility(View.VISIBLE);
            viewHolder.tvSignInType.setText("扫码考勤");
        }
        if(signInfo.getSignState() != null){
            viewHolder.rbSignCode.setVisibility(View.GONE);
            viewHolder.rbSignScan.setVisibility(View.GONE);
            viewHolder.tvSignIn.setVisibility(View.VISIBLE);
            int stateCode = signInfo.getSignState();
            if (stateCode == 1) {
                viewHolder.tvSignIn.setText("出勤");
                viewHolder.tvSignIn.setTextColor(Color.parseColor("#00CED1"));
                viewHolder.tvSignInTime.setText(signInfo.getSignTime());
            } else if (stateCode == 2) {
                viewHolder.tvSignIn.setText("异常");
                viewHolder.tvSignIn.setTextColor(Color.parseColor("#FF8C00"));
                viewHolder.tvSignInTime.setText(signInfo.getSignTime());
            } else if (stateCode == 3) {
                viewHolder.tvSignIn.setText("旷课");
                viewHolder.tvSignIn.setTextColor(Color.parseColor("#FF4500"));
                viewHolder.tvSignInTime.setText(signInfo.getSignTime());
            } else if (stateCode == 0) {    //未签到
                if (signType == 1) {    //显示数字签到按钮
                    viewHolder.rbSignCode.setVisibility(View.VISIBLE);
                    viewHolder.tvSignInTime.setText("数字考勤");
                } else  {               //显示扫码签到按钮
                    viewHolder.rbSignScan.setVisibility(View.VISIBLE);
                    viewHolder.tvSignInTime.setText("扫码考勤");
                }
                viewHolder.tvSignInType.setText("");
                viewHolder.tvSignIn.setVisibility(View.GONE);
            }
        }else{
            viewHolder.tvSignInTime.setText(signInfo.getTaskEnd());
            viewHolder.tvSignIn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return signInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName;
        TextView tvSignInTime;
        TextView tvSignInType;
        TextView tvSignIn;
        RoundButton rbSignCode;
        RoundButton rbSignScan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tv_task_name);
            tvSignInTime = itemView.findViewById(R.id.tv_sign_time);
            tvSignInType = itemView.findViewById(R.id.tv_check_in_type);
            tvSignIn = itemView.findViewById(R.id.tv_check_in_status);
            rbSignCode = itemView.findViewById(R.id.rb_sign_code);
            rbSignScan = itemView.findViewById(R.id.rb_sign_scan);
        }
    }
}
