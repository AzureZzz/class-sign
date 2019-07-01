package com.example.classhelp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.entity.SignInfo;
import com.example.classhelp.utils.OkHttpUtils;
import com.example.classhelp.utils.SPUtils;
import com.google.gson.JsonObject;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.popupwindow.popup.XUIListPopup;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TeacherSignListAdapter extends RecyclerView.Adapter<TeacherSignListAdapter.ViewHolder> {

    private List<SignInfo> signInfoList;
    private Context context;

    public TeacherSignListAdapter(List<SignInfo> signInfoList, Context context) {
        this.signInfoList = signInfoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_signed_list, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.rbSignState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                final SignInfo signInfo = signInfoList.get(position);
                new BottomSheet.BottomListSheetBuilder(context)
                        .addItem("旷课")
                        .addItem("出勤")
                        .setOnSheetItemClickListener(new BottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                            @Override
                            public void onClick(BottomSheet dialog, final View itemView, final int position,
                                                String tag) {
                                dialog.dismiss();
//                                XToast.success(context,position+"").show();
                                Map<String,String> map = new HashMap<>();
                                map.put(Contract.Token.TOKEN, SPUtils.getToken(context));
                                map.put(Contract.SignTask.ID,String.valueOf(signInfo.getTaskId()));
                                map.put(Contract.User.ID,String.valueOf(signInfo.getUserId()));
                                switch (position){
                                    case 0: //旷课
                                        map.put(Contract.JSONKEY.SIGNSTATE,"0");
                                        break;
                                    case 1: //出勤
                                        map.put(Contract.JSONKEY.SIGNSTATE,"1");
                                        break;
                                }
                                new ChangeStateTask().execute(map);
                            }
                        })
                        .setIsCenter(true)
                        .build()
                        .show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherSignListAdapter.ViewHolder viewHolder, int i) {
        SignInfo signInfo = signInfoList.get(i);
        int state = signInfo.getSignState();
        if (state == 1) {
            viewHolder.rbSignState.setText("出勤");
            viewHolder.tvSignTime.setText(signInfo.getSignTime());
            viewHolder.rbSignState.setTextColor(Color.parseColor("#008577"));
        } else if (state == 2) {
            viewHolder.rbSignState.setText("异常");
            viewHolder.tvSignTime.setText(signInfo.getSignTime());
            viewHolder.rbSignState.setTextColor(Color.parseColor("#FF0000"));
        } else if (state == 3)  {
            viewHolder.rbSignState.setText("旷课");
            viewHolder.tvSignTime.setText("");
            viewHolder.rbSignState.setTextColor(Color.parseColor("#FF0000"));
        }else{
            viewHolder.rbSignState.setText("未签到");
            viewHolder.tvSignTime.setText("");
            viewHolder.rbSignState.setTextColor(Color.parseColor("#858C96"));
        }
        viewHolder.tvStuName.setText(signInfo.getUserName());
    }

    @Override
    public int getItemCount() {
        return signInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStuName;
        TextView tvSignTime;
        RoundButton rbSignState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSignTime = itemView.findViewById(R.id.tv_sign_time);
            tvStuName = itemView.findViewById(R.id.tv_stu_name);
            rbSignState = itemView.findViewById(R.id.rb_sign_state);
        }
    }

    class ChangeStateTask extends AsyncTask<Map<String,String>,Void,String>{

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            String url = context.getString(R.string.root_url)+"/ljg/sign/reviseSignRec";
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
//            XToast.success(context,s).show();
            JsonObject res = OkHttpUtils.GSON.fromJson(s, JsonObject.class);
            if (res.get(Contract.JSONKEY.CODE).getAsInt() == 200) {
            }
        }
    };
}
