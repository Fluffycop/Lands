package com.github.fluffycop.lands.entity;

import com.github.fluffycop.lands.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Invite {

    private final Town town;
    private final UUID uuid;
    private final long expireTime;

    public Invite(Town town, Player player) {
        this.town = town;
        this.uuid = player.getUniqueId();
        this.expireTime = System.currentTimeMillis() + Config.get().inviteExpire * 1000;
    }

    public Town getTown() {
        return town;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public boolean isValid() {
        return Bukkit.getOfflinePlayer(uuid).isOnline() && System.currentTimeMillis() < expireTime;
    }
}
