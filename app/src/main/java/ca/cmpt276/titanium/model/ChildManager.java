package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Loads, saves, and interacts with groups of Child objects.
 *
 * @author Titanium
 */
public class ChildManager {
  private static final Gson GSON = new Gson();
  private static final String CHILDREN_JSON_KEY = "childrenJson";
  private static final String COIN_FLIP_QUEUE_JSON_KEY = "coinFlipQueueJson";

  private static final boolean DEFAULT_CHILD_IS_CHOOSING = true;

  private static ChildManager instance;
  private static SharedPreferences prefs;
  private static CoinFlipManager coinFlipManager;
  private static TaskManager taskManager;

  private static ArrayList<Child> children = new ArrayList<>();
  private static ArrayList<UUID> coinFlipQueue = new ArrayList<>();

  private static boolean childIsChoosing = DEFAULT_CHILD_IS_CHOOSING;

  private ChildManager(Context context) {
    ChildManager.prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static ChildManager getInstance(Context context) {
    if (instance == null) {
      ChildManager.instance = new ChildManager(context);
      ChildManager.coinFlipManager = CoinFlipManager.getInstance(context);
      ChildManager.taskManager = TaskManager.getInstance(context);
    }

    loadSavedData();
    return instance;
  }

  private static void loadSavedData() {
    String childrenJson = prefs.getString(CHILDREN_JSON_KEY, null);
    String coinFlipQueueJson = prefs.getString(COIN_FLIP_QUEUE_JSON_KEY, null);

    if (childrenJson != null) {
      Type childrenType = new TypeToken<ArrayList<Child>>() {
      }.getType();

      ChildManager.children = GSON.fromJson(childrenJson, childrenType);
    }

    if (coinFlipQueueJson != null) {
      Type coinFlipQueueType = new TypeToken<ArrayList<UUID>>() {
      }.getType();

      ChildManager.coinFlipQueue = GSON.fromJson(coinFlipQueueJson, coinFlipQueueType);
    }
  }

  private void saveData() {
    String childrenJson = GSON.toJson(children);
    String coinFlipQueueJson = GSON.toJson(coinFlipQueue);

    SharedPreferences.Editor prefsEditor = prefs.edit();
    prefsEditor.putString(CHILDREN_JSON_KEY, childrenJson);
    prefsEditor.putString(COIN_FLIP_QUEUE_JSON_KEY, coinFlipQueueJson);
    prefsEditor.apply();
  }

  public Child getChild(UUID uniqueID) {
    if (uniqueID != null) {
      for (Child child : children) {
        if (uniqueID.equals(child.getUniqueID())) {
          return child;
        }
      }

      throw new NoSuchElementException("No child with provided uniqueID");
    } else {
      throw new IllegalArgumentException("uniqueID cannot be null");
    }
  }

  public void addChild(String name, String portraitPath) {
    Child child = new Child(name, portraitPath);

    children.add(child);
    coinFlipQueue.add(child.getUniqueID());
    saveData();

    if (children.size() == 1) {
      taskManager.addToAllTasks(child.getUniqueID());
    }
  }

  public void removeChild(UUID uniqueID) {
    Child child = getChild(uniqueID);

    UUID nextUniqueID = children.get((children.indexOf(child) + 1) % children.size()).getUniqueID();
    nextUniqueID = (children.size() != 1) ? nextUniqueID : null;
    taskManager.childDeletedFromHistory(uniqueID);
    taskManager.updateTaskQueues(uniqueID, nextUniqueID);

    children.remove(child);
    coinFlipQueue.remove(child.getUniqueID());
    saveData();

    coinFlipManager.removeCoinFlips(uniqueID);
  }

  public void setName(UUID uniqueID, String name) {
    getChild(uniqueID).setName(name);
    saveData();
  }

  public void setPortraitPath(UUID uniqueID, String portraitPath) {
    getChild(uniqueID).setPortraitPath(portraitPath);
    saveData();
  }

  public Child getChoosingChild() {
    return getChild(coinFlipQueue.get(0));
  }

  public void moveToFrontOfQueue(UUID childUniqueID) {
    coinFlipQueue.remove(childUniqueID);
    coinFlipQueue.add(0, childUniqueID);
    saveData();
  }

  public void moveToBackOfQueue(UUID childUniqueID) {
    coinFlipQueue.remove(childUniqueID);
    coinFlipQueue.add(childUniqueID);
    saveData();
  }

  public ArrayList<Child> getChildren() {
    return children;
  }

  public ArrayList<UUID> getCoinFlipQueue() {
    return coinFlipQueue;
  }

  public boolean isChildIsChoosing() {
    return childIsChoosing;
  }

  public void setChildIsChoosing(boolean childIsChoosing) {
    ChildManager.childIsChoosing = childIsChoosing;
  }
}
