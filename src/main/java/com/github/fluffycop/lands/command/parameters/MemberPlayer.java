package com.github.fluffycop.lands.command.parameters;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.entity.Player;

public class MemberPlayer extends OnlinePlayer {
    public MemberPlayer(Player player) {
        super(player);
    }

    @Override
    public String toString() {
        return "MemberPlayer{" +
               "player=" + player +
               '}';
    }
}
