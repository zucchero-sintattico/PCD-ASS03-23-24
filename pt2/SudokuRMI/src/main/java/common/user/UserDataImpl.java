package common.user;

import java.awt.*;

public record UserDataImpl(String name, Color color) implements UserData {
    public UserDataImpl(String name) {
        this(name, new Color(name.hashCode()));
    }
}