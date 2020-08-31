package com.github.fluffycop.lands.logic.impl.json;

import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.entity.Town;
import com.github.fluffycop.lands.logic.impl.AbstractRelationManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JsonRelationManager extends AbstractRelationManager<Map<String, TownRelationData>> {

    public JsonRelationManager(LandsPlugin pl) {
        super(pl);
    }

    @Override
    public Map<String, TownRelationData> getPersistentData() {
        Map<String, TownRelationData> data = new HashMap<>();
        townToLeader.forEach((town, leader) -> {
            if(pl.getTownManager().isValid(town)) {
                data.putIfAbsent(town.getName(), new TownRelationData());
                data.get(town.getName()).setLeaderUid(leader.getUniqueId().toString());
            }
        });
        townToPlayerMap.forEach((town, playerSet) -> {
            TownRelationData townRelationData = data.get(town.getName());
            if (townRelationData != null && pl.getTownManager().isValid(town)) {
                townRelationData.setMemberUids(
                        playerSet.stream()
                                .map(p -> p.getUniqueId().toString())
                                .collect(Collectors.toSet())
                );
            }
        });
        return data;
    }

    @Override
    public void populate(Map<String, TownRelationData> data) {
        data.forEach((townName, tData) -> {
            Town town = pl.getTownManager().getTown(townName);
            if (pl.getTownManager().isValid(town)) {
                OfflinePlayer leader = Bukkit.getOfflinePlayer(UUID.fromString(tData.getLeaderUid()));
                Set<OfflinePlayer> playerSet = tData.getMemberUids().stream()
                        .map(UUID::fromString)
                        .map(Bukkit::getOfflinePlayer)
                        .collect(Collectors.toSet());
                townToLeader.put(town, leader);
                playerSet.forEach(p -> playerToTownMap.put(p, town));
                Set<OfflinePlayer> threadSafeSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
                threadSafeSet.addAll(playerSet);
                townToPlayerMap.put(town, playerSet);
            }
        });
    }
}
