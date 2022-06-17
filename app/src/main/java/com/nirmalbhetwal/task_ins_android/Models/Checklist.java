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

    public Checklist(int id, int taskId, @NotNull String name, @Nullable Date completedDate, @Nullable Date createdDate) {
        this.id = id;
        this.taskId = taskId;
        this.name = name;
        this.completedDate = completedDate;
        this.createdDate = createdDate;
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
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Nullable
    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(@Nullable Date completedDate) {
        this.completedDate = completedDate;
    }

    @Nullable
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nullable Date createdDate) {
        this.createdDate = createdDate;
    }
}
