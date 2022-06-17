package com.nirmalbhetwal.task_ins_android.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = ("subtasks"))
public class SubTaskTable implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int subTaskId;

    @ColumnInfo(name= "title")
    private String title;


    @ColumnInfo(name = "completed")
    private String completed;

    @ForeignKey
            (entity = TaskTable.class,
                    parentColumns = "id_task",
                    childColumns = "id_fktask",
                    onDelete = CASCADE
            )


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


    public String getCompleted() {
        return completed;
    }

    public void setCompleted(int subTaskId) {
        this.subTaskId = subTaskId;
    }

    public int getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(int subTaskId) {
        this.subTaskId = subTaskId;
    }
}
