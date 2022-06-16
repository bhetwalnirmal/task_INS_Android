package com.nirmalbhetwal.task_ins_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.nirmalbhetwal.task_ins_android.adapters.TaskAdapter;
import com.nirmalbhetwal.task_ins_android.database.TaskDB;
import com.nirmalbhetwal.task_ins_android.databinding.ActivityMainBinding;
import com.nirmalbhetwal.task_ins_android.listeners.TaskListener;
import com.nirmalbhetwal.task_ins_android.model.Task;
import com.nirmalbhetwal.task_ins_android.utils.RecyclerAdapter;
import com.nirmalbhetwal.task_ins_android.utils.RecyclerViewClickInterface;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements TaskListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    public final static int REQUEST_CODE_ADD_TASK = 1;
    public final static int REQUEST_CODE_UPDATE_TASK = 2;
    public final static int REQUEST_CODE_SHOW_TASKS = 3;
    public final static int REQUEST_CODE_SELECT_IMAGE = 4;
    public final static int REQUEST_CODE_STORAGE_PERMISSION = 5;
    public final static int REQUEST_AUDIO_PERMISSION = 6;

    private int taskClickedPosition = -1;

    private RecyclerView tasksRecyclerView;
    private List<Task> taskList;
    private TaskAdapter taskAdapters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView imageAddTaskMain = findViewById(R.id.imageAddTaskMain);
        imageAddTaskMain.setOnClickListener(v -> startActivityForResult(
                new Intent(getApplicationContext(), CreateTaskActivity.class),
                REQUEST_CODE_ADD_TASK
        ));

        tasksRecyclerView = findViewById(R.id.tasksRecycleView);
        tasksRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );
        taskList = new ArrayList<>();
        taskAdapters = new TaskAdapter(taskList, this);
        tasksRecyclerView.setAdapter(taskAdapters);

        // taskList = new ArrayList<>();
        //tasksAdapter = new TasksAdapters(taskList);
        //tasksRecyclerView.setAdapter(tasksAdapter);

        getTask(REQUEST_CODE_SHOW_TASKS, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskAdapters.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(taskList.size() != 0){
                    taskAdapters.searchTasks(s.toString());
                }
            }
        });


        findViewById(R.id.imageAddTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateTaskActivity.class),
                        REQUEST_CODE_ADD_TASK
                );
            }
        });

        findViewById(R.id.imageAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });

    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_SELECT_IMAGE);
    }

    private String encode(Uri imageUri) throws FileNotFoundException {
        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        String encodedImage = encodeImage(selectedImage);
        return encodedImage;
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    @Override
    public void onTableTaskClicked(Task tableTask, int position) {
        taskClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateTaskActivity.class);
        intent.putExtra("isViewUpdate", true);
        intent.putExtra("tableTask", tableTask);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_TASK);
    }

    // Checking if the task list is empty , which indicates that the app just started since we have
    // Declared it as a global variable
    // But for this case we are adding all the notes from the database and notify the adapter about
    // The new loaded Dataset
    private void getTask (final int requestCode, final boolean isTaskDeleted) {

        @SuppressLint("StaticFieldLeak")
        class GetTask_HS extends AsyncTask<Void, Void, List<Task>> {

            @Override
            protected List<Task> doInBackground(Void... voids) {
                 /*return TaskDatabase
                         .getDatabase(getApplicationContext())
                         .taskDao().getAllTasks(); */

                return TaskDB
                        .getDatabase(getApplicationContext())
                        .taskDao().getAllTasks();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Task> tableTasks){
                super.onPostExecute(tableTasks);
                 /*if (taskList.size()==0) {
                    taskList.addAll(tasks);
                    tasksAdapter.notifyDataSetChanged();
                }
                else {
                    taskList.add(0,tasks.get(0));
                    tasksAdapter.notifyItemInserted(0);
                }
                tasksRecyclerView.smoothScrollToPosition(0);
                // Scrolling the recyclerview to the top */

                if (requestCode == REQUEST_CODE_SHOW_TASKS){
                    taskList.clear();
                    taskList.addAll(tableTasks);
                    taskAdapters.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_TASK) {
                    taskList.add(0, tableTasks.get(0));
                    taskAdapters.notifyItemInserted(0);
                    tasksRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_TASK){
                    taskList.remove(taskClickedPosition);

                    if (isTaskDeleted){
                        taskAdapters.notifyItemRemoved(taskClickedPosition);
                    } else{
                        taskList.add(taskClickedPosition, tableTasks.get(taskClickedPosition));
                        taskAdapters.notifyItemChanged(taskClickedPosition);

                    }
                }
                Log.d("My_TableTasks", taskList.toString());
            }
        }
        new GetTask_HS().execute();

    }
}