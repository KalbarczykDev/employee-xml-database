package main.java.model;

public enum Type {
    EXTERNAL("INTERNAL"), INTERNAL("EXTERNAL");
    private final String value;

    Type(final String value) {
        this.value = value;
    }

    public static Type fromString(final String value) {
        for (var type : Type.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + value);
    }

    @Override
    public String toString() {
        return value;
    }


}
