package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

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
    private static final String NEXT_PICKER_UNIQUE_ID_JSON_KEY = "nextPickerUniqueIDJson";

    private static CoinFlipHistory instance;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;
    private static ChildManager childManager;
    private static CoinFlipChildQueue coinFlipChildQueue;

    private static ArrayList<CoinFlip> coinFlipHistory = new ArrayList<>();
    private static UUID nextPickerUniqueID;

    private CoinFlipHistory(Context context) {
        CoinFlipHistory.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        CoinFlipHistory.prefsEditor = prefs.edit();
    }

    public static CoinFlipHistory getInstance(Context context) {
        if (instance == null) {
            CoinFlipHistory.instance = new CoinFlipHistory(context);
            CoinFlipHistory.childManager = ChildManager.getInstance(context);
            CoinFlipHistory.coinFlipChildQueue = CoinFlipChildQueue.getInstance(context);
        }

        loadSavedData();
        return instance;
    }

    private static void loadSavedData() {
        String coinFlipHistoryJson = prefs.getString(COIN_FLIP_HISTORY_JSON_KEY, null);
        String nextPickerUniqueIDJson = prefs.getString(NEXT_PICKER_UNIQUE_ID_JSON_KEY, null);

        if (coinFlipHistoryJson != null) {
            Type coinFlipHistoryType = new TypeToken<ArrayList<CoinFlip>>() {
            }.getType();

            CoinFlipHistory.coinFlipHistory = GSON.fromJson(coinFlipHistoryJson, coinFlipHistoryType);
        }

        if (nextPickerUniqueIDJson != null) {
            CoinFlipHistory.nextPickerUniqueID = GSON.fromJson(nextPickerUniqueIDJson, UUID.class);
        }
    }

    private static void saveData() {
        String coinFlipHistoryJson = GSON.toJson(coinFlipHistory);
        String nextPickerUniqueIDJson = GSON.toJson(nextPickerUniqueID);

        prefsEditor.putString(COIN_FLIP_HISTORY_JSON_KEY, coinFlipHistoryJson);
        prefsEditor.putString(NEXT_PICKER_UNIQUE_ID_JSON_KEY, nextPickerUniqueIDJson);
        prefsEditor.apply();
    }

    public void addCoinFlip(Coin chosenSide, Coin result) {
        coinFlipHistory.add(new CoinFlip(nextPickerUniqueID, chosenSide, result));
        incrementNextPickerUniqueID();
        saveData();
    }

    public void updateCoinFlipHistory(boolean isChildBeingAdded, UUID childUniqueID) {
        if (isChildBeingAdded) {
            if (childManager.getChildren().size() == 1) {
                CoinFlipHistory.nextPickerUniqueID = childUniqueID;
            } else if (childManager.getChildren().size() == 2 && coinFlipHistory.size() > 0) {
                incrementNextPickerUniqueID(); // TODO: Don't enter here unless the last history entry was done by child that used to be only child! Maybe it was done by other child that was already removed
            }
        } else { // child with childUniqueID will be removed after this function returns
            ArrayList<CoinFlip> coinFlipHistoryCopy = new ArrayList<>(coinFlipHistory);

            for (int i = 0; i < coinFlipHistoryCopy.size(); i++) {
                if (childUniqueID.equals(coinFlipHistoryCopy.get(i).getPickerUniqueID())) {
                    coinFlipHistory.remove(coinFlipHistoryCopy.get(i));
                }
            }

            if (childManager.getChildren().size() == 1) {
                CoinFlipHistory.nextPickerUniqueID = null;
            } else if (childUniqueID.equals(nextPickerUniqueID)) {
                incrementNextPickerUniqueID();
            }
        }

        saveData();
    }

    private void incrementNextPickerUniqueID() {
        for (int i = 0; i < childManager.getChildren().size(); i++) {
            if (nextPickerUniqueID.equals(coinFlipChildQueue.getChildrenQueue().get(i).getUniqueID())) {
                CoinFlipHistory.nextPickerUniqueID = coinFlipChildQueue.getChildrenQueue().get((i + 1) %
                        coinFlipChildQueue.getChildrenQueue().size()).getUniqueID();
                break;
            }
        }
    }

    public ArrayList<CoinFlip> getCoinFlipHistory() {
        return coinFlipHistory;
    }

    public UUID getNextPickerUniqueID() {
        return nextPickerUniqueID;
    }

    public static void setNextPickerUniqueID(UUID nextPickerUniqueID) {
        CoinFlipHistory.nextPickerUniqueID = nextPickerUniqueID;
        saveData();
    }
}
