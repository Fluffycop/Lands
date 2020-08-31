package com.github.fluffycop.lands.logic.impl.json;

import com.github.fluffycop.lands.LandsPlugin;
import com.github.fluffycop.lands.entity.Town;
import com.github.fluffycop.lands.logic.impl.AbstractTownManager;

import java.util.Set;

public class JsonTownManager extends AbstractTownManager<Set<String>> {

    public JsonTownManager(LandsPlugin pl) {
        super(pl);
    }

    @Override
    public Set<String> getPersistentData() {
        return stringToTownMap.keySet();
    }

    @Override
    public void populate(Set<String> data) {
        data.forEach(name -> stringToTownMap.put(name, new Town(name)));
    }
}
