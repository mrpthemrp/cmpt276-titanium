package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoinFlipHistory {
    private static final String COIN_FLIP_HISTORY_KEY = "coin_flip_history";
    private static final String EMPTY_STRING = "";
    private static List<CoinFlip> coinFlipHistory;

    private GsonBuilder gsonBuilder;
    private Gson gson;
    private SharedPreferences sharedPreferences;

    public CoinFlipHistory(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        coinFlipHistory = new ArrayList<>();

        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .setPrettyPrinting()
                .create();

        initializeCoinFlipHistory();
    }

    private void initializeCoinFlipHistory() {
        String coinFlipsJson = sharedPreferences.getString(COIN_FLIP_HISTORY_KEY, EMPTY_STRING);
        CoinFlip[] coinFlips = gson.fromJson(coinFlipsJson, CoinFlip[].class);
        if (coinFlips != null) {
            Collections.addAll(coinFlipHistory, coinFlips);
        }
    }

    public void addCoinFlipToHistory(CoinFlip coinFlip) {
        coinFlipHistory.add(coinFlip);

        String coinFlipsJson = gson.toJson(coinFlipHistory);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COIN_FLIP_HISTORY_KEY, coinFlipsJson);
        editor.apply();
    }

    public static List<CoinFlip> getCoinFlipHistory() {
        return coinFlipHistory;
    }
}
