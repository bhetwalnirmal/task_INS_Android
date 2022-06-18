package com.nirmalbhetwal.task_ins_android.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nirmalbhetwal.task_ins_android.entities.TableTask;


@Database(entities = {TableTask.class}, version = 2, exportSchema = false)
public abstract class TableTaskDB extends RoomDatabase {

    private static TableTaskDB tableTaskDB;

    public static synchronized TableTaskDB getDatabase(Context context) {
        if (tableTaskDB == null) {
            tableTaskDB = Room.databaseBuilder(
                    context,
                    TableTaskDB.class,
                    "table_task_DB"
            ).build();
        }
        return tableTaskDB;
    }

    public abstract TableTaskDao tableTaskDao();

}