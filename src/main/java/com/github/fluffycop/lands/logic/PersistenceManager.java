package com.github.fluffycop.lands.logic;

import com.github.fluffycop.lands.logic.LandManager;
import com.github.fluffycop.lands.logic.RelationManager;
import com.github.fluffycop.lands.logic.TownManager;

import java.util.concurrent.Future;

public interface PersistenceManager {
    void setup();

    Future<Void> saveRelations();
    Future<Void> saveLand();
    Future<Void> saveTowns();

    Future<Void> loadRelations();
    Future<Void> loadLand();
    Future<Void> loadTowns();

    void shutdown();
}
