package org.src.common;

import java.io.Serializable;
import java.util.Optional;

public interface Cell extends Serializable {

    Point2d getPosition();

    Optional<User> isSelected();
    void select(User user);

    Optional<Integer> getNumber();
    void setNumber(int number);
    void setAtEmpty();

    boolean isImmutable();
    void setImmutable(boolean immutable);
}
