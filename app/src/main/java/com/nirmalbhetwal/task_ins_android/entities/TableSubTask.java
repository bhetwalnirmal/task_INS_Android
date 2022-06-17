package com.nirmalbhetwal.task_ins_android.entities;


import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = ("tableSubTask"), foreignKeys = @ForeignKey (entity = TableTask.class,
        parentColumns = "id_task",
        childColumns = "id_fksubtask",
        onDelete = CASCADE
))
public class TableSubTask implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id_subtask;

    @ColumnInfo(name= "title")
    private String title;

    @ColumnInfo(name = "task_status")
    private Boolean taskStatus;

    public int getId_subtask() {
        return id_subtask;
    }

    public void setId_subtask(int id_subtask) {
        this.id_subtask = id_subtask;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Boolean taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getId_fksubtask() {
        return id_fksubtask;
    }

    public void setId_fksubtask(int id_fksubtask) {
        this.id_fksubtask = id_fksubtask;
    }

    private int id_fksubtask;

}
