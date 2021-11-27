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
 * This class represents a group of children.
 * Handles loading and saving from JSON. Generates a unique ID for a child.
 */
public class ChildManager {
  private static final Gson GSON = new Gson();
  private static final String CHILDREN_JSON_KEY = "childrenJson";

  private static ChildManager instance;
  private static SharedPreferences prefs;
  private static CoinFlipManager coinFlipManager;
  private static ChildQueueManager childQueueManager;
  private static TaskManager taskManager;

  private static ArrayList<Child> children = new ArrayList<>();

  private ChildManager(Context context) {
    ChildManager.prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static ChildManager getInstance(Context context) {
    if (instance == null) {
      ChildManager.instance = new ChildManager(context);
      ChildManager.coinFlipManager = CoinFlipManager.getInstance(context);
      ChildManager.childQueueManager = ChildQueueManager.getInstance(context);
      ChildManager.taskManager = TaskManager.getInstance(context);
    }

    loadSavedData();
    return instance;
  }

  private static void loadSavedData() {
    String childrenJson = prefs.getString(CHILDREN_JSON_KEY, null);

    if (childrenJson != null) {
      Type childrenType = new TypeToken<ArrayList<Child>>() {
      }.getType();

      ChildManager.children = GSON.fromJson(childrenJson, childrenType);
    }
  }

  private void saveData() {
    String childrenJson = GSON.toJson(children);
    prefs.edit().putString(CHILDREN_JSON_KEY, childrenJson).apply();
  }

  public Child getChild(UUID uniqueID) {
    if (uniqueID != null) {
      for (int i = 0; i < children.size(); i++) {
        if (uniqueID.equals(children.get(i).getUniqueID())) {
          return children.get(i);
        }
      }
    }

    return null;
  }

  public void addChild(String name, String portraitPath) {
    Child newChild = new Child(name, portraitPath);

    ChildManager.children.add(newChild);
    saveData();

    coinFlipManager.updateCoinFlipHistory(true, newChild.getUniqueID());

    childQueueManager.addChild(newChild.getUniqueID());

    if (children.size() == 1) {
      taskManager.addToAllTasks(children.get(0).getUniqueID());
    }
  }

  public void removeChild(UUID uniqueID) {
    if (uniqueID != null) {
      for (int i = 0; i < children.size(); i++) {
        if (uniqueID.equals(children.get(i).getUniqueID())) {
          coinFlipManager.updateCoinFlipHistory(false, uniqueID);

          childQueueManager.removeChild(uniqueID);

          int childrenIndex = children.indexOf(getChild(uniqueID)) + 1;
          UUID nextUniqueID = children.get(childrenIndex % children.size()).getUniqueID();
          nextUniqueID = (children.size() != 1) ? nextUniqueID : null;
          taskManager.updateTasksBeforeRemovingChild(uniqueID, nextUniqueID);

          ChildManager.children.remove(i);
          saveData();
          break;
        }
      }
    }
  }

  public void setChildName(UUID uniqueID, String name) {
    Child child = getChild(uniqueID);

    if (child != null) {
      child.setName(name);
      saveData();
    }
  }

  public ArrayList<Child> getChildren() {
    return children;
  }
}
