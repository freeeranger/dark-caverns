package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** A rare cavern landmark with a thick trunk, grounded roots and a ceiling-aware crown. */
public final class GiantTwistwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final ResourceLocation TANGLED_HALLOW = DarkCaverns.id("tangled_hallow");
    private static final double TAU = Math.PI * 2;
    private static final double GOLDEN_ANGLE = 2.399963229728653;
    private static final int MIN_CLEARANCE = 54;
    private static final int MAX_CLEARANCE = 80;

    public GiantTwistwoodTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (!level.getBiome(origin).is(TANGLED_HALLOW)
                || !ground(level.getBlockState(origin.below()))
                || !supportedBase(level, origin)) return false;

        int clearance = 0;
        while (clearance < MAX_CLEARANCE && writable(level, origin.above(clearance))) clearance++;
        if (clearance < MIN_CLEARANCE) return false;
        int height = Math.min(52 + context.random().nextInt(21), clearance - 5);
        if (height < 48) return false;

        TreePlan plan = plan(level, origin, context.random(), height);
        if (plan.roots() < 4 || plan.branches() < 5) return false;
        plan.leaves().keySet().removeAll(plan.logs().keySet());
        for (BlockPos pos : plan.logs().keySet()) if (!writable(level, pos)) return false;
        // Cavern roofs are naturally uneven. Let foliage conform to nearby rock instead of
        // requiring a perfectly empty rectangular volume, while every structural log remains
        // strictly collision-safe.
        plan.leaves().keySet().removeIf(pos -> !writable(level, pos));

        supportLeaves(plan.logs(), plan.leaves());
        plan.leaves().entrySet().removeIf(entry -> entry.getValue() > 6);
        if (plan.leaves().size() < 900) return false;
        plan.logs().forEach((pos, state) -> level.setBlock(pos, state, 2));
        plan.leaves()
                .forEach(
                        (pos, distance) ->
                                level.setBlock(
                                        pos,
                                        CustomBlocks.TWISTWOOD_LEAVES
                                                .get()
                                                .defaultBlockState()
                                                .setValue(LeavesBlock.DISTANCE, distance)
                                                .setValue(LeavesBlock.PERSISTENT, false),
                                        2));
        return true;
    }

    private static TreePlan plan(
            WorldGenLevel level, BlockPos origin, RandomSource random, int height) {
        var logs = new LinkedHashMap<BlockPos, BlockState>();
        var leaves = new LinkedHashMap<BlockPos, Integer>();
        var spine = new ArrayList<BlockPos>();
        double phase = random.nextDouble() * TAU;
        int handedness = random.nextBoolean() ? 1 : -1;
        int trunkHeight = (int) Math.round(height * .60);
        double leanAngle = phase + handedness * .55;
        BlockPos previous = origin;
        BlockPos[] previousLobes = new BlockPos[2];

        for (int y = 0; y <= trunkHeight; y++) {
            double t = y / (double) trunkHeight;
            double lean = 5.2 * t * t;
            double bow = Math.sin(t * Math.PI) * 1.8;
            int centerX =
                    (int)
                            Math.round(
                                    Math.cos(leanAngle) * lean
                                            + Math.cos(leanAngle + Math.PI / 2) * bow);
            int centerZ =
                    (int)
                            Math.round(
                                    Math.sin(leanAngle) * lean
                                            + Math.sin(leanAngle + Math.PI / 2) * bow);
            BlockPos center = origin.offset(centerX, y, centerZ);
            connectVertical(logs, previous, center);
            double coreRadius = 1.85 - t * .35 + Math.sin(phase + t * TAU * 1.7) * .12;
            trunkDisc(level, logs, center, origin.getY(), coreRadius);

            // Broad overlapping lobes rotate with the grain. Their blocks stay vertical so the
            // twist reads in the silhouette without bright end-grain pegs on the trunk surface.
            double twist = phase + handedness * t * TAU * .92;
            double lobeReach = 1.7 - t * .35;
            double lobeRadius = 1.55 - t * .25;
            for (int lobe = 0; lobe < previousLobes.length; lobe++) {
                double angle = twist + lobe * Math.PI;
                BlockPos lobeCenter =
                        center.offset(
                                (int) Math.round(Math.cos(angle) * lobeReach),
                                0,
                                (int) Math.round(Math.sin(angle) * lobeReach));
                if (previousLobes[lobe] != null)
                    connectVertical(logs, previousLobes[lobe], lobeCenter);
                trunkDisc(level, logs, lobeCenter, origin.getY(), lobeRadius);
                previousLobes[lobe] = lobeCenter;
            }
            spine.add(center);
            previous = center;
        }

        int rooted = roots(level, logs, origin, random, phase, handedness);
        int completedBranches = 0;

        // A few deliberately different lower limbs avoid the evenly spaced candelabra silhouette.
        // Their broad bases overlap the trunk so each junction reads as a division of its mass.
        for (int branch = 0; branch < 4; branch++) {
            int forkY = (int) Math.round(height * (.27 + branch * .09)) + random.nextInt(5) - 2;
            BlockPos fork = spine.get(forkY);
            double angle = phase + branch * GOLDEN_ANGLE + (random.nextDouble() - .5) * .55;
            BranchPlan limb =
                    branchedLimb(
                            fork,
                            angle,
                            18 + random.nextInt(7),
                            2 + random.nextInt(8),
                            branch == 0 ? -3 : -1.5 + random.nextDouble() * 3,
                            2.45,
                            branch * 31,
                            random);
            if (limb.logs().keySet().stream().anyMatch(pos -> !writable(level, pos))) continue;
            logs.putAll(limb.logs());
            leaves.putAll(limb.leaves());
            completedBranches++;
        }

        // The trunk ends by dividing into three unequal leaders instead of continuing as a pole
        // through the canopy. Their terminal forks overlap into one broad, irregular crown.
        for (int leader = 0; leader < 3; leader++) {
            int forkY = trunkHeight - leader * 2;
            BlockPos fork = spine.get(forkY);
            double angle =
                    phase + handedness * .8 + leader * TAU / 3 + (random.nextDouble() - .5) * .35;
            BranchPlan limb =
                    branchedLimb(
                            fork,
                            angle,
                            9 + random.nextInt(5) + leader,
                            Math.max(9, height - 5 - forkY - random.nextInt(4)),
                            1.5 + random.nextDouble() * 2,
                            2.35,
                            200 + leader * 37,
                            random);
            if (limb.logs().keySet().stream().anyMatch(pos -> !writable(level, pos))) continue;
            logs.putAll(limb.logs());
            leaves.putAll(limb.leaves());
            completedBranches++;
        }
        return new TreePlan(origin, logs, leaves, rooted, completedBranches);
    }

    private static void trunkDisc(
            WorldGenLevel level,
            Map<BlockPos, BlockState> logs,
            BlockPos center,
            int floorY,
            double radius) {
        int extent = (int) Math.ceil(radius);
        for (int x = -extent; x <= extent; x++) {
            for (int z = -extent; z <= extent; z++) {
                if (x * x + z * z > radius * radius + .35) continue;
                BlockPos pos = center.offset(x, 0, z);
                if (pos.getY() == floorY && !ground(level.getBlockState(pos.below()))) continue;
                log(logs, pos, Direction.Axis.Y);
            }
        }
    }

    private static int roots(
            WorldGenLevel level,
            Map<BlockPos, BlockState> logs,
            BlockPos origin,
            RandomSource random,
            double phase,
            int handedness) {
        int complete = 0;
        for (int root = 0; root < 7; root++) {
            BlockPos tip = origin;
            int reached = 0;
            int length = 11 + random.nextInt(8);
            double angle = phase + root * TAU / 7 + (random.nextDouble() - .5) * .3;
            double curve = handedness * (.12 + random.nextDouble() * .2);
            for (int step = 1; step <= length; step++) {
                double progress = step / (double) length;
                BlockPos target =
                        origin.offset(
                                (int) Math.round(Math.cos(angle + curve * progress) * step),
                                0,
                                (int) Math.round(Math.sin(angle + curve * progress) * step));
                List<BlockPos> segment = path(tip, target);
                if (segment.stream()
                        .anyMatch(
                                pos ->
                                        !ground(level.getBlockState(pos.below()))
                                                || !writable(level, pos))) break;
                BlockPos last = tip;
                for (BlockPos pos : segment) {
                    Direction.Axis axis =
                            pos.getX() != last.getX() ? Direction.Axis.X : Direction.Axis.Z;
                    log(logs, pos, axis);
                    int buttressHeight =
                            (int) Math.round(6 * Math.pow(Math.max(0, 1 - progress), 1.35));
                    for (int y = 1; y <= buttressHeight; y++)
                        log(logs, pos.above(y), Direction.Axis.Y);
                    last = pos;
                }
                tip = target;
                reached = step;
            }
            if (reached >= 3) complete++;
        }
        return complete;
    }

    private static BranchPlan branchedLimb(
            BlockPos fork,
            double angle,
            int length,
            int rise,
            double sag,
            double baseRadius,
            int salt,
            RandomSource random) {
        var logs = new LinkedHashMap<BlockPos, BlockState>();
        var leaves = new LinkedHashMap<BlockPos, Integer>();
        double bend = (random.nextDouble() - .5) * .7;
        List<BlockPos> primary = limb(logs, fork, angle, length, rise, bend, sag, baseRadius);
        foliageSpray(leaves, primary, salt + 41, .64);

        for (int forkIndex = 0; forkIndex < 2; forkIndex++) {
            BlockPos secondaryFork = primary.get((int) (primary.size() * (.52 + forkIndex * .17)));
            double side = forkIndex == 0 ? -1 : 1;
            double forkAngle = angle + side * (.52 + random.nextDouble() * .28) + bend * .35;
            List<BlockPos> secondary =
                    limb(
                            logs,
                            secondaryFork,
                            forkAngle,
                            8 + random.nextInt(5),
                            2 + random.nextInt(6),
                            (random.nextDouble() - .5) * .45,
                            -1 + random.nextDouble() * 2,
                            1.35);
            foliageSpray(leaves, secondary, salt + forkIndex * 11 + 47, .52);
            foliageTuft(leaves, secondary.getLast().above(), salt + forkIndex * 11);

            if (forkIndex == 0) {
                BlockPos twigFork = secondary.get((int) (secondary.size() * .58));
                double twigAngle = forkAngle - side * (.7 + random.nextDouble() * .25);
                List<BlockPos> twig =
                        limb(
                                logs,
                                twigFork,
                                twigAngle,
                                4 + random.nextInt(3),
                                random.nextInt(4),
                                0,
                                0,
                                .8);
                foliageTuft(leaves, twig.getLast().above(), salt + 23);
            }
        }
        foliageTuft(leaves, primary.getLast().above(), salt + 29);
        return new BranchPlan(logs, leaves);
    }

    private static void foliageSpray(
            Map<BlockPos, Integer> leaves, List<BlockPos> branch, int salt, double startFraction) {
        int start = (int) (branch.size() * startFraction);
        for (int index = start; index < branch.size() - 1; index += 3) {
            BlockPos pos = branch.get(index);
            int dx = Math.floorMod(salt + index * 3, 3) - 1;
            int dy = Math.floorMod(salt + index * 5, 3) - 1;
            int dz = Math.floorMod(salt + index * 7, 3) - 1;
            foliageBlob(leaves, pos.offset(dx, dy, dz), 2, 2, 2, salt + index);
        }
    }

    private static List<BlockPos> limb(
            Map<BlockPos, BlockState> logs,
            BlockPos fork,
            double angle,
            int length,
            int rise,
            double bend,
            double sag,
            double baseRadius) {
        var points = new ArrayList<BlockPos>();
        BlockPos previous = fork;
        for (int step = 1; step <= length; step++) {
            double progress = step / (double) length;
            double reach = length * (progress * .55 + progress * progress * .45);
            double limbAngle = angle + bend * progress * progress;
            int y = (int) Math.round(rise * progress + sag * Math.sin(Math.PI * progress));
            BlockPos next =
                    fork.offset(
                            (int) Math.round(Math.cos(limbAngle) * reach),
                            y,
                            (int) Math.round(Math.sin(limbAngle) * reach));
            Direction.Axis axis = horizontalAxis(limbAngle);
            connectLimb(logs, previous, next, axis);
            double radius = .55 + (baseRadius - .55) * Math.pow(1 - progress, .7);
            branchSection(logs, next, axis, radius);
            points.add(next);
            previous = next;
        }
        return points;
    }

    private static void branchSection(
            Map<BlockPos, BlockState> logs, BlockPos center, Direction.Axis axis, double radius) {
        int extent = (int) Math.ceil(radius);
        for (int vertical = -extent; vertical <= extent; vertical++) {
            for (int perpendicular = -extent; perpendicular <= extent; perpendicular++) {
                if (vertical * vertical + perpendicular * perpendicular > radius * radius + .2)
                    continue;
                int x = axis == Direction.Axis.X ? 0 : perpendicular;
                int z = axis == Direction.Axis.X ? perpendicular : 0;
                log(logs, center.offset(x, vertical, z), axis);
            }
        }
    }

    private static Direction.Axis horizontalAxis(double angle) {
        return Math.abs(Math.cos(angle)) >= Math.abs(Math.sin(angle))
                ? Direction.Axis.X
                : Direction.Axis.Z;
    }

    private static void connectLimb(
            Map<BlockPos, BlockState> logs, BlockPos from, BlockPos to, Direction.Axis axis) {
        while (!from.equals(to)) {
            int dx = Integer.signum(to.getX() - from.getX());
            int dy = Integer.signum(to.getY() - from.getY());
            int dz = Integer.signum(to.getZ() - from.getZ());
            int rx = Math.abs(to.getX() - from.getX());
            int ry = Math.abs(to.getY() - from.getY());
            int rz = Math.abs(to.getZ() - from.getZ());
            if (ry >= rx && ry >= rz && dy != 0) from = from.offset(0, dy, 0);
            else if (rx >= rz && dx != 0) from = from.offset(dx, 0, 0);
            else from = from.offset(0, 0, dz);
            log(logs, from, axis);
        }
    }

    private static void connectVertical(
            Map<BlockPos, BlockState> logs, BlockPos from, BlockPos to) {
        while (!from.equals(to)) {
            int dx = Integer.signum(to.getX() - from.getX());
            int dy = Integer.signum(to.getY() - from.getY());
            int dz = Integer.signum(to.getZ() - from.getZ());
            if (dy != 0) from = from.offset(0, dy, 0);
            else if (dx != 0) from = from.offset(dx, 0, 0);
            else from = from.offset(0, 0, dz);
            log(logs, from, Direction.Axis.Y);
        }
    }

    private static List<BlockPos> path(BlockPos from, BlockPos to) {
        var result = new ArrayList<BlockPos>();
        while (from.getX() != to.getX()) {
            from = from.offset(Integer.signum(to.getX() - from.getX()), 0, 0);
            result.add(from);
        }
        while (from.getZ() != to.getZ()) {
            from = from.offset(0, 0, Integer.signum(to.getZ() - from.getZ()));
            result.add(from);
        }
        return result;
    }

    private static void log(Map<BlockPos, BlockState> logs, BlockPos pos, Direction.Axis axis) {
        logs.putIfAbsent(
                pos,
                CustomBlocks.TWISTWOOD_LOG
                        .get()
                        .defaultBlockState()
                        .setValue(RotatedPillarBlock.AXIS, axis));
    }

    private static void foliageTuft(Map<BlockPos, Integer> leaves, BlockPos center, int salt) {
        int direction = Math.floorMod(salt * 5 + 3, 8);
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dz = {0, 1, 1, 1, 0, -1, -1, -1};
        foliageBlob(leaves, center, 3, 3, 3, salt);
        foliageBlob(
                leaves, center.offset(dx[direction] * 2, 1, dz[direction] * 2), 3, 2, 3, salt + 1);
        int cross = (direction + 2 + Math.floorMod(salt, 3)) % 8;
        foliageBlob(leaves, center.offset(dx[cross] * 2, -1, dz[cross] * 2), 2, 2, 2, salt + 2);
    }

    private static void foliageBlob(
            Map<BlockPos, Integer> leaves,
            BlockPos center,
            int radiusX,
            int radiusY,
            int radiusZ,
            int salt) {
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int z = -radiusZ; z <= radiusZ; z++) {
                for (int y = -radiusY; y <= radiusY; y++) {
                    double distance =
                            x * x / (double) (radiusX * radiusX)
                                    + y * y / (double) (radiusY * radiusY)
                                    + z * z / (double) (radiusZ * radiusZ);
                    BlockPos pos = center.offset(x, y, z);
                    if (distance <= .68 + edgeNoise(pos, salt) * .46) leaves.put(pos, 7);
                }
            }
        }
    }

    private static double edgeNoise(BlockPos pos, int salt) {
        long value = pos.asLong() ^ (long) salt * 0x9e3779b97f4a7c15L;
        value ^= value >>> 30;
        value *= 0xbf58476d1ce4e5b9L;
        value ^= value >>> 27;
        value *= 0x94d049bb133111ebL;
        value ^= value >>> 31;
        return (value & 1023) / 1023.0;
    }

    private static void supportLeaves(
            Map<BlockPos, BlockState> logs, Map<BlockPos, Integer> leaves) {
        var frontier = new ArrayDeque<BlockPos>(logs.keySet());
        while (!frontier.isEmpty()) {
            BlockPos pos = frontier.remove();
            int distance = logs.containsKey(pos) ? 0 : leaves.get(pos);
            if (distance >= 6) continue;
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = pos.relative(direction);
                if (leaves.getOrDefault(neighbor, 0) > distance + 1) {
                    leaves.put(neighbor, distance + 1);
                    frontier.add(neighbor);
                }
            }
        }
    }

    private static boolean supportedBase(WorldGenLevel level, BlockPos origin) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos side = origin.relative(direction);
            if (!ground(level.getBlockState(side.below())) || !writable(level, side)) return false;
        }
        return true;
    }

    private static boolean ground(BlockState state) {
        return state.getFluidState().isEmpty()
                && (state.is(CustomBlocks.OVERGROWN_CARFSTONE.get())
                        || state.is(BlockTags.DIRT)
                        || state.is(net.minecraft.world.level.block.Blocks.FARMLAND));
    }

    private static boolean writable(WorldGenLevel level, BlockPos pos) {
        if (pos.getY() < level.getMinBuildHeight()
                || pos.getY() >= level.getMaxBuildHeight()
                || !level.ensureCanWrite(pos)) return false;
        BlockState state = level.getBlockState(pos);
        return state.getFluidState().isEmpty()
                && (state.isAir()
                        || state.is(CustomBlocks.UNDERSPROUTS.get())
                        || state.is(CustomBlocks.TALL_UNDERSPROUTS.get())
                        || state.is(CustomBlocks.MIGHTY_UNDERSPROUTS.get()));
    }

    /** The stable anchor is intentionally retained for a future root-room/template pass. */
    private record TreePlan(
            BlockPos anchor,
            Map<BlockPos, BlockState> logs,
            Map<BlockPos, Integer> leaves,
            int roots,
            int branches) {}

    private record BranchPlan(Map<BlockPos, BlockState> logs, Map<BlockPos, Integer> leaves) {}
}
