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
public class CoinFlipManager {
  private static final Gson GSON = new Gson();
  private static final String COIN_FLIP_HISTORY_JSON_KEY = "coinFlipHistoryJson";
  private static final String NEXT_PICKER_UNIQUE_ID_JSON_KEY = "nextPickerUniqueIDJson";

  private static CoinFlipManager instance;
  private static SharedPreferences prefs;
  private static SharedPreferences.Editor prefsEditor;
  private static ChildManager childManager;
  private static ChildQueueManager childQueueManager;

  private static ArrayList<CoinFlip> coinFlipHistory = new ArrayList<>();
  private static UUID nextPickerUniqueID;

  private CoinFlipManager(Context context) {
    CoinFlipManager.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    CoinFlipManager.prefsEditor = prefs.edit();
  }

  public static CoinFlipManager getInstance(Context context) {
    if (instance == null) {
      CoinFlipManager.instance = new CoinFlipManager(context);
      CoinFlipManager.childManager = ChildManager.getInstance(context);
      CoinFlipManager.childQueueManager = ChildQueueManager.getInstance(context);
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

      CoinFlipManager.coinFlipHistory = GSON.fromJson(coinFlipHistoryJson, coinFlipHistoryType);
    }

    if (nextPickerUniqueIDJson != null) {
      CoinFlipManager.nextPickerUniqueID = GSON.fromJson(nextPickerUniqueIDJson, UUID.class);
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

  public static void setNextPickerUniqueID(UUID nextPickerUniqueID) {
    CoinFlipManager.nextPickerUniqueID = nextPickerUniqueID;
    saveData();
  }

  public void updateCoinFlipHistory(boolean isChildBeingAdded, UUID childUniqueID) {
    if (isChildBeingAdded) {
      if (childManager.getChildren().size() == 1) {
        CoinFlipManager.nextPickerUniqueID = childUniqueID;
      } else if (childManager.getChildren().size() == 2 && coinFlipHistory.size() > 0) {
        incrementNextPickerUniqueID();
        // TODO: Don't enter here unless the last history entry was done by child that used to be
        //  only child! Maybe it was done by other child that was already removed
      }
    } else { // child with childUniqueID will be removed after this function returns
      ArrayList<CoinFlip> coinFlipHistoryCopy = new ArrayList<>(coinFlipHistory);

      for (int i = 0; i < coinFlipHistoryCopy.size(); i++) {
        if (childUniqueID.equals(coinFlipHistoryCopy.get(i).getPickerUniqueID())) {
          coinFlipHistory.remove(coinFlipHistoryCopy.get(i));
        }
      }

      if (childManager.getChildren().size() == 1) {
        CoinFlipManager.nextPickerUniqueID = null;
      } else if (childUniqueID.equals(nextPickerUniqueID)) {
        incrementNextPickerUniqueID();
      }
    }

    saveData();
  }

  public ArrayList<CoinFlip> getCoinFlipHistory() {
    return coinFlipHistory;
  }

  public UUID getNextPickerUniqueID() {
    return nextPickerUniqueID;
  }

  private void incrementNextPickerUniqueID() {
    for (int i = 0; i < childManager.getChildren().size(); i++) {
      if (nextPickerUniqueID.equals(childQueueManager.getChildQueue().get(i))) {
        ArrayList<UUID> childQueue = childQueueManager.getChildQueue();
        CoinFlipManager.nextPickerUniqueID = childQueue.get((i + 1) % childQueue.size());
        break;
      }
    }
  }
}
