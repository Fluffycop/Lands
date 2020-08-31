package com.github.fluffycop.lands.entity;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;

public class ChunkPosition {
    private final String world;
    private final int x;
    private final int z;

    public ChunkPosition(World world, int x, int z) {
        this.world = world.getName();
        this.x = x;
        this.z = z;
    }

    public ChunkPosition(Chunk chunk) {
        this(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public String getWorldName() {
        return world;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public long getChunkKey() {
        return Chunk.getChunkKey(x, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPosition that = (ChunkPosition) o;
        return x == that.x &&
               z == that.z &&
               Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z);
    }


}
