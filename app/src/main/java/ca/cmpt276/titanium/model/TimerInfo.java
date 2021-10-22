package ca.cmpt276.titanium.model;

public class TimerInfo {
    private int durationSeconds;
    private int remainingSeconds;
    private boolean running = false;
    private boolean paused = false;

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
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
