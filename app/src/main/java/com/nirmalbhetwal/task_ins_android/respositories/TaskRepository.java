package com.nirmalbhetwal.task_ins_android.respositories;

import com.nirmalbhetwal.task_ins_android.Models.Task;
import com.nirmalbhetwal.task_ins_android.daos.TaskDao;

public class TaskRepository {
    TaskDao taskDao;

    public void insertTask(Task task) {
        this.getRepository().insertTask(task);
    }

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public TaskDao getRepository() {
        return taskDao;
    }
}
