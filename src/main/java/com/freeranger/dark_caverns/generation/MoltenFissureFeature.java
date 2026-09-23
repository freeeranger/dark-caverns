package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluids;

/**
 * Long lava fissures described in world space and independently reconstructed by every affected
 * chunk. Only the owning chunk is edited, so the result is independent of generation order.
 */
public final class MoltenFissureFeature extends Feature<MoltenFissureConfiguration> {
    private static final int CONTROL_SPACING = 9;
    private static final int HALO = 1;
    private static final int LAVA_TICK_DELAY = 30;

    public MoltenFissureFeature() {
        super(MoltenFissureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<MoltenFissureConfiguration> context) {
        WorldGenLevel level = context.level();
        MoltenFissureConfiguration config = context.config();
        ChunkPos chunk = new ChunkPos(context.origin());
        Set<BlockPos> desired = collectPaths(level, chunk, config);
        if (desired.isEmpty()) return false;

        // Project the world-space line onto the nearest exposed floor. The one-column halo lets
        // neighboring chunks independently agree on slope transitions at their shared edge.
        Map<BlockPos, Column> projected = new LinkedHashMap<>();
        for (BlockPos target : desired) {
            Column column = column(level, target, config.floorSearchRange());
            if (column != null
                    && level.getBiome(column.surface()).is(DarkCaverns.id("molten_depths"))) {
                projected.put(target, column);
            }
        }
        if (projected.isEmpty()) return false;

        Set<BlockPos> connectedTargets = new LinkedHashSet<>();
        for (var entry : projected.entrySet()) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos neighborTarget = entry.getKey().relative(direction);
                Column neighbor = projected.get(neighborTarget);
                if (neighbor != null
                        && Math.abs(neighbor.lava().getY() - entry.getValue().lava().getY()) <= 1) {
                    connectedTargets.add(entry.getKey());
                    connectedTargets.add(neighborTarget);
                }
            }
        }
        projected.keySet().retainAll(connectedTargets);
        if (projected.isEmpty()) return false;

        Map<BlockPos, Column> baseColumns = new LinkedHashMap<>();
        projected.values().forEach(column -> baseColumns.putIfAbsent(column.lava(), column));

        Set<BlockPos> openings = new LinkedHashSet<>();
        for (Column column : baseColumns.values()) {
            if (!column.existing()) openings.add(column.surface());
        }

        Set<BlockPos> plannedLava = new LinkedHashSet<>(baseColumns.keySet());
        for (var entry : projected.entrySet()) {
            for (Direction direction : List.of(Direction.EAST, Direction.SOUTH)) {
                Column neighbor = projected.get(entry.getKey().relative(direction));
                if (neighbor == null) continue;
                Column column = entry.getValue();
                if (Math.abs(neighbor.lava().getY() - column.lava().getY()) == 1) {
                    Column higher =
                            neighbor.lava().getY() > column.lava().getY() ? neighbor : column;
                    plannedLava.add(higher.lava().below());
                }
            }
        }

        // Iteratively trim unsafe edge cells. Their untouched natural block becomes a sealed end
        // cap, so one cliff does not reject an otherwise useful regional fissure.
        boolean changed;
        do {
            Set<BlockPos> rejected = new LinkedHashSet<>();
            for (BlockPos lava : plannedLava) {
                if (!sealed(level, lava, plannedLava)) rejected.add(lava);
            }
            changed = plannedLava.removeAll(rejected);
        } while (changed);
        if (plannedLava.isEmpty()) return false;

