package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoinFlipHistory {
    private static final String COIN_FLIP_HISTORY_KEY = "coin_flip_history";
    private static final String EMPTY_STRING = "";
    private static ArrayList<CoinFlip> coinFlipHistory;
    private static final Gson GSON = new Gson();
    private SharedPreferences sharedPreferences;

    public CoinFlipHistory(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        coinFlipHistory = new ArrayList<>();

        initializeCoinFlipHistory();
    }

    private void initializeCoinFlipHistory() {
        String coinFlipsJson = sharedPreferences.getString(COIN_FLIP_HISTORY_KEY, "");
        Type coinFlipsType = new TypeToken<ArrayList<CoinFlip>>(){}.getType();

        if (!coinFlipsJson.equals("")) {
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
