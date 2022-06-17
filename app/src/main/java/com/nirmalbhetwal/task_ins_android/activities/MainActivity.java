package com.nirmalbhetwal.task_ins_android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nirmalbhetwal.task_ins_android.Listener.TableTaskListeners;
import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.adapter.TaskListAdapter;
import com.nirmalbhetwal.task_ins_android.databinding.ActivityMainBinding;
import com.nirmalbhetwal.task_ins_android.db.TableTaskDB;
import com.nirmalbhetwal.task_ins_android.entities.TableTask;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText inputSearch;
    RecyclerView tasksRecycleView;
    ImageView imageAddTaskMain;
    private List<TableTask> tableTasksList;
    private TaskListAdapter tableTaskAdapters;
    private int taskClickedPosition = -1;

    public final static int REQUEST_CODE_ADD_TASK = 1;
    public final static int REQUEST_CODE_UPDATE_TASK = 2;
    public final static int REQUEST_CODE_SHOW_TASKS = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tableTaskAdapters.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(tableTasksList.size() != 0){
                    tableTaskAdapters.searchTasks(s.toString());
                }
            }
        });

        tasksRecycleView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );
        tableTasksList = new ArrayList<>();
        tableTaskAdapters = new TaskListAdapter(MainActivity.this,tableTasksList);
        tasksRecycleView.setAdapter(tableTaskAdapters);

        getTask(REQUEST_CODE_SHOW_TASKS, false);

}
       private void findId() {
        inputSearch=findViewById(R.id.inputSearch);
        tasksRecycleView=findViewById(R.id.tasksRecycleView);
        imageAddTaskMain=findViewById(R.id.imageAddTaskMain);
        imageAddTaskMain.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        // Intent to create new task activity here..
        if(view.getId() == R.id.imageAddTaskMain){
            Intent addTaskWindow = new Intent(this, CreateTaskActivity.class);
            startActivity(addTaskWindow);
        }
    }

    private void getTask (final int requestCode, final boolean isTaskDeleted) {

        @SuppressLint("StaticFieldLeak")
        class GetTask_HS extends AsyncTask<Void, Void, List<TableTask>> {

            @Override
            protected List<TableTask> doInBackground(Void... voids) {

                return TableTaskDB
                        .getDatabase(getApplicationContext())
                        .tableTaskDao().getAllTableTask();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<TableTask> tableTasks){
                super.onPostExecute(tableTasks);

                if (requestCode == REQUEST_CODE_SHOW_TASKS){
                    tableTasksList.clear();
                    tableTasksList.addAll(tableTasks);
                    tableTaskAdapters.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_TASK) {
                    tableTasksList.add(0, tableTasks.get(0));
                    tableTaskAdapters.notifyItemInserted(0);
                    tasksRecycleView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_TASK){
                    tableTasksList.remove(taskClickedPosition);

                    if (isTaskDeleted){
                        tableTaskAdapters.notifyItemRemoved(taskClickedPosition);
                    } else{
                        tableTasksList.add(taskClickedPosition, tableTasks.get(taskClickedPosition));
                        tableTaskAdapters.notifyItemChanged(taskClickedPosition);

                    }
                }
                Log.d("My_TableTasks", tableTasksList.toString());
            }
        }
        new GetTask_HS().execute();

    }


}