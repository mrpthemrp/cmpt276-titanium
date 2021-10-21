package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Children {
    private static final Children instance = new Children();
    private static final Logger logger = Logger.getLogger(Children.class.getName());
    private static ArrayList<Child> children = new ArrayList<>();
    private static ArrayList<CoinFlip> coinFlips = new ArrayList<>();

    public Children() {

    }

    public static Children getInstance(Context context) {
        loadSavedData(context);
        return instance;
    }

    private static void loadSavedData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ca.cmpt276.titanium", Context.MODE_PRIVATE);

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

    private void saveData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ca.cmpt276.titanium", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

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

    public void addChild(Context context, String name) {
        Child newChild = new Child(generateUniqueChildId(), name);
        Children.children.add(newChild);
        saveData(context);
    }

    public Child getChild(Context context, int uniqueId) {
        for (int i = 0; i < children.size(); i++) {
            if (uniqueId == children.get(i).getUniqueId()) {
                return children.get(i);
            }
        }

        logger.log(Level.WARNING, "Attempted to get Child object with nonexistent unique ID");
        return null;
    }

    public void removeChild(Context context, int uniqueId) {
        Child badChildIndex = null;

        for (int i = 0; i < children.size(); i++) {
            if (uniqueId == children.get(i).getUniqueId()) {
                badChildIndex = Children.children.get(i);
            }
        }

        if (badChildIndex != null) {
            Children.children.remove(badChildIndex);
        } else {
            logger.log(Level.WARNING, "Attempted to remove Child object with nonexistent unique ID from Children.children");
        }

        saveData(context);
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

    public void addCoinFlip(Context context) {
        CoinFlip newCoinFlip = new CoinFlip(generateUniqueCoinFlipId());
        Children.coinFlips.add(newCoinFlip);
        saveData(context);
    }

    public CoinFlip getCoinFlip(int uniqueId) {
        for (int i = 0; i < coinFlips.size(); i++) {
            if (uniqueId == coinFlips.get(i).getUniqueId()) {
                return coinFlips.get(i);
            }
        }

        logger.log(Level.WARNING, "Attempted to get CoinFlip object with nonexistent unique ID");
        return null;
    }

    public void removeCoinFlip(Context context, int uniqueId) {
        CoinFlip deletedCoinFlipIndex = null;

        for (int i = 0; i < coinFlips.size(); i++) {
            if (uniqueId == coinFlips.get(i).getUniqueId()) {
                deletedCoinFlipIndex = coinFlips.get(i);
            }
        }

        if (deletedCoinFlipIndex != null) {
            Children.coinFlips.remove(deletedCoinFlipIndex);
        } else {
            logger.log(Level.WARNING, "Attempted to remove CoinFlip object with nonexistent unique ID from Children.coinFlips");
        }

        saveData(context);
    }
}
