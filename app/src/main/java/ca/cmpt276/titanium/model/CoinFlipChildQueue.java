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
 * This class represents the children queue for coin flips.
 */
public class CoinFlipChildQueue {
    private static final Gson GSON = new Gson();
    private static final String CHILDREN_QUEUE_JSON_KEY = "childrenQueueJson";
    private static final int FIRST_INDEX = 0;

    private static CoinFlipChildQueue instance;
    private static SharedPreferences prefs;

    private static ArrayList<Child> childrenQueue = new ArrayList<>();

    private CoinFlipChildQueue(Context context) {
        CoinFlipChildQueue.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static CoinFlipChildQueue getInstance(Context context) {
        if (instance == null) {
            CoinFlipChildQueue.instance = new CoinFlipChildQueue(context);
        }

        loadSavedData();
        return instance;
    }

    private static void loadSavedData() {
        String childrenQueueJson = prefs.getString(CHILDREN_QUEUE_JSON_KEY, null);

        if (childrenQueueJson != null) {
            Type childrenType = new TypeToken<ArrayList<Child>>() {
            }.getType();

            CoinFlipChildQueue.childrenQueue = GSON.fromJson(childrenQueueJson, childrenType);
        }
    }

    public void saveData() {
        String childrenQueueJson = GSON.toJson(childrenQueue);
        prefs.edit().putString(CHILDREN_QUEUE_JSON_KEY, childrenQueueJson).apply();
    }

    public Child getChild(UUID uniqueID) {
        if (uniqueID != null) {
            for (int i = 0; i < childrenQueue.size(); i++) {
                if (uniqueID.equals(childrenQueue.get(i).getUniqueID())) {
                    return childrenQueue.get(i);
                }
            }
        }

        return null;
    }

    public ArrayList<Child> getChildrenQueue() {
        return childrenQueue;
    }

    public int getChildQueueIndex(UUID uniqueID) {
        for (int i = 0; i < childrenQueue.size(); i++) {
            if (childrenQueue.get(i).getUniqueID().toString().equals(uniqueID.toString())) {
                return i;
            }
        }

        throw new NoSuchElementException("Child with given unique ID does not exist.");
    }

    public void moveChildPositionToFront(UUID uniqueID) {
        int childIndex = getChildQueueIndex(uniqueID);

        Child childToMove = getChild(uniqueID);
        childrenQueue.remove(childIndex);
        childrenQueue.add(FIRST_INDEX, childToMove);
        saveData();
    }

    public void moveChildPositionToBack(UUID uniqueID) {
        int childIndex = getChildQueueIndex(uniqueID);

        Child childToMove = getChild(uniqueID);
        childrenQueue.remove(childIndex);
        childrenQueue.add(childrenQueue.size(), childToMove);
        saveData();
    }
}
