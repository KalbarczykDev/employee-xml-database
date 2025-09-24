package main.java.model;

public enum Type {
    EXTERNAL("INTERNAL"), INTERNAL("EXTERNAL");
    private final String value;

    Type(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
