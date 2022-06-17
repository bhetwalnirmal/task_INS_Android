package com.nirmalbhetwal.task_ins_android.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nirmalbhetwal.task_ins_android.dao.TaskTableDao;
import com.nirmalbhetwal.task_ins_android.entities.TaskTable;

@Database(entities = {TaskTable.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {
    private static TaskDatabase taskDatabase;

    public static synchronized TaskDatabase getDatabase(Context context){
        if (taskDatabase == null){
            taskDatabase = Room.databaseBuilder(
                    context,
                    TaskDatabase.class,
                    "tasks_db"
            ).build();
        }
        return taskDatabase;
    }

    public abstract TaskTableDao taskDao();

}
