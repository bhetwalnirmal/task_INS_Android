package com.nirmalbhetwal.task_ins_android.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.nirmalbhetwal.task_ins_android.Models.Task;
import com.nirmalbhetwal.task_ins_android.Models.TaskImage;

import java.util.List;

@Dao
public interface TaskImageDao {
    @Query("Select * from task_images")
    List<TaskImage> getTaskImages();

    @Insert
    long insertTask(TaskImage taskImage);
}
