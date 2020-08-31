package com.github.fluffycop.lands;

import com.github.fluffycop.lands.command.CommandManager;
import com.github.fluffycop.lands.engine.EngineModule;
import com.github.fluffycop.lands.engine.LandProtectionModule;
import com.github.fluffycop.lands.logic.InviteManager;
import com.github.fluffycop.lands.logic.LandManager;
import com.github.fluffycop.lands.logic.RelationManager;
import com.github.fluffycop.lands.logic.TownManager;
import com.github.fluffycop.lands.logic.impl.InviteManagerImpl;
import com.github.fluffycop.lands.logic.impl.json.JsonLandManager;
import com.github.fluffycop.lands.logic.impl.json.JsonRelationManager;
import com.github.fluffycop.lands.logic.impl.json.JsonTownManager;
import com.github.fluffycop.lands.logic.PersistenceManager;
import com.github.fluffycop.lands.logic.impl.json.JsonPersistenceManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LandsPlugin extends JavaPlugin {

    private PersistenceManager persistenceManager;

    private TownManager<?> townManager;
    private LandManager<?> landManager;
    private RelationManager<?> relationManager;

    private InviteManager inviteManager;

    private CommandManager commandManager;

    private EngineModule[] modules;

    private boolean malformedSetup = false;

    @Override
    public void onEnable() {
        try {
            Log.init(this.getLogger());
            Config.init(this);

            //persistent aspects
            townManager = new JsonTownManager(this);
            landManager = new JsonLandManager(this);
            relationManager = new JsonRelationManager(this);

            persistenceManager = new JsonPersistenceManager(this);
            persistenceManager.setup();
            loadPersistentManagers();
            setupAutosave();

            //not persistent stuff
            inviteManager = new InviteManagerImpl(this);

            setupListeners();

            commandManager = new CommandManager(this);
            commandManager.configureManager();
            commandManager.setupCommands();
        } catch (Exception e) {
            e.printStackTrace();
            malformedSetup = true;
            getPluginLoader().disablePlugin(this);
        }
    }

    private void setupListeners() {
        modules = new EngineModule[]{
                new LandProtectionModule(this)
        };
        for (EngineModule module : modules) {
            module.setupListeners();
        }
    }

    private void setupAutosave() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, new AutosaveRunnable(this), Config.get().autosavePeriod * 20);
    }

    private void loadPersistentManagers() {
        try {
            //source of truth for towns & if they're valid, so needs to be loaded in before everything else
            persistenceManager.loadTowns().get();
            Future<Void> relationsJob = persistenceManager.loadRelations();
            Future<Void> landJob = persistenceManager.loadLand();

            landJob.get();
            relationsJob.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.severe("Encountered an issue while loading data. Aborting startup process.");
            throw new Error(e);
        }
    }

    @Override
    public void onDisable() {
        if(!malformedSetup) {
            persistenceManager.shutdown();
        } else {
            Log.info("Detected malformed setup. Aborting save processes.");
        }
    }

    public InviteManager getInviteManager() {
        return inviteManager;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public TownManager<?> getTownManager() {
        return townManager;
    }

    public LandManager<?> getLandManager() {
        return landManager;
    }

    public RelationManager<?> getRelationManager() {
        return relationManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
