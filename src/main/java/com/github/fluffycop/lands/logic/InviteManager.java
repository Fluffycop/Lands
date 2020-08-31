package com.github.fluffycop.lands.logic;

import com.github.fluffycop.lands.entity.Invite;
import com.github.fluffycop.lands.entity.Town;
import org.bukkit.entity.Player;

public interface InviteManager {
    boolean invite(Town town, Player player);
    boolean uninvite(Town town, Player player);

    Invite getInvite(Town town, Player player, boolean mustBeValid);
}
