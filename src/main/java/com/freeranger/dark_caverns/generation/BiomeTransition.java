package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

/** Horizontal, finite-width biome blending. Caches belong to one surface pass, never a world. */
public final class BiomeTransition {
    public static final int ROCKY = 0;
    public static final int FOREST = 1;
    public static final int MOLTEN = 2;
    // Surface/feature generation guarantees BIOMES only in the adjacent chunks. With this
    // grid and radius, even vanilla's fuzzy biome lookup stays inside that dependency ring.
    public static final int RADIUS = 16;
    private static final int GRID = 8;
    private final Function<BlockPos, Holder<Biome>> biomes;
    private final Map<Long, Integer> samples = new HashMap<>();

    public BiomeTransition(Function<BlockPos, Holder<Biome>> biomes) {
        this.biomes = biomes;
    }

    public static int identity(Holder<Biome> biome) {
        if (biome.is(DarkCaverns.id("glimmershroom_forest"))) return FOREST;
        if (biome.is(DarkCaverns.id("molten_depths"))) return MOLTEN;
        return ROCKY;
    }

    public Weights weights(int x, int z) {
        double forest = 0;
        double molten = 0;
        double total = 0;
        int gx = Math.floorDiv(x, GRID);
        int gz = Math.floorDiv(z, GRID);
        // A separable tent kernel stays continuous at lattice and chunk boundaries.
        for (int ix = gx - 1; ix <= gx + 2; ix++) {
            double wx = Math.max(0, 1 - Math.abs(ix * GRID - x) / (double) RADIUS);
            for (int iz = gz - 1; iz <= gz + 2; iz++) {
                double w = wx * Math.max(0, 1 - Math.abs(iz * GRID - z) / (double) RADIUS);
                if (w == 0) continue;
                final int bx = ix * GRID;
                final int bz = iz * GRID;
                int biome =
                        samples.computeIfAbsent(
                                BlockPos.asLong(bx, 0, bz),
                                key -> identity(biomes.apply(new BlockPos(bx, 128, bz))));
                if (biome == FOREST) forest += w;
                if (biome == MOLTEN) molten += w;
                total += w;
            }
        }
        return new Weights(forest / total, molten / total);
    }

    public record Weights(double forest, double molten) {
        public double cover(int biome) {
            // Direct forest/heat contact exposes a rocky buffer on both sides.
            return switch (biome) {
                case FOREST -> forest * (1 - molten) * (1 - molten);
                case MOLTEN -> molten * (1 - forest) * (1 - forest);
                default -> Math.max(0, 1 - forest - molten);
            };
        }

        public int material(double patch) {
            if (patch < cover(MOLTEN)) return MOLTEN;
            if (patch >= 1 - cover(FOREST)) return FOREST;
            return ROCKY;
        }

        public double chance(int biome, boolean core) {
            double weight = cover(biome);
            if (!core) return weight;
            double t = Math.clamp((weight - 0.55) / 0.45, 0, 1);
            return weight * t * t * (3 - 2 * t);
        }
    }
}
