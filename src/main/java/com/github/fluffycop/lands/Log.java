package com.github.fluffycop.lands;

import java.util.logging.Logger;

public class Log {
    private static Logger LOGGER;

    public static void init(Logger logger) {
        LOGGER = logger;
    }

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void severe(String msg) {
        LOGGER.severe(msg);
    }

    public static void warning(String msg) {
        LOGGER.warning(msg);
    }
}
