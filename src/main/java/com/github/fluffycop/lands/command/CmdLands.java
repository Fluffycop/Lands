package com.github.fluffycop.lands.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.github.fluffycop.lands.Config;
import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.Log;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("lands")
public class CmdLands extends BaseCommand {

    private final LandsPlugin pl;

    public CmdLands(LandsPlugin pl) {
        this.pl = pl;
    }

    @CommandAlias("reload")
    @Syntax("")
    @CommandPermission("lands.admin.reload")
    public void reload(Player sender) {
        Log.info(sender.getName() + " has reloaded the config...");
        CompletableFuture.supplyAsync(() -> Config.load(pl))
                .thenAcceptAsync(cfg -> {
                    Config.set(cfg);
                    Log.info("Successfully reloaded the config.");
                    sender.sendMessage(ChatColor.GRAY + "Successfully reloaded the config");
                });
    }

    @CommandAlias("info")
    @Syntax("")
    @CommandPermission("lands.admin.info")
    public void info(Player sender) {
        sender.sendMessage("Running Lands v" + pl.getDescription().getVersion() + " by " + pl.getDescription().getAuthors().get(0));
    }

    @CatchUnknown
    @Default
    @Syntax("")
    @CommandAlias("help")
    public void help(Player sender, CommandHelp help) {
        help.showHelp();
    }
}
