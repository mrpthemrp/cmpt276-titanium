package ca.cmpt276.titanium.model;

import java.util.UUID;

/**
 * This class is used to get and set the attributes of a single child.
 */
public class Child {
    private final UUID uniqueID;
    private String name;

    public Child(String name) {
        this.uniqueID = UUID.randomUUID();
        setName(name);
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        char[] nameChars = name.toCharArray();

        for (char nameChar : nameChars) {
            if (!Character.isLetter(nameChar)) {
                throw new IllegalArgumentException("Names must contain only letters");
            }
        }

        this.name = name;
    }
}
