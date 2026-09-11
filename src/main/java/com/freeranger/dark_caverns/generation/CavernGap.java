package com.freeranger.dark_caverns.generation;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

/** The two non-air boundaries surrounding an air block. Fluids are boundaries, never clearance. */
public record CavernGap(BlockPos floor, BlockPos ceiling) {
    public int height() {
        return ceiling.getY() - floor.getY() - 1;
    }

    public static Optional<CavernGap> find(BlockGetter level, BlockPos origin, int maxSearch) {
        if (level.isOutsideBuildHeight(origin) || !level.getBlockState(origin).isAir())
            return Optional.empty();
        BlockPos.MutableBlockPos floor = origin.mutable();
        BlockPos.MutableBlockPos ceiling = origin.mutable();
        int bottom = Math.max(level.getMinBuildHeight(), origin.getY() - maxSearch);
        int top = Math.min(level.getMaxBuildHeight() - 1, origin.getY() + maxSearch);
        while (floor.getY() > bottom && level.getBlockState(floor).isAir()) floor.move(0, -1, 0);
        while (ceiling.getY() < top && level.getBlockState(ceiling).isAir()) ceiling.move(0, 1, 0);
        if (level.getBlockState(floor).isAir() || level.getBlockState(ceiling).isAir())
            return Optional.empty();
        return Optional.of(new CavernGap(floor.immutable(), ceiling.immutable()));
    }
}
