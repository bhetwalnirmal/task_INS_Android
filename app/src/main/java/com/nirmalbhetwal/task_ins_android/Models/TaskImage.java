package com.nirmalbhetwal.task_ins_android.Models;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class TaskImage implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NotNull
    @ColumnInfo(name = "task_id")
    private int taskId;
    @NotNull
    @ColumnInfo(name = "image_name")
    private String imageName;

    public TaskImage(int id, int taskId, @NotNull String imageName) {
        this.id = id;
        this.taskId = taskId;
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @NotNull
    public String getImageName() {
        return imageName;
    }

    public void setImageName(@NotNull String imageName) {
        this.imageName = imageName;
    }
}
