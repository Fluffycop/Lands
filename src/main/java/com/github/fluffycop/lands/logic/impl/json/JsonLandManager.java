package com.github.fluffycop.lands.logic.impl.json;

import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.entity.ChunkPosition;
import com.github.fluffycop.lands.entity.Town;
import com.github.fluffycop.lands.logic.impl.AbstractLandManager;

import java.util.*;

public class JsonLandManager extends AbstractLandManager<Map<String, Set<ChunkPosition>>> {

    public JsonLandManager(LandsPlugin pl) {
        super(pl);
    }

    @Override
    public Map<String, Set<ChunkPosition>> getPersistentData() {
        Map<String, Set<ChunkPosition>> data = new HashMap<>();
        landToTownMap.forEach((chunkPos, town) -> {
            if(pl.getTownManager().isValid(town)) {
                String key = town.getName();
                data.putIfAbsent(key, new HashSet<>());
                data.get(key).add(chunkPos);
            }
        });
        return data;
    }

    @Override
    public void populate(Map<String, Set<ChunkPosition>> data) {
        data.forEach((townStr, chunkPosSet) -> {
            Town town = pl.getTownManager().getTown(townStr);
            if(pl.getTownManager().isValid(town)) {
                chunkPosSet.forEach(pos -> {
                    landToTownMap.put(pos, town);
                });
                townToLandMap.put(town, chunkPosSet);
            }
        });
    }
}
