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
}
