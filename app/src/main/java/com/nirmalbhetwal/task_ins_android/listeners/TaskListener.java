package com.nirmalbhetwal.task_ins_android.listeners;

import com.nirmalbhetwal.task_ins_android.model.Task;

public interface TaskListener {
    void onTableTaskClicked(Task task, int position);
}