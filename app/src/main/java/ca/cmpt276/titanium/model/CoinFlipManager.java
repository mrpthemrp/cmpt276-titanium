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
 * Loads, saves, and interacts with a group of CoinFlip objects.
 *
 * @author Titanium
 */
public class CoinFlipManager {
  private static final Gson GSON = new Gson();
  private static final String COIN_FLIP_HISTORY_JSON_KEY = "coinFlipHistoryJson";

  private static CoinFlipManager instance;
  private static SharedPreferences prefs;
  private static ChildManager childManager;

  private static ArrayList<CoinFlip> coinFlipHistory = new ArrayList<>();

  private CoinFlipManager(Context context) {
    CoinFlipManager.prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static CoinFlipManager getInstance(Context context) {
    if (instance == null) {
      CoinFlipManager.instance = new CoinFlipManager(context);
      CoinFlipManager.childManager = ChildManager.getInstance(context);
    }

    loadSavedData();
    return instance;
  }

  private static void loadSavedData() {
    String coinFlipHistoryJson = prefs.getString(COIN_FLIP_HISTORY_JSON_KEY, null);

    if (coinFlipHistoryJson != null) {
      Type coinFlipHistoryType = new TypeToken<ArrayList<CoinFlip>>() {
      }.getType();

      CoinFlipManager.coinFlipHistory = GSON.fromJson(coinFlipHistoryJson, coinFlipHistoryType);
    }
  }

  private void saveData() {
    String coinFlipHistoryJson = GSON.toJson(coinFlipHistory);
    prefs.edit().putString(COIN_FLIP_HISTORY_JSON_KEY, coinFlipHistoryJson).apply();
  }

  public void addCoinFlip(Coin childChosenSide, Coin result) {
    UUID childUniqueID = childManager.getChoosingChild().getUniqueID();
    coinFlipHistory.add(new CoinFlip(childChosenSide, result, childUniqueID));
    childManager.moveToBackOfQueue(childUniqueID);
    saveData();
  }

  public void removeCoinFlips(UUID childUniqueID) {
    coinFlipHistory.removeIf(coinFlip -> childUniqueID.equals(coinFlip.getChildUniqueID()));
    saveData();
  }

  public ArrayList<CoinFlip> getCoinFlipHistory() {
    return coinFlipHistory;
  }
}
