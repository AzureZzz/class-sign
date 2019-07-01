package com.example.classhelp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.classhelp.R;
import com.example.classhelp.entity.Student;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {

    private Context context;
    private List<Student> students;

    public StudentListAdapter(Context context, List<Student> students) {
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_student_list, viewGroup, false);
        StudentViewHolder studentViewHolder = new StudentViewHolder(view);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder studentViewHolder, int i) {
        Student student = students.get(i);
        studentViewHolder.tvName.setText(student.getUserName());
        studentViewHolder.tvStudentNo.setText(student.getUserNo());
        //todo: add user head image
        Glide.with(context).load(student.getUserHeadSrc()).into(studentViewHolder.rivHeadImage);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvStudentNo;
        //        RadiusImageView rivHeadImage;
        CircleImageView rivHeadImage;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_stu_name);
            tvStudentNo = itemView.findViewById(R.id.tv_stu_no);
            rivHeadImage = itemView.findViewById(R.id.riv_stu_head_img);
        }
    }

}
