package com.freeranger.dark_caverns.generation;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/** Gap-aware formations, retaining the legacy IDs for existing datapacks. */
public final class SpikeFeature extends Feature<CavernFormationConfiguration> {
    private static final int SEARCH_LIMIT = 256;
    private static final int FOUNDATION_SEARCH = 4;
    private static final int MAX_SURFACE_ORE = 32;
    private static final int SURFACE_ORE_PATCH_SIZE = 4;
    private static final Direction[] DIRECTIONS = Direction.values();

    public SpikeFeature(Codec<CavernFormationConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<CavernFormationConfiguration> context) {
        var level = context.level();
        var random = context.random();
        var config = context.config();
        var found = CavernGap.find(level, context.origin(), SEARCH_LIMIT);
        if (found.isEmpty() || found.get().height() < config.minGap()) return false;
        var gap = found.get();
        boolean floor = isAnchor(level, gap.floor(), config.state());
        boolean ceiling = isAnchor(level, gap.ceiling(), config.state());
        if (!floor && !ceiling) return false;
        int height = gap.height();

        if (floor
                && ceiling
                && height >= 24
                && height <= config.maxHeight() * 2
                && random.nextFloat() < config.columnChance()) {
            int lower =
                    Mth.clamp(
                            height / 2 + random.nextInt(5) - 2,
                            height - config.maxHeight(),
                            config.maxHeight());
            int radius = radius(config, Math.max(lower, height - lower), random);
            boolean placed = grow(level, gap.floor(), 1, lower, radius, config, random);
            // The two tips share an axis and meet without an air seam.
            return grow(level, gap.ceiling(), -1, height - lower, radius, config, random) || placed;
        }
        if (floor && ceiling && random.nextFloat() < config.opposingChance()) {
            int budget = Math.min(config.maxHeight() * 2, height - 3 - random.nextInt(3));
            // Very small gaps cannot accommodate a pair while retaining clearance.
            if (budget >= 4) {
                int lower =
                        Mth.clamp(
                                (int) (budget * (0.4F + random.nextFloat() * 0.2F)),
                                Math.max(2, budget - config.maxHeight()),
                                Math.min(config.maxHeight(), budget - 2));
                boolean placed =
                        grow(
                                level,
                                gap.floor(),
                                1,
                                lower,
                                radius(config, lower, random),
                                config,
                                random);
                int upper = budget - lower;
                return grow(
                                level,
                                gap.ceiling(),
                                -1,
                                upper,
                                radius(config, upper, random),
                                config,
                                random)
                        || placed;
            }
        }

        boolean hanging = ceiling && (!floor || random.nextFloat() < config.ceilingChance());
        int length =
                Math.min(
                        config.maxHeight(),
                        Math.max(2, (int) (height * (0.22F + random.nextFloat() * 0.22F))));
        BlockPos anchor = hanging ? gap.ceiling() : gap.floor();
        int direction = hanging ? -1 : 1;
        boolean placed =
                grow(
                        level,
                        anchor,
                        direction,
                        length,
                        radius(config, length, random),
                        config,
                        random);
        if (height < 24 && placed) {
            for (int i = 0; i < 2; i++) {
                BlockPos satellite =
                        context.origin().offset(random.nextInt(7) - 3, 0, random.nextInt(7) - 3);
                if (!level.ensureCanWrite(satellite)) continue;
                var nearby = CavernGap.find(level, satellite, 24);
                if (nearby.isEmpty() || nearby.get().height() < config.minGap()) continue;
                BlockPos root = hanging ? nearby.get().ceiling() : nearby.get().floor();
                grow(
                        level,
                        root,
                        direction,
                        Math.min(length - 1, nearby.get().height() / 3),
                        1,
                        config,
                        random);
            }
        }
        return placed;
    }

    private static int radius(
            CavernFormationConfiguration config, int height, RandomSource random) {
        return Math.min(config.maxRadius(), Math.max(1, height / 5 + random.nextInt(2)));
    }

    private static boolean isAnchor(WorldGenLevel level, BlockPos pos, BlockState material) {
        return level.ensureCanWrite(pos)
                && level.getBlockState(pos).is(material.getBlock())
                && level.getBlockState(pos).getFluidState().isEmpty();
    }

