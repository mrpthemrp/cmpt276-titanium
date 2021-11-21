package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * This class stores the timer information.
 */
public class Timer {
    private static final String DURATION_KEY = "durationMilliseconds";
    private static final String REMAINING_KEY = "remainingMilliseconds";
    private static final String RUNNING_KEY = "isRunning";
    private static final String PAUSED_KEY = "isPaused";
    private static final String GUI_ENABLED_KEY = "isGUIEnabled";

    private static final int DEFAULT_MILLISECONDS = 0;
    private static final boolean DEFAULT_RUNNING = false;
    private static final boolean DEFAULT_PAUSED = false;
    private static final boolean DEFAULT_GUI_ENABLED = true;

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
        return prefs.getLong(DURATION_KEY, DEFAULT_MILLISECONDS);
    }

    public void setDurationMilliseconds(long durationMilliseconds) {
        if (durationMilliseconds < 0) {
            throw new IllegalArgumentException("durationMilliseconds must be >= 0");
        }

        prefsEditor.putLong(DURATION_KEY, durationMilliseconds).apply();
    }

    public long getRemainingMilliseconds() {
        return prefs.getLong(REMAINING_KEY, DEFAULT_MILLISECONDS);
    }

    public void setRemainingMilliseconds(long remainingMilliseconds) {
        if (remainingMilliseconds < 0) {
            throw new IllegalArgumentException("remainingMilliseconds must be >= 0");
        }

        prefsEditor.putLong(REMAINING_KEY, remainingMilliseconds).apply();
    }

    public boolean isRunning() {
        return prefs.getBoolean(RUNNING_KEY, DEFAULT_RUNNING);
    }

    public void setRunning() {
        prefsEditor.putBoolean(RUNNING_KEY, true);
        prefsEditor.putBoolean(PAUSED_KEY, false);
        prefsEditor.apply();
    }

    public boolean isPaused() {
        return prefs.getBoolean(PAUSED_KEY, DEFAULT_PAUSED);
    }

    public void setPaused() {
        prefsEditor.putBoolean(RUNNING_KEY, false);
        prefsEditor.putBoolean(PAUSED_KEY, true);
        prefsEditor.apply();
    }

    public void setStopped() {
        prefsEditor.putLong(REMAINING_KEY, getDurationMilliseconds());
        prefsEditor.putBoolean(RUNNING_KEY, false);
        prefsEditor.putBoolean(PAUSED_KEY, false);
        prefsEditor.apply();
    }

    public boolean isGUIEnabled() {
        return prefs.getBoolean(GUI_ENABLED_KEY, DEFAULT_GUI_ENABLED);
    }

    public void setGUIEnabled(boolean isGUIEnabled) {
        prefsEditor.putBoolean(GUI_ENABLED_KEY, isGUIEnabled).apply();
    }
}
