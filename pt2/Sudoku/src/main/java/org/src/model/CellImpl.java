package org.src.model;

import org.src.common.Cell;
import org.src.common.Point2d;
import org.src.common.User;

import java.util.Optional;

public class CellImpl implements Cell {

    private final Point2d position;
    User userHowSelected = null;
    private Integer number;
    private boolean isImmutable = false;


    public CellImpl(Point2d point2d) {
        this.position = point2d;
    }
    public CellImpl(Point2d point2d, boolean isImmutable) {
        this.position = point2d;
        this.isImmutable = isImmutable;
    }

    @Override
    public Point2d getPosition() {
        return position;
    }

    @Override
    public Optional<User> isSelected() {
        return Optional.ofNullable(userHowSelected);
    }

    @Override
    public void selectCell(User user) {
        userHowSelected = user;
    }

    @Override
    public Optional<Integer> getNumber() {
        return Optional.ofNullable(number);
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean isImmutable() {
        return this.isImmutable;
    }

    @Override
    public void isImmutable(boolean immutable) {
        this.isImmutable = immutable;
    }
}
