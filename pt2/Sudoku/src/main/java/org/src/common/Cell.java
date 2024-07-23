package org.src.common;

import java.util.Optional;

public interface Cell {

    Point2d getPosition();

    Optional<User> isSelected();
    void selectCell(User user);

    Optional<Integer> getNumber();
    void setNumber(int number);
}
