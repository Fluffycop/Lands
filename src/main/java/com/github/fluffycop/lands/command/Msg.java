package com.github.fluffycop.lands.command;

import com.github.fluffycop.lands.util.StringUtil;
import org.bukkit.ChatColor;

public enum Msg {

    CREATE_TOWN(ChatColor.GRAY + "You have created a town called " + ChatColor.GREEN + "%s",
            "%s created a new town called: %s"),
    TOWN_NAME_TAKEN(ChatColor.RED + "This town name is already taken", null),

    DISBAND_TOWN(ChatColor.GRAY + "You have disbanded your town.",
            "%s disbanded their town: %s"),
    DISBAND_TOWN_OTHER(ChatColor.GREEN + "%s" + ChatColor.GRAY + " has disbanded your town.", null),

    CHUNK_ALREADY_CLAIMED("This chunk is already claimed", null),
    CLAIM_CHUNK(ChatColor.GREEN + "%s" + ChatColor.GRAY + " claimed chunk a chunk for your town",
            "%s claimed x: %s z: %s in world %s for %s"),

    UNCLAIM_CHUNK(ChatColor.GREEN + "%s" + ChatColor.GRAY + " unclaimed chunk a chunk for your town",
            "%s unclaimed x: %s z: %s in world %s for %s"),
    DONT_OWN_CHUNK(null, ChatColor.RED + "You do not own this land."),

    INVITE("You have been invited to " + ChatColor.GREEN + "%s",
            "%s invited %s for %s"),
    INVITE_RECEIVER(ChatColor.GRAY + "You have been invited to " + ChatColor.GREEN + "%s", null),
    INVITE_FAIL(ChatColor.RED + "This player already has a pending invite.", null)
    ;
    public final String msg;
    public final String log;

    Msg(String msg, String log) {
        this.msg = msg;
        this.log = log;
    }

    public String formatLog(Object... params) {
        return String.format(this.log, getArgs(params));
    }

    public String formatMsg(Object... params) {
        return String.format(this.msg, getArgs(params));
    }

    private String[] getArgs(Object... params) {
        return params == null ? new String[0] : StringUtil.fromParams(params);
    }
}
