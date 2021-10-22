package ca.cmpt276.titanium.model;

public class TimerInfo {
    private int durationSeconds;
    private int remainingSeconds;
    private boolean isRunning = false;
    private boolean isPaused = false;

    public TimerInfo(int durationSeconds) {
        this.durationSeconds = durationSeconds;
        this.remainingSeconds = durationSeconds;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }
}
