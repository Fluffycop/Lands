package com.github.fluffycop.lands.logic;

import com.github.fluffycop.lands.entity.Town;

import java.util.Collection;

public interface TownManager<T> extends Persistent<T> {
    Town getTown(String name);
    Town createTown(String name);
    boolean deleteTown(Town name);

    default boolean isValid(String name) {
        return getTown(name) != null;
    }

    default boolean isValid(Town town) {
        return isValid(town.getName());
    }

    Collection<Town> getAllTowns();
}
