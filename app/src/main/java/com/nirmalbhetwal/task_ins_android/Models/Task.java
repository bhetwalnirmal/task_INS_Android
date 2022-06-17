package com.nirmalbhetwal.task_ins_android.Models;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "tasks")
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NotNull
    @ColumnInfo(name = "name")
    private String name;
    @NotNull
    @ColumnInfo(name = "category")
    private String category;
    @NotNull
    @ColumnInfo(name = "priority")
    private int priority;
    @Nullable
    @ColumnInfo(name = "audio_name")
    private String audioName;

    @Nullable
    @ColumnInfo(name = "completed_date")
    private Date completedDate;

    public Task(@NotNull String name, @NotNull String category, int priority, @Nullable String audioName, @Nullable Date completedDate) {
        this.name = name;
        this.category = category;
        this.priority = priority;
        this.audioName = audioName;
        this.completedDate = completedDate;
    }

    @NotNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NotNull String category) {
        this.category = category;
    }

    @Nullable
    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(@Nullable Date completedDate) {
        this.completedDate = completedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Nullable
    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(@Nullable String audioName) {
        this.audioName = audioName;
    }
}
