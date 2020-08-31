package com.github.fluffycop.lands.logic;

import com.github.fluffycop.lands.entity.Town;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public interface RelationManager<T> extends Persistent<T> {
    boolean addPlayer(Town town, OfflinePlayer player);

    boolean removePlayer(Town town, OfflinePlayer player);

    OfflinePlayer makeLeader(Town town, OfflinePlayer player);

    OfflinePlayer getLeader(Town town);

    Town getTown(OfflinePlayer player);

    default boolean isInATown(OfflinePlayer player) {
        return getTown(player) != null;
    }

    default boolean isInTown(Town town, OfflinePlayer player) {
        return getTown(player).equals(town);
    }

    default boolean isLeader(Town town, OfflinePlayer player) {
        return getLeader(town).equals(player);
    }

    void delete(Town town);

    Collection<OfflinePlayer> getPlayers(Town town);

    default Collection<Player> getOnlinePlayers(Town town) {
        return getPlayers(town).stream()
                .filter(OfflinePlayer::isOnline)
                .map(p -> (Player) p)
                .collect(Collectors.toSet());
    }
}
