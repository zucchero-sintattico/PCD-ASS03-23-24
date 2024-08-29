package logic.user;

import java.awt.*;

public record UserImpl(String name, Color color) implements User {

    public UserImpl(String name) {
        this(name, new Color(name.hashCode()));
    }

}