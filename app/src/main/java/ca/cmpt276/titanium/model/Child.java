package ca.cmpt276.titanium.model;

public class Child {
    private final int uniqueId;
    private String name;

    public Child(String name, int uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
