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
 * This class represents the children queue for coin flips.
 */
public class ChildQueueManager {
  private static final Gson GSON = new Gson();
  private static final String CHILD_QUEUE_JSON_KEY = "childrenQueueJson";

  private static ChildQueueManager instance;
  private static SharedPreferences prefs;

  private static ArrayList<UUID> childQueue = new ArrayList<>();

  private ChildQueueManager(Context context) {
    ChildQueueManager.prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static ChildQueueManager getInstance(Context context) {
    if (instance == null) {
      ChildQueueManager.instance = new ChildQueueManager(context);
    }

    loadSavedData();
    return instance;
  }

  private static void loadSavedData() {
    String childQueueJson = prefs.getString(CHILD_QUEUE_JSON_KEY, null);

    if (childQueueJson != null) {
      Type childQueueType = new TypeToken<ArrayList<UUID>>() {
      }.getType();

      ChildQueueManager.childQueue = GSON.fromJson(childQueueJson, childQueueType);
    }
  }

  public void saveData() {
    String childrenQueueJson = GSON.toJson(childQueue);
    prefs.edit().putString(CHILD_QUEUE_JSON_KEY, childrenQueueJson).apply();
  }

  public void addChild(UUID uniqueID) {
    childQueue.add(uniqueID);
    saveData();
  }

  public void removeChild(UUID uniqueID) {
    childQueue.remove(uniqueID);
    saveData();
  }

  public void moveToFront(UUID uniqueID) {
    childQueue.remove(uniqueID);
    childQueue.add(0, uniqueID);
    saveData();
  }

  public void moveToBack(UUID uniqueID) {
    childQueue.remove(uniqueID);
    childQueue.add(childQueue.size(), uniqueID);
    saveData();
  }

  public ArrayList<UUID> getChildQueue() {
    return childQueue;
  }
}
