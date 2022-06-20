package com.nirmalbhetwal.task_ins_android.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.nirmalbhetwal.task_ins_android.model.TableSubTask;
import com.nirmalbhetwal.task_ins_android.model.TableTask;

import java.util.List;

@Dao
public interface TableTaskDao {

    @Query("SELECT * FROM tableTask")
    List<TableTask> getAllTableTask();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTableTask (TableTask tableTask);

    @Delete
    void deleteTask(TableTask tableTask);

    @Query("SELECT * FROM tableSubTask where category = :catId")
    List<TableSubTask> getAllSubTask(int catId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubTaskTable (TableSubTask tableSubTask);

    @Delete
    void deleteSubTask(TableSubTask tableSubTask);



}