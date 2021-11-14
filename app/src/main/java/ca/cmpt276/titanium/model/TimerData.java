package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class stores the timer information.
 */
public class TimerData {
    private static final int INVALID_MILLISECONDS = -1;
    private static final boolean DEFAULT_RUNNING = false;
    private static final boolean DEFAULT_PAUSED = false;
    private static final boolean DEFAULT_GUI_ENABLED = true;

    private static TimerData instance;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;

    private TimerData(Context context) {
        TimerData.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        TimerData.prefsEditor = prefs.edit();
    }

    public static TimerData getInstance(Context context) {
        if (instance == null) {
            TimerData.instance = new TimerData(context);
        }

        return instance;
    }

    public long getDurationMilliseconds() {
        long durationMilliseconds = prefs.getLong("durationMilliseconds", INVALID_MILLISECONDS);
        durationMilliseconds = durationMilliseconds <= 0 ? 0 : durationMilliseconds;
        return durationMilliseconds;
    }

    public void setDurationMilliseconds(long durationMilliseconds) {
        if (durationMilliseconds < 0) {
            throw new IllegalArgumentException("durationMilliseconds must be >= 0");
        }

        prefsEditor.putLong("durationMilliseconds", durationMilliseconds);
        prefsEditor.apply();
    }

    public long getRemainingMilliseconds() {
        long remainingMilliseconds = prefs.getLong("remainingMilliseconds", INVALID_MILLISECONDS);
        remainingMilliseconds = remainingMilliseconds <= 0 ? 0 : remainingMilliseconds;
        return remainingMilliseconds;
    }

    public void setRemainingMilliseconds(long remainingMilliseconds) {
        if (remainingMilliseconds < 0) {
            throw new IllegalArgumentException("remainingMilliseconds must be >= 0");
        }

        prefsEditor.putLong("remainingMilliseconds", remainingMilliseconds);
        prefsEditor.apply();
    }

    public boolean isRunning() {
        return prefs.getBoolean("isRunning", DEFAULT_RUNNING);
    }

    public void setRunning() {
        prefsEditor.putBoolean("isRunning", true);
        prefsEditor.putBoolean("isPaused", false);
        prefsEditor.apply();
    }

    public boolean isPaused() {
        return prefs.getBoolean("isPaused", DEFAULT_PAUSED);
    }

    public void setPaused() {
        prefsEditor.putBoolean("isRunning", false);
        prefsEditor.putBoolean("isPaused", true);
        prefsEditor.apply();
    }

    public void setStopped() {
        prefsEditor.putLong("remainingMilliseconds", getDurationMilliseconds());
        prefsEditor.putBoolean("isRunning", false);
        prefsEditor.putBoolean("isPaused", false);
        prefsEditor.apply();
    }

    public boolean isGUIEnabled() {
        return prefs.getBoolean("isGUIEnabled", DEFAULT_GUI_ENABLED);
    }

    public void setGUIEnabled(boolean isGUIEnabled) {
        prefsEditor.putBoolean("isGUIEnabled", isGUIEnabled);
        prefsEditor.apply();
    }
}
