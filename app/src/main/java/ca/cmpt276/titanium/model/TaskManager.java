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
 * Loads, saves, and interacts with a group of Task objects.
 *
 * @author Titanium
 */
public class TaskManager {
    private static final Gson GSON = new Gson();
    private static final String TASKS_JSON_KEY = "tasksJson";
    private static final String HISTORY_TASKS_JSON_KEY = "tasksHistoryJson";

    private static TaskManager instance;
    private static SharedPreferences prefs;

    private static ArrayList<Task> tasks = new ArrayList<>();
    private static ArrayList<TaskHistory> taskHistory = new ArrayList<>();

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
        String taskHistoryJson = prefs.getString(HISTORY_TASKS_JSON_KEY, null);

        if (tasksJson != null) {
            Type tasksType = new TypeToken<ArrayList<Task>>() {
            }.getType();

            TaskManager.tasks = GSON.fromJson(tasksJson, tasksType);
        }

        if (taskHistoryJson != null) {
            Type taskHistoryType = new TypeToken<ArrayList<TaskHistory>>() {
            }.getType();

            TaskManager.taskHistory = GSON.fromJson(taskHistoryJson, taskHistoryType);
        }
    }

    private void saveData() {
        String tasksJson = GSON.toJson(tasks);
        prefs.edit().putString(TASKS_JSON_KEY, tasksJson).apply();
    }

    private void saveHistoryData() {
        String taskHistoryJson = GSON.toJson(taskHistory);
        prefs.edit().putString(HISTORY_TASKS_JSON_KEY, taskHistoryJson).apply();
    }

    public void addTask(String taskName, UUID childUniqueID) {
        tasks.add(new Task(taskName, childUniqueID));
        saveData();
    }

    public void addHistoryTask(String historyTaskName, UUID childUniqueID) {
        taskHistory.add(new TaskHistory(historyTaskName, childUniqueID));
        saveHistoryData();
    }

    public void addToAllTasks(UUID childUniqueID) {
        for (Task task : tasks) {
            task.setChildUniqueID(childUniqueID);
        }
        saveData();
    }

    public void updateTaskQueues(UUID childUniqueID, UUID nextChildUniqueID) {
        for (Task task : tasks) {
            if (childUniqueID.equals(task.getChildUniqueID())) {
                task.setChildUniqueID(nextChildUniqueID);
            }
        }
        saveData();
    }

    public void childDeletedFromHistory(UUID childUniqueID) {
        taskHistory.removeIf(task -> childUniqueID.equals(task.getChildUniqueID()));
        saveHistoryData();
    }

    public void taskDeletedFromHistory(String historyTaskName){
        taskHistory.removeIf(task -> historyTaskName.equals(task.getHistoryTaskName()));
        saveHistoryData();
    }

    public void editTaskFromHistory(String oldTask, String newTask){
        for(TaskHistory task : taskHistory){
            if(oldTask.equals(task.getHistoryTaskName())){
                task.setHistoryTaskName(newTask);
            }
        }
        saveHistoryData();
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

    public ArrayList<TaskHistory> getTaskHistory() {
        return taskHistory;
    }

    public ArrayList<TaskHistory> createListForSpecificTask(String taskName) {
        System.out.println(taskName);
        ArrayList<TaskHistory> dataHistoryOfTask = new ArrayList<>();

        if (taskHistory.size() != 0) {
            for (TaskHistory task : taskHistory) {
                if (task.getHistoryTaskName().equals(taskName)) {
                    dataHistoryOfTask.add(task);
                }
            }
        }
        return dataHistoryOfTask;
    }
}
