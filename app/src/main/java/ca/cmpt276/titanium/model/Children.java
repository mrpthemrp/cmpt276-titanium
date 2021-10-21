package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Children {
    private static Children instance;
    private static ArrayList<Child> children = new ArrayList<>();
    private static ArrayList<CoinFlip> coinFlips = new ArrayList<>();

    public Children() {

    }

    public static Children getInstance() {
        if (instance == null) {
            instance = new Children();
        }
        return instance;
    }

    public void loadSavedData(Context context) {
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

    public void addChild(Context context, Child child) {
        Children.children.add(child);
        saveData(context);
    }

    public Child getChild(int id) {
        for (int i = 0; i < children.size(); i++) {
            if (id == children.get(i).getId()) {
                return children.get(i);
            }
        }
        return null;
    }

    public void removeChild(Context context, Child child) {
        Children.children.remove(child);
        saveData(context);
    }

    public void addCoinFlip(Context context, CoinFlip coinFlip) {
        Children.coinFlips.add(coinFlip);
        saveData(context);
    }

    public CoinFlip getCoinFlip(int id) {
        for (int i = 0; i < coinFlips.size(); i++) {
            if (id == coinFlips.get(i).getId()) {
                return coinFlips.get(i);
            }
        }
        return null;
    }

    public void removeCoinFlip(Context context, CoinFlip coinFlip) {
        Children.coinFlips.remove(coinFlip);
        saveData(context);
    }
}
