package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TimerInfo {
    private static TimerInfo instance;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;

    private TimerInfo() {

    }

    public static TimerInfo getInstance(Context context) {
        if (instance == null) {
            TimerInfo.instance = new TimerInfo();
        }

        if (prefs == null || prefsEditor == null) {
            TimerInfo.prefs = PreferenceManager.getDefaultSharedPreferences(context);
            TimerInfo.prefsEditor = prefs.edit();
        }

        return instance;
    }

    public int getDurationSeconds() {
        return prefs.getInt("duration_seconds", -1);
    }

    public void setDurationSeconds(int durationSeconds) {
        prefsEditor.putInt("duration_seconds", durationSeconds);
        prefsEditor.apply();
    }

    public int getRemainingSeconds() {
        return prefs.getInt("remaining_seconds", -1);
    }

    public void setRemainingSeconds(int remainingSeconds) {
        prefsEditor.putInt("remaining_seconds", remainingSeconds);
        prefsEditor.apply();
    }

    public boolean isRunning() {
        return prefs.getBoolean("is_running", false);
    }

    public void setRunning(boolean isRunning) {
        prefsEditor.putBoolean("is_running", isRunning);
        prefsEditor.apply();
    }

    public boolean isPaused() {
        return prefs.getBoolean("is_paused", false);
    }

    public void setPaused(boolean isPaused) {
        prefsEditor.putBoolean("is_paused", isPaused);
        prefsEditor.apply();
    }

    public int getNextDurationSeconds() {
        return prefs.getInt("next_duration_seconds", -1);
    }

    public void setNextDurationSeconds(int nextDurationSeconds) {
        prefsEditor.putInt("next_duration_seconds", nextDurationSeconds);
        prefsEditor.apply();
    }
}