    private static boolean grow(
            WorldGenLevel level,
            BlockPos anchor,
            int direction,
            int height,
            int radius,
            CavernFormationConfiguration config,
            RandomSource random) {
        BlockState material = config.state();
        if (height < 1 || !isAnchor(level, anchor, material)) return false;
        boolean decorateSurface =
                direction < 0 && config.surfaceOre().isPresent() && config.surfaceOreChance() > 0;
        List<BlockPos> formationBlocks = decorateSurface ? new ArrayList<>() : List.of();
        double phase = random.nextDouble() * Math.PI * 2;
        boolean placed = false;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double angle = Math.atan2(dz, dx);
                double edge =
                        radius
                                * (1
                                        + 0.12 * Math.sin(3 * angle + phase)
                                        + 0.08 * Math.cos(5 * angle - phase));
                double distance = Math.sqrt(dx * dx + dz * dz) / edge;
                if (distance > 1) continue;
                int length = Math.max(1, (int) Math.ceil(height * Math.pow(1 - distance, 1.35)));
                // Every strand starts on actual matching stone. Never replace terrain,
                // structures or fluids, and never extend foundations into a lower cavern.
                BlockPos root =
                        anchor.offset(dx, direction * Math.min(FOUNDATION_SEARCH, height), dz);
                boolean supported = false;
                for (int step = 0;
                        step <= FOUNDATION_SEARCH + Math.min(FOUNDATION_SEARCH, height);
                        step++) {
                    if (!level.ensureCanWrite(root) || level.isOutsideBuildHeight(root)) break;
                    var state = level.getBlockState(root);
                    if (!state.isAir()) {
                        supported = isAnchor(level, root, material);
                        break;
                    }
                    root = root.offset(0, -direction, 0);
                }
                if (!supported) continue;
                int end = anchor.getY() + direction * length;
                for (BlockPos pos = root.offset(0, direction, 0);
                        direction * (end - pos.getY()) >= 0;
                        pos = pos.offset(0, direction, 0)) {
                    if (level.isOutsideBuildHeight(pos)
                            || !level.ensureCanWrite(pos)
                            || !level.getBlockState(pos).isAir()) break;
                    boolean set = level.setBlock(pos, material, Block.UPDATE_CLIENTS);
                    placed |= set;
                    if (set && decorateSurface) formationBlocks.add(pos.immutable());
                }
            }
        }
        if (decorateSurface && !formationBlocks.isEmpty()) {
            placeSurfaceOre(
                    level,
                    formationBlocks,
                    material,
                    config.surfaceOre().orElseThrow(),
                    config.surfaceOreChance(),
                    random);
        }
        return placed;
    }

    private static void placeSurfaceOre(
            WorldGenLevel level,
            List<BlockPos> formationBlocks,
            BlockState material,
            BlockState ore,
            float chance,
            RandomSource random) {
        Set<BlockPos> formation = new HashSet<>(formationBlocks);
        List<BlockPos> exposed = new ArrayList<>();
        for (BlockPos pos : formationBlocks) {
            if (!level.getBlockState(pos).is(material.getBlock())) continue;
            for (Direction direction : DIRECTIONS) {
                BlockPos neighbor = pos.relative(direction);
                if (!formation.contains(neighbor) && level.getBlockState(neighbor).isAir()) {
                    exposed.add(pos);
                    break;
                }
            }
        }
        if (exposed.isEmpty()) return;

        int target = Mth.clamp(Mth.ceil(exposed.size() * chance), 1, MAX_SURFACE_ORE);
        Set<BlockPos> eligible = new HashSet<>(exposed);
        int orePlaced = 0;
        while (orePlaced < target && !eligible.isEmpty()) {
            BlockPos seed = takeRandomEligible(exposed, eligible, random);
            if (seed == null) break;
            orePlaced +=
                    growSurfaceOrePatch(
                            level,
                            seed,
                            eligible,
                            material,
                            ore,
                            Math.min(SURFACE_ORE_PATCH_SIZE, target - orePlaced),
                            random);
        }
    }

    private static BlockPos takeRandomEligible(
            List<BlockPos> exposed, Set<BlockPos> eligible, RandomSource random) {
        for (int attempt = 0; attempt < Math.min(16, exposed.size()); attempt++) {
            BlockPos candidate = exposed.get(random.nextInt(exposed.size()));
            if (eligible.contains(candidate)) return candidate;
        }
        for (BlockPos candidate : exposed) {
            if (eligible.contains(candidate)) return candidate;
        }
        return null;
    }

    private static int growSurfaceOrePatch(
            WorldGenLevel level,
            BlockPos seed,
            Set<BlockPos> eligible,
            BlockState material,
            BlockState ore,
            int budget,
            RandomSource random) {
        List<BlockPos> frontier = new ArrayList<>();
        Set<BlockPos> queued = new HashSet<>();
        frontier.add(seed);
        queued.add(seed);
        int placed = 0;
        while (placed < budget && !frontier.isEmpty()) {
            int index = random.nextInt(frontier.size());
            BlockPos current = frontier.remove(index);
            queued.remove(current);
            if (!eligible.remove(current)) continue;
            if (level.getBlockState(current).is(material.getBlock())
                    && level.setBlock(current, ore, Block.UPDATE_CLIENTS)) {
                placed++;
            }

            int firstDirection = random.nextInt(DIRECTIONS.length);
            for (int offset = 0; offset < DIRECTIONS.length; offset++) {
                Direction direction = DIRECTIONS[(firstDirection + offset) % DIRECTIONS.length];
                BlockPos neighbor = current.relative(direction);
                if (eligible.contains(neighbor) && queued.add(neighbor)) {
                    frontier.add(neighbor);
                }
            }
        }
        return placed;
    }
}
