package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class holds data for all tasks.
 * Handles loading and saving using JSON and Shared Preferences.
 */
public class TaskManager {
  private static final Gson GSON = new Gson();
  private static final String TASKS_JSON_KEY = "tasksJson";

  private static TaskManager instance;
  private static SharedPreferences prefs;

  private static ArrayList<Task> tasks = new ArrayList<>();

  private TaskManager(Context context) {
    TaskManager.prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static TaskManager getInstance(Context context) {
    if (instance == null) {
      TaskManager.instance = new TaskManager(context);
    }

    loadSavedData();
    return instance;
  }

  private static void loadSavedData() {
    String tasksJson = prefs.getString(TASKS_JSON_KEY, null);

    if (tasksJson != null) {
      Type tasksType = new TypeToken<ArrayList<Task>>() {
      }.getType();

      TaskManager.tasks = GSON.fromJson(tasksJson, tasksType);
    }
  }

  private void saveData() {
    String tasksJson = GSON.toJson(tasks);
    prefs.edit().putString(TASKS_JSON_KEY, tasksJson).apply();
  }

  public void addTask(String taskName, UUID childUniqueID) {
    tasks.add(new Task(taskName, childUniqueID));
    saveData();
  }

  public void addToAllTasks(UUID childUniqueID) {
    for (int i = 0; i < tasks.size(); i++) {
      tasks.get(i).setChildUniqueID(childUniqueID);
    }

    saveData();
  }

  public void updateTasksBeforeRemovingChild(UUID childUniqueID, UUID nextChildUniqueID) {
    for (int i = 0; i < tasks.size(); i++) {
      if (childUniqueID.equals(tasks.get(i).getChildUniqueID())) {
        tasks.get(i).setChildUniqueID(nextChildUniqueID);
      }
    }

    saveData();
  }

  public void removeTask(int taskIndex) {
    tasks.remove(taskIndex);
    saveData();
  }

  public void setTaskName(int taskIndex, String taskName) {
    tasks.get(taskIndex).setTaskName(taskName);
    saveData();
  }

  public void setChildUniqueID(int taskIndex, UUID childUniqueID) {
    tasks.get(taskIndex).setChildUniqueID(childUniqueID);
    saveData();
  }

  public ArrayList<Task> getTasks() {
    return tasks;
  }
}
