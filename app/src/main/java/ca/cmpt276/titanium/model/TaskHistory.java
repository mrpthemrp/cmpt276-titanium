package ca.cmpt276.titanium.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TaskHistory {

    private String historyTaskName;
    private final UUID childUniqueID;
    private final String date;

    TaskHistory(String historyTaskName, UUID childUniqueID){
        this.historyTaskName = historyTaskName;
        this.childUniqueID = childUniqueID;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd"));
    }

    public String getHistoryTaskName() {
        return historyTaskName;
    }

    public UUID getChildUniqueID() {
        return childUniqueID;
    }

    public String getDate() {
        return date;
    }

    public void setHistoryTaskName(String historyTaskName) {
        this.historyTaskName = historyTaskName;
    }
}
