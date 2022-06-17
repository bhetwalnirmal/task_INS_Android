package com.nirmalbhetwal.task_ins_android.entities;

import static android.arch.persistence.room.ForeignKey.CASCADE;

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


    private int id_fksubtask;

}
