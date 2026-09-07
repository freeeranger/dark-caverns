package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomStructureTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

/**
 * Jigsaw placement shared by the four Dark Caverns structures. Surface structures follow the world
 * surface; cavern structures scan down for a solid shelf with open space above it.
 */
public final class CavernsJigsawStructure extends Structure {
    private static final int CAVERN_ROOF_MARGIN = 20;
    private static final int CAVERN_CLEARANCE_HEIGHT = 3;
    private static final int CAVERN_FLOOR_OFFSET = 15;
    private static final int CAVERN_SEARCH_MINIMUM_BELOW_SEA_LEVEL = 2;
    private static final int CAVERN_FALLBACK_BELOW_SEA_LEVEL = 13;

    public static final MapCodec<CavernsJigsawStructure> CODEC =
            RecordCodecBuilder.<CavernsJigsawStructure>mapCodec(
                            instance ->
                                    instance.group(
                                                    settingsCodec(instance),
                                                    StructureTemplatePool.CODEC
                                                            .fieldOf("start_pool")
                                                            .forGetter(
                                                                    structure ->
                                                                            structure.startPool),
                                                    Codec.intRange(
                                                                    JigsawStructure.MIN_DEPTH,
                                                                    JigsawStructure.MAX_DEPTH)
                                                            .optionalFieldOf("size", 10)
                                                            .forGetter(
                                                                    structure ->
                                                                            structure.maxDepth),
                                                    Codec.intRange(
                                                                    1,
                                                                    JigsawStructure
                                                                            .MAX_TOTAL_STRUCTURE_RANGE)
                                                            .optionalFieldOf(
                                                                    "max_distance_from_center", 80)
                                                            .forGetter(
                                                                    structure ->
                                                                            structure
                                                                                    .maxDistanceFromCenter),
                                                    Codec.BOOL
                                                            .optionalFieldOf(
                                                                    "use_expansion_hack", false)
                                                            .forGetter(
                                                                    structure ->
                                                                            structure
                                                                                    .useExpansionHack),
                                                    Codec.BOOL
                                                            .optionalFieldOf(
                                                                    "cavern_placement", false)
                                                            .forGetter(
                                                                    structure ->
                                                                            structure
                                                                                    .cavernPlacement),
                                                    Codec.BOOL
                                                            .optionalFieldOf(
                                                                    "honor_forgotten_tower_config",
                                                                    false)
                                                            .forGetter(
                                                                    structure ->
                                                                            structure
                                                                                    .honorForgottenTowerConfig))
                                            .apply(instance, CavernsJigsawStructure::new))
                    .validate(CavernsJigsawStructure::verifyRange);

    private final Holder<StructureTemplatePool> startPool;
    private final int maxDepth;
    private final int maxDistanceFromCenter;
    private final boolean useExpansionHack;
    private final boolean cavernPlacement;
    private final boolean honorForgottenTowerConfig;

    public CavernsJigsawStructure(
            StructureSettings settings,
            Holder<StructureTemplatePool> startPool,
            int maxDepth,
            int maxDistanceFromCenter,
            boolean useExpansionHack,
            boolean cavernPlacement,
            boolean honorForgottenTowerConfig) {
        super(settings);
        this.startPool = startPool;
        this.maxDepth = maxDepth;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.useExpansionHack = useExpansionHack;
        this.cavernPlacement = cavernPlacement;
        this.honorForgottenTowerConfig = honorForgottenTowerConfig;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (honorForgottenTowerConfig && !ServerConfig.generateForgottenTower()) {
            return Optional.empty();
        }

        ChunkPos chunk = context.chunkPos();
        int x = chunk.getMiddleBlockX();
        int z = chunk.getMiddleBlockZ();
        return cavernPlacement
                ? findCavernGenerationPoint(context, x, z)
                : findSurfaceGenerationPoint(context, x, z);
    }

    private Optional<GenerationStub> findCavernGenerationPoint(
            GenerationContext context, int x, int z) {
        int floorY = findCavernShelf(context, x, z);
        int minimum = context.heightAccessor().getMinBuildHeight() + 1;
        BlockPos start = new BlockPos(x, Math.max(minimum, floorY - CAVERN_FLOOR_OFFSET), z);
        return addPieces(context, start, Optional.empty());
    }

    private Optional<GenerationStub> findSurfaceGenerationPoint(
            GenerationContext context, int x, int z) {
        int surfaceY =
                context.chunkGenerator()
                        .getFirstOccupiedHeight(
                                x,
                                z,
                                Heightmap.Types.WORLD_SURFACE_WG,
                                context.heightAccessor(),
                                context.randomState());
        NoiseColumn column =
                context.chunkGenerator()
                        .getBaseColumn(x, z, context.heightAccessor(), context.randomState());
        if (!column.getBlock(surfaceY).getFluidState().isEmpty()) {
            return Optional.empty();
        }
        return addPieces(
                context, new BlockPos(x, 0, z), Optional.of(Heightmap.Types.WORLD_SURFACE_WG));
    }

    private Optional<GenerationStub> addPieces(
            GenerationContext context, BlockPos start, Optional<Heightmap.Types> projection) {
        return JigsawPlacement.addPieces(
                context,
                startPool,
                Optional.empty(),
                maxDepth,
                start,
                useExpansionHack,
                projection,
                maxDistanceFromCenter,
                PoolAliasLookup.EMPTY,
                DimensionPadding.ZERO,
                LiquidSettings.APPLY_WATERLOGGING);
    }

    private static int findCavernShelf(GenerationContext context, int x, int z) {
        int maximum = context.heightAccessor().getMaxBuildHeight() - CAVERN_ROOF_MARGIN;
        int minimum =
                context.chunkGenerator().getSeaLevel() - CAVERN_SEARCH_MINIMUM_BELOW_SEA_LEVEL;
        NoiseColumn column =
                context.chunkGenerator()
                        .getBaseColumn(x, z, context.heightAccessor(), context.randomState());

        for (int y = maximum; y > minimum; y--) {
            BlockState floor = column.getBlock(y);
            BlockState clearance =
                    column.getBlock(
                            Math.min(
                                    y + CAVERN_CLEARANCE_HEIGHT,
                                    context.heightAccessor().getMaxBuildHeight() - 1));
            if (!floor.isAir() && floor.getFluidState().isEmpty() && clearance.isAir()) {
                return y;
            }
        }

        return context.chunkGenerator().getSeaLevel() - CAVERN_FALLBACK_BELOW_SEA_LEVEL;
    }

    private static DataResult<CavernsJigsawStructure> verifyRange(
            CavernsJigsawStructure structure) {
        int terrainMargin =
                switch (structure.terrainAdaptation()) {
                    case NONE -> 0;
                    case BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE -> 12;
                };
        if (structure.maxDistanceFromCenter + terrainMargin
                > JigsawStructure.MAX_TOTAL_STRUCTURE_RANGE) {
            return DataResult.error(
                    () ->
                            "Structure size including terrain adaptation must not exceed "
                                    + JigsawStructure.MAX_TOTAL_STRUCTURE_RANGE);
        }
        return DataResult.success(structure);
    }

    @Override
    public StructureType<?> type() {
        return CustomStructureTypes.CAVERNS_JIGSAW.get();
    }
}
