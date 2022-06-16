package com.nirmalbhetwal.task_ins_android.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nirmalbhetwal.task_ins_android.model.Task;

@Database(entities = {Task.class}, version = 2, exportSchema = false)
public abstract class TaskDB extends RoomDatabase {

    private static TaskDB taskDB;

    public static synchronized TaskDB getDatabase(Context context) {
        if (taskDB == null) {
            taskDB = Room.databaseBuilder(
                    context,
                    TaskDB.class,
                    "table_task"
            ).build();
        }
        return taskDB;
    }

    public abstract TaskDao taskDao();

}
