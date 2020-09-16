package com.github.fluffycop.lands.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.*;
import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.Log;
import com.github.fluffycop.lands.command.parameters.MemberPlayer;
import com.github.fluffycop.lands.command.parameters.OtherTown;
import com.github.fluffycop.lands.command.parameters.TownlessPlayer;
import com.github.fluffycop.lands.command.parameters.WrappedString;
import com.github.fluffycop.lands.entity.ChunkPosition;
import com.github.fluffycop.lands.entity.Town;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@CommandAlias("town|t")
public class CmdTown extends BaseCommand {
    private final LandsPlugin pl;

    public CmdTown(LandsPlugin pl) {
        this.pl = pl;
    }

    @HelpCommand
    @Default
    @CatchUnknown
    @Syntax("[page]")
    @Description("Shows the help menu")
    @CommandPermission("lands.help")
    public void help(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @CommandPermission("lands.cmd.create")
    @Description("Create a new town")
    @Syntax("<name>")
    public void create(@Conditions("town:status=townless") Town town, Player sender, WrappedString name) {
        Town newTown = pl.getTownManager().createTown(name.str);
        if (newTown == null) {
            throw new ConditionFailedException(Msg.TOWN_NAME_TAKEN.msg);
        } else {
            Log.info(Msg.CREATE_TOWN.formatLog(sender, newTown));
            pl.getRelationManager().makeLeader(newTown, sender);
            pl.getRelationManager().addPlayer(newTown, sender);
            sender.sendMessage(Msg.CREATE_TOWN.formatMsg(newTown));
        }
    }

    @Subcommand("disband")
    @CommandPermission("lands.cmd.disband")
    @Description("Disband your town")
    @Syntax("")
    public void disband(@Conditions("town:status=leader") Town town, Player sender) {
        Log.info(Msg.DISBAND_TOWN.formatLog(sender, town));
        pl.getTownManager().deleteTown(town);
        pl.getRelationManager().delete(town);
        pl.getLandManager().delete(town);
        sender.sendMessage(Msg.DISBAND_TOWN.formatMsg());
        msgMembers(town, p -> !p.equals(sender), Msg.DISBAND_TOWN_OTHER.formatMsg(sender))
    }

    @Subcommand("claim")
    @CommandPermission("lands.cmd.claim")
    @Description("Claim the chunk you are currently in")
    @Syntax("")
    public void claim(@Conditions("town:status=leader") Town town, ChunkPosition pos, Player sender) {
        if (pl.getLandManager().getOwner(pos) != null) {
            throw new ConditionFailedException(Msg.CHUNK_ALREADY_CLAIMED.msg);
        }
        Log.info(Msg.CLAIM_CHUNK.formatMsg(sender, pos.getX(), pos.getZ(), pos.getWorldName(), town));
        pl.getLandManager().claim(town, pos);
        sender.sendMessage(Msg.CLAIM_CHUNK.formatMsg("You"));
        msgMembers(town, p -> !p.equals(sender), Msg.CLAIM_CHUNK.formatMsg(sender));
    }

    @Subcommand("unclaim")
    @CommandPermission("lands.cmd.unclaim")
    @Description("Unclaim the chunk you are currently in")
    @Syntax("")
    public void unclaim(@Conditions("town:status=leader") Town town, ChunkPosition pos, Player sender) {
        Town currOwner = pl.getLandManager().getOwner(pos);
        if (!town.equals(currOwner)) {
            throw new ConditionFailedException(Msg.DONT_OWN_CHUNK.msg);
        }
        Log.info(Msg.UNCLAIM_CHUNK.formatLog(sender, pos.getX(), pos.getZ(), pos.getWorldName(), town));
        pl.getLandManager().unclaim(pos);
        sender.sendMessage(Msg.UNCLAIM_CHUNK.formatMsg("You"));
        msgMembers(town, p -> !p.equals(sender), Msg.UNCLAIM_CHUNK.formatMsg(sender));
    }

    @Subcommand("invite")
    @CommandPermission("lands.cmd.invite")
    @Syntax("<player>")
    public void invite(@Conditions("town:status=leader") Town town, Player sender, TownlessPlayer target) {
        boolean success = pl.getInviteManager().invite(town, target.player);
        if (success) {
            Log.info(sender.getName() + " invited " + target.player.getName() + " for " + town.getName());
            sender.sendMessage(ChatColor.GRAY + "Invited " + ChatColor.GREEN + target.player.getName() + ChatColor.GRAY + " to your town.");
            target.player.sendMessage(ChatColor.GRAY + "You have been invited to " + ChatColor.GREEN + town.getName());
        } else {
            throw new ConditionFailedException("This player already has a pending invite.");
        }
    }

    @Subcommand("join")
    @CommandPermission("lands.cmd.join")
    @Syntax("<town>")
    public void join(@Conditions("town:status=townless") Town town, Player sender, OtherTown otherTown) {
        boolean wasInvited = pl.getInviteManager().uninvite(otherTown.town, sender);
        if (!wasInvited) {
            throw new ConditionFailedException("You were not invited to that town.");
        }
        Log.info(sender.getName() + " joined " + town.getName());
        pl.getRelationManager().addPlayer(otherTown.town, sender);
        sender.sendMessage(ChatColor.GRAY + "You have joined " + ChatColor.GREEN + otherTown.town.getName());
        pl.getRelationManager().getOnlinePlayers(otherTown.town)
                .forEach(p -> p.sendMessage(ChatColor.GREEN + sender.getName() + ChatColor.GRAY + " has joined your town."));

    }

    @Subcommand("uninvite")
    @CommandPermission("lands.cmd.uninvite")
    @Syntax("<player>")
    public void uninvite(@Conditions("town:status=leader") Town town, Player sender, TownlessPlayer target) {
        boolean wasInvited = pl.getInviteManager().uninvite(town, target.player);
        if (!wasInvited) {
            throw new ConditionFailedException("That player does not have a pending invite.");
        }
        Log.info(sender.getName() + " uninvited " + target.player.getName() + " for " + town.getName());
        sender.sendMessage(ChatColor.GRAY + "Uninvited " + ChatColor.GREEN + sender.getName() + " from your town.");
    }

    @Subcommand("leave")
    @CommandPermission("lands.cmd.leave")
    @Syntax("")
    public void leave(@Conditions("town:status=member") Town town, Player sender) {
        boolean isLeader = pl.getRelationManager().isLeader(town, sender);
        if (isLeader) {
            throw new ConditionFailedException("You cannot leave while being the leader of a town. Try resigning from your position first.");
        }
        Log.info(sender.getName() + " left town: " + town.getName());
        pl.getRelationManager().removePlayer(town, sender);
        sender.sendMessage(ChatColor.GRAY + "You left " + ChatColor.GREEN + town.getName() + ChatColor.GRAY + ".");
        pl.getRelationManager().getOnlinePlayers(town).stream()
                .filter(p -> !p.equals(sender))
                .forEach(p -> p.sendMessage(ChatColor.GREEN + sender.getName() + " has left your town."));
    }

    @Subcommand("resign")
    @CommandPermission("lands.cmd.resign")
    @Syntax("<player>")
    public void resign(@Conditions("town:status=leader") Town town, Player sender, MemberPlayer target) {
        if (target.player.equals(sender)) {
            throw new ConditionFailedException("You cannot resign leadership to yourself.");
        }
        Log.info(sender.getName() + " resigned from  town " + town.getName());
        pl.getRelationManager().makeLeader(town, target.player);
        sender.sendMessage(ChatColor.GRAY + "You have resigned leadership to " + ChatColor.GREEN + target.player.getName());
        target.player.sendMessage(ChatColor.GREEN + sender.getName() + ChatColor.GRAY + " has given you leadership of the town.");
        pl.getRelationManager().getOnlinePlayers(town).stream()
                .filter(p -> !p.equals(sender) || !p.equals(target.player))
                .forEach(p -> p.sendMessage(ChatColor.GREEN + sender.getName() + ChatColor.GRAY + " has resigned from leadership."));
    }

    @Subcommand("kick")
    @CommandPermission("lands.cmd.kick")
    @Syntax("<player>")
    public void kick(@Conditions("town:status=leader") Town town, Player sender, MemberPlayer target) {
        if (target.player.equals(sender)) {
            throw new ConditionFailedException("You cannot kick yourself.");
        }
        Log.info(sender.getName() + " was kicked from town " + town.getName() + " by " + sender.getName());
        pl.getRelationManager().removePlayer(town, target.player);
        sender.sendMessage(ChatColor.GRAY + "You have kicked out " + ChatColor.GREEN + target.player.getName());
        target.player.sendMessage(ChatColor.GRAY + "You have been kicked by " + ChatColor.GREEN + sender.getName());
        pl.getRelationManager().getOnlinePlayers(town).stream()
                .filter(p -> !p.equals(sender))
                .forEach(p -> p.sendMessage(ChatColor.GREEN + sender.getName() + ChatColor.GRAY + " has kicked out " + ChatColor.GREEN + target.player.getName()));
    }

    private void msgMembers(Town town, String msg) {
        msgMembers(town, p -> true, msg);
    }

    private void msgMembers(Town town, Predicate<OfflinePlayer> filter, String msg) {
        pl.getRelationManager().getOnlinePlayers(town).stream()
                .filter(filter)
                .forEach(p -> p.sendMessage(msg));
    }
}
