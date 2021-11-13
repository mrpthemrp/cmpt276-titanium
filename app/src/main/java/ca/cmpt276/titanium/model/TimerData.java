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
        return prefs.getLong("duration_milliseconds", INVALID_MILLISECONDS);
    }

    public void setDurationMilliseconds(long durationMilliseconds) {
        prefsEditor.putLong("duration_milliseconds", durationMilliseconds);
        prefsEditor.apply();
    }

    public long getRemainingMilliseconds() {
        return prefs.getLong("remaining_milliseconds", INVALID_MILLISECONDS);
    }

    public void setRemainingMilliseconds(long remainingMilliseconds) {
        prefsEditor.putLong("remaining_milliseconds", remainingMilliseconds);
        prefsEditor.apply();
    }

    public boolean isRunning() {
        return prefs.getBoolean("is_running", DEFAULT_RUNNING);
    }

    public void setRunning() {
        prefsEditor.putBoolean("is_running", true);
        prefsEditor.putBoolean("is_paused", false);
        prefsEditor.apply();
    }

    public boolean isPaused() {
        return prefs.getBoolean("is_paused", DEFAULT_PAUSED);
    }

    public void setPaused() {
        prefsEditor.putBoolean("is_running", false);
        prefsEditor.putBoolean("is_paused", true);
        prefsEditor.apply();
    }

    public void setStopped() {
        prefsEditor.putLong("remaining_milliseconds", getDurationMilliseconds());
        prefsEditor.putBoolean("is_running", false);
        prefsEditor.putBoolean("is_paused", false);
        prefsEditor.apply();
    }
}
