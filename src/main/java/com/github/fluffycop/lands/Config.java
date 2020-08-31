package com.github.fluffycop.lands;

import com.github.fluffycop.lands.util.FileUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Config {
    //boilerplate
    private static final ObjectMapper<Config> MAPPER;

    static {
        try {
            MAPPER = ObjectMapper.forClass(Config.class); // We hold on to the instance of our ObjectMapper
        } catch (ObjectMappingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Config loadFrom(ConfigurationNode node) throws ObjectMappingException {
        return MAPPER.bindToNew().populate(node);
    }

    public ConfigurationNode saveTo(ConfigurationNode node) throws ObjectMappingException {
        MAPPER.bind(this).serialize(node);
        return node;
    }

    //singleton stuff
    private static volatile Config INSTANCE;

    public static Config get() {
        return INSTANCE;
    }

    public static void init(LandsPlugin pl) {
        Log.info("Initializing the config...");
        INSTANCE = load(pl);
        Log.info("Successfully initialized the config.");
    }

    public static void set(Config cfg) {
        INSTANCE = cfg;
    }

    public static Config load(LandsPlugin pl) {
        File configFile = new File(pl.getDataFolder() + File.separator + "config.conf");
        Config cfg = null;
        if (!firstRun(configFile, pl)) {
            Log.info("Reading config.conf...");
            HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                    .setFile(configFile)
                    .build();
            ConfigurationNode node;
            try {
                node = loader.load();
                cfg = loadFrom(node);
            } catch (IOException e) {
                Log.severe("Encountered an error while trying to read the config");
                throw new IOError(e);
            } catch (ObjectMappingException e) {
                Log.severe("Encountered an error while trying to read the config");
                throw new Error(e);
            }
        }
        return cfg == null ? new Config() : cfg;
    }

    private static boolean firstRun(File configFile, LandsPlugin pl) {
        if (!configFile.exists()) {
            Log.info("config.conf does not exist. Generating a default config file...");
            try {
                pl.getDataFolder().mkdirs();
                configFile.createNewFile();
                FileUtil.copyInputStreamToFile(pl.getResource("config.conf"), configFile);
            } catch (IOException e) {
                Log.severe("Encountered an error while trying to generate a config file.");
                throw new IOError(e);
            }
        }
        return !configFile.exists();
    }

    @Setting(comment = "in seconds")
    public int inviteExpire = 60;
    @Setting(comment = "in seconds")
    public int autosavePeriod = 300;
    @Setting
    public boolean playersCanAccessWild = true;
}
