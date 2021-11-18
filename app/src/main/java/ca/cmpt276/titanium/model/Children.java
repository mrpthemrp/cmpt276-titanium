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

    private static Children instance;
    private static SharedPreferences prefs;
    private static ArrayList<Child> children = new ArrayList<>();

    private Children(Context context) {
        Children.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Children getInstance(Context context) {
        if (instance == null) {
            Children.instance = new Children(context);
        }

        loadSavedData();
        return instance;
    }

    private static void loadSavedData() {
        String childrenJson = prefs.getString("childrenJson", null);

        if (childrenJson != null) {
            Type childrenType = new TypeToken<ArrayList<Child>>() {
            }.getType();

            Children.children = GSON.fromJson(childrenJson, childrenType);
        }
    }

    private void saveData() {
        String childrenJson = GSON.toJson(children);
        prefs.edit().putString("childrenJson", childrenJson).apply();
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

    public void addChild(String name) {
        Children.children.add(new Child(name));
        saveData();
    }

    public void removeChild(UUID uniqueID) {
        if (uniqueID!= null) {
            for (int i = 0; i < children.size(); i++) {
                if (uniqueID.equals(children.get(i).getUniqueID())) {
                    Children.children.remove(i);
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
