package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

/**
 * Winding, thin volcanic lava rivers / canals cut into cavern floors in Molten Depths. Lined with
 * magma beds and natural stepping stone bridges every few blocks to maintain full walking
 * traversability while creating high-speed lava highways for Scorchsteel armor.
 */
public final class MoltenCanalFeature extends Feature<NoneFeatureConfiguration> {
    public MoltenCanalFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var origin = context.origin();
        var level = context.level();
        var random = context.random();

        if (origin.getY() < 14
                || origin.getY() > level.getMaxBuildHeight() - 16
                || !level.getBlockState(origin).isAir()
                || !natural(level.getBlockState(origin.below()))) {
            return false;
        }

        if (!level.getBiome(origin).is(DarkCaverns.id("molten_depths"))) {
            return false;
        }

        return buildCanal(level, origin, random);
    }

    private static boolean buildCanal(WorldGenLevel level, BlockPos origin, RandomSource random) {
        int length = 18 + random.nextInt(14);
        double angle = random.nextDouble() * Math.PI * 2;

        Set<BlockPos> lava = new LinkedHashSet<>();
        Set<BlockPos> air = new LinkedHashSet<>();
        Set<BlockPos> bed = new LinkedHashSet<>();
        Set<BlockPos> crossings = new LinkedHashSet<>();

        double x = origin.getX();
        double z = origin.getZ();
        int y = origin.below().getY();

        int successfulSteps = 0;

        for (int step = 0; step < length; step++) {
            int bx = (int) Math.round(x);
            int bz = (int) Math.round(z);

            // Find the cavern floor surface around this position (+/- 1 block)
            int floorY = Integer.MIN_VALUE;
            for (int dy : new int[] {0, 1, -1}) {
                BlockPos check = new BlockPos(bx, y + dy, bz);
                if (level.getBlockState(check.above()).isAir()
                        && natural(level.getBlockState(check))) {
                    floorY = y + dy;
                    break;
                }
            }

            // If the floor drops by 2+ or rises by 2+, terminate the canal path cleanly
            if (floorY == Integer.MIN_VALUE) {
                break;
            }
            y = floorY;

            // Compute perpendicular direction for optional 2-block wide section
            double perpAngle = angle + Math.PI / 2.0;
            int nx = (int) Math.round(Math.cos(perpAngle));
            int nz = (int) Math.round(Math.sin(perpAngle));

            // Natural stepping crossing every 6 blocks
            boolean isCrossing = (step > 1 && step < length - 2 && (step % 6 == 0));

            BlockPos centerFloor = new BlockPos(bx, y, bz);
            var channelCols = new LinkedHashSet<BlockPos>();
            channelCols.add(centerFloor);

            // Optional 2nd block width if flat and wide enough (thin river)
            BlockPos sideFloor = centerFloor.offset(nx, 0, nz);
            if (level.getBlockState(sideFloor.above()).isAir()
                    && natural(level.getBlockState(sideFloor))
                    && random.nextFloat() < 0.60F) {
                channelCols.add(sideFloor);
            }

            for (BlockPos col : channelCols) {
                if (isCrossing) {
                    crossings.add(col);
                    bed.add(col.below());
                } else {
                    lava.add(col);
                    bed.add(col.below());
                }
                for (int ya = 1; ya <= 2; ya++) {
                    air.add(col.above(ya));
                }
            }

            x += Math.cos(angle);
            z += Math.sin(angle);
            angle += (random.nextDouble() - 0.5) * 0.40;
            successfulSteps++;
        }

        // Need at least 8 segments and 12 lava cells for a meaningful canal
        if (successfulSteps < 8 || lava.size() < 12) {
            return false;
        }

        // Strict containment check: no lava cell may leak into air or open drops
        for (BlockPos pos : lava) {
            if (!level.ensureCanWrite(pos) || !natural(level.getBlockState(pos))) {
                return false;
            }
            BlockPos below = pos.below();
            if (!bed.contains(below) && !natural(level.getBlockState(below))) {
                return false;
            }
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos wall = pos.relative(direction);
                if (!lava.contains(wall)
                        && !crossings.contains(wall)
                        && !natural(level.getBlockState(wall))) {
                    return false;
                }
            }
        }

        for (BlockPos pos : air) {
            BlockState state = level.getBlockState(pos);
            if (!level.ensureCanWrite(pos) || !state.isAir()) {
                return false;
            }
        }

        // Apply edits
        air.forEach(pos -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2));

        for (BlockPos pos : bed) {
            if (level.ensureCanWrite(pos)) {
                BlockState bedState =
                        random.nextFloat() < 0.60F
                                ? Blocks.MAGMA_BLOCK.defaultBlockState()
                                : CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState();
                level.setBlock(pos, bedState, 2);
            }
        }

        // River banks: subtle glowing magma on surface ground only
        for (BlockPos pos : lava) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos bank = pos.relative(dir);
                if (!lava.contains(bank) && !crossings.contains(bank)) {
                    if (level.ensureCanWrite(bank)
                            && natural(level.getBlockState(bank))
                            && level.getBlockState(bank.above()).isAir()) {
                        if (random.nextFloat() < 0.25F) {
                            level.setBlock(bank, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        // Stepping stone crossings: natural full blocks across the stream
        for (BlockPos pos : crossings) {
            BlockState crossingState =
                    random.nextBoolean()
                            ? Blocks.MAGMA_BLOCK.defaultBlockState()
                            : CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState();
            level.setBlock(pos, crossingState, 2);
        }

        lava.forEach(pos -> level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 2));
        int tickDelay = lavaTickDelay(level);
        lava.forEach(pos -> level.scheduleTick(pos, Fluids.LAVA, tickDelay));

        return true;
    }

    private static int lavaTickDelay(WorldGenLevel level) {
        try {
            return Fluids.LAVA.getTickDelay(level);
        } catch (Throwable ignored) {
            return 30;
        }
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())
                || state.is(Blocks.MAGMA_BLOCK);
    }
}
