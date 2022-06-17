package com.nirmalbhetwal.task_ins_android.abstracts;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.nirmalbhetwal.task_ins_android.Models.Task;
import com.nirmalbhetwal.task_ins_android.converters.Converters;
import com.nirmalbhetwal.task_ins_android.daos.TaskDao;

@Database(entities = Task.class, exportSchema = false, version = 1)
@TypeConverters(Converters.class)
public abstract class TaskDatabase extends RoomDatabase {
    private static final String DB_NAME = "tasks_db";
    private static TaskDatabase instance;

    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TaskDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    public abstract TaskDao TaskDao();

    public TaskDao getRepository () {
        return TaskDao();
    }
}
