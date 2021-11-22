package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;


public class Tasks {
    private static final Tasks instance = new Tasks();
    private static final String TASK_LIST = "taskName";
    private static final String TASK_PREF = "taskPref";
    private static final String CHILD_LIST = "childName";
    private static final String CHILD_PREF = "childPref";
    private static SharedPreferences prefs, childPrefs;
    private static final Gson GSON = new Gson();
    private static ArrayList<String> listOfTasks = new ArrayList<>();
    private static ArrayList<Child> childListForTasks = new ArrayList<>();

    public void saveTaskChildData(Context context) {
        String json = GSON.toJson(listOfTasks);
        String jsonChild = GSON.toJson(childListForTasks);
        prefs.edit().putString(TASK_PREF, json).apply();
        childPrefs.edit().putString(CHILD_PREF, jsonChild).apply();
    }

    public void loadTaskData(Context context) {
        Tasks.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(TASK_PREF, null);

        if(json != null){
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Tasks.listOfTasks = GSON.fromJson(json, type);
        }
    }

    public void loadChildData(Context context){
        Tasks.childPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonChild = childPrefs.getString(CHILD_PREF, null);

        if(jsonChild != null){
            Type type = new TypeToken<ArrayList<Child>>() {
            }.getType();
            Tasks.childListForTasks = GSON.fromJson(jsonChild, type);
        }
    }

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
        if (childListForTasks.size() == 0) {
            return;
        }
        childListForTasks.remove(index);
    }

    public void clearChildList() {
        for (int i = 0; i < childListForTasks.size(); i++) {
            removeChild(i);
        }
    }

    public UUID getChildID(int index) {
        if (index >= childListForTasks.size()) {
            index = 0;
        }
        return childListForTasks.get(index).getUniqueID();
    }

    public void updateChild(UUID Id, Child child, int size) {
        if (size == 1) {
            for (int i = 0; i < childListForTasks.size(); i++) {
                removeChild(i);
            }
            return;
        }
        for (int i = 0; i < childListForTasks.size(); i++) {
            if (childListForTasks.get(i).getUniqueID().equals(Id)) {
                childListForTasks.set(i, child);
            }
        }
    }

    public void editChild(int index, Child newName) {
        if (index >= childListForTasks.size()) {
            index = 0;
        }
        childListForTasks.set(index, newName);
    }

    public ArrayList<Child> getListOfChildren() {
        return childListForTasks;
    }
}
