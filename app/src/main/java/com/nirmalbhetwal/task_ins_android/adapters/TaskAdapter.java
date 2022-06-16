package com.nirmalbhetwal.task_ins_android.adapters;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.listeners.TaskListener;
import com.nirmalbhetwal.task_ins_android.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private List<Task> tablesTasks;
    private TaskListener tableTaskListeners;
    private Timer timer;
    private List<Task> tableTaskssource;

    public TaskAdapter(List<Task> tablesTasks, TaskListener tableTaskListeners) {
        this.tablesTasks = tablesTasks;
        this.tableTaskListeners = tableTaskListeners;
        tableTaskssource = tablesTasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_category_row_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setTableTask(tablesTasks.get(position));
        holder.layoutTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableTaskListeners.onTableTaskClicked(tablesTasks.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tablesTasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView textTableTitle, textTableCategory, textTableDate, textProgress, textComplete;
        LinearLayout layoutTask;
        RoundedImageView imageTableTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTableTitle = itemView.findViewById(R.id.textRVTitle);
            textTableCategory = itemView.findViewById(R.id.textRVCategory);
            textTableDate = itemView.findViewById(R.id.textRVCreatedDateTime);
            layoutTask = itemView.findViewById(R.id.layoutTask);
            imageTableTask = itemView.findViewById(R.id.imageRVTask);
            textProgress = itemView.findViewById(R.id.textProgress);
            textComplete = itemView.findViewById(R.id.textComplete);
        }

        void setTableTask(Task tableTask) {
            textTableTitle.setText(tableTask.getTitle());
            if (tableTask.getCategory().trim().isEmpty()) {
                textTableCategory.setVisibility(View.GONE);
            } else {
                textTableCategory.setText(tableTask.getCategory());
            }

            textTableDate.setText(tableTask.getCreateDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutTask.getBackground();
            if (tableTask.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(tableTask.getColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            if (tableTask.getImagePath() != null) {

                byte[] bytes = Base64.decode(tableTask.getImagePath(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageTableTask.setImageBitmap(bitmap);
                //imageTableTask.setImageBitmap(BitmapFactory.decodeFile(tableTask.getImagePath()));
                imageTableTask.setVisibility(View.VISIBLE);
            } else {
                imageTableTask.setVisibility(View.GONE);
            }
/*
            if (tableTask.getCompleted() == "Completed") {
                textComplete.setVisibility(View.VISIBLE);

            }
            else {
                textProgress.setVisibility(View.VISIBLE);
            }
            */

        }
    }

    public void searchTasks(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    tablesTasks = tableTaskssource;
                } else {
                    ArrayList<Task> temp = new ArrayList<>();
                    for (Task tableTask : tableTaskssource) {
                        if (tableTask.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || tableTask.getCategory().toLowerCase().contains(searchKeyword.toLowerCase())
                                || tableTask.getCategory().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(tableTask);
                        }
                    }
                    tablesTasks = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }
}

