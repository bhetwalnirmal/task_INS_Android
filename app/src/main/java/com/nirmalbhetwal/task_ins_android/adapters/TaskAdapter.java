package com.nirmalbhetwal.task_ins_android.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolders>{

    private List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolders(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_category_row_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolders holder, int position) {
        holder.setTask(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class TaskViewHolders extends RecyclerView.ViewHolder {
        TextView textTaskTitle, textTaskCategory, textTaskCreateDate;
        public TaskViewHolders(@NonNull View itemView) {
            super(itemView);
            textTaskTitle = itemView.findViewById(R.id.textRVTitle);
            textTaskCategory = itemView.findViewById(R.id.textRVCategory);
            textTaskCreateDate = itemView.findViewById(R.id.textRVCreatedDateTime);
        }
        void setTask(Task task) {
            textTaskTitle.setText(task.getTaskText());
            if (task.getCategory().trim().isEmpty()) {
                textTaskCategory.setVisibility(View.GONE);
            }
            else
            {
                textTaskCategory.setText(task.getCategory());
                //textTaskCategory.setText(task.getCatName());
            }
            textTaskCreateDate.setText(task.getCreateDateTime());

        }
    }
}

