package com.nirmalbhetwal.task_ins_android.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "tasks")
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NotNull
    @ColumnInfo(name = "name")
    private String name;
    @NotNull
    @ColumnInfo(name = "priority")
    private int priority;
    @Nullable
    @ColumnInfo(name = "audio_name")
    private String audioName;
    private ArrayList<Checklist> checklists;
    private ArrayList<TaskImage> images;
}
