package com.nirmalbhetwal.task_ins_android.model;

public class Task {
    private int taskId;
    private int categoryId;
    private String taskName;
    private String taskStatus;
    private String createdDate;
    private String taskColor;
    private String taskPriority;

    public Task(int taskId, int categoryId, String taskName, String taskStatus, String createdDate, String taskColor, String taskPriority) {
        this.taskId = taskId;
        this.categoryId = categoryId;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.createdDate = createdDate;
        this.taskColor = taskColor;
        this.taskPriority = taskPriority;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getTaskColor() {
        return taskColor;
    }

    public void setTaskColor(String taskColor) {
        this.taskColor = taskColor;
    }

    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }
}
