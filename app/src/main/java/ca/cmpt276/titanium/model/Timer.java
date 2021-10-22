package ca.cmpt276.titanium.model;

public class Timer {
    private static final Timer instance = new Timer();

    public Timer() {

    }

    public static Timer getInstance() {
        return instance;
    }
}
