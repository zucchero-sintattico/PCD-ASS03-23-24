package org.src.model;

import org.src.common.Cell;
import org.src.common.Point2d;
import org.src.common.User;

import java.util.Optional;

public class CellImpl implements Cell {

    private final Point2d position;
    private boolean immutable;
    private Integer number;
    private User user;

    public CellImpl(Point2d point2d) {
        this(point2d, false);
    }

    public CellImpl(Point2d point2d, boolean immutable) {
        this.position = point2d;
        this.immutable = immutable;
    }

    @Override
    public Point2d getPosition() {
        return position;
    }

    @Override
    public Optional<User> isSelected() {
        return Optional.ofNullable(user);
    }

    @Override
    public void select(User user) {
        this.user = user;
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
    public void setAtEmpty() {
        this.number = null;
    }

    @Override
    public boolean isImmutable() {
        return this.immutable;
    }

    @Override
    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }

    @Override
    public String toString() {
        return "CellImpl{" +
                "position=" + position +
                ", immutable=" + immutable +
                ", number=" + number +
                ", user=" + user +
                '}';
    }
}
