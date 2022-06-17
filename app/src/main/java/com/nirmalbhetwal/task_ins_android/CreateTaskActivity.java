package com.nirmalbhetwal.task_ins_android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nirmalbhetwal.task_ins_android.Models.Task;
import com.nirmalbhetwal.task_ins_android.abstracts.TaskDatabase;
import com.nirmalbhetwal.task_ins_android.daos.TaskDao;
import com.nirmalbhetwal.task_ins_android.respositories.TaskRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE = 1;
    LinearLayout lllayoutAddImage;
    ImageView ivSaveTask, ivBackButton, ivLowPriority, ivMediumPriority, ivHighPriority;
    TaskRepository taskRepository;
    CoordinatorLayout clBaseLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // initialized the variables
        lllayoutAddImage = (LinearLayout) findViewById(R.id.layoutAddImage);
        ivBackButton = (ImageView) findViewById(R.id.ivBack);
        ivSaveTask = (ImageView) findViewById(R.id.ivSave);
        ivLowPriority = (ImageView) findViewById(R.id.ivLowPriority);
        ivMediumPriority = (ImageView) findViewById(R.id.ivMediumPriority);
        ivHighPriority = (ImageView) findViewById(R.id.ivHighPriority);
        clBaseLayout = (CoordinatorLayout) findViewById(R.id.clBaseLayout);

        ivBackButton.setOnClickListener(this);
        ivSaveTask.setOnClickListener(this);
        ivLowPriority.setOnClickListener(this);
        ivMediumPriority.setOnClickListener(this);
        ivHighPriority.setOnClickListener(this);

        //
        TaskDatabase.getInstance(this).getRepository().insertTask(new Task("asdf", "sdf", 1, "ssdf", new Date()));

        List<Task> tasks = TaskDatabase.getInstance(this).getRepository().getTasks();

        for (Task task : tasks) {
            Log.d("TAG_", " " + task.getName());
        }

        lllayoutAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ArrayList<String> imagesEncodedList = new ArrayList<String>();

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        for(Uri uri : mArrayUri) {
                            Log.d("LOG_TAG", String.valueOf(uri));
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ivLowPriority:
                clBaseLayout.setBackgroundColor(Color.parseColor(Constants.colorLowPriority));
                break;
            case R.id.ivMediumPriority:
                clBaseLayout.setBackgroundColor(Color.parseColor(Constants.colorMediumPriority));
                break;
            case R.id.ivHighPriority:
                clBaseLayout.setBackgroundColor(Color.parseColor(Constants.colorHighPriority));
                break;
            default:
                break;
        }
    }
}