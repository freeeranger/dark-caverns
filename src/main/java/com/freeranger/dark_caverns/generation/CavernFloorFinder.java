package com.freeranger.dark_caverns.generation;

import java.util.List;
import java.util.OptionalInt;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Selects a dry supported floor with clearance over the complete structure footprint. */
public final class CavernFloorFinder {
    public static final int MAX_FOUNDATION_DEPTH = 4;

    private CavernFloorFinder() {}

    public static OptionalInt find(
            List<NoiseColumn> footprint,
            int minY,
            int maxY,
            int structureHeight,
            RandomSource random) {
        int selected = 0;
        int candidates = 0;
        int bestFoundation = Integer.MAX_VALUE;
        for (int y = minY; y <= maxY - structureHeight; y++) {
            if (!fits(footprint, y, structureHeight)) continue;
            int foundation = 0;
            for (var column : footprint) foundation += foundationDepth(column, y) - 1;
            if (foundation > bestFoundation) continue;
            if (foundation < bestFoundation) {
                bestFoundation = foundation;
                candidates = 0;
            }
            // Prefer minimal foundations; equally suitable floors have no height bias.
            if (random.nextInt(++candidates) == 0) selected = y;
        }
        return candidates == 0 ? OptionalInt.empty() : OptionalInt.of(selected);
    }

    private static boolean fits(List<NoiseColumn> footprint, int floorY, int height) {
        if (footprint.isEmpty()) return false;
        for (var column : footprint) {
            if (foundationDepth(column, floorY) == 0) return false;
            var floor = column.getBlock(floorY);
            if (!floor.getFluidState().isEmpty() || floor.is(Blocks.BEDROCK)) return false;
            // The template includes its ground at local Y=0. Leave one air block above its roof.
            for (int y = floorY + 1; y <= floorY + height; y++) {
                if (!column.getBlock(y).isAir()) return false;
            }
        }
        return true;
    }

    private static boolean support(BlockState state) {
        return !state.isAir() && state.getFluidState().isEmpty() && !state.is(Blocks.BEDROCK);
    }

    public static int foundationDepth(NoiseColumn column, int floorY) {
        for (int depth = 1; depth <= MAX_FOUNDATION_DEPTH; depth++) {
            var state = column.getBlock(floorY - depth);
            if (state.isAir()) continue;
            return support(state) && support(column.getBlock(floorY - depth - 1)) ? depth : 0;
        }
        return 0;
    }
}
