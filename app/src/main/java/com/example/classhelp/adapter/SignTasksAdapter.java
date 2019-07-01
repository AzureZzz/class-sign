package com.example.classhelp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.classhelp.Contract;
import com.example.classhelp.R;
import com.example.classhelp.activity.SignInfoActivity;
import com.example.classhelp.activity.TaskDetailActivity;
import com.example.classhelp.entity.SignTask;

import java.util.List;

public class SignTasksAdapter extends RecyclerView.Adapter<SignTasksAdapter.SignTasksViewHolder> {

    private Context context;
    private List<SignTask> signTasks;

    public SignTasksAdapter(Context context, List<SignTask> signTasks) {
        this.context = context;
        this.signTasks = signTasks;
    }

    @NonNull
    @Override
    public SignTasksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_sign_task_list,viewGroup,false);
        final SignTasksViewHolder tasksViewHolder = new SignTasksViewHolder(view);
        tasksViewHolder.rlTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = tasksViewHolder.getAdapterPosition();
                SignTask signTask = signTasks.get(position);
//                Intent intent = new Intent(context, SignInfoActivity.class);
                Intent intent = new Intent(context, TaskDetailActivity.class);
                intent.putExtra(Contract.SignTask.ID,signTask.getTaskId()+"");
                intent.putExtra(Contract.SignTask.QRCODESRC,signTask.getTaskQrSrc());
                intent.putExtra(Contract.SignTask.TASKCODE,signTask.getTaskCode());
                intent.putExtra(Contract.SignTask.NAME,signTask.getTaskName());
                intent.putExtra(Contract.SignTask.TYPE,String.valueOf(signTask.getTaskType()));
                v.getContext().startActivity(intent);
            }
        });
        return tasksViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SignTasksViewHolder signTasksViewHolder, int i) {
        SignTask task = signTasks.get(i);
        signTasksViewHolder.tvTaskName.setText(task.getTaskName());
        if(task.getTaskType() == 1){
            signTasksViewHolder.tvTaskType.setText("数字考勤");
        }else{
            signTasksViewHolder.tvTaskType.setText("扫码考勤");
        }
        signTasksViewHolder.tvTaskStart.setText(task.getTaskStart());
        signTasksViewHolder.tvTaskEnd.setText(task.getTaskEnd());
    }

    @Override
    public int getItemCount() {
        return signTasks.size();
    }

    class SignTasksViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlTask;
        TextView tvTaskName;
        TextView tvTaskType;
        TextView tvTaskStart;
        TextView tvTaskEnd;
        public SignTasksViewHolder(@NonNull View itemView) {
            super(itemView);
            rlTask = itemView.findViewById(R.id.rl_task);
            tvTaskName = itemView.findViewById(R.id.tv_task_name);
            tvTaskType = itemView.findViewById(R.id.tv_task_type);
            tvTaskStart = itemView.findViewById(R.id.tv_task_start);
            tvTaskEnd = itemView.findViewById(R.id.tv_task_end);
        }
    }
}
