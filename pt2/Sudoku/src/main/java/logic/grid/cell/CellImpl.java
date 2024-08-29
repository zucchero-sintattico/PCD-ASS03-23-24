package logic.grid.cell;

import common.Point2d;
import logic.user.User;

import java.util.Optional;

public class CellImpl implements Cell {

    private final Point2d position;
    private User user;
    private Integer number;
    private boolean immutable;

    public CellImpl(Point2d point2d) {
        this.position = point2d;
    }

    public CellImpl(Point2d point2d, boolean isImmutable) {
        this.position = point2d;
        this.immutable = isImmutable;
    }

    @Override
    public Point2d getPosition() {
        return this.position;
    }

    @Override
    public Optional<User> isSelected() {
        return Optional.ofNullable(this.user);
    }

    @Override
    public void selectCell(User user) {
        this.user = user;
    }

    @Override
    public Optional<Integer> getNumber() {
        return Optional.ofNullable(this.number);
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

}