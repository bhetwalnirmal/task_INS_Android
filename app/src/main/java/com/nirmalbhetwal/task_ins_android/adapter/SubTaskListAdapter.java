package com.nirmalbhetwal.task_ins_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.database.TableTaskDB;
import com.nirmalbhetwal.task_ins_android.listeners.TableSubTaskListeners;
import com.nirmalbhetwal.task_ins_android.model.TableSubTask;

import java.util.List;
import java.util.Timer;


public class SubTaskListAdapter extends RecyclerView.Adapter<SubTaskListAdapter.TableSubTaskViewHolder> {

    private List<TableSubTask> tablesSubTasks;
    private TableSubTaskListeners tableSubTaskListeners;
    private Timer timer;
    private List<TableSubTask> tableSubTaskSources;

    public SubTaskListAdapter(List<TableSubTask> tableSubTasks, TableSubTaskListeners tableSubTaskListeners) {
        this.tablesSubTasks = tableSubTasks;
        this.tableSubTaskListeners = tableSubTaskListeners;
        tableSubTaskSources = tableSubTasks;
    }

    @NonNull
    @Override
    public TableSubTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TableSubTaskViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_subtask,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TableSubTaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setTableSubtask(tablesSubTasks.get(position));
//        holder.layoutSubTask.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tableSubTaskListeners.onTableSubTaskClicked(tablesSubTasks.get(position), position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return tablesSubTasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class TableSubTaskViewHolder extends RecyclerView.ViewHolder{

        TextView textSubCategory;
        CheckBox isCompleted;
        LinearLayout layoutSubTask;

        public TableSubTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textSubCategory = itemView.findViewById(R.id.subTaskTitle);
            isCompleted = itemView.findViewById(R.id.isCompleted);
            layoutSubTask = itemView.findViewById(R.id.layoutAddSubTask);
        }

        void setTableSubtask(TableSubTask tableSubTask) {
            textSubCategory.setText(tableSubTask.getTitle());
            if (tableSubTask.getStatus() == 1){
                isCompleted.isChecked();
            }else{
                isCompleted.setChecked(false);
            }

        }
    }

//    public void searchTasks(final String searchKeyword){
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (searchKeyword.trim().isEmpty()) {
//                    tablesTasks = tableTaskssource;
//                } else {
//                    ArrayList<TableTask> temp = new ArrayList<>();
//                    for (TableTask tableTask : tableTaskssource) {
//                        if (tableTask.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
//                                || tableTask.getCategory().toLowerCase().contains(searchKeyword.toLowerCase())
//                                || tableTask.getCategory().toLowerCase().contains(searchKeyword.toLowerCase())) {
//                            temp.add(tableTask);
//                        }
//                    }
//                    tablesTasks = temp;
//                }
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        notifyDataSetChanged();
//                    }
//                });
//            }
//        }, 500);
//    }

    public void removeItem(int position, Context context) {
        @SuppressLint("StaticFieldLeak")
        class DeleteSubTaskFunc extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                TableTaskDB.getDatabase(context).tableTaskDao()
                        .deleteSubTask(tablesSubTasks.get(position));
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                tablesSubTasks.remove(position);
                notifyItemRemoved(position);
            }
        }
        new DeleteSubTaskFunc().execute();
    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }

}
