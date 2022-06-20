package com.nirmalbhetwal.task_ins_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.activities.CreateTaskActivity;
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
        tableSubTaskSources = tableSubTaskSources;
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
        holder.layoutSubTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SUB_TASK", "Subtask edit was called");
                TableSubTask subtask = tablesSubTasks.get(position);
                //subtask.setStatus(subtask.getStatus() == 1? 0:1 );
                tableSubTaskListeners.onTableSubTaskClicked(subtask, position);
            }
        });
        holder.isCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SUB_TASK", "Subtask edit was called");
                TableSubTask subtask = tablesSubTasks.get(position);
                //subtask.setStatus(subtask.getStatus() == 1? 0:1 );
                tableSubTaskListeners.onTableSubTaskClicked(subtask, position);
            }
        });
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
            layoutSubTask = itemView.findViewById(R.id.layoutSubtask);
        }

        void setTableSubtask(TableSubTask tableSubTask) {
            textSubCategory.setText(tableSubTask.getTitle());
            Log.d("SUB_TASK", "" + tableSubTask.getStatus());
            if (tableSubTask.getStatus() == 1){
                isCompleted.setChecked(true);
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


    public void updateSubtask(int position, Context context) {
        TableSubTask tableSubTask = tablesSubTasks.get(position);
        @SuppressLint("StaticFieldLeak")
        class UpdateSubTaskFunc extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                tableSubTask.setStatus(tableSubTask.getStatus() == 1 ? 0 : 1);
                Log.d("SUB_TASK", "Editing:" + tableSubTask.getStatus());
                TableTaskDB.getDatabase(context).tableTaskDao()
                        .insertSubTaskTable(tableSubTask);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                tablesSubTasks.remove(tableSubTask);
                //tableSubTask.setStatus(tableSubTask.getStatus() == 1 ? 0 : 1);
                tablesSubTasks.add(position, tableSubTask);
                notifyDataSetChanged();
            }
        }
        new UpdateSubTaskFunc().execute();

    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }

}
