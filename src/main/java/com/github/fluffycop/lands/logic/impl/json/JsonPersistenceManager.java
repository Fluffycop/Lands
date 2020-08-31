package com.github.fluffycop.lands.logic.impl.json;

import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.Log;
import com.github.fluffycop.lands.entity.ChunkPosition;
import com.github.fluffycop.lands.logic.PersistenceManager;
import com.github.fluffycop.lands.logic.TownManager;
import com.github.fluffycop.lands.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


//TODO relations and land are split up into files, so write some code to serialize and write an object to a file
public class JsonPersistenceManager implements PersistenceManager {

    private final LandsPlugin pl;

    private final File relationsFolder;
    private final File landFolder;
    private final File townsFile;

    public JsonPersistenceManager(LandsPlugin pl) {
        this.pl = pl;
        this.relationsFolder = new File(pl.getDataFolder() + File.separator + "data" + File.separator + "relations");
        this.landFolder = new File(pl.getDataFolder() + File.separator + "data" + File.separator + "land");
        this.townsFile = new File(pl.getDataFolder() + File.separator + "data" + File.separator + "towns.json");
    }

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //setup file paths and whatnot
    @Override
    public void setup() {
        firstRun();
    }

    private void firstRun() {
        if (!relationsFolder.exists()) {
            relationsFolder.mkdirs();
        }
        if (!landFolder.exists()) {
            landFolder.mkdirs();
        }
        if (!townsFile.exists()) {
            try {
                townsFile.createNewFile();
            } catch (IOException e) {
                throw new IOError(e);
            }
        }
    }

    private final ExecutorService relationsPool = Executors.newSingleThreadExecutor();
    private final ExecutorService landPool = Executors.newSingleThreadExecutor();
    private final ExecutorService townPool = Executors.newSingleThreadExecutor();

    @Override
    public Future<Void> saveRelations() {
        return relationsPool.submit(() -> {
            Log.info("Saving relation data...");
            JsonRelationManager manager = (JsonRelationManager) pl.getRelationManager();
            saveManagerData(relationsFolder, manager.getPersistentData());
            Log.info("Finished saving relations data");
        }, null);
    }

    @Override
    public Future<Void> saveLand() {
        return landPool.submit(() -> {
            Log.info("Saving land data...");
            JsonLandManager manager = (JsonLandManager) pl.getLandManager();
            saveManagerData(landFolder, manager.getPersistentData());
            Log.info("Finished saving land data");
        }, null);
    }

    @Override
    public Future<Void> saveTowns() {
        return townPool.submit(() -> {
            Log.info("Saving town data...");
            JsonTownManager manager = (JsonTownManager) pl.getTownManager();
            Set<String> dataSet = manager.getPersistentData();
            createAndSave(this.townsFile, dataSet);
            Log.info("Finished saving town data.");
            purgeDeletedTowns(dataSet);
        }, null);
    }

    private void purgeDeletedTowns(Set<String> set) {
        Log.info("Purging deleted towns...");
        File[] relFiles = relationsFolder.listFiles();
        File[] landFiles = landFolder.listFiles();
        if(relFiles != null) {
            for(File file : relFiles) {
                if(!set.contains(FileUtil.nameWithoutExtension(file))) {
                    file.delete();
                }
            }
        } else {
            throw new IOError(new IOException("An unexpected error occurred while trying to list the files in " + relationsFolder.getAbsolutePath() + "."));
        }
        if (landFiles != null) {
            for(File file : landFiles) {
                if(!set.contains(FileUtil.nameWithoutExtension(file))) {
                    file.delete();
                }
            }
        } else {
            throw new IOError(new IOException("An unexpected error occurred while trying to list the files in " + landFolder.getAbsolutePath() + "."));
        }
    }

    private <T> void saveManagerData(File folder, Map<String, T> dataMap) {
        dataMap.forEach((fileName, data) -> {
            File file = new File(folder.getAbsolutePath() + File.separator + fileName + ".json");
            createAndSave(file, data);
        });
    }

    private <T> void createAndSave(File file, T data) {
        if (!file.exists()) {
            Log.info(file.getName() + " doesn't exist. Creating a new file...");
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.severe("Encountered an issue while creating " + file.getName());
                throw new IOError(e);
            }
        }
        String json = gson.toJson(data);
        try {
            FileUtil.write(json, file);
        } catch (IOException e) {
            Log.info("Encountered an issue while writing to " + file.getName());
            throw new IOError(e);
        }
    }

    private <T> T readAndDeserialize(File file, Class<T> type) {
        return readAndDeserialize(file, new TypeToken<T>() {
        });
    }

    private <T> T readAndDeserialize(File file, TypeToken<T> type) {
        if (!FileUtil.isEmpty(file)) {
            Log.info("Reading " + file.getName());
            String json;
            try {
                json = FileUtil.read(file);
            } catch (IOException e) {
                Log.severe("Encountered an exception while reading " + file.getName());
                throw new IOError(e);
            }
            return gson.fromJson(json, type.getType());
        } else {
            Log.info(file.getName() + " is empty, skipping...");
            return null;
        }
    }

    private <T> Map<String, T> loadManagerData(File folder, TypeToken<T> type) {
        File[] files = folder.listFiles();
        if (files != null) {
            Map<String, T> dataMap = new HashMap<>();
            for (File file : files) {
                String townName = FileUtil.nameWithoutExtension(file);
                T data = readAndDeserialize(file, type);
                if (data != null) {
                    dataMap.put(townName, data);
                }
            }
            return dataMap;
        } else {
            throw new IOError(new IOException("An unexpected error occurred while trying to list the files in " + folder.getAbsolutePath() + "."));
        }
    }

    @Override
    public Future<Void> loadRelations() {
        return relationsPool.submit(() -> {
            Log.info("Loading relations data...");
            Map<String, TownRelationData> dataMap = loadManagerData(relationsFolder, new TypeToken<TownRelationData>(){});
            JsonRelationManager manager = (JsonRelationManager) pl.getRelationManager();
            manager.populate(dataMap);
            Log.info("Finished loading relations data.");
        }, null);
    }

    @Override
    public Future<Void> loadLand() {
        return landPool.submit(() -> {
            Log.info("Loading land data...");
            Map<String, Set<ChunkPosition>> dataMap = loadManagerData(landFolder, new TypeToken<Set<ChunkPosition>>(){});
            JsonLandManager manager = (JsonLandManager) pl.getLandManager();
            manager.populate(dataMap);
            Log.info("Finished loading land data.");
        }, null);
    }

    @Override
    public Future<Void> loadTowns() {
        return townPool.submit(() -> {
            Log.info("Loading data from " + townsFile.getName() + "...");
            Set<String> data = readAndDeserialize(townsFile, new TypeToken<Set<String>>() {
            });
            if (data != null) {
                JsonTownManager manager = (JsonTownManager) pl.getTownManager();
                manager.populate(data);
            }
            Log.info("Finished loading data from " + townsFile.getName());
        }, null);
    }

    @Override
    public void shutdown() {
        try {
            this.saveTowns().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new Error(e);
        }
        this.saveLand();
        this.saveRelations();

        relationsPool.shutdown();
        landPool.shutdown();
        townPool.shutdown();
    }
}
