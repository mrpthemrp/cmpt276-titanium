package ca.cmpt276.titanium.model;

import java.util.ArrayList;

public class Tasks {
    private final ArrayList<String> listOfTasks = new ArrayList<>();

    public void addTask(String task){
        listOfTasks.add(task);
    }

    public void removeTask(int index){
        listOfTasks.remove(index);
    }

    public void editTask(int index, String newTask){
        listOfTasks.set(index, newTask);
    }

    public String getTask(int index){
        return listOfTasks.get(index);
    }
}
