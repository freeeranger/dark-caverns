package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Fallen timber, rooted stumps and low thickets arranged in sparse woodland pockets. */
public final class HallowClutterFeature extends Feature<NoneFeatureConfiguration> {
    private static final Direction[] HORIZONTAL = {
        Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    public HallowClutterFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (!exposedFloor(level, origin)) return false;

        var cluster = new LinkedHashMap<BlockPos, BlockState>();
        tryPiece(level, cluster, piece(level, origin, random));
        int satellites = 1 + random.nextInt(2);
        for (int index = 0; index < satellites; index++) {
            BlockPos nearby =
                    findNearbyFloor(
                            level,
                            origin.offset(random.nextInt(13) - 6, 0, random.nextInt(13) - 6));
            if (nearby != null) tryPiece(level, cluster, piece(level, nearby, random));
        }
        if (cluster.isEmpty()) return false;
        cluster.forEach((pos, state) -> level.setBlock(pos, state, 2));
        placeDeadwoodMud(level, cluster, random);
        return true;
    }

    private static void placeDeadwoodMud(
            WorldGenLevel level, Map<BlockPos, BlockState> cluster, RandomSource random) {
        var mud = new LinkedHashMap<BlockPos, BlockState>();
        for (var entry : cluster.entrySet()) {
            BlockState state = entry.getValue();
            if (!state.is(CustomBlocks.TWISTWOOD_LOG.get())) continue;
            float chance =
                    state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? .45F : .70F;
            BlockPos ground = entry.getKey().below();
            if (random.nextFloat() < chance && mudBase(level, ground))
                mud.put(ground, Blocks.MUD.defaultBlockState());
        }
        for (BlockPos ground : new ArrayList<>(mud.keySet())) {
            for (Direction direction : HORIZONTAL) {
                BlockPos nearby = ground.relative(direction);
                if (random.nextFloat() < .14F && mudGround(level, nearby))
                    mud.put(nearby, Blocks.MUD.defaultBlockState());
            }
        }
        mud.forEach((pos, state) -> level.setBlock(pos, state, 2));
    }

    private static boolean mudGround(WorldGenLevel level, BlockPos ground) {
        return mudBase(level, ground) && level.getBlockState(ground.above()).isAir();
    }

    private static boolean mudBase(WorldGenLevel level, BlockPos ground) {
        return level.ensureCanWrite(ground)
                && level.getBiome(ground).is(DarkCaverns.id("tangled_hallow"))
                && level.getBlockState(ground).is(CustomBlocks.OVERGROWN_CARFSTONE.get());
    }

    private static Map<BlockPos, BlockState> piece(
            WorldGenLevel level, BlockPos origin, RandomSource random) {
        float kind = random.nextFloat();
        if (kind < 0.50F) return fallenLog(level, origin, random);
        if (kind < 0.82F) return rootedStump(level, origin, random);
        return thicket(level, origin, random);
    }

    private static Map<BlockPos, BlockState> fallenLog(
            WorldGenLevel level, BlockPos origin, RandomSource random) {
        Direction direction = horizontal(random);
        BlockState log = log(direction.getAxis());
        int preferredLength = 4 + random.nextInt(4);
        for (int length = preferredLength; length >= 3; length--) {
            var piece = new LinkedHashMap<BlockPos, BlockState>();
            BlockPos start = origin.relative(direction, -(length / 2));
            boolean fits = true;
            for (int step = 0; step < length; step++) {
                BlockPos pos = start.relative(direction, step);
                if (!exposedFloor(level, pos)) {
                    fits = false;
                    break;
                }
                piece.put(pos, log);
            }
            if (!fits) continue;
            Direction branchDirection =
                    random.nextBoolean()
                            ? direction.getClockWise()
                            : direction.getCounterClockWise();
            BlockPos branch =
                    (random.nextBoolean() ? start : start.relative(direction, length - 1))
                            .relative(branchDirection);
            if (exposedFloor(level, branch)) piece.put(branch, log(branchDirection.getAxis()));
            return piece;
        }
        return Map.of();
    }

    private static Map<BlockPos, BlockState> rootedStump(
            WorldGenLevel level, BlockPos origin, RandomSource random) {
        var piece = new LinkedHashMap<BlockPos, BlockState>();
        int height = 1 + random.nextInt(2);
        for (int y = 0; y < height; y++) piece.put(origin.above(y), log(Direction.Axis.Y));
        int first = random.nextInt(HORIZONTAL.length);
        int roots = 2 + random.nextInt(3);
        for (int index = 0; index < roots; index++) {
            Direction direction = HORIZONTAL[(first + index) % HORIZONTAL.length];
            int length = 1 + random.nextInt(2);
            for (int step = 1; step <= length; step++) {
                BlockPos root = origin.relative(direction, step);
                if (!exposedFloor(level, root)) break;
                piece.put(root, log(direction.getAxis()));
            }
        }
        return piece;
    }

    private static Map<BlockPos, BlockState> thicket(
            WorldGenLevel level, BlockPos origin, RandomSource random) {
        var piece = new LinkedHashMap<BlockPos, BlockState>();
        piece.put(origin, log(Direction.Axis.Y));
        BlockState leaves =
                CustomBlocks.TWISTWOOD_LEAVES
                        .get()
                        .defaultBlockState()
                        .setValue(LeavesBlock.PERSISTENT, true)
                        .setValue(LeavesBlock.DISTANCE, 7);
        for (Direction direction : HORIZONTAL) {
            BlockPos leaf = origin.relative(direction);
            if (exposedFloor(level, leaf) && random.nextFloat() < 0.85F) piece.put(leaf, leaves);
        }
        piece.put(origin.above(), leaves);
        if (random.nextBoolean()) piece.put(origin.above(2), leaves);
        return piece;
    }

    private static void tryPiece(
            WorldGenLevel level,
            Map<BlockPos, BlockState> cluster,
            Map<BlockPos, BlockState> piece) {
        if (piece.isEmpty()) return;
        for (BlockPos pos : piece.keySet()) {
            if (cluster.containsKey(pos)
                    || !level.ensureCanWrite(pos)
                    || !level.getBlockState(pos).isAir()
                    || !level.getBiome(pos).is(DarkCaverns.id("tangled_hallow"))) return;
        }
        cluster.putAll(piece);
    }

    private static BlockPos findNearbyFloor(WorldGenLevel level, BlockPos center) {
        for (int distance = 0; distance <= 3; distance++) {
            BlockPos above = center.above(distance);
            if (exposedFloor(level, above)) return above;
            if (distance > 0) {
                BlockPos below = center.below(distance);
                if (exposedFloor(level, below)) return below;
            }
        }
        return null;
    }

    private static boolean exposedFloor(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir()
                && level.getBlockState(pos.above()).isAir()
                && level.getBlockState(pos.below()).is(CustomBlocks.OVERGROWN_CARFSTONE.get());
    }

    private static Direction horizontal(RandomSource random) {
        return HORIZONTAL[random.nextInt(HORIZONTAL.length)];
    }

    private static BlockState log(Direction.Axis axis) {
        return CustomBlocks.TWISTWOOD_LOG
                .get()
                .defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, axis);
    }
}
