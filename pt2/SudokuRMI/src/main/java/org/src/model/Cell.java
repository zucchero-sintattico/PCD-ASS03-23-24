package org.src.model;

import org.src.common.Point2d;
import org.src.common.User;

import java.util.Optional;

public record Cell(Point2d position, boolean immutable, Optional<Integer> number, Optional<User> user) {
    public Cell(Point2d position) {
        this(position, false, Optional.empty(), Optional.empty());
    }
    public Cell(Point2d position, boolean immutable) {
        this(position, immutable, Optional.empty(), Optional.empty());
    }
    public Cell setNumber(int number) {
        return new Cell(position, immutable, Optional.of(number), user);
    }
    public Cell setImmutable(boolean immutable) {
        return new Cell(position, immutable, number, user);
    }
    public Cell removeNumber() {
        return new Cell(position, immutable, Optional.empty(), user);
    }
    public Cell setUser(User user) {
        return new Cell(position, immutable, number, Optional.of(user));
    }
    public Cell removeUser() {
        return new Cell(position, immutable, number, Optional.empty());
    }
}
