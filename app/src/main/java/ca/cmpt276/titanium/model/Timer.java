package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * Loads and saves timer fields.
 *
 * @author Titanium
 */
public class Timer {
  private static final String DURATION_MILLISECONDS_KEY = "durationMilliseconds";
  private static final String REMAINING_MILLISECONDS_KEY = "remainingMilliseconds";
  private static final String IS_RUNNING_KEY = "isRunning";
  private static final String IS_PAUSED_KEY = "isPaused";
  private static final String IS_GUI_ENABLED_KEY = "isGUIEnabled";
  private static final String TIME_FACTOR_KEY = "timeFactor";

  private static final int DEFAULT_MILLISECONDS = 0;
  private static final boolean DEFAULT_IS_RUNNING = false;
  private static final boolean DEFAULT_IS_PAUSED = false;
  private static final boolean DEFAULT_IS_GUI_ENABLED = true;
  private static final float DEFAULT_TIME_FACTOR = 1.0f;

  private static Timer instance;
  private static SharedPreferences prefs;
  private static SharedPreferences.Editor prefsEditor;

  private Timer(Context context) {
    Timer.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    Timer.prefsEditor = prefs.edit();
  }

  public static Timer getInstance(Context context) {
    if (instance == null) {
      Timer.instance = new Timer(context);
    }

    return instance;
  }

  public long getDurationMilliseconds() {
    return prefs.getLong(DURATION_MILLISECONDS_KEY, DEFAULT_MILLISECONDS);
  }

  public void setDurationMilliseconds(long durationMilliseconds) {
    if (durationMilliseconds < 0) {
      throw new IllegalArgumentException("durationMilliseconds must be >= 0");
    }

    prefsEditor.putLong(DURATION_MILLISECONDS_KEY, durationMilliseconds).apply();
  }

  public long getRemainingMilliseconds() {
    return prefs.getLong(REMAINING_MILLISECONDS_KEY, DEFAULT_MILLISECONDS);
  }

  public void setRemainingMilliseconds(long remainingMilliseconds) {
    if (remainingMilliseconds < 0) {
      throw new IllegalArgumentException("remainingMilliseconds must be >= 0");
    }

    prefsEditor.putLong(REMAINING_MILLISECONDS_KEY, remainingMilliseconds).apply();
  }

  public boolean isRunning() {
    return prefs.getBoolean(IS_RUNNING_KEY, DEFAULT_IS_RUNNING);
  }

  public void setRunning() {
    prefsEditor.putBoolean(IS_RUNNING_KEY, true);
    prefsEditor.putBoolean(IS_PAUSED_KEY, false);
    prefsEditor.apply();
  }

  public boolean isPaused() {
    return prefs.getBoolean(IS_PAUSED_KEY, DEFAULT_IS_PAUSED);
  }

  public void setPaused() {
    prefsEditor.putBoolean(IS_RUNNING_KEY, false);
    prefsEditor.putBoolean(IS_PAUSED_KEY, true);
    prefsEditor.apply();
  }

  public void setStopped() {
    prefsEditor.putLong(REMAINING_MILLISECONDS_KEY, getDurationMilliseconds());
    prefsEditor.putBoolean(IS_RUNNING_KEY, DEFAULT_IS_RUNNING);
    prefsEditor.putBoolean(IS_PAUSED_KEY, DEFAULT_IS_PAUSED);
    prefsEditor.putBoolean(IS_GUI_ENABLED_KEY, DEFAULT_IS_GUI_ENABLED);
    prefsEditor.putFloat(TIME_FACTOR_KEY, DEFAULT_TIME_FACTOR);
    prefsEditor.apply();
  }

  public boolean isGUIEnabled() {
    return prefs.getBoolean(IS_GUI_ENABLED_KEY, DEFAULT_IS_GUI_ENABLED);
  }

  public void setGUIEnabled(boolean isGUIEnabled) {
    prefsEditor.putBoolean(IS_GUI_ENABLED_KEY, isGUIEnabled).apply();
  }

  public float getTimeFactor() {
    return prefs.getFloat(TIME_FACTOR_KEY, DEFAULT_TIME_FACTOR);
  }

  public void setTimeFactor(float timeFactor) {
    prefsEditor.putFloat(TIME_FACTOR_KEY, timeFactor).apply();
  }
}
