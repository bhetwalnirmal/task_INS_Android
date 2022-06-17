package com.nirmalbhetwal.task_ins_android.Models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class TaskWithChecklistAndImages {
    @Embedded
    Task task;

    @Relation(parentColumn = "id", entityColumn = "task_id")
    private List<Checklist> checklists;

    public TaskWithChecklistAndImages(Task task, List<Checklist> checklists) {
        this.task = task;
        this.checklists = checklists;
    }
//    @Relation(entity = Checklist.class, parentColumn = "id", entityColumn = "task_id")
//    private ArrayList<TaskImage> images;
//
//    public Task getTask() {
//        return task;
//    }
//
//    public void setTask(Task task) {
//        this.task = task;
//    }
//
//    public ArrayList<Checklist> getChecklists() {
//        return checklists;
//    }
//
//    public void setChecklists(ArrayList<Checklist> checklists) {
//        this.checklists = checklists;
//    }
//
//    public ArrayList<TaskImage> getImages() {
//        return images;
//    }
//
//    public void setImages(ArrayList<TaskImage> images) {
//        this.images = images;
//    }
}
