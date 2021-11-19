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
 * This class represents the coin flip history.
 * Handles loading and saving from JSON.
 */
public class CoinFlipHistory {
    private static final Gson GSON = new Gson();
    private static final String COIN_FLIP_HISTORY_JSON_KEY = "coinFlipHistoryJson";
    private static final String PICKER_UNIQUE_ID_JSON_KEY = "pickerUniqueIDJson";

    private static CoinFlipHistory instance;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;
    private static Children children;

    private static ArrayList<CoinFlip> coinFlipHistory = new ArrayList<>();
    private static UUID pickerUniqueID;

    private CoinFlipHistory(Context context) {
        CoinFlipHistory.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        CoinFlipHistory.prefsEditor = prefs.edit();
    }

    public static CoinFlipHistory getInstance(Context context) {
        if (instance == null) {
            CoinFlipHistory.instance = new CoinFlipHistory(context);
            CoinFlipHistory.children = Children.getInstance(context);
        }

        loadSavedData();
        return instance;
    }

    private static void loadSavedData() {
        String coinFlipHistoryJson = prefs.getString(COIN_FLIP_HISTORY_JSON_KEY, null);
        String pickerUniqueIDJson = prefs.getString(PICKER_UNIQUE_ID_JSON_KEY, null);

        if (coinFlipHistoryJson != null) {
            Type coinFlipHistoryType = new TypeToken<ArrayList<CoinFlip>>() {
            }.getType();

            CoinFlipHistory.coinFlipHistory = GSON.fromJson(coinFlipHistoryJson, coinFlipHistoryType);
        }

        if (pickerUniqueIDJson != null) {
            CoinFlipHistory.pickerUniqueID = GSON.fromJson(pickerUniqueIDJson, UUID.class);
        }
    }

    private void saveData() {
        String coinFlipHistoryJson = GSON.toJson(coinFlipHistory);
        String pickerUniqueIDJson = GSON.toJson(pickerUniqueID);

        prefsEditor.putString(COIN_FLIP_HISTORY_JSON_KEY, coinFlipHistoryJson);
        prefsEditor.putString(PICKER_UNIQUE_ID_JSON_KEY, pickerUniqueIDJson);
        prefsEditor.apply();
    }

    public void addCoinFlip(Coin chosenSide, Coin result) {
        coinFlipHistory.add(new CoinFlip(pickerUniqueID, chosenSide, result));
        incrementPickerUniqueID();
        saveData();
    }

    public void updateCoinFlipHistory(boolean childAdded, UUID childUniqueID) {
        if (childAdded) {
            if (children.getChildren().size() == 1) {
                CoinFlipHistory.pickerUniqueID = childUniqueID;
            }
        } else {
            ArrayList<CoinFlip> coinFlipHistoryCopy = new ArrayList<>(coinFlipHistory);

            for (int i = 0; i < coinFlipHistoryCopy.size(); i++) {
                if (childUniqueID.equals(coinFlipHistoryCopy.get(i).getPickerUniqueID())) {
                    coinFlipHistory.remove(coinFlipHistoryCopy.get(i));
                }
            }

            if (children.getChildren().size() == 0) {
                CoinFlipHistory.pickerUniqueID = null;
            } else if (childUniqueID.equals(pickerUniqueID)) {
                incrementPickerUniqueID();
            }
        }

        saveData();
    }

    private void incrementPickerUniqueID() {
        for (int i = 0; i < children.getChildren().size(); i++) {
            if (pickerUniqueID.equals(children.getChildren().get(i).getUniqueID())) {
                CoinFlipHistory.pickerUniqueID = children.getChildren().get((i + 1) % children.getChildren().size()).getUniqueID();
                break;
            }
        }
    }

    public ArrayList<CoinFlip> getCoinFlipHistory() {
        return coinFlipHistory;
    }

    public UUID getPickerUniqueID() {
        return pickerUniqueID;
    }
}
