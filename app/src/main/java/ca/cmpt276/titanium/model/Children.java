package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class represents a group of children.
 * Handles loading and saving from JSON. Generates a unique ID for a child.
 */
public class Children {
    private static final Gson GSON = new Gson();
    private static final String CHILDREN_JSON_KEY = "childrenJson";
    private static final Tasks taskManager = Tasks.getInstance();
    private static Children instance;
    private static SharedPreferences prefs;
    private static CoinFlipHistory coinFlipHistory;
    private static ChildrenQueue childrenQueue;
    private static ArrayList<Child> children = new ArrayList<>();

    private Children(Context context) {
        Children.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Children getInstance(Context context) {
        if (instance == null) {
            Children.instance = new Children(context);
            Children.coinFlipHistory = CoinFlipHistory.getInstance(context);
            Children.childrenQueue = ChildrenQueue.getInstance(context);
        }

        loadSavedData();
        return instance;
    }

    private static void loadSavedData() {
        String childrenJson = prefs.getString(CHILDREN_JSON_KEY, null);

        if (childrenJson != null) {
            Type childrenType = new TypeToken<ArrayList<Child>>() {
            }.getType();

            Children.children = GSON.fromJson(childrenJson, childrenType);
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
        Children.children.add(newChild);
        coinFlipHistory.updateCoinFlipHistory(true, newChild.getUniqueID());
        childrenQueue.getChildrenQueue().add(newChild);
        childrenQueue.saveData();
        saveData();
    }

    public void removeChild(UUID uniqueID) {
        if (uniqueID != null) {
            for (int i = 0; i < children.size(); i++) {
                if (uniqueID.equals(children.get(i).getUniqueID())) {
                    coinFlipHistory.updateCoinFlipHistory(false, uniqueID);

                    childrenQueue.getChildrenQueue().remove(childrenQueue.getChildQueueIndex(uniqueID));
                    childrenQueue.saveData();

                    int nextChild = i;
                    nextChild += 1;

                    if (nextChild >= children.size()) {
                        nextChild = 0;
                    }
                    Child child = children.get(nextChild);
                    taskManager.updateChild(uniqueID, child, children.size());
                    Children.children.remove(i);
                    saveData();
                    break;
                }
            }
        }
    }

    public void setChildName(UUID uniqueID, String name) {
        Child child = getChild(uniqueID);
        Child childInQueue = childrenQueue.getChild(uniqueID);

        if (child != null) {
            child.setName(name);
            childInQueue.setName(name);
            saveData();
            childrenQueue.saveData();
        }
    }

    public ArrayList<Child> getChildren() {
        return children;
    }
}
