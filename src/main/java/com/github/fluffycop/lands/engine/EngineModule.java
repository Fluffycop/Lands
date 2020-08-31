package com.github.fluffycop.lands.engine;

import com.github.fluffycop.lands.LandsPlugin;

public abstract class EngineModule {
    protected final LandsPlugin pl;

    public EngineModule(LandsPlugin pl) {
        this.pl = pl;
    }

    public abstract void setupListeners();
}
