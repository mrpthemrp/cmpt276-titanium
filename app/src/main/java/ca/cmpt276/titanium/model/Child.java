package ca.cmpt276.titanium.model;

public class Child {
    private final int uniqueId;
    private String name;
    private boolean isSelected;

    public Child(int uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.isSelected = false;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
