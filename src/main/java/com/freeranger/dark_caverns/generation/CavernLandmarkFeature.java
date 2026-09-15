package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** Small templates placed on generated cave floors, without structure-start noise sampling. */
public final class CavernLandmarkFeature extends Feature<CavernLandmarkConfiguration> {
    private static final ResourceLocation MOLTEN = DarkCaverns.id("molten_depths");
    private static final int NO_FLOOR = Integer.MIN_VALUE;

    public CavernLandmarkFeature() {
        super(CavernLandmarkConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<CavernLandmarkConfiguration> context) {
        var level = context.level();
        var config = context.config();
        var template = level.getLevel().getStructureManager().get(config.template());
        if (template.isEmpty()) return false;
        return decorate(
                        level,
                        new ChunkPos(context.origin()),
                        context.random(),
                        config,
                        template.get())
                > 0;
    }

    public static int decorate(
            WorldGenLevel level,
            ChunkPos chunk,
            RandomSource random,
            CavernLandmarkConfiguration config,
            StructureTemplate template) {
        var size = template.getSize();
        // Keep every template, foundation and neighbor-shape update inside its owning chunk.
        // Oversized datapack replacements must not silently spill into another chunk's slots.
        if (size.getX() < 1
                || size.getX() > 5
                || size.getZ() < 1
                || size.getZ() > 5
                || size.getY() < 1
                || size.getY() > 8) return 0;
        int[] slots = slots(level.getSeed(), chunk);
        int ax = slots[0], az = slots[1];
        int x = chunk.getMinBlockX() + (config.groups() ? ax : 15 - ax);
        int z = chunk.getMinBlockZ() + (config.groups() ? az : 15 - az);
        int[] last = {NO_FLOOR, NO_FLOOR, NO_FLOOR};
        int count = 0;
        var pos = new BlockPos.MutableBlockPos();
        for (int y = Math.max(15, level.getMinBuildHeight() + 6);
                y < level.getMaxBuildHeight() - size.getY() - 1;
                y++) {
            pos.set(x, y, z);
            if (!floor(level, pos)
                    || (last[0] != NO_FLOOR && y - last[0] < config.verticalSpacing())
                    || random.nextFloat() >= config.chance()) continue;
            if (!level.getBiome(pos).is(MOLTEN)
                    || random.nextFloat()
                            >= TransitionPlacementCache.weights(level, x, z)
                                    .cover(BiomeTransition.MOLTEN)) continue;
            if (!placeOnFloor(level, chunk, template, pos.immutable(), random)) continue;
            last[0] = y;
            count++;
            if (config.groups() && random.nextInt(4) == 0) {
                int grouped =
                        nearby(
                                level,
                                chunk,
                                template,
                                chunk.getMinBlockX() + 15 - ax,
                                y,
                                z,
                                last[1],
                                config.verticalSpacing(),
                                random);
                if (grouped != NO_FLOOR) {
                    last[1] = grouped;
                    count++;
                }
                if (random.nextBoolean()) {
                    grouped =
                            nearby(
                                    level,
                                    chunk,
                                    template,
                                    x,
                                    y,
                                    chunk.getMinBlockZ() + 15 - az,
                                    last[2],
                                    config.verticalSpacing(),
                                    random);
                    if (grouped != NO_FLOOR) {
                        last[2] = grouped;
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /** Shared layout for both features; never depends on feature order or neighboring writes. */
    public static int[] slots(long seed, ChunkPos chunk) {
        var random =
                RandomSource.create(
                        seed
                                ^ chunk.x * 341873128712L
                                ^ chunk.z * 132897987541L
                                ^ 0x4c414e444d41524bL);
        return new int[] {random.nextBoolean() ? 4 : 11, random.nextBoolean() ? 4 : 11};
    }

    private static int nearby(
            WorldGenLevel level,
            ChunkPos chunk,
            StructureTemplate template,
            int x,
            int y,
            int z,
            int last,
            int spacing,
            RandomSource random) {
        for (int dy : new int[] {0, -1, 1, -2, 2}) {
            var pos = new BlockPos(x, y + dy, z);
            if ((last == NO_FLOOR || pos.getY() - last >= spacing)
                    && floor(level, pos)
                    && placeOnFloor(level, chunk, template, pos, random)) return pos.getY();
        }
        return NO_FLOOR;
    }

    public static boolean placeOnFloor(
            WorldGenLevel level,
            ChunkPos owner,
            StructureTemplate template,
            BlockPos floor,
            RandomSource random) {
        if (!level.getBiome(floor).is(MOLTEN)) return false;
        var rotation = Rotation.getRandom(random);
        var size = template.getSize(rotation);
        if (size.getX() < 1
                || size.getX() > 5
                || size.getZ() < 1
                || size.getZ() > 5
                || size.getY() < 1
                || size.getY() > 8) return false;
        var corner = floor.offset(-size.getX() / 2, 0, -size.getZ() / 2);
        var start = template.getZeroPositionWithTransform(corner, Mirror.NONE, rotation);
        var settings =
                new StructurePlaceSettings()
                        .setRotation(rotation)
                        .setIgnoreEntities(true)
                        .setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
        var bounds = template.getBoundingBox(settings, start);
        settings.setBoundingBox(bounds);
        if (bounds.minX() < owner.getMinBlockX() + 1
                || bounds.maxX() > owner.getMaxBlockX() - 1
                || bounds.minZ() < owner.getMinBlockZ() + 1
                || bounds.maxZ() > owner.getMaxBlockZ() - 1
                || bounds.minY() - 5 < level.getMinBuildHeight()
                || bounds.maxY() + 1 >= level.getMaxBuildHeight()) return false;
        Map<BlockPos, BlockState> foundation = new LinkedHashMap<>();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                var base = new BlockPos(x, bounds.minY(), z);
                var state = level.getBlockState(base);
                if (!level.ensureCanWrite(base) || (!state.isAir() && !natural(state)))
                    return false;
                for (int y = bounds.minY() + 1; y <= bounds.maxY() + 1; y++) {
                    var clear = new BlockPos(x, y, z);
                    if (!level.ensureCanWrite(clear) || !level.getBlockState(clear).isAir())
                        return false;
                }
                int depth;
                for (depth = 1; depth <= CavernFloorFinder.MAX_FOUNDATION_DEPTH; depth++) {
                    var below = base.below(depth);
                    if (!level.ensureCanWrite(below)) return false;
                    var support = level.getBlockState(below);
                    if (support.isAir()) {
                        foundation.put(
                                below, CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState());
                    } else {
                        if (!natural(support) || !natural(level.getBlockState(below.below())))
                            return false;
                        break;
                    }
                }
                if (depth > CavernFloorFinder.MAX_FOUNDATION_DEPTH) return false;
            }
        }
        // All clearance/support checks finish before the first write. Vanilla preserves rotated
        // block states, skull block-entity data and fence/stair connections from the templates.
        if (!template.placeInWorld(level, start, start, settings, random, 2)) return false;
        foundation.forEach((pos, state) -> level.setBlock(pos, state, 2));
        return true;
    }

    private static boolean floor(WorldGenLevel level, BlockPos pos) {
        return natural(level.getBlockState(pos)) && level.getBlockState(pos.above()).isAir();
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.MOLTEN_CARFSTONE.get());
    }
}
