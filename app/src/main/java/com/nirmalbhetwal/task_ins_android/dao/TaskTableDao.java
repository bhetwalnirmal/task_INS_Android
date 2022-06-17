package com.nirmalbhetwal.task_ins_android.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.nirmalbhetwal.task_ins_android.entities.TaskTable;

import java.util.List;

@Dao
public interface TaskTableDao {

    @Query("SELECT * FROM tasks")
    List<TaskTable> getAllTableTask();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTableTask (TaskTable tableTask);

    @Delete
    void deleteTask(TaskTable tableTask);
}