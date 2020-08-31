package com.github.fluffycop.lands.logic.impl;

import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.entity.Town;
import com.github.fluffycop.lands.logic.TownManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractTownManager<T> implements TownManager<T> {
    protected final LandsPlugin pl;

    protected final ConcurrentMap<String, Town> stringToTownMap = new ConcurrentHashMap<>();

    public AbstractTownManager(LandsPlugin pl) {
        this.pl = pl;
    }

    private Town get(String name) {
        return stringToTownMap.get(name.toLowerCase());
    }

    private Town put(String name, Town town) {
        return stringToTownMap.put(name.toLowerCase(), town);
    }

    private Town remove(String name) {
        return stringToTownMap.remove(name.toLowerCase());
    }

    @Override
    public Town getTown(String name) {
        return get(name.toLowerCase());
    }

    @Override
    public Town createTown(String name) {
        Town currNameHolder = get(name);
        if(currNameHolder != null) {
            return null;
        }
        Town newTown = new Town(name);
        put(name, newTown);
        return newTown;
    }

    @Override
    public boolean deleteTown(Town town) {
        return remove(town.getName()) != null;
    }

    @Override
    public Collection<Town> getAllTowns() {
        return stringToTownMap.values();
    }
}
