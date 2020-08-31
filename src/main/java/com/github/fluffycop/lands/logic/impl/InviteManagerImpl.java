package com.github.fluffycop.lands.logic.impl;

import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.entity.Invite;
import com.github.fluffycop.lands.entity.Town;
import com.github.fluffycop.lands.logic.InviteManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InviteManagerImpl implements InviteManager {
    private final LandsPlugin pl;

    ConcurrentMap<Town, ConcurrentMap<OfflinePlayer, Invite>> townPlayerToInviteMap = new ConcurrentHashMap<>();

    public InviteManagerImpl(LandsPlugin pl) {
        this.pl = pl;
    }

    @Override
    public boolean invite(Town town, Player player) {
        Invite currInv = getInvite(town, player, true);
        if(currInv == null) {
            this.put(town, player, new Invite(town, player));
        }
        return currInv == null;
    }



    @Override
    public boolean uninvite(Town town, Player player) { //remove invite regardless of validity
        return this.remove(town, player) != null;
    }

    private Invite put(Town town, Player player, Invite invite) {
        townPlayerToInviteMap.putIfAbsent(town, new ConcurrentHashMap<>());
        Map<OfflinePlayer, Invite> invites = townPlayerToInviteMap.get(town);
        return invites.put(player, invite);
    }

    private Invite remove(Town town, Player player) {
        Map<OfflinePlayer, Invite> invites = townPlayerToInviteMap.get(town);
        if(invites != null) {
            return invites.remove(player);
        }
        return null;
    }

    @Override
    public Invite getInvite(Town town, Player player, boolean mustBeValid) {
        Map<OfflinePlayer, Invite> invites = townPlayerToInviteMap.get(town);
        if(invites != null) {
            Invite inv = invites.get(player);
            if(mustBeValid) {
                return inv.isValid() ? inv : null;
            } else {
                return inv;
            }
        }
        return null;
    }
}
