package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a group of children.
 * Handles loading and saving from JSON. Generates a unique ID for a child.
 */
public class Children {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = Logger.getLogger(Children.class.getName());

    private static Children instance;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;
    private static ArrayList<Child> children = new ArrayList<>();

    private Children(Context context) {
        Children.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Children.prefsEditor = prefs.edit();
        loadSavedData();
    }

    public static Children getInstance(Context context) {
        if (instance == null) {
            Children.instance = new Children(context);
        }

        return instance;
    }

    public void loadSavedData() {
        String childrenJson = prefs.getString("children_json", null);

        Type childrenType = new TypeToken<ArrayList<Child>>() {
        }.getType();

        if (childrenJson != null) {
            Children.children = GSON.fromJson(childrenJson, childrenType);
        } else {
            LOGGER.log(Level.INFO, "No Child objects were loaded into Children.children");
        }
    }

    public void saveData() {
        String childrenJson = GSON.toJson(children);

        prefsEditor.putString("children_json", childrenJson);
        prefsEditor.apply();
    }

    private UUID generateUniqueChildId() {
        return UUID.randomUUID();
    }

    public void addChild(String name) {
        Child newChild = new Child(generateUniqueChildId(), name);
        Children.children.add(newChild);
        saveData();
    }

    public Child getChild(UUID uniqueID) {
        if (uniqueID == null) {
            return null;
        } else {
            for (int i = 0; i < children.size(); i++) {
                if (uniqueID.toString().equals(children.get(i).getUniqueID().toString())) {
                    return children.get(i);
                }
            }
        }

        LOGGER.log(Level.WARNING, "Attempted to get Child object with nonexistent unique ID");
        return null;
    }

    public void removeChild(UUID uniqueId) {
        Child badChildIndex = null;

        for (int i = 0; i < children.size(); i++) {
            if (uniqueId.toString().equals(children.get(i).getUniqueID().toString())) {
                badChildIndex = Children.children.get(i);
            }
        }

        if (badChildIndex != null) {
            Children.children.remove(badChildIndex);
        } else {
            LOGGER.log(Level.WARNING, "Attempted to remove Child object with nonexistent " +
                    "unique ID from Children.children");
        }

        saveData();
    }

    public ArrayList<Child> getChildren() {
        return children;
    }
}