        // At a one-block rise, keep the lower opening capped by its original floor. The buried
        // connector above joins both lava levels in an L shape, while the cap prevents the higher
        // cell from spilling sideways into the cavern. This also remains safe across chunk edges.
        Set<BlockPos> cappedOpenings = new LinkedHashSet<>();
        for (BlockPos opening : openings) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos neighbor = opening.relative(direction);
                if (plannedLava.contains(neighbor)
                        || level.getBlockState(neighbor).is(Blocks.LAVA)) {
                    cappedOpenings.add(opening);
                    break;
                }
            }
        }

        Map<BlockPos, Column> activeColumns = new LinkedHashMap<>();
        for (var entry : baseColumns.entrySet()) {
            if (plannedLava.contains(entry.getKey()))
                activeColumns.put(entry.getKey(), entry.getValue());
        }
        if (activeColumns.isEmpty()) return false;

        Set<BlockPos> air = new LinkedHashSet<>();
        for (Column column : activeColumns.values()) {
            if (!column.existing() && !cappedOpenings.contains(column.surface())) {
                air.add(column.surface());
            }
        }

        long worldSeed = level.getSeed();
        for (Column column : activeColumns.values()) {
            if (column.existing() || !chunk.equals(new ChunkPos(column.lava()))) continue;
            BlockPos bed = column.lava().below();
            BlockState bedState =
                    chance(worldSeed, bed, 0x4f1bbcdcL) < config.magmaChance()
                            ? Blocks.MAGMA_BLOCK.defaultBlockState()
                            : CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState();
            level.setBlock(bed, bedState, 2);
        }

        // Sparse scorched accents keep the bank organic instead of outlining the entire crack.
        for (Column column : activeColumns.values()) {
            if (column.existing() || !chunk.equals(new ChunkPos(column.surface()))) continue;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos bank = column.surface().relative(direction);
                if (!chunk.equals(new ChunkPos(bank))
                        || air.contains(bank)
                        || plannedLava.contains(bank)
                        || !level.ensureCanWrite(bank)
                        || !natural(level.getBlockState(bank))
                        || !level.getBlockState(bank.above()).isAir()) continue;
                if (chance(worldSeed, bank, 0x632be59bL) < config.magmaChance()) {
                    level.setBlock(bank, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
                }
            }
        }

        air.stream()
                .filter(pos -> chunk.equals(new ChunkPos(pos)))
                .forEach(pos -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2));
        Set<BlockPos> written = new LinkedHashSet<>();
        for (BlockPos lava : plannedLava) {
            if (!chunk.equals(new ChunkPos(lava)) || level.getBlockState(lava).is(Blocks.LAVA)) {
                continue;
            }
            level.setBlock(lava, Blocks.LAVA.defaultBlockState(), 2);
            written.add(lava);
        }
        written.forEach(pos -> level.scheduleTick(pos, Fluids.LAVA, LAVA_TICK_DELAY));
        return !written.isEmpty();
    }

    private static Set<BlockPos> collectPaths(
            WorldGenLevel level, ChunkPos chunk, MoltenFissureConfiguration config) {
        int minX = chunk.getMinBlockX();
        int maxX = chunk.getMaxBlockX();
        int minZ = chunk.getMinBlockZ();
        int maxZ = chunk.getMaxBlockZ();
        int reach = config.maxLength();
        int minRegionX = Math.floorDiv(minX - reach, config.regionSize());
        int maxRegionX = Math.floorDiv(maxX + reach, config.regionSize());
        int minRegionZ = Math.floorDiv(minZ - reach, config.regionSize());
        int maxRegionZ = Math.floorDiv(maxZ + reach, config.regionSize());
        int low = Math.max(14, level.getMinBuildHeight() + 8);
        int high = level.getMaxBuildHeight() - 16;
        int firstBand = ceilToMultiple(low, config.verticalSpacing());
        Set<BlockPos> desired = new LinkedHashSet<>();

        for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
            for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                for (int lavaY = firstBand; lavaY <= high; lavaY += config.verticalSpacing()) {
                    RandomSource random =
                            RandomSource.create(
                                    descriptorSeed(level.getSeed(), regionX, regionZ, lavaY));
                    if (random.nextFloat() >= config.chancePerBand()) continue;

                    double startX =
                            (long) regionX * config.regionSize()
                                    + random.nextDouble() * config.regionSize();
                    double startZ =
                            (long) regionZ * config.regionSize()
                                    + random.nextDouble() * config.regionSize();
                    int length =
                            config.minLength()
                                    + random.nextInt(config.maxLength() - config.minLength() + 1);
                    double angle = random.nextDouble() * Math.PI * 2;
                    List<BlockPos> main =
                            trace(
                                    random,
                                    startX,
                                    startZ,
                                    lavaY,
                                    angle,
                                    length,
                                    config.bendStrength());
                    addInside(desired, main, minX, maxX, minZ, maxZ);
                    addBulges(desired, main, random, config.bulgeChance(), minX, maxX, minZ, maxZ);

                    if (main.size() > 24 && random.nextFloat() < config.branchChance()) {
                        int branchIndex =
                                main.size() / 3 + random.nextInt(Math.max(1, main.size() / 3));
                        BlockPos before = main.get(branchIndex - 1);
                        BlockPos after = main.get(branchIndex + 1);
                        double tangent =
                                Math.atan2(
                                        after.getZ() - before.getZ(), after.getX() - before.getX());
                        double branchAngle =
                                tangent
                                        + (random.nextBoolean() ? 1 : -1)
                                                * (0.65 + random.nextDouble() * 0.45);
                        int branchLength =
                                Math.max(18, config.minLength() / 4)
                                        + random.nextInt(Math.max(1, config.minLength() / 5));
                        BlockPos branchStart = main.get(branchIndex);
                        List<BlockPos> branch =
                                trace(
                                        random,
                                        branchStart.getX(),
                                        branchStart.getZ(),
                                        lavaY,
                                        branchAngle,
                                        branchLength,
                                        config.bendStrength() * 1.35F);
                        addInside(desired, branch, minX, maxX, minZ, maxZ);
                        addBulges(
                                desired,
                                branch,
                                random,
                                config.bulgeChance() * 0.5F,
                                minX,
                                maxX,
                                minZ,
                                maxZ);
                    }
                }
            }
        }
        return desired;
    }

    private static List<BlockPos> trace(
            RandomSource random,
            double x,
            double z,
            int y,
            double angle,
            int length,
            float bendStrength) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos current = new BlockPos((int) Math.round(x), y, (int) Math.round(z));
        path.add(current);
        for (int step = 1; step <= length; step++) {
            if (step % CONTROL_SPACING == 0) {
                angle += (random.nextDouble() * 2 - 1) * bendStrength;
            }
            x += Math.cos(angle);
            z += Math.sin(angle);
            int targetX = (int) Math.round(x);
            int targetZ = (int) Math.round(z);
            int dx = Integer.compare(targetX, current.getX());
            int dz = Integer.compare(targetZ, current.getZ());
            if (dx != 0 && dz != 0) {
                // Insert an orthogonal connector; diagonal corner contacts are not fluid paths.
                if ((step & 1) == 0) {
                    current = current.offset(dx, 0, 0);
                    addDistinct(path, current);
                    current = current.offset(0, 0, dz);
                } else {
                    current = current.offset(0, 0, dz);
                    addDistinct(path, current);
                    current = current.offset(dx, 0, 0);
                }
                addDistinct(path, current);
            } else if (dx != 0 || dz != 0) {
                current = current.offset(dx, 0, dz);
                addDistinct(path, current);
            }
        }
        return path;
    }

    private static void addDistinct(List<BlockPos> path, BlockPos pos) {
        if (!path.getLast().equals(pos)) path.add(pos);
    }

    private static void addInside(
            Set<BlockPos> destination,
            List<BlockPos> path,
            int minX,
            int maxX,
            int minZ,
            int maxZ) {
        for (BlockPos pos : path) {
            if (pos.getX() >= minX - HALO
                    && pos.getX() <= maxX + HALO
                    && pos.getZ() >= minZ - HALO
                    && pos.getZ() <= maxZ + HALO) destination.add(pos);
        }
    }

    private static void addBulges(
            Set<BlockPos> destination,
            List<BlockPos> path,
            RandomSource random,
            float chance,
            int minX,
            int maxX,
            int minZ,
            int maxZ) {
        for (int i = 8; i < path.size() - 8; i++) {
            if (random.nextFloat() >= chance) continue;
            BlockPos before = path.get(i - 1);
            BlockPos after = path.get(i + 1);
            int dx = after.getX() - before.getX();
            int dz = after.getZ() - before.getZ();
            int side = random.nextBoolean() ? 1 : -1;
            BlockPos bulge =
                    Math.abs(dx) >= Math.abs(dz)
                            ? path.get(i).offset(0, 0, side)
                            : path.get(i).offset(side, 0, 0);
            if (bulge.getX() >= minX - HALO
                    && bulge.getX() <= maxX + HALO
                    && bulge.getZ() >= minZ - HALO
                    && bulge.getZ() <= maxZ + HALO) destination.add(bulge);
        }
    }

    private static Column column(WorldGenLevel level, BlockPos target, int searchRange) {
        for (int distance = 0; distance <= searchRange; distance++) {
            int variants = distance == 0 ? 1 : 2;
            for (int variant = 0; variant < variants; variant++) {
                int dy = distance == 0 ? 0 : (variant == 0 ? -distance : distance);
                BlockPos lava = target.offset(0, dy, 0);
                BlockPos surface = lava.above();
                if (!level.ensureCanWrite(lava)
                        || !level.ensureCanWrite(lava.below())
                        || !level.ensureCanWrite(surface)) continue;
                if (level.getBlockState(lava).is(Blocks.LAVA)
                        && level.getBlockState(surface).isAir()) {
                    return new Column(lava, surface, true);
                }
                if (natural(level.getBlockState(lava))
                        && natural(level.getBlockState(lava.below()))
                        && natural(level.getBlockState(surface))
                        && level.getBlockState(surface.above()).isAir()
                        && level.getBlockState(surface.above(2)).isAir()) {
                    return new Column(lava, surface, false);
                }
            }
        }
        return null;
    }

    private static boolean sealed(WorldGenLevel level, BlockPos lava, Set<BlockPos> planned) {
        BlockState state = level.getBlockState(lava);
        if (!level.ensureCanWrite(lava) || (!state.is(Blocks.LAVA) && !natural(state)))
            return false;
        BlockPos below = lava.below();
        if (!planned.contains(below)
                && !level.getBlockState(below).is(Blocks.LAVA)
                && !natural(level.getBlockState(below))) return false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos wall = lava.relative(direction);
            if (!planned.contains(wall)
                    && !level.getBlockState(wall).is(Blocks.LAVA)
                    && !natural(level.getBlockState(wall))) return false;
        }
        return true;
    }

    private static int ceilToMultiple(int value, int multiple) {
        return Math.floorDiv(value + multiple - 1, multiple) * multiple;
    }

    private static long descriptorSeed(long seed, int regionX, int regionZ, int y) {
        long mixed = seed ^ 0x9e3779b97f4a7c15L;
        mixed ^= (long) regionX * 0x632be59bd9b4e019L;
        mixed ^= (long) regionZ * 0x8cb92baa3f3d8dd7L;
        mixed ^= (long) y * 0x94d049bb133111ebL;
        mixed ^= mixed >>> 30;
        mixed *= 0xbf58476d1ce4e5b9L;
        mixed ^= mixed >>> 27;
        mixed *= 0x94d049bb133111ebL;
        return mixed ^ mixed >>> 31;
    }

    private static float chance(long seed, BlockPos pos, long salt) {
        long mixed = descriptorSeed(seed ^ salt, pos.getX(), pos.getZ(), pos.getY());
        return (mixed >>> 40) / (float) (1 << 24);
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())
                || state.is(Blocks.MAGMA_BLOCK);
    }

    private record Column(BlockPos lava, BlockPos surface, boolean existing) {}
}
