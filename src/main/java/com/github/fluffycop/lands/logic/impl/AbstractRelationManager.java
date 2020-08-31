package com.github.fluffycop.lands.logic.impl;

import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.entity.Town;
import com.github.fluffycop.lands.logic.RelationManager;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractRelationManager<T> implements RelationManager<T> {

    protected final LandsPlugin pl;

    public AbstractRelationManager(LandsPlugin pl) {
        this.pl = pl;
    }

    protected final ConcurrentMap<Town, Set<OfflinePlayer>> townToPlayerMap = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Town, OfflinePlayer> townToLeader = new ConcurrentHashMap<>();
    protected final ConcurrentMap<OfflinePlayer, Town> playerToTownMap = new ConcurrentHashMap<>();

    @Override
    public boolean addPlayer(Town town, OfflinePlayer player) {
        townToPlayerMap.putIfAbsent(town, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        Town previousTown = playerToTownMap.put(player, town);
        townToPlayerMap.get(town).add(player);
        return previousTown != null;
    }

    @Override
    public boolean removePlayer(Town town, OfflinePlayer player) {
        Set<OfflinePlayer> players = townToPlayerMap.get(town);
        if(players != null) {
            players.remove(player);
        }
        return playerToTownMap.remove(player, town);
    }

    @Override
    public OfflinePlayer makeLeader(Town town, OfflinePlayer player) {
        return townToLeader.put(town, player);
    }

    @Override
    public Town getTown(OfflinePlayer player) {
        return playerToTownMap.get(player);
    }

    @Override
    public OfflinePlayer getLeader(Town town) {
        return townToLeader.get(town);
    }

    @Override
    public void delete(Town town) {
        Set<OfflinePlayer> players = townToPlayerMap.remove(town);
        if(players != null) {
            players.forEach(playerToTownMap::remove);
        }
        townToLeader.remove(town);
    }

    @Override
    public Collection<OfflinePlayer> getPlayers(Town town) {
        Set<OfflinePlayer> players = townToPlayerMap.get(town);
        return players == null ? Collections.newSetFromMap(new ConcurrentHashMap<>()) : players;
    }
}
