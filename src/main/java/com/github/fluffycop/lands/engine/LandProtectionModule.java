package com.github.fluffycop.lands.engine;

import com.github.fluffycop.lands.Config;
import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.entity.ChunkPosition;
import com.github.fluffycop.lands.entity.Town;
import ninja.egg82.events.BukkitEvents;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class LandProtectionModule extends EngineModule {
    public LandProtectionModule(LandsPlugin pl) {
        super(pl);
    }

    private static final String ERROR_MESSAGE = ChatColor.RED + "You cannot do that here.";

    @Override
    public void setupListeners() {
        BukkitEvents.subscribe(pl, BlockBreakEvent.class, EventPriority.NORMAL)
                .handler(e -> {
                    if(!hasBuildAccess(e.getBlock(), e.getPlayer())) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ERROR_MESSAGE);
                    }
                });
        BukkitEvents.subscribe(pl, BlockPlaceEvent.class, EventPriority.NORMAL)
                .handler(e -> {
                    if(!hasBuildAccess(e.getBlock(), e.getPlayer())) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ERROR_MESSAGE);
                    }
                });
        BukkitEvents.merge(pl, PlayerBucketEvent.class, PlayerBucketFillEvent.class, PlayerBucketEmptyEvent.class)
                .handler(e -> {
                    if(!hasBuildAccess(e.getBlock(), e.getPlayer())) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ERROR_MESSAGE);
                    }
                });
    }

    private boolean hasBuildAccess(Block block, Player player) {
        ChunkPosition pos = new ChunkPosition(block.getChunk());
        Town owner = pl.getLandManager().getOwner(pos);
        Town playerTown = pl.getRelationManager().getTown(player);
        return (Config.get().playersCanAccessWild && owner == null) || owner.equals(playerTown);
    }
}
