package ca.cmpt276.titanium.model;

public class Child {
    private final int uniqueId;
    private String name;

    public Child(int uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
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
