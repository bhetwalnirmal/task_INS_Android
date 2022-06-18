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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nirmalbhetwal.task_ins_android.R;
import com.nirmalbhetwal.task_ins_android.database.TableTaskDB;
import com.nirmalbhetwal.task_ins_android.entities.TableTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {

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
    private Spinner taskSpinner;
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
    private boolean isPlaving=false;
    private static final int REQUEST_AUDIO_PERMISSION = 101;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private static final int GALLERY_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(v -> saveTask());

        inputTaskTitle = findViewById(R.id.inputTaskTitle);
        inputTaskCat = findViewById(R.id.inputTaskCategory);
        inputTaskDesc = findViewById(R.id.inputTaskDesc);
        textCreateDateTime = findViewById(R.id.textCreateDateTime);
        viewCategoryIndicator = findViewById(R.id.viewCategoryIndicator);
        imageTableTask = findViewById(R.id.imageTask);


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
        setCategoryIndicatorColor();
    }

    private void setViewOrUpdateTableTask() {
        inputTaskTitle.setText(alreadyAvailableTableTask.getTitle());
        inputTaskDesc.setText(alreadyAvailableTableTask.getTaskText());
        inputTaskCat.setText(alreadyAvailableTableTask.getCategory());

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

    private void initMore() {

        final ImageView imagePriorityLow = findViewById(R.id.imagePriorityLow);
        final ImageView imagePriorityMedium = findViewById(R.id.imagePriorityMedium);
        final ImageView imagePriorityHigh = findViewById(R.id.imagePriorityHigh);

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

    }}