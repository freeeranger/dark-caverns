package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

/** Horizontal, finite-width biome blending. Caches belong to one surface pass, never a world. */
public final class BiomeTransition {
    public static final int ROCKY = 0;
    public static final int FOREST = 1;
    public static final int MOLTEN = 2;
    public static final int HALLOW = 3;
    // Surface/feature generation guarantees BIOMES only in the adjacent chunks. With this
    // grid and radius, even vanilla's fuzzy biome lookup stays inside that dependency ring.
    public static final int RADIUS = 16;
    private static final int GRID = 8;
    private final Function<BlockPos, Holder<Biome>> biomes;
    private static final ResourceLocation FOREST_ID = DarkCaverns.id("glimmershroom_forest");
    private static final ResourceLocation MOLTEN_ID = DarkCaverns.id("molten_depths");
    private static final ResourceLocation HALLOW_ID = DarkCaverns.id("tangled_hallow");
    private final Long2IntOpenHashMap samples = new Long2IntOpenHashMap();

    public BiomeTransition(Function<BlockPos, Holder<Biome>> biomes) {
        this.biomes = biomes;
    }

    public static int identity(Holder<Biome> biome) {
        if (biome.is(FOREST_ID)) return FOREST;
        if (biome.is(MOLTEN_ID)) return MOLTEN;
        if (biome.is(HALLOW_ID)) return HALLOW;
        return ROCKY;
    }

    public Weights weights(int x, int z) {
        double forest = 0;
        double molten = 0;
        double hallow = 0;
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
                if (biome == HALLOW) hallow += w;
                total += w;
            }
        }
        return new Weights(forest / total, molten / total, hallow / total);
    }

    public record Weights(double forest, double molten, double hallow) {
        public Weights(double forest, double molten) {
            this(forest, molten, 0);
        }

        public double cover(int biome) {
            // Direct forest/heat contact exposes a rocky buffer on both sides.
            return switch (biome) {
                case FOREST -> forest * (1 - molten) * (1 - molten);
                case HALLOW -> hallow * (1 - molten) * (1 - molten);
                case MOLTEN -> molten * (1 - forest - hallow) * (1 - forest - hallow);
                default -> Math.max(0, 1 - forest - molten - hallow);
            };
        }

        public int material(double patch) {
            if (patch < cover(MOLTEN)) return MOLTEN;
            if (patch >= 1 - cover(FOREST)) return FOREST;
            if (patch >= 1 - cover(FOREST) - cover(HALLOW)) return HALLOW;
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
