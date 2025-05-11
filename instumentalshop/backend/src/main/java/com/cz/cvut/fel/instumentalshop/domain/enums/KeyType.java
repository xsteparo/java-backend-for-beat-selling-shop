package com.cz.cvut.fel.instumentalshop.domain.enums;

public enum KeyType {
    C("C"),
    C_SHARP("C#"),
    D("D"),
    D_SHARP("D#"),
    E("E"),
    F("F"),
    F_SHARP("F#"),
    G("G"),
    G_SHARP("G#"),
    A("A"),
    A_SHARP("A#"),
    B("B");

    private final String label;

    KeyType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static KeyType fromString(String text) {
        for (KeyType kt : KeyType.values()) {
            if (kt.label.equalsIgnoreCase(text)) {
                return kt;
            }
        }
        throw new IllegalArgumentException("Unknown key: " + text);
    }
}

