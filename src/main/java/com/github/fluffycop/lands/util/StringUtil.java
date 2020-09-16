package com.github.fluffycop.lands.util;

import com.github.fluffycop.lands.entity.Town;
import org.bukkit.command.CommandSender;

public interface StringUtil {
    static String[] fromParams(Object... params) {
        String[] args = new String[params.length];
        for (int i = 0; i < args.length; i++) {
            Object o = params[i];
            if (o instanceof Town) {
                args[i] = ((Town)o).getName();
            } else if (o instanceof CommandSender) {
                args[i] = ((CommandSender)o).getName();
            } else if (o instanceof String) {
                args[i] = (String)o;
            } else {
                args[i] = o + "";
            }
        }
        return args;
    }
}
