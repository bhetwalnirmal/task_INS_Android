package com.nirmalbhetwal.task_ins_android.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContextWrapper;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.nirmalbhetwal.task_ins_android.Categories;
import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.TaskCompleted;
import com.nirmalbhetwal.task_ins_android.db.TableTaskDB;
import com.nirmalbhetwal.task_ins_android.entities.TableTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateTaskActivity  extends AppCompatActivity {
    private EditText inputTaskTitle, inputTaskDesc ,inputTaskCat;
    private TextView textCreateDateTime;
    private View viewCategoryIndicator;
    private String selectedTaskColor;
    private ImageView imageTableTask;
    private String selectedImageBase64;
    private TableTask alreadyAvailableTableTask;
    private AlertDialog dialogDeleteTask;
    private AlertDialog dialogAudioRecord;
    private Spinner spinner;
    private Categories selectedCategory;
    private Spinner taskSpinner;
    private String audioFilePath;
    private TaskCompleted taskCompleted;
    private String taskProgress;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private ImageView ibRecord;
    private ImageView ibPlay;
    private TextView tvTime;
    private TextView tvRecordingPath;
    private ImageView ivSimpleBq;
    private boolean isRecording=false;
    private boolean isPlaving=false;
    private static final int REQUEST_AUDIO_PERMISSION = 101;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private static final int GALLERY_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        TaskCompleted.initCompleted();
        Categories.initCategories();

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());


        inputTaskTitle = findViewById(R.id.inputTaskTitle);
        inputTaskCat = findViewById(R.id.inputTaskCategory);
        inputTaskDesc = findViewById(R.id.inputTaskDesc);
        textCreateDateTime = findViewById(R.id.textCreateDateTime);
        viewCategoryIndicator = findViewById(R.id.viewCategoryIndicator);


        textCreateDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        selectedTaskColor = "#333333";
        selectedImageBase64 = "";
        taskProgress = "Incomplete";



        if (getIntent().getBooleanExtra("isViewUpdate", false)) {
            alreadyAvailableTableTask = (TableTask) getIntent().getSerializableExtra("tableTask");
            setViewOrUpdateTableTask();
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
//        setCategoryIndicatorColor();
    }

    private void setViewOrUpdateTableTask() {
        inputTaskTitle.setText(alreadyAvailableTableTask.getTitle());
        inputTaskDesc.setText(alreadyAvailableTableTask.getTaskDesc());
        inputTaskCat.setText(alreadyAvailableTableTask.getCategory());

        ArrayList<TaskCompleted> taskCompleteds = TaskCompleted.getTaskCompletedArrayList();
        for (TaskCompleted taskCompleted : taskCompleteds) {
            if (taskCompleted.getCompleted().equals(alreadyAvailableTableTask.getTaskStatus())){
                this.taskCompleted = taskCompleted;
            }
        }
        if (taskCompleted != null){
            taskSpinner.setSelection(taskCompleteds.indexOf(selectedCategory));
            taskSpinner.setVisibility(View.VISIBLE);

        }else
        {
            taskSpinner.setSelection(1);
            taskSpinner.setVisibility(View.VISIBLE);
        }
/*
        ArrayList<Categories> categories = Categories.getCategoriesArrayList();
        for (Categories category : categories) {
            if (category.getCatName().equals(alreadyAvailableTableTask.getCategory())) {
                this.selectedCategory = category;
            }
        }
        if (selectedCategory != null) {
            spinner.setSelection(categories.indexOf(selectedCategory));
        } else {
            spinner.setSelection(0);
        }
*/
        textCreateDateTime.setText(alreadyAvailableTableTask.getCreateDate());
        // TODO: 17/06/2022 Here we are taking just one image
        if (alreadyAvailableTableTask.getImagePath() != null && !alreadyAvailableTableTask.getImagePath().split(",")[0].trim().isEmpty()) {
            // decode base64 string
            // TODO: 17/06/2022 Here we are taking just one image
            byte[] bytes = Base64.decode(alreadyAvailableTableTask.getImagePath().split(",")[0], Base64.DEFAULT);
            // Initialize bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // set bitmap on imageView
            imageTableTask.setImageBitmap(bitmap);
            imageTableTask.setVisibility(View.VISIBLE);
            // TODO: 17/06/2022 Here we are taking just one image
            selectedImageBase64 = alreadyAvailableTableTask.getImagePath().split(",")[0];
        }
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
        tableTask.setTaskDesc(inputTaskDesc.getText().toString());
        tableTask.setCreateDate(textCreateDateTime.getText().toString());
        // TODO: 17/06/2022 please pass string[] value uncomment below line
//        tableTask.setImagePath(selectedImageBase64);
        tableTask.setTaskStatus(taskProgress);

        if (alreadyAvailableTableTask != null) {
            tableTask.setId_task(alreadyAvailableTableTask.getId_task());
        }

        // ROOM does not allow database operation on the main thread
        // Use of Async tableTask to bypass it.

        @SuppressLint("StaticFieldLeak")
        class SaveTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                TableTaskDB.getDatabase(getApplicationContext()).tableTaskDao().insertTableTask(tableTask);
                return null;

//                TaskDatabase.getTaskDatabase(getApplicationContext()).taskDao().insertTask(tableTask);
//                return null;
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

    private void initMore() {

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

        if (alreadyAvailableTableTask != null) {
            findViewById(R.id.layoutDeleteTask).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutDeleteTask).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog();
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
            });
            view.findViewById(R.id.audioPlay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isPlaving){
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
                        isPlaving = true;
                    }

                    else {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        //  mediaPlayer = null;
                        // mediaPlayer = new MediaPlayer();
                        isPlaving = false;
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


    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
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
                    // TODO: 17/06/2022 Handle multiple image selected 
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
}
