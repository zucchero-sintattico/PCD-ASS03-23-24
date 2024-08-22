package org.src.model.grid.cell;

import org.src.common.Point2d;
import org.src.model.User;

import java.util.Optional;

public interface Cell {
    Point2d position();

    boolean immutable();

    Optional<Integer> number();

    Optional<User> user();

    Cell setNumber(int number);

    Cell setImmutable(boolean immutable);

    Cell removeNumber();

    Cell setUser(User user);

    Cell removeUser();
}
