package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TimerInfo {
    private static final int INVALID_SECONDS = -1;
    private static final boolean DEFAULT_RUNNING = false;
    private static final boolean DEFAULT_PAUSED = false;
    private static final boolean DEFAULT_STOPPED = true;

    private static TimerInfo instance;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;

    private TimerInfo(Context context) {
        TimerInfo.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        TimerInfo.prefsEditor = prefs.edit();
    }

    public static TimerInfo getInstance(Context context) {
        if (instance == null) {
            TimerInfo.instance = new TimerInfo(context);
        }

        return instance;
    }

    public int getDurationSeconds() {
        return prefs.getInt("duration_seconds", INVALID_SECONDS);
    }

    public void setDurationSeconds(int durationSeconds) {
        prefsEditor.putInt("duration_seconds", durationSeconds);
        prefsEditor.apply();
    }

    public int getRemainingSeconds() {
        return prefs.getInt("remaining_seconds", INVALID_SECONDS);
    }

    public void setRemainingSeconds(int remainingSeconds) {
        prefsEditor.putInt("remaining_seconds", remainingSeconds);
        prefsEditor.apply();
    }

    public boolean isRunning() {
        return prefs.getBoolean("is_running", DEFAULT_RUNNING);
    }

    public void setRunning() {
        prefsEditor.putBoolean("is_running", true);
        prefsEditor.putBoolean("is_paused", false);
        prefsEditor.putBoolean("is_stopped", false);
        prefsEditor.apply();
    }

    public boolean isPaused() {
        return prefs.getBoolean("is_paused", DEFAULT_PAUSED);
    }

    public void setPaused() {
        prefsEditor.putBoolean("is_running", false);
        prefsEditor.putBoolean("is_paused", true);
        prefsEditor.putBoolean("is_stopped", false);
        prefsEditor.apply();
    }

    public boolean isStopped() {
        return prefs.getBoolean("is_stopped", DEFAULT_STOPPED);
    }

    public void setStopped() {
        prefsEditor.putBoolean("is_running", false);
        prefsEditor.putBoolean("is_paused", false);
        prefsEditor.putBoolean("is_stopped", true);
        prefsEditor.apply();
    }

    public int getNextDurationSeconds() {
        return prefs.getInt("next_duration_seconds", INVALID_SECONDS);
    }

    public void setNextDurationSeconds(int nextDurationSeconds) {
        prefsEditor.putInt("next_duration_seconds", nextDurationSeconds);
        prefsEditor.apply();
    }
}
