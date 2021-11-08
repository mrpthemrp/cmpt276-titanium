package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class stores the timer information.
 */
public class TimerInfo {
    private static SharedPreferences prefs;

    private TimerInfo(Context context) {
        TimerInfo.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
