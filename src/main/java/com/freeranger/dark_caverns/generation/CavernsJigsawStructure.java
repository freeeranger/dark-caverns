package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomStructureTypes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

/**
 * Jigsaw placement shared by the four Dark Caverns structures. Surface structures follow the world
 * surface; cavern structures select a supported, dry footprint with full template clearance.
 */
public final class CavernsJigsawStructure extends Structure {
    private static final int CAVERN_ROOF_MARGIN = 20;

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
        // Resolve the actual template and rotation before checking its footprint and headroom.
        var candidate = addPieces(context, new BlockPos(x, 128, z), Optional.empty());
        if (candidate.isEmpty()) return Optional.empty();
        var pieces = candidate.get().getPiecesBuilder();
        if (pieces.isEmpty()) return Optional.empty();
        var bounds = pieces.getBoundingBox();
        // Bound noise sampling for datapacks with unusually large, multi-piece start pools.
        if (bounds.getXSpan() > 64 || bounds.getZSpan() > 64) return Optional.empty();
        var columns = new ArrayList<NoiseColumn>();
        for (int bx = bounds.minX(); bx <= bounds.maxX(); bx++) {
            for (int bz = bounds.minZ(); bz <= bounds.maxZ(); bz++) {
                columns.add(
                        context.chunkGenerator()
                                .getBaseColumn(
                                        bx, bz, context.heightAccessor(), context.randomState()));
            }
        }
        var floor =
                CavernFloorFinder.find(
                        columns,
                        Math.max(
                                context.heightAccessor().getMinBuildHeight() + 6,
                                context.chunkGenerator().getSeaLevel() + 4),
                        context.heightAccessor().getMaxBuildHeight() - CAVERN_ROOF_MARGIN,
                        bounds.getYSpan(),
                        context.random());
        if (floor.isEmpty()) return Optional.empty();
        int offset = floor.getAsInt() - bounds.minY();
        for (var piece : pieces.build().pieces()) piece.move(0, offset, 0);
        return Optional.of(
                new GenerationStub(
                        candidate.get().position().offset(0, offset, 0), Either.right(pieces)));
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

    @Override
    public void afterPlace(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            RandomSource random,
            BoundingBox writable,
            ChunkPos chunk,
            PiecesContainer pieces) {
        if (!cavernPlacement) return;
        for (var piece : pieces.pieces()) {
            var box = piece.getBoundingBox();
            for (int x = Math.max(box.minX(), writable.minX());
                    x <= Math.min(box.maxX(), writable.maxX());
                    x++) {
                for (int z = Math.max(box.minZ(), writable.minZ());
                        z <= Math.min(box.maxZ(), writable.maxZ());
                        z++) {
                    BlockPos floor = new BlockPos(x, box.minY(), z);
                    if (writable.isInside(floor)) fillFoundation(level, floor);
                }
            }
        }
    }

    public static void fillFoundation(WorldGenLevel level, BlockPos floor) {
        if (level.isOutsideBuildHeight(floor) || !level.ensureCanWrite(floor)) return;
        var material = level.getBlockState(floor);
        if (!material.is(CustomBlocks.CARFSTONE.get())
                && !material.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                && !material.is(CustomBlocks.GLIMMERGRASS_BLOCK.get())) return;
        BlockState[] column = new BlockState[CavernFloorFinder.MAX_FOUNDATION_DEPTH + 2];
        int bottom = floor.getY() - column.length + 1;
        for (int y = 0; y < column.length; y++) {
            BlockPos pos = new BlockPos(floor.getX(), bottom + y, floor.getZ());
            if (level.isOutsideBuildHeight(pos) || !level.ensureCanWrite(pos)) return;
            column[y] = level.getBlockState(pos);
        }
        int depth =
                CavernFloorFinder.foundationDepth(new NoiseColumn(bottom, column), floor.getY());
        if (material.is(CustomBlocks.GLIMMERGRASS_BLOCK.get()))
            material = CustomBlocks.CARFSTONE.get().defaultBlockState();
        for (int dy = 1; dy < depth; dy++)
            level.setBlock(floor.below(dy), material, Block.UPDATE_CLIENTS);
    }
}
