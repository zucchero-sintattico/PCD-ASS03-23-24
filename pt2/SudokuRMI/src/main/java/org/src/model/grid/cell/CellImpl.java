package org.src.model.grid.cell;

import org.src.common.Point2d;
import org.src.model.user.UserDataImpl;

import java.io.Serializable;
import java.util.Optional;

public class CellImpl implements Cell, Serializable {

    private final Point2d position;
    private final boolean immutable;
    private final Integer number;
    private final UserDataImpl user;

    public CellImpl(Point2d position) {
        this(position, false, null, null);
    }

    public CellImpl(Point2d position, boolean immutable) {
        this(position, immutable, null, null);
    }

    public CellImpl(Point2d position, boolean immutable, Integer number, UserDataImpl user) {
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
    public Optional<UserDataImpl> user() {
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
    public Cell setUser(UserDataImpl user) {
        return new CellImpl(position, immutable, number, user);
    }

    @Override
    public Cell removeUser() {
        return new CellImpl(position, immutable, number, null);
    }
}

