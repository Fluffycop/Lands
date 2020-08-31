package com.github.fluffycop.lands.entity;

import java.util.Objects;

public class Town {
    private final String name;

    public Town(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return name.equals(town.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
