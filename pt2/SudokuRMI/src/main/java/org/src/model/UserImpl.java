package org.src.model;

import org.src.common.User;

import java.awt.*;

public class UserImpl implements User {

    Color color;
    String name;

    public UserImpl(String name) {;
        this.name = name;
        this.color = new Color(name.hashCode());

    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Color getColor() {
        return color;
    }
}
