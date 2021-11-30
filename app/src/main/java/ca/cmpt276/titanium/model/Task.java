package ca.cmpt276.titanium.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * This class represents a single task and its data.
 */
public class Task {
  private String taskName;
  private UUID childUniqueID;
  private static LocalDate date;

  public Task(String taskName, UUID childUniqueID) {
    this.taskName = taskName;
    this.childUniqueID = childUniqueID;
  }

  public Task(String taskName, UUID childUniqueID, LocalDate date){
    this.taskName = taskName;
    this.childUniqueID = childUniqueID;
    Task.date = date;
  }

  public String getTaskName() {
    return taskName;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public UUID getChildUniqueID() {
    return childUniqueID;
  }

  public void setChildUniqueID(UUID childUniqueID) {
    this.childUniqueID = childUniqueID;
  }
}
