package org.src.model.grid.cell;

import org.src.common.Point2d;
import org.src.model.user.UserDataImpl;

import java.util.Optional;

public interface Cell {
    Point2d position();

    boolean immutable();

    Optional<Integer> number();

    Optional<UserDataImpl> user();

    Cell setNumber(int number);

    Cell setImmutable(boolean immutable);

    Cell removeNumber();

    Cell setUser(UserDataImpl user);

    Cell removeUser();
}
