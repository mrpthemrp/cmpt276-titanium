package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Children {
    private static final Children instance = new Children();
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
        }

        if (coinFlipsJson != null) {
            Children.coinFlips = gson.fromJson(coinFlipsJson, coinFlipsType);
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
        }

        return uniqueId;
    }

    public void addChild(Context context, String name) {
        Child newChild = new Child(name, generateUniqueChildId());
        Children.children.add(newChild);
        saveData(context);
    }

    public Child getChild(Context context, int uniqueId) {
        for (int i = 0; i < children.size(); i++) {
            if (uniqueId == children.get(i).getUniqueId()) {
                return children.get(i);
            }
        }

        return null;
    }

    public void removeChild(Context context, int uniqueId) {
        Child badChild = null;

        for (int i = 0; i < children.size(); i++) {
            if (uniqueId == children.get(i).getUniqueId()) {
                badChild = Children.children.get(i);
            }
        }

        if (badChild != null) {
            Children.children.remove(badChild);
        }

        saveData(context);
    }

    private int generateUniqueCoinFlipId() {
        int uniqueId = 0;

        if (coinFlips != null && !coinFlips.isEmpty()) {
            uniqueId = coinFlips.get(coinFlips.size() - 1).getUniqueId() + 1;
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

        return null;
    }

    public void removeCoinFlip(Context context, int uniqueId) {
        CoinFlip deletedCoinFlip = null;

        for (int i = 0; i < coinFlips.size(); i++) {
            if (uniqueId == coinFlips.get(i).getUniqueId()) {
                deletedCoinFlip = coinFlips.get(i);
            }
        }

        if (deletedCoinFlip != null) {
            Children.coinFlips.remove(deletedCoinFlip);
        }

        saveData(context);
    }
}
