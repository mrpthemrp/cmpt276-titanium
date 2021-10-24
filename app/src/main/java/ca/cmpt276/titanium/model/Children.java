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
    private static final Logger logger = Logger.getLogger(Children.class.getName());
    private static Children instance;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;
    private static ArrayList<Child> children = new ArrayList<>();
    private static ArrayList<CoinFlip> coinFlips = new ArrayList<>();

    private Children(Context context) {
        Children.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Children.prefsEditor = prefs.edit();
    }

    public static Children getInstance(Context context) {
        if (instance == null) {
            Children.instance = new Children(context);
        }

        loadSavedData();
        return instance;
    }

    private static void loadSavedData() {
        String childrenJson = prefs.getString("children", null);
        String coinFlipsJson = prefs.getString("coin_flips", null);

        Gson gson = new Gson();
        Type childrenType = new TypeToken<ArrayList<Child>>(){}.getType();
        Type coinFlipsType = new TypeToken<ArrayList<CoinFlip>>(){}.getType();

        if (childrenJson != null) {
            Children.children = gson.fromJson(childrenJson, childrenType);
        } else {
            logger.log(Level.INFO, "No Child objects were loaded into Children.children");
        }

        if (coinFlipsJson != null) {
            Children.coinFlips = gson.fromJson(coinFlipsJson, coinFlipsType);
        } else {
            logger.log(Level.INFO, "No CoinFlip objects were loaded into Children.coinFlips");
        }
    }

    public void saveData() {
        Gson gson = new Gson();
        String childrenJson = gson.toJson(children);
        String coinFlipsJson = gson.toJson(coinFlips);

        prefsEditor.putString("children", childrenJson);
        prefsEditor.putString("coin_flips", coinFlipsJson);

        prefsEditor.apply();
    }

    private int generateUniqueChildId() {
        int uniqueId = 0;

        if (children != null && !children.isEmpty()) {
            uniqueId = children.get(children.size() - 1).getUniqueId() + 1;
        } else {
            logger.log(Level.INFO, "Children.children unique ID generation restarted from 0");
        }

        return uniqueId;
    }

    public void addChild(String name, boolean isSelected) {
        Child newChild = new Child(generateUniqueChildId(), name, isSelected);
        Children.children.add(newChild);
        saveData();
    }

    public Child getChild(int uniqueId) {
        for (int i = 0; i < children.size(); i++) {
            if (uniqueId == children.get(i).getUniqueId()) {
                return children.get(i);
            }
        }

        logger.log(Level.WARNING, "Attempted to get Child object with nonexistent unique ID");
        return null;
    }

    public int getNumOfChildren(){
        return this.children.size();
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
            logger.log(Level.WARNING, "Attempted to remove Child object with nonexistent " +
                    "unique ID from Children.children");
        }

        saveData();
    }

    private int generateUniqueCoinFlipId() {
        int uniqueId = 0;

        if (coinFlips != null && !coinFlips.isEmpty()) {
            uniqueId = coinFlips.get(coinFlips.size() - 1).getUniqueId() + 1;
        } else {
            logger.log(Level.INFO, "Children.coinFlips unique ID generation restarted from 0");
        }

        return uniqueId;
    }

    public void addCoinFlip() {
        CoinFlip newCoinFlip = new CoinFlip(generateUniqueCoinFlipId());
        Children.coinFlips.add(newCoinFlip);
        saveData();
    }

    public CoinFlip getCoinFlip(int uniqueId) {
        for (int i = 0; i < coinFlips.size(); i++) {
            if (uniqueId == coinFlips.get(i).getUniqueId()) {
                return coinFlips.get(i);
            }
        }

        logger.log(Level.WARNING, "Attempted to get CoinFlip object with nonexistent unique " +
                "ID");
        return null;
    }

    public void removeCoinFlip(int uniqueId) {
        CoinFlip deletedCoinFlipIndex = null;

        for (int i = 0; i < coinFlips.size(); i++) {
            if (uniqueId == coinFlips.get(i).getUniqueId()) {
                deletedCoinFlipIndex = coinFlips.get(i);
            }
        }

        if (deletedCoinFlipIndex != null) {
            Children.coinFlips.remove(deletedCoinFlipIndex);
        } else {
            logger.log(Level.WARNING, "Attempted to remove CoinFlip object with nonexistent " +
                    "unique ID from Children.coinFlips");
        }

        saveData();
    }

    public static ArrayList<Child> getChildren() {
        return children;
    }
}
