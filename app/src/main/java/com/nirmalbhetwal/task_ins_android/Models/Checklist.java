package com.nirmalbhetwal.task_ins_android.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "checklists")
public class Checklist implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NotNull
    @ColumnInfo(name = "task_id")
    private int taskId;
    @NotNull
    @ColumnInfo(name = "name")
    private String name;
    @Nullable
    @ColumnInfo(name = "completed_date")
    private Date completedDate;
    @Nullable
    @ColumnInfo(name = "created_date")
    private Date createdDate;
}
