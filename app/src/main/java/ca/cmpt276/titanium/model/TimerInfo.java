package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TimerInfo {
    private static final int INVALID_MILLISECONDS = -1;
    private static final boolean DEFAULT_RUNNING = false;
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
        prefsEditor.putBoolean("is_stopped", false);
        prefsEditor.apply();
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
        prefsEditor.putLong("remaining_milliseconds", prefs.getLong("duration_milliseconds", INVALID_MILLISECONDS));
        prefsEditor.putBoolean("is_running", false);
        prefsEditor.putBoolean("is_paused", false);
        prefsEditor.putBoolean("is_stopped", true);
        prefsEditor.apply();
    }
}
