package com.github.fluffycop.lands.logic.impl;

import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.entity.ChunkPosition;
import com.github.fluffycop.lands.entity.Town;
import com.github.fluffycop.lands.logic.LandManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractLandManager<T> implements LandManager<T> {
    protected final LandsPlugin pl;

    protected final ConcurrentMap<ChunkPosition, Town> landToTownMap = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Town, Set<ChunkPosition>> townToLandMap = new ConcurrentHashMap<>();

    public AbstractLandManager(LandsPlugin pl) {
        this.pl = pl;
    }

    @Override
    public Town claim(Town town, ChunkPosition position) {
        townToLandMap.putIfAbsent(town, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        Town previousOwner = landToTownMap.put(position, town);
        Set<ChunkPosition> positions = townToLandMap.get(town);
        if(positions != null) {
            positions.add(position);
        }
        return previousOwner;
    }

    @Override
    public Town unclaim(ChunkPosition position) {
        Town previousOwner = landToTownMap.remove(position);
        if(previousOwner != null) {
            Set<ChunkPosition> positions = townToLandMap.get(previousOwner);
            if(positions != null) {
                positions.remove(position);
            }
        }
        return previousOwner;
    }

    @Override
    public Town getOwner(ChunkPosition position) {
        return landToTownMap.get(position);
    }

    @Override
    public Set<ChunkPosition> unclaimAll(Town town) {
        Set<ChunkPosition> positions = townToLandMap.remove(town);
        if(positions != null) {
            positions.forEach(landToTownMap::remove);
        }
        return positions == null ? new HashSet<>() : positions;
    }
}
