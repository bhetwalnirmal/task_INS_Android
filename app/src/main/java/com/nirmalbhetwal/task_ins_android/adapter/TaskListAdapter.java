package com.nirmalbhetwal.task_ins_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nirmalbhetwal.task_ins_android.Listener.TableTaskListeners;
import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.entities.TableTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TableTaskViewHolder> {

    private List<TableTask> tablesTasks;
    private TableTaskListeners tableTaskListeners;
    private Timer timer;
    private Context mc;
    private List<TableTask> tableTaskssource;

    public TaskListAdapter(Context mc, List<TableTask> tablesTasks) {
        this.tablesTasks = tablesTasks;
        this.mc = mc;
      //  this.tableTaskListeners = tableTaskListeners;
        tableTaskssource = tablesTasks;
    }


    @NonNull
    @Override
    public TableTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TableTaskViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_task,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TableTaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setTableTask(tablesTasks.get(position));
        // tableTaskListeners.onTableTaskClicked(tablesTasks.get(position), position);
       holder.edit_task.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });
        holder.delete_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (tablesTasks.get(position).getTaskStatus().equals("Completed")) {
            holder.task_status.setBackground(mc.getResources().getDrawable(R.drawable.status_complete_indicatior));
            holder.status_task.setBackground(mc.getResources().getDrawable(R.drawable.background_taskcolor_low));
        }
        else if(tablesTasks.get(position).getTaskStatus().equals("Incompleted")){
            holder.task_status.setBackground(mc.getResources().getDrawable(R.drawable.status_incomplete_indicatior));
            holder.status_task.setBackground(mc.getResources().getDrawable(R.drawable.background_taskcolor_high));
        }
        else
        {
            holder.task_status.setBackground(mc.getResources().getDrawable(R.drawable.background_category_indicator));
            holder.status_task.setBackground(mc.getResources().getDrawable(R.drawable.background_taskcolor_medium));
        }

    }

    @Override
    public int getItemCount() {
        return tablesTasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class TableTaskViewHolder extends RecyclerView.ViewHolder{
        View task_status;
        TextView txt_task_title, txt_task_category, txt_create_date_time, txt_due_date_time;
        //LinearLayout layoutTask;
        ImageView task_img,status_task,edit_task,delete_task;

        public TableTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            task_status = itemView.findViewById(R.id.task_status);
            task_img = itemView.findViewById(R.id.task_img);
            txt_task_title = itemView.findViewById(R.id.txt_task_title);
            txt_task_category = itemView.findViewById(R.id.txt_task_category);
            txt_create_date_time = itemView.findViewById(R.id.txt_create_date_time);
            txt_due_date_time = itemView.findViewById(R.id.txt_due_date_time);
            status_task = itemView.findViewById(R.id.status_task);
            edit_task = itemView.findViewById(R.id.edit_task);
            delete_task = itemView.findViewById(R.id.delete_task);
        }

        void setTableTask(TableTask tableTask) {
            txt_task_title.setText(tableTask.getTitle());
            txt_create_date_time.setText(tableTask.getTitle());
            txt_due_date_time.setText(tableTask.getTitle());
            if (tableTask.getCategory().trim().isEmpty()){
                txt_task_category.setVisibility(View.GONE);
            }
            else
            {
                txt_task_category.setText(tableTask.getCategory());
            }
            if (tableTask.getImagePath() != null) {
                File imgFile = new  File(tableTask.getImagePath()[0]);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                byte[] bytes= Base64.decode(tableTask.getImagePath(),Base64.DEFAULT);
//                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                task_img.setImageBitmap(myBitmap);
                //imageTableTask.setImageBitmap(BitmapFactory.decodeFile(tableTask.getImagePath()));
                task_img.setVisibility(View.VISIBLE);
            }
            else {
                task_img.setVisibility(View.GONE);
            }

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
                    ArrayList<TableTask> temp = new ArrayList<>();
                    for (TableTask tableTask : tableTaskssource) {
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