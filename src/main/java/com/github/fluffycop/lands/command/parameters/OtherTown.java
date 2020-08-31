package com.github.fluffycop.lands.command.parameters;

import com.github.fluffycop.lands.entity.Town;

public class OtherTown {
    public final Town town;

    public OtherTown(Town other) {
        this.town = other;
    }

    public Town getTown() {
        return this.town;
    }
}
