package ca.cmpt276.titanium.model;

import java.util.ArrayList;


public class Tasks {
    private final ArrayList<String> listOfTasks = new ArrayList<>();
    private final ArrayList<Child> childListForTasks = new ArrayList<>();

    private static final Tasks instance = new Tasks();

    private Tasks() {
    }

    public static Tasks getInstance() {
        return instance;
    }

    public void addTask(String task) {
        listOfTasks.add(task);
    }

    public void removeTask(int index) {
        listOfTasks.remove(index);
    }

    public void editTask(int index, String newTask) {
        listOfTasks.set(index, newTask);
    }

    public String getTask(int index) {
        return listOfTasks.get(index);
    }

    public int numberOfTasks() {
        return listOfTasks.size();
    }

    public ArrayList<String> getListOfTasks() {
        return listOfTasks;
    }

    public void addChild(Child child) {
        childListForTasks.add(child);
    }

    public void removeChild(int index) {
        childListForTasks.remove(index);
    }

    public void nextChild(int index, Child nextChild) {
        childListForTasks.set(index, nextChild);
    }

    public String getChild(int index) {
        return childListForTasks.get(index).getName();
    }

    public void editChild(int index, Child newName) {
        childListForTasks.set(index, newName);
    }

    public ArrayList<Child> getListOfChildren() {
        return childListForTasks;
    }
}
