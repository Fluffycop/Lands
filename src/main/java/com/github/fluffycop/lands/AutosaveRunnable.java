package com.github.fluffycop.lands;

import org.bukkit.Bukkit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AutosaveRunnable implements Runnable {
    private final LandsPlugin pl;

    public AutosaveRunnable(LandsPlugin pl) {
        this.pl = pl;
    }

    @Override
    public void run() {
        Log.info("Autosaving...");
        try {
            Future<Void> landFuture = pl.getPersistenceManager().saveLand();
            Future<Void> relationsFuture = pl.getPersistenceManager().saveRelations();
            Future<Void> townsFuture =pl.getPersistenceManager().saveTowns();

            landFuture.get();
            relationsFuture.get();
            townsFuture.get();
            Log.info("Successfully autosaved. Scheduling next autosave in " + Config.get().autosavePeriod + " seconds");
        } catch (InterruptedException | ExecutionException e) {
            Log.severe("Encountered an issue while autosaving.");
            throw new Error(e);
        } finally {
            Bukkit.getScheduler().runTaskLater(pl, new AutosaveRunnable(pl), Config.get().autosavePeriod * 20);
        }
    }
}
