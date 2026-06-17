package com.main.CoreWorks.Resources;

public class Modifier {
    private final String type;
    private final float value;

    Modifier(String name, float val) {
        type = name;
        value = val;
    }

    public float getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
