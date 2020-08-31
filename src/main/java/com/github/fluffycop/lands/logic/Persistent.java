package com.github.fluffycop.lands.logic;

public interface Persistent<T> {
    T getPersistentData();

    void populate(T data);
}
