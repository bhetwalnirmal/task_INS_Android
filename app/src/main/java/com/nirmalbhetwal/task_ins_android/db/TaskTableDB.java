package com.nirmalbhetwal.task_ins_android.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.friends.task_friends_android.dao.TableTaskDao;
import com.friends.task_friends_android.entities.TableTask;
import com.nirmalbhetwal.task_ins_android.dao.TaskTableDao;
import com.nirmalbhetwal.task_ins_android.entities.TaskTable;

@Database(entities = {TaskTable.class}, version = 2, exportSchema = false)
public abstract class TaskTableDB extends RoomDatabase {

    private static TaskTableDB taskTableDb;

    public static synchronized TaskTableDB getDatabase(Context context) {
        if (taskTableDb == null) {
            taskTableDb = Room.databaseBuilder(
                    context,
                    TaskTableDB.class,
                    "tasks"
            ).build();
        }
        return taskTableDb;
    }

    public abstract TaskTableDao tableTaskDao();

}