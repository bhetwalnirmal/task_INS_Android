package com.nirmalbhetwal.task_ins_android.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = ("tableTask"))
public class TableTask implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name= "title")
    private String title;

    @ColumnInfo(name = "create_date")
    private String createDate;

    @ColumnInfo(name = "create_time")
    private String createTime;

    @ColumnInfo(name = "due_date")
    private String dueDate;

    @ColumnInfo(name = "due_time")
    private String dueTime;


    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "sub_task")
    private String[] subTask;

    @ColumnInfo(name = "image_path")
    private byte[] imagePath;

    @ColumnInfo(name = "audio_path")
    private String audioPath;

    @ColumnInfo(name = "task_status")
    private String taskStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String[] getSubTask() {
        return subTask;
    }

    public void setSubTask(String[] subTask) {
        this.subTask = subTask;
    }

    public byte[] getImagePath() {
        return imagePath;
    }

    public void setImagePath(byte[] imagePath) {
        this.imagePath = imagePath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}