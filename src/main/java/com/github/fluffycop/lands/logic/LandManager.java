package com.github.fluffycop.lands.logic;

import com.github.fluffycop.lands.entity.ChunkPosition;
import com.github.fluffycop.lands.entity.Town;

import java.util.Set;

public interface LandManager<T> extends Persistent<T> {
    Town claim(Town town, ChunkPosition position);

    default boolean isOwned(ChunkPosition position) {
        return getOwner(position) != null;
    }

    Town unclaim(ChunkPosition position);

    Town getOwner(ChunkPosition position);

    Set<ChunkPosition> unclaimAll(Town town);

    default void delete(Town town) {
        unclaimAll(town);
    }
}
