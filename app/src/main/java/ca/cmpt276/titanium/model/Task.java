package ca.cmpt276.titanium.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a task.
 *
 * @author Titanium
 */
public class Task {
  private String taskName;
  private UUID childUniqueID;

  public Task(String taskName, UUID childUniqueID) {
    this.taskName = taskName;
    this.childUniqueID = childUniqueID;
  }

  public String getTaskName() {
    return taskName;
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
