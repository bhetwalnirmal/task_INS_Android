package com.nirmalbhetwal.task_ins_android.Models;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class Task implements Serializable {
    private String name;
    private ArrayList<Checklist> checklists;
    private ArrayList<String> images;
    private String audioName;
}
