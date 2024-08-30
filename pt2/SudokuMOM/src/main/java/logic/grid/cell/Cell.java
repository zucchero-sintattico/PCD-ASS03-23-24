package logic.grid.cell;

import common.Point2d;
import logic.user.User;

import java.util.Optional;

public interface Cell {

    Point2d getPosition();

    Optional<User> isSelected();
    void selectCell(User user);

    Optional<Integer> getNumber();
    void setNumber(int number);
    void setAtEmpty();

    boolean isImmutable();
    void setImmutable(boolean immutable);
    
}