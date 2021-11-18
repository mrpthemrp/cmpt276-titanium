package ca.cmpt276.titanium.model;

import java.util.UUID;

/**
 * This class is used to get and set the attributes of a single child.
 */
public class Child {
    private final UUID uniqueID;
    private String name;

    public Child(UUID uniqueID, String name) {
        this.uniqueID = uniqueID;
        this.name = name;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
