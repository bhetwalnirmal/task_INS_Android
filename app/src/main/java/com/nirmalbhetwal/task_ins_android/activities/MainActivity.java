package com.nirmalbhetwal.task_ins_android.activities;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.nirmalbhetwal.task_ins_android.Listener.TableTaskListeners;
import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.adapter.TaskListAdapter;
import com.nirmalbhetwal.task_ins_android.databinding.ActivityMainBinding;
import com.nirmalbhetwal.task_ins_android.entities.TableTask;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText inputSearch;
    RecyclerView tasksRecycleView;
    ImageView imageAddTaskMain;
    private List<TableTask> tableTasksList;
    private TaskListAdapter tableTaskAdapters;
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
    }
}