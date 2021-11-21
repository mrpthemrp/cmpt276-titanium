package ca.cmpt276.titanium.model;

import java.util.ArrayList;
import java.util.UUID;


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
        if(childListForTasks.size() == 0){
            return;
        }
        childListForTasks.remove(index);
    }

    public void clearChildList(){
        for(int i = 0; i < childListForTasks.size(); i++){
            removeChild(i);
        }
    }

    public UUID getChildID(int index){
        if(index >= childListForTasks.size()){
            index = 0;
        }
        return childListForTasks.get(index).getUniqueID();
    }

    public void updateChild(UUID Id, Child child, int size){
        if(size == 1){
            for(int i = 0; i < childListForTasks.size(); i++){
                removeChild(i);
            }
            return;
        }
        for(int i = 0; i < childListForTasks.size(); i++){
            if(childListForTasks.get(i).getUniqueID().equals(Id)){
                childListForTasks.set(i, child);
            }
        }
    }

    public void editChild(int index, Child newName) {
        if(index >= childListForTasks.size()){
            index = 0;
        }
        childListForTasks.set(index, newName);
    }

    public ArrayList<Child> getListOfChildren() {
        return childListForTasks;
    }
}
