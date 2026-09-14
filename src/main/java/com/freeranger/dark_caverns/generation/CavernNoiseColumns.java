package com.freeranger.dark_caverns.generation;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.AbstractList;
import java.util.List;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/** Lazy, candidate-local base columns. Adjacent columns share a vanilla interpolation cell. */
public final class CavernNoiseColumns {
    private CavernNoiseColumns() {}

    public static List<NoiseColumn> footprint(
            ChunkGenerator generator,
            LevelHeightAccessor height,
            RandomState random,
            BoundingBox bounds) {
        // Do not bypass a third-party generator's overridden base-column behavior, aquifers,
        // or ore-vein sampling. The Dark Caverns generator uses neither of those subsystems.
        NoiseGeneratorSettings settings =
                generator.getClass() == NoiseBasedChunkGenerator.class
                        ? ((NoiseBasedChunkGenerator) generator).generatorSettings().value()
                        : null;
        var noise =
                settings == null ? null : settings.noiseSettings().clampToHeightAccessor(height);
        boolean batch =
                settings != null
                        && !settings.isAquifersEnabled()
                        && !settings.oreVeinsEnabled()
                        && noise.height() > 0
                        && Math.floorMod(noise.minY(), noise.getCellHeight()) == 0
                        && noise.height() % noise.getCellHeight() == 0;
        return new AbstractList<>() {
            private final NoiseColumn[] columns =
                    new NoiseColumn[bounds.getXSpan() * bounds.getZSpan()];
            private final Long2ObjectOpenHashMap<NoiseColumn[]> cells =
                    new Long2ObjectOpenHashMap<>();

            @Override
            public int size() {
                return columns.length;
            }

            @Override
            public NoiseColumn get(int index) {
                java.util.Objects.checkIndex(index, size());
                if (columns[index] != null) return columns[index];
                int x = bounds.minX() + index / bounds.getZSpan();
                int z = bounds.minZ() + index % bounds.getZSpan();
                if (!batch) return columns[index] = generator.getBaseColumn(x, z, height, random);
                int width = settings.noiseSettings().getCellWidth();
                int cx = Math.floorDiv(x, width), cz = Math.floorDiv(z, width);
                var cell =
                        cells.computeIfAbsent(
                                ChunkPos.asLong(cx, cz),
                                key ->
                                        new Cell(random, cx * width, cz * width, settings, height)
                                                .columns());
                return columns[index] =
                        cell[Math.floorMod(x, width) * width + Math.floorMod(z, width)];
            }
        };
    }

    private static final class Cell extends NoiseChunk {
        private final int x, z;
        private final NoiseGeneratorSettings settings;
        private final LevelHeightAccessor height;

        Cell(
                RandomState random,
                int x,
                int z,
                NoiseGeneratorSettings settings,
                LevelHeightAccessor height) {
            super(
                    1,
                    random,
                    x,
                    z,
                    settings.noiseSettings().clampToHeightAccessor(height),
                    DensityFunctions.BeardifierMarker.INSTANCE,
                    settings,
                    fluids(settings),
                    Blender.empty());
            this.x = x;
            this.z = z;
            this.settings = settings;
            this.height = height;
        }

        NoiseColumn[] columns() {
            var noise = settings.noiseSettings().clampToHeightAccessor(height);
            int width = noise.getCellWidth(), cellHeight = noise.getCellHeight();
            int minY = noise.minY(), countY = noise.height() / cellHeight;
            BlockState[][] states = new BlockState[width * width][noise.height()];
            initializeForFirstCellX();
            advanceCellX(0);
            try {
                for (int cy = countY - 1; cy >= 0; cy--) {
                    selectCellYZ(cy, 0);
                    for (int dy = cellHeight - 1; dy >= 0; dy--) {
                        int offsetY = cy * cellHeight + dy;
                        updateForY(minY + offsetY, dy / (double) cellHeight);
                        for (int dx = 0; dx < width; dx++) {
                            updateForX(x + dx, dx / (double) width);
                            for (int dz = 0; dz < width; dz++) {
                                updateForZ(z + dz, dz / (double) width);
                                BlockState state = getInterpolatedState();
                                states[dx * width + dz][offsetY] =
                                        state == null ? settings.defaultBlock() : state;
                            }
                        }
                    }
                }
            } finally {
                stopInterpolation();
            }
            NoiseColumn[] result = new NoiseColumn[states.length];
            for (int i = 0; i < result.length; i++) result[i] = new NoiseColumn(minY, states[i]);
            return result;
        }

        private static Aquifer.FluidPicker fluids(NoiseGeneratorSettings settings) {
            // Same global fluid picker as NoiseBasedChunkGenerator.getBaseColumn.
            var lava = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
            var sea = new Aquifer.FluidStatus(settings.seaLevel(), settings.defaultFluid());
            return (x, y, z) -> y < Math.min(-54, settings.seaLevel()) ? lava : sea;
        }
    }
}
