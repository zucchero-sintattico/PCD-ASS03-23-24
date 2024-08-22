package org.src.model;

import java.awt.*;

public record User(String name, Color color) {
    public User(String name) {
        this(name, new Color(name.hashCode()));
    }
}
