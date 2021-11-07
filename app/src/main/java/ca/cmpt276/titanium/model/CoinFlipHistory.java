package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CoinFlipHistory {
    private static final Gson GSON = new Gson();
    private static final String EMPTY_STRING = "";
    private static final String COIN_FLIP_HISTORY_KEY = "coin_flip_history";

    private static ArrayList<CoinFlip> coinFlipHistory;
    private final SharedPreferences sharedPreferences;

    public CoinFlipHistory(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        coinFlipHistory = new ArrayList<>();

        initializeCoinFlipHistory();
    }

    private void initializeCoinFlipHistory() {
        String coinFlipsJson = sharedPreferences.getString(COIN_FLIP_HISTORY_KEY, EMPTY_STRING);
        Type coinFlipsType = new TypeToken<ArrayList<CoinFlip>>() {
        }.getType();

        if (!coinFlipsJson.equals(EMPTY_STRING)) {
            CoinFlipHistory.coinFlipHistory = GSON.fromJson(coinFlipsJson, coinFlipsType);
        }
    }

    public void addCoinFlipToHistory(CoinFlip coinFlip) {
        coinFlipHistory.add(coinFlip);

        String coinFlipsJson = GSON.toJson(coinFlipHistory);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COIN_FLIP_HISTORY_KEY, coinFlipsJson);
        editor.apply();
    }

    public static ArrayList<CoinFlip> getCoinFlipHistory() {
        return CoinFlipHistory.coinFlipHistory;
    }
}
