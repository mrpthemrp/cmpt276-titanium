package ca.cmpt276.titanium.model;

import java.util.UUID;

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
