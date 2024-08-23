package org.src.model.grid.cell;

import org.src.common.Point2d;
import org.src.model.user.UserData;

import java.io.Serializable;
import java.util.Optional;

public interface Cell extends Serializable {

    Point2d position();

    boolean immutable();

    Optional<Integer> number();

    Optional<UserData> user();

    Cell setNumber(int number);

    Cell setImmutable(boolean immutable);

    Cell removeNumber();

    Cell setUser(UserData user);

    Cell removeUser();

}