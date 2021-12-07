package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Loads and saves number of breaths.
 *
 * @author Titanium
 */
public class BreathManager {
  private static final Gson GSON = new Gson();
  private static final String BREATH_JSON_KEY = "breathJson";

  private static BreathManager instance;
  private static SharedPreferences prefs;

  private static int numBreaths;

  private BreathManager(Context context) {
    BreathManager.prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static BreathManager getInstance(Context context) {
    if (instance == null) {
      BreathManager.instance = new BreathManager(context);
    }

    loadSavedData();
    return instance;
  }

  public static void loadSavedData() {
    String breathJson = prefs.getString(BREATH_JSON_KEY, null);

    if (breathJson != null) {
      Type breathType = new TypeToken<Integer>() {
      }.getType();

      BreathManager.numBreaths = GSON.fromJson(breathJson, breathType);
    }
  }

  public void saveData() {
    String breathJson = GSON.toJson(numBreaths);

    SharedPreferences.Editor prefsEditor = prefs.edit();
    prefsEditor.putString(BREATH_JSON_KEY, breathJson);
    prefsEditor.apply();
  }

  public int getNumBreaths() {
    return numBreaths;
  }

  public static void setNumBreaths(int numBreaths) {
    BreathManager.numBreaths = numBreaths;
  }
}


