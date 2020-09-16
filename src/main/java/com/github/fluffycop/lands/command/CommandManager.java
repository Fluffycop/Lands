package com.github.fluffycop.lands.command;

import co.aikar.commands.*;
import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.command.parameters.MemberPlayer;
import com.github.fluffycop.lands.command.parameters.TownlessPlayer;
import com.github.fluffycop.lands.command.parameters.WrappedString;
import com.github.fluffycop.lands.entity.ChunkPosition;
import com.github.fluffycop.lands.command.parameters.OtherTown;
import com.github.fluffycop.lands.entity.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class CommandManager {

    private final LandsPlugin pl;
    private final PaperCommandManager manager;

    private final CmdTown cmdTown;
    private final CmdLands cmdLands;

    public CommandManager(LandsPlugin pl) {
        this.pl = pl;
        this.manager = new PaperCommandManager(pl);
        //init commands
        cmdTown = new CmdTown(pl);
        cmdLands = new CmdLands(pl);
    }

    public void configureManager() {
        manager.enableUnstableAPI("help");
        manager.setFormat(MessageType.ERROR, ChatColor.RED);
        manager.setFormat(MessageType.SYNTAX, ChatColor.RED, ChatColor.RED, ChatColor.RED);
        manager.setFormat(MessageType.HELP, ChatColor.GOLD, ChatColor.YELLOW);
        manager.setFormat(MessageType.INFO, ChatColor.GRAY, ChatColor.GREEN);

        registerContexts();
        registerConditions();
        registerCompletions();
    }

    private void registerContexts() {
        manager.getCommandContexts().registerIssuerOnlyContext(Town.class, c -> {
            Player player = c.getPlayer();
            if(player == null) {
                throw new InvalidCommandArgument("Town is a player only context");
            }
            return pl.getRelationManager().getTown(player);
        });
        //OtherTown because this is an argument not a calling context
        manager.getCommandContexts().registerIssuerAwareContext(OtherTown.class, c -> {
            String townName = c.popFirstArg();
            Town town = pl.getTownManager().getTown(townName);
            if(town == null) {
                throw new InvalidCommandArgument("That is not a valid town");
            }
            return new OtherTown(town);
        });
        manager.getCommandContexts().registerIssuerOnlyContext(ChunkPosition.class, c -> {
            Player player = c.getPlayer();
            if(player == null) {
                throw new InvalidCommandArgument("ChunkPosition is a player only context");
            }
            return new ChunkPosition(player.getChunk());
        });
        manager.getCommandContexts().registerContext(TownlessPlayer.class, c -> {
            String name = c.popFirstArg();
            Player player = Bukkit.getPlayer(name);
            if(player == null) {
                throw new ConditionFailedException("That player is not online.");
            }
            if(pl.getRelationManager().getTown(player) != null) {
                throw new ConditionFailedException("That player is already in a town.");
            }
            return new TownlessPlayer(player);
        });
        manager.getCommandContexts().registerContext(MemberPlayer.class, c -> {
            String name = c.popFirstArg();
            Player player = Bukkit.getPlayer(name);
            if(player == null) {
                throw new ConditionFailedException("That player is not online.");
            }
            if(!pl.getRelationManager().getTown(player).equals(pl.getRelationManager().getTown(c.getPlayer()))) {
                throw new ConditionFailedException("That player is not in your town.");
            }
            return new MemberPlayer(player);
        });
        manager.getCommandContexts().registerContext(WrappedString.class, c -> {
            String str = c.popFirstArg();
            return new WrappedString(str);
        });
    }

    private void registerConditions() {
        manager.getCommandConditions().addCondition(Town.class, "town", (c, exec, t) -> { //status:town or status:townless
            //the status we want is not in a town
            //a player in a town will throw an error
            if(c.getConfigValue("status", "townless").equals("townless")
               && pl.getRelationManager().isInATown(c.getIssuer().getPlayer())) {
                throw new ConditionFailedException("You cannot do this while in a town.");
            }
            //status we want is in a town
            //a townless player will throw an error
            if(c.getConfigValue("status", "townless").equals("member")
               && !pl.getRelationManager().isInATown(c.getIssuer().getPlayer())) {
                throw new ConditionFailedException("You must be in a town to do this.");
            }
            //must be a leader
            //a member or townless player will fail the condition
            if(c.getConfigValue("status", "townless").equals("leader")) {
                Player player = c.getIssuer().getPlayer();
                if(!(pl.getRelationManager().isInATown(player)
                     && pl.getRelationManager().isLeader(t, c.getIssuer().getPlayer()))) {
                    throw new ConditionFailedException("You must be a leader of your town to do this.");
                }
            }
        });
    }

    private void registerCompletions() {
        manager.getCommandCompletions().registerAsyncCompletion("town",
                c -> pl.getTownManager().getAllTowns().stream().map(Town::getName).collect(Collectors.toSet()));
    }

    public void setupCommands() {
        manager.registerCommand(cmdTown);
        manager.registerCommand(cmdLands);
    }
}
