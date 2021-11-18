package ca.cmpt276.titanium.model;

import java.util.UUID;

// TODO: Remove uniqueIDs since we can just use object IDs

/**
 * This class represents a single child.
 */
public class Child {
    private final UUID uniqueId;
    private String name;

    public Child(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
