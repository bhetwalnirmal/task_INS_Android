package com.nirmalbhetwal.task_ins_android.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.adapter.SubTaskListAdapter;
import com.nirmalbhetwal.task_ins_android.adapter.TaskListAdapter;
import com.nirmalbhetwal.task_ins_android.database.TableTaskDB;
import com.nirmalbhetwal.task_ins_android.listeners.TableSubTaskListeners;
import com.nirmalbhetwal.task_ins_android.listeners.TableTaskListeners;
import com.nirmalbhetwal.task_ins_android.model.TableSubTask;
import com.nirmalbhetwal.task_ins_android.model.TableTask;
import com.nirmalbhetwal.task_ins_android.utils.CustomDatePicker;
import com.nirmalbhetwal.task_ins_android.utils.SwipeToDeleteCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TableSubTaskListeners {

    private EditText inputTaskTitle, inputTaskDesc ,inputTaskCat;
    private TextView textCreateDateTime;
    private View viewCategoryIndicator;
    private String selectedTaskColor;
    private ImageView imageTableTask;
    private String selectedImageBase64;
    private TableTask alreadyAvailableTableTask;
    private AlertDialog dialogDeleteTask;
    private AlertDialog dialogAudioRecord;
    private String audioFilePath;
    private String taskProgress;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private ImageView ibRecord;
    private ImageView ibPlay;
    private TextView tvTime;
    private TextView tvRecordingPath;
    private ImageView ivSimpleBq;
    private boolean isRecording=false;
    private boolean isPlaying =false;
    private static final int REQUEST_AUDIO_PERMISSION = 101;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private static final int GALLERY_REQUEST = 100;
    private TextView addUpdateButton;
    private TextView tvDueDate;
    private DatePicker dueDatePicker;


    public final static int REQUEST_CODE_ADD_SUBTASK = 1;
    public final static int REQUEST_CODE_UPDATE_SUBTASK = 2;
    public final static int REQUEST_CODE_SHOW_SUBTASKS = 3;

    private RecyclerView tasksRecyclerView;
    private List<TableSubTask> tableSubTasksList;
    private SubTaskListAdapter subTaskListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        ImageView imageDelete = findViewById(R.id.imageSave);
        imageDelete.setOnClickListener(v -> showDeleteDialog());

        inputTaskTitle = findViewById(R.id.inputTaskTitle);
        inputTaskCat = findViewById(R.id.inputTaskCategory);
        inputTaskDesc = findViewById(R.id.inputTaskDesc);
        textCreateDateTime = findViewById(R.id.textCreateDateTime);
        viewCategoryIndicator = findViewById(R.id.viewCategoryIndicator);
        imageTableTask = findViewById(R.id.imageTask);
        addUpdateButton = findViewById(R.id.textAddUpdate);
        tvDueDate = findViewById(R.id.tvDueDatePicker);

        addUpdateButton.setOnClickListener(view -> saveTask());
        textCreateDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        selectedTaskColor = "#333333";
        selectedImageBase64 = "";
        taskProgress = "Incomplete";

        tasksRecyclerView = findViewById(R.id.subTasksRecycleView);
        tasksRecyclerView.setLayoutManager(
                new LinearLayoutManager(CreateTaskActivity.this)
        );
        tableSubTasksList = new ArrayList<>();
        subTaskListAdapter = new SubTaskListAdapter(tableSubTasksList, CreateTaskActivity.this);
        tasksRecyclerView.setAdapter(subTaskListAdapter);

        enableSwipeToDeleteAndUndo();

        if (getIntent().getBooleanExtra("isViewUpdate", false)) {
            alreadyAvailableTableTask = (TableTask) getIntent().getSerializableExtra("tableTask");
            setViewOrUpdateTableTask();
            getTask(REQUEST_CODE_SHOW_SUBTASKS, false);
        }

        if (getIntent().getBooleanExtra("isFromQuickAction", false)) {
            String type = getIntent().getStringExtra("quickActionType");
            if (type != null) {
                if(type.equals("image")) {
                    selectedImageBase64 = getIntent().getStringExtra("imagePath");
                    imageTableTask.setImageBitmap(BitmapFactory.decodeFile(selectedImageBase64));
                    imageTableTask.setVisibility(View.VISIBLE);
                    //selectedImageBase64 = alreadyAvailableTableTask.getImagePath();
                }
            }
        }

        initMore();
        setCategoryIndicatorColor();
    }

    private void setViewOrUpdateTableTask() {
        inputTaskTitle.setText(alreadyAvailableTableTask.getTitle());
        inputTaskDesc.setText(alreadyAvailableTableTask.getTaskText());
        inputTaskCat.setText(alreadyAvailableTableTask.getCategory());
        addUpdateButton.setText("Update Task");
        findViewById(R.id.layoutStatus).setVisibility(View.VISIBLE);
        textCreateDateTime.setText(alreadyAvailableTableTask.getCreateDateTime());
        if (alreadyAvailableTableTask.getImagePath() != null && !alreadyAvailableTableTask.getImagePath().trim().isEmpty()) {
            // decode base64 string
            byte[] bytes = Base64.decode(alreadyAvailableTableTask.getImagePath(), Base64.DEFAULT);
            // Initialize bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // set bitmap on imageView
            imageTableTask.setImageBitmap(bitmap);
            imageTableTask.setVisibility(View.VISIBLE);
            selectedImageBase64 = alreadyAvailableTableTask.getImagePath();
        }
        tvDueDate.setText(alreadyAvailableTableTask.getSetDueDate());
    }


    private void saveTask() {
        String taskTitle = inputTaskTitle.getText().toString().trim();
        String taskCategory = inputTaskCat.getText().toString().trim();
        if (taskTitle.isEmpty()) {
            Toast.makeText(this, "TITLE CANNOT BE EMPTY!! ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (taskCategory.isEmpty()) {
            Toast.makeText(this, "CATEGORY CANNOT BE EMPTY!!", Toast.LENGTH_SHORT).show();
            return;
        }

        final TableTask tableTask = new TableTask();
        tableTask.setTitle(inputTaskTitle.getText().toString());
        tableTask.setCategory(inputTaskCat.getText().toString());
        // tableTask.setCategory(selectedCategory.getCatName());
        tableTask.setTaskText(inputTaskDesc.getText().toString());
        tableTask.setCreateDateTime(textCreateDateTime.getText().toString());
        tableTask.setColor(selectedTaskColor);
        tableTask.setImagePath(selectedImageBase64);
        tableTask.setCompleted(taskProgress);

        if (dueDatePicker != null) {
            tableTask.setSetDueDate(this.parseDueDate());
        }

        if (alreadyAvailableTableTask != null) {
            tableTask.setId(alreadyAvailableTableTask.getId());
        }

        // Use of Async task to access data in room database.

        @SuppressLint("StaticFieldLeak")
        class SaveTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                TableTaskDB.getDatabase(getApplicationContext()).tableTaskDao().insertTableTask(tableTask);
                return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new SaveTask().execute();
    }

    private String parseDueDate() {
        DatePicker datePicker = this.dueDatePicker;
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        String dateTime = dateFormat.format(calendar.getTime());
        tvDueDate.setText(dateTime);
        return dateTime;
    }

    private void initMore() {

        final ImageView imagePriorityLow = findViewById(R.id.imagePriorityLow);
        final ImageView imagePriorityMedium = findViewById(R.id.imagePriorityMedium);
        final ImageView imagePriorityHigh = findViewById(R.id.imagePriorityHigh);

        CheckBox taskStatusCheckBox = findViewById(R.id.checkBoxStatus);

        // TODO: 20/06/2022 Verification of Marking ENTIRE TASK AS COMPLETED
        taskStatusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                @SuppressLint("StaticFieldLeak")
                class GetTask_HS extends AsyncTask<Void, Void, List<TableSubTask>>{

                    @Override
                    protected List<TableSubTask> doInBackground(Void... voids) {

                        return TableTaskDB
                                .getDatabase(getApplicationContext())
                                .tableTaskDao().getAllSubTask(alreadyAvailableTableTask.getId());
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    protected void onPostExecute(List<TableSubTask> tableSubTasks){
                        super.onPostExecute(tableSubTasks);
                        for (TableSubTask subTask:tableSubTasks){
                            if (subTask.getStatus() != 1){
                                compoundButton.setChecked(!b);
                                break;
                            }
                        }
                        finish();

                    }
                }
                new GetTask_HS().execute();
            }
        });

        findViewById(R.id.viewColorLow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTaskColor = "#FF018786";
                imagePriorityLow.setImageResource(R.drawable.ic_done);
                imagePriorityMedium.setImageResource(0);
                imagePriorityHigh.setImageResource(0);
                setCategoryIndicatorColor();
            }
        });
        findViewById(R.id.viewColorMedium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTaskColor = "#FDBE3B";
                imagePriorityLow.setImageResource(0);
                imagePriorityMedium.setImageResource(R.drawable.ic_done);
                imagePriorityHigh.setImageResource(0);
                setCategoryIndicatorColor();
            }
        });
        findViewById(R.id.viewColorHigh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTaskColor = "#FF4842";
                imagePriorityLow.setImageResource(0);
                imagePriorityMedium.setImageResource(0);
                imagePriorityHigh.setImageResource(R.drawable.ic_done);
                setCategoryIndicatorColor();
            }
        });

        if (alreadyAvailableTableTask != null && alreadyAvailableTableTask.getColor() != null && !alreadyAvailableTableTask.getColor().trim().isEmpty()) {
            switch (alreadyAvailableTableTask.getColor()) {
                case "#FF018786":
                    findViewById(R.id.viewColorLow).performClick();
                    break;
                case "#FDBE3B":
                    findViewById(R.id.viewColorMedium).performClick();
                    break;
                case "#FF4842":
                    findViewById(R.id.viewColorHigh).performClick();
                    break;
            }
        }

        findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateTaskActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });
        findViewById(R.id.layoutAudioRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioDialog();
            }
        });

        findViewById(R.id.layoutDueDatePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDatePicker customDatePicker;
                customDatePicker = new CustomDatePicker();
                customDatePicker.show(getSupportFragmentManager(), "DATE PICK");
            }
        });


        findViewById(R.id.textAddUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });

        if (alreadyAvailableTableTask != null) {
            findViewById(R.id.layoutAddSubTask).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutAddSubTask).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateTaskActivity.this);
                    builder.setTitle("Add Subtask");

                    // Set up the input
                    final EditText input = new EditText(CreateTaskActivity.this);
                    // Specify the type of input expected; this, for example,
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (input.getText().toString() != "") {
                                final TableSubTask tableSubTask = new TableSubTask();

                                tableSubTask.setTitle(input.getText().toString());
                                tableSubTask.setCatId(alreadyAvailableTableTask.getId());
                                tableSubTask.setStatus(0);

                                @SuppressLint("StaticFieldLeak")
                                class SaveSubTask extends AsyncTask<Void, Void, Void> {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        TableTaskDB.getDatabase(getApplicationContext()).tableTaskDao().insertSubTaskTable(tableSubTask);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void unused) {
                                        super.onPostExecute(unused);
                                        Intent intent = new Intent();
                                        intent.putExtra("isNewTaskAdded", true);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }

                                new SaveSubTask().execute();

                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }
    }

    private void requestRecordingPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION);
    }

    public boolean checkRecordingPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            requestRecordingPermission();
            return false;
        }
        return true;
    }

    private String getRecordingFilePath()
    {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File music = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File (music, "testFile" + ".mp3");
        return file.getPath();
    }

    private void showAudioDialog(){
        if (dialogAudioRecord == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateTaskActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_audio_dialoug,
                    (ViewGroup) findViewById(R.id.layoutAudioContainer)
            );
            builder.setView(view);
            dialogAudioRecord = builder.create();
            if (dialogAudioRecord.getWindow() != null) {
                dialogAudioRecord.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.audioRecord).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                CreateTaskActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                REQUEST_CODE_STORAGE_PERMISSION
                        );

                    } else {
                        if (!isRecording){
                            isRecording = true;
                            mediaRecorder = new MediaRecorder();
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            mediaRecorder.setOutputFile(getRecordingFilePath());
                            audioFilePath = getRecordingFilePath();
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            try {
                                mediaRecorder.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaRecorder.start();
                        }
                        else {
                            mediaRecorder.stop();
                            mediaRecorder.release();
                            mediaRecorder=null;
                        }

                    }

                }
            });
            view.findViewById(R.id.audioPlay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isPlaying){
                        if (audioFilePath!=null)
                        {
                            try {
                                mediaPlayer.setDataSource(getRecordingFilePath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {

                            Toast.makeText(getApplicationContext(), "No Recording", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mediaPlayer.start();
                        isPlaying = true;
                    }

                    else {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        //  mediaPlayer = null;
                        // mediaPlayer = new MediaPlayer();
                        isPlaying = false;
                    }

                }
            });
            view.findViewById(R.id.textDismiss).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAudioRecord.dismiss();
                }
            });
        }
        dialogAudioRecord.show();
    }



    private void showDeleteDialog() {
        if (dialogDeleteTask == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateTaskActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_dialoug,
                    (ViewGroup) findViewById(R.id.layoutDeleteTaskContainer)
            );
            builder.setView(view);
            dialogDeleteTask = builder.create();
            if (dialogDeleteTask.getWindow() != null) {
                dialogDeleteTask.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteTask).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteTaskFunc extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            TableTaskDB.getDatabase(getApplicationContext()).tableTaskDao()
                                    .deleteTask(alreadyAvailableTableTask);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent = new Intent();
                            intent.putExtra("isTaskDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteTaskFunc().execute();
                }
            });

            view.findViewById(R.id.textCancelDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteTask.dismiss();
                }
            });
        }
        dialogDeleteTask.show();
    }

    private void setCategoryIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewCategoryIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedTaskColor));
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_AUDIO_PERMISSION){
            if (grantResults.length > 0){
                boolean permissionToRecord=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if (permissionToRecord){

                    Toast.makeText(this, "Permission Given", Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageTableTask.setImageBitmap(bitmap);
                        imageTableTask.setVisibility(View.VISIBLE);
                        selectedImageBase64 = encode(selectedImageUri);
//                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    // Checking if the task list is empty , which indicates that the app just started since we have
    // Declared it as a global variable
    // But for this case we are adding all the notes from the database and notify the adapter about
    // The new loaded Dataset
    private void getTask (final int requestCode, final boolean isSubTaskDeleted) {

        @SuppressLint("StaticFieldLeak")
        class GetTask_HS extends AsyncTask<Void, Void, List<TableSubTask>>{

            @Override
            protected List<TableSubTask> doInBackground(Void... voids) {

                return TableTaskDB
                        .getDatabase(getApplicationContext())
                        .tableTaskDao().getAllSubTask(alreadyAvailableTableTask.getId());
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<TableSubTask> tableSubTasks){
                super.onPostExecute(tableSubTasks);

                if (requestCode == REQUEST_CODE_SHOW_SUBTASKS){
                    tableSubTasksList.clear();
                    tableSubTasksList.addAll(tableSubTasks);
                    subTaskListAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_SUBTASK) {
                    tableSubTasks.add(0, tableSubTasks.get(0));
                    subTaskListAdapter.notifyItemInserted(0);
                    tasksRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_SUBTASK){
//                    tableSubTasksList.remove(taskClickedPosition);
//
//                    if (isTaskDeleted){
//                        tableTaskAdapters.notifyItemRemoved(taskClickedPosition);
//                    } else{
//                        tableTasksList.add(taskClickedPosition, tableTasks.get(taskClickedPosition));
//                        tableTaskAdapters.notifyItemChanged(taskClickedPosition);
//
//                    }
                }
                Log.d("My_TableTasks", tableSubTasksList.toString());
            }
        }
        new GetTask_HS().execute();

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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                subTaskListAdapter.removeItem(position, CreateTaskActivity.this);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(tasksRecyclerView);
    }

    @Override
    public void onTableSubTaskClicked(TableSubTask tableTask, int position) {
        // TODO: 20/06/2022
        Log.d("SUB_TASK", "Editing:" + tableTask.getStatus());
        subTaskListAdapter.updateSubtask(position, CreateTaskActivity.this);

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        // set datepicker after clicking the user clicks the picker
        this.dueDatePicker = datePicker;
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        String dateTime = dateFormat.format(calendar.getTime());
        tvDueDate.setText(dateTime);
    }
}