package com.nirmalbhetwal.task_ins_android.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.nirmalbhetwal.task_ins_android.Models.Checklist;
import com.nirmalbhetwal.task_ins_android.Models.Task;
import com.nirmalbhetwal.task_ins_android.Models.TaskWithChecklistAndImages;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("Select * from tasks")
    List<Task> getTasks();

    @Insert
    long insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("Select * from tasks where id = :id" )
    Task find(int id);

//    @Query("Select * from tasks")
//    List<TaskWithChecklistAndImages> getTaskWithChecklistAndImages();
}
