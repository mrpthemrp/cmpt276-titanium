package ca.cmpt276.titanium.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TimerInfo {

    private static SharedPreferences prefs;

    private TimerInfo(Context context) {
        TimerInfo.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
