package com.freeranger.dark_caverns.generation;

import java.lang.ref.WeakReference;
import java.util.Objects;
import net.minecraft.world.level.WorldGenLevel;

/** One bounded tile per worker, keyed by generation-region identity, never just world seed. */
public final class TransitionPlacementCache {
    private static final ThreadLocal<Tile> LOCAL = new ThreadLocal<>();

    private TransitionPlacementCache() {}

    public static BiomeTransition.Weights weights(WorldGenLevel level, int x, int z) {
        int cx = Math.floorDiv(x, 16), cz = Math.floorDiv(z, 16);
        Tile tile = LOCAL.get();
        if (tile == null || tile.level.get() != level || tile.x != cx || tile.z != cz) {
            tile = new Tile(level, cx, cz);
            LOCAL.set(tile);
        }
        int index = Math.floorMod(x, 16) * 16 + Math.floorMod(z, 16);
        if (tile.weights[index] == null) tile.weights[index] = tile.blend.weights(x, z);
        return tile.weights[index];
    }

    private static final class Tile {
        private final WeakReference<WorldGenLevel> level;
        private final int x, z;
        private final BiomeTransition blend;
        private final BiomeTransition.Weights[] weights = new BiomeTransition.Weights[256];

        Tile(WorldGenLevel level, int x, int z) {
            this.level = new WeakReference<>(level);
            this.x = x;
            this.z = z;
            // The retained callback must not keep the region, chunks or server alive.
            WeakReference<WorldGenLevel> reference = this.level;
            this.blend =
                    new BiomeTransition(
                            pos -> Objects.requireNonNull(reference.get()).getBiome(pos));
        }
    }
}
