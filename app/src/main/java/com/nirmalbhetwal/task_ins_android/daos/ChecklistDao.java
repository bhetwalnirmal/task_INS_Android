package com.nirmalbhetwal.task_ins_android.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.nirmalbhetwal.task_ins_android.Models.Checklist;
import com.nirmalbhetwal.task_ins_android.Models.Task;

import java.util.List;

@Dao
public interface ChecklistDao {
    @Query("Select * from checklists")
    List<Task> getTasks();

    @Insert
    void insertChecklist(Checklist checklist);
}
