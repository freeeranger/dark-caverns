package com.freeranger.dark_caverns.generation;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
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
            boolean placed = grow(level, gap.floor(), 1, lower, radius, config.state(), random);
            // The two tips share an axis and meet without an air seam.
            return grow(level, gap.ceiling(), -1, height - lower, radius, config.state(), random)
                    || placed;
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
                                config.state(),
                                random);
                int upper = budget - lower;
                return grow(
                                level,
                                gap.ceiling(),
                                -1,
                                upper,
                                radius(config, upper, random),
                                config.state(),
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
                        config.state(),
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
                        config.state(),
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
            BlockState material,
            RandomSource random) {
        if (height < 1 || !isAnchor(level, anchor, material)) return false;
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
                    placed |= level.setBlock(pos, material, Block.UPDATE_CLIENTS);
                }
            }
        }
        return placed;
    }
}
