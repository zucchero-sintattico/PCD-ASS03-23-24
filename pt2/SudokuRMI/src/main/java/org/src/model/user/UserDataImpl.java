package org.src.model.user;

import java.awt.*;

public class UserDataImpl implements UserData {

    private final String name;
    private final Color color;

    public UserDataImpl(String name) {
        this.name = name;
        this.color = new Color(name.hashCode());
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Color color() {
        return this.color;
    }

}