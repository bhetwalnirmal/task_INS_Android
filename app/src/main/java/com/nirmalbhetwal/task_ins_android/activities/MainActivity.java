package com.nirmalbhetwal.task_ins_android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.adapter.TaskListAdapter;
import com.nirmalbhetwal.task_ins_android.db.TableTaskDB;
import com.nirmalbhetwal.task_ins_android.entities.TableTask;
import com.nirmalbhetwal.task_ins_android.listeners.TableTaskListeners;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TableTaskListeners {

    public final static int REQUEST_CODE_ADD_TASK = 1;
    public final static int REQUEST_CODE_UPDATE_TASK = 2;
    public final static int REQUEST_CODE_SHOW_TASKS = 3;
    public final static int REQUEST_CODE_STORAGE_PERMISSION = 5;
    public final static int REQUEST_AUDIO_PERMISSION = 6;


    private RecyclerView tasksRecyclerView;
    private List<TableTask> tableTasksList;
    private TaskListAdapter tableTaskAdapters;

    private int taskClickedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageAddTaskMain = findViewById(R.id.imageAddTaskMain);
        imageAddTaskMain.setOnClickListener(v -> startActivityForResult(
                new Intent(getApplicationContext(), CreateTaskActivity.class),
                REQUEST_CODE_ADD_TASK
        ));

        tasksRecyclerView = findViewById(R.id.tasksRecycleView);
        tasksRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );
        tableTasksList = new ArrayList<>();
        tableTaskAdapters = new TaskListAdapter(tableTasksList, this);
        tasksRecyclerView.setAdapter(tableTaskAdapters);

        getTask(REQUEST_CODE_SHOW_TASKS, false);

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
    public void onTableTaskClicked(TableTask tableTask, int position) {
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
        class GetTask_HS extends AsyncTask<Void, Void, List<TableTask>>{

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
                    tasksRecyclerView.smoothScrollToPosition(0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == RESULT_OK) {
            getTask(REQUEST_CODE_SHOW_TASKS, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_TASK && resultCode == RESULT_OK){
            if (data != null){
                getTask(REQUEST_CODE_UPDATE_TASK, data.getBooleanExtra("isTaskDeleted", false));
            }
        }
    }
}