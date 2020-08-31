package com.github.fluffycop.lands.command.parameters;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TownlessPlayer extends OnlinePlayer {

    public TownlessPlayer(Player player) {
        super(player);
    }

    @Override
    public String toString() {
        return "TownlessPlayer{" +
               "player=" + player +
               '}';
    }
}
