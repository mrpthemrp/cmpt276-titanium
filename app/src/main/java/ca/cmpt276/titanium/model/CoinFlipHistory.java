package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This class represents the coin flip history.
 * Handles loading and saving from JSON.
 */
public class CoinFlipHistory {
    private static final Gson GSON = new Gson();
    private static final String EMPTY_STRING = "";
    private static final String COIN_FLIP_HISTORY_KEY = "coin_flip_history";
    public static final int FIRST_CHILD_INDEX = 0;
    private final Children children;

    private static ArrayList<CoinFlip> coinFlipHistory;
    private final SharedPreferences sharedPreferences;

    public CoinFlipHistory(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        coinFlipHistory = new ArrayList<>();
        children = Children.getInstance(context);

        initializeCoinFlipHistory();
    }

    public static ArrayList<CoinFlip> getCoinFlipHistory() {
        return CoinFlipHistory.coinFlipHistory;
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

    public void saveCoinFlip(Coin coinChosen, Coin coinSideLandedOn) {
        CoinFlip coinFlip;
        LocalDateTime timeOfFlip = LocalDateTime.now();

        Child childOfNextTurn = getChildForNextTurn();
        if (CoinFlipHistory.getCoinFlipHistory().isEmpty()) {
            coinFlip = new CoinFlip(children.getChildren().get(0), coinChosen, timeOfFlip, coinSideLandedOn);
        } else {
            coinFlip = new CoinFlip(childOfNextTurn, coinChosen, timeOfFlip, coinSideLandedOn);
        }

        addCoinFlipToHistory(coinFlip);
    }

    public Child getChildForNextTurn() {
        ArrayList<Child> childrenArray = children.getChildren();

        Child childToPickLastTurn = getChildOfLastTurn();
        if (childToPickLastTurn == null && !childrenArray.isEmpty()) {
            return childrenArray.get(FIRST_CHILD_INDEX);
        }

        Child childOfNextTurn;
        for (int i = 0; i < childrenArray.size(); i++) {
            if (childrenArray.get(i).getUniqueId().toString().equals(childToPickLastTurn.getUniqueId().toString())) {
                childOfNextTurn = childrenArray.get((i + 1) % childrenArray.size());
                return childOfNextTurn;
            }
        }

        return childrenArray.get(FIRST_CHILD_INDEX);
    }

    private Child getChildOfLastTurn() {
        int sizeOfHistory = CoinFlipHistory.getCoinFlipHistory().size();
        if (!CoinFlipHistory.getCoinFlipHistory().isEmpty()) {
            return CoinFlipHistory.getCoinFlipHistory().get(sizeOfHistory - 1).getChildWhoPicksSide();
        }

        return null;
    }
}
