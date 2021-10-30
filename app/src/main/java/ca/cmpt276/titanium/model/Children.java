package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    }

    public static Children getInstance(Context context) {
        if (instance == null) {
            Children.instance = new Children(context);
        }

        return instance;
    }

    public void loadSavedData() {
        String childrenJson = prefs.getString("children_json", null);

        Type childrenType = new TypeToken<ArrayList<Child>>(){}.getType();

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

    private int generateUniqueChildId() {
        int uniqueId = 0;

        if (children != null && !children.isEmpty()) {
            uniqueId = children.get(children.size() - 1).getUniqueId() + 1;
        } else {
            LOGGER.log(Level.INFO, "Children.children unique ID generation restarted from 0");
        }

        return uniqueId;
    }

    public void addChild(String name) {
        Child newChild = new Child(generateUniqueChildId(), name);
        Children.children.add(newChild);
    }

    public Child getChild(int uniqueId) {
        for (int i = 0; i < children.size(); i++) {
            if (uniqueId == children.get(i).getUniqueId()) {
                return children.get(i);
            }
        }

        LOGGER.log(Level.WARNING, "Attempted to get Child object with nonexistent unique ID");
        return null;
    }

    public void removeChild(int uniqueId) {
        Child badChildIndex = null;

        for (int i = 0; i < children.size(); i++) {
            if (uniqueId == children.get(i).getUniqueId()) {
                badChildIndex = Children.children.get(i);
            }
        }

        if (badChildIndex != null) {
            Children.children.remove(badChildIndex);
        } else {
            LOGGER.log(Level.WARNING, "Attempted to remove Child object with nonexistent " +
                    "unique ID from Children.children");
        }
    }

    public ArrayList<Child> getChildren() {
        return children;
    }
}
