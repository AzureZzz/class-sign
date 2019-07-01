package com.example.classhelp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.activity.StudentClassActivity;
import com.example.classhelp.activity.TeacherClassActivity;
import com.example.classhelp.entity.ClassInfo;
import com.example.classhelp.utils.SPUtils;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.layout.XUILinearLayout;

import java.util.List;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ViewHolder> {

    private Context context;
    private List<ClassInfo> classInfoList;
    private int[] backgrounds = {
            R.drawable.jb_blue,
            R.drawable.jb_yellow,
            R.drawable.jb_light_green,
            R.drawable.jb_purple,
            R.drawable.jb_red,
            R.drawable.jb_dark_green,
            R.drawable.jb_pink
    };

    public ClassListAdapter(List<ClassInfo> list) {
        classInfoList = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        XUILinearLayout xuiView;
        RelativeLayout rlClass;
        TextView tvClassNum;
        TextView tvClassCode;
        TextView tvClassItemName;
        ImageView ivMainCardMenu;
        RadiusImageView rivType;
        FloatingActionButton fabType;
        TextView tvClassTeacherName;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            xuiView = view.findViewById(R.id.xui_view);
            rlClass = view.findViewById(R.id.rl_class);
            tvClassCode = view.findViewById(R.id.tv_class_code);
            tvClassNum = view.findViewById(R.id.tv_class_num);
            tvClassItemName = view.findViewById(R.id.tv_class_item_name);
            ivMainCardMenu = view.findViewById(R.id.iv_main_fragment_menu);
            rivType = view.findViewById(R.id.riv_type);
//            fabType = view.findViewById(R.id.fab_type);
            tvClassTeacherName = view.findViewById(R.id.tv_class_teacher_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        if (context == null) {
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_class_list,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        //点击课堂
        viewHolder.xuiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                ClassInfo classInfo = classInfoList.get(position);
                Intent intent;
                if (SPUtils.getUserType(context) == 1) {
                    intent = new Intent(v.getContext(), StudentClassActivity.class);
                    intent.putExtra(Contract.Class.ID, classInfo.getClassId() + "");
                } else {
                    intent = new Intent(v.getContext(), TeacherClassActivity.class);
                    intent.putExtra(Contract.Class.ID, classInfo.getClassId() + "");
                }
                v.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        ClassInfo classInfo = classInfoList.get(i);
        viewHolder.tvClassNum.setText("课 号：" + classInfo.getClassNo());
        viewHolder.tvClassItemName.setText(classInfo.getClassName());
        viewHolder.tvClassCode.setText(classInfo.getClassCode());
        viewHolder.tvClassTeacherName.setText(classInfo.getTeacherName());
        if (classInfo.getClassType() != null) {
//            viewHolder.fabType.setImageResource(R.drawable.ic_type_teacher);
            viewHolder.rivType.setImageResource(R.drawable.ic_type_teacher);
        }else {
//            viewHolder.fabType.setImageResource(R.drawable.ic_type_teacher);
            viewHolder.rivType.setImageResource(R.drawable.ic_type_student);
        }
//        viewHolder.fabType.setBackgroundResource(backgrounds[i % backgrounds.length]);
        viewHolder.rlClass.setBackgroundResource(backgrounds[i % backgrounds.length]);
    }

    @Override
    public int getItemCount() {
        return classInfoList.size();
    }
}
