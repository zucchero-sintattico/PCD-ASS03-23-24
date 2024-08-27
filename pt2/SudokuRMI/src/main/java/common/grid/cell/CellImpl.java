package common.grid.cell;

import common.Point2d;
import common.user.UserData;

import java.util.Optional;

public class CellImpl implements Cell {

    private final Point2d position;
    private final boolean immutable;
    private final Integer number;
    private final UserData user;

    public CellImpl(Point2d position) {
        this(position, false, null, null);
    }

    public CellImpl(Point2d position, boolean immutable) {
        this(position, immutable, null, null);
    }

    public CellImpl(Point2d position, boolean immutable, Integer number, UserData user) {
        this.position = position;
        this.immutable = immutable;
        this.number = number;
        this.user = user;
    }

    @Override
    public Point2d position() {
        return position;
    }

    @Override
    public boolean immutable() {
        return immutable;
    }

    @Override
    public Optional<Integer> number() {
        return Optional.ofNullable(number);
    }

    @Override
    public Optional<UserData> user() {
        return Optional.ofNullable(user);
    }

    @Override
    public Cell setNumber(int number) {
        return new CellImpl(position, immutable, number, user);
    }

    @Override
    public Cell setImmutable(boolean immutable) {
        return new CellImpl(position, immutable, number, user);
    }

    @Override
    public Cell removeNumber() {
        return new CellImpl(position, immutable, null, user);
    }

    @Override
    public Cell setUser(UserData user) {
        return new CellImpl(position, immutable, number, user);
    }

    @Override
    public Cell removeUser() {
        return new CellImpl(position, immutable, number, null);
    }

}