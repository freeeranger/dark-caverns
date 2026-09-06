package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.core.DarkCavernsConfig;
import com.freeranger.dark_caverns.registry.CustomStructureTypes;
import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

/**
 * Jigsaw structure placement shared by the four legacy structures. Surface
 * structures follow the world surface; cavern structures scan down for a
 * solid shelf with open space above it, matching the old generator's intent.
 */
public final class PortedJigsawStructure extends Structure {
    public static final MapCodec<PortedJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            Codec.intRange(0, 20).optionalFieldOf("size", 10).forGetter(structure -> structure.maxDepth),
            Codec.intRange(1, 128).optionalFieldOf("max_distance_from_center", 80)
                    .forGetter(structure -> structure.maxDistanceFromCenter),
            Codec.BOOL.optionalFieldOf("use_expansion_hack", false)
                    .forGetter(structure -> structure.useExpansionHack),
            Codec.BOOL.optionalFieldOf("cavern_placement", false)
                    .forGetter(structure -> structure.cavernPlacement),
            Codec.BOOL.optionalFieldOf("honor_forgotten_tower_config", false)
                    .forGetter(structure -> structure.honorForgottenTowerConfig)
    ).apply(instance, PortedJigsawStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final int maxDepth;
    private final int maxDistanceFromCenter;
    private final boolean useExpansionHack;
    private final boolean cavernPlacement;
    private final boolean honorForgottenTowerConfig;

    public PortedJigsawStructure(
            StructureSettings settings,
            Holder<StructureTemplatePool> startPool,
            int maxDepth,
            int maxDistanceFromCenter,
            boolean useExpansionHack,
            boolean cavernPlacement,
            boolean honorForgottenTowerConfig
    ) {
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
        if (honorForgottenTowerConfig && !DarkCavernsConfig.COMMON.generateForgottenTower.get()) {
            return Optional.empty();
        }

        ChunkPos chunk = context.chunkPos();
        int x = chunk.getMiddleBlockX();
        int z = chunk.getMiddleBlockZ();
        BlockPos start;
        Optional<Heightmap.Types> projection;

        if (cavernPlacement) {
            int floorY = findCavernShelf(context, x, z);
            int minimum = context.heightAccessor().getMinBuildHeight() + 1;
            start = new BlockPos(x, Math.max(minimum, floorY - 15), z);
            projection = Optional.empty();
        } else {
            start = new BlockPos(x, 0, z);
            projection = Optional.of(Heightmap.Types.WORLD_SURFACE_WG);
        }

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
                LiquidSettings.APPLY_WATERLOGGING
        );
    }

    private static int findCavernShelf(GenerationContext context, int x, int z) {
        int maximum = context.heightAccessor().getMaxBuildHeight() - 20;
        int minimum = context.chunkGenerator().getSeaLevel() - 2;
        NoiseColumn column = context.chunkGenerator().getBaseColumn(
                x,
                z,
                context.heightAccessor(),
                context.randomState()
        );

        for (int y = maximum; y > minimum; y--) {
            BlockState floor = column.getBlock(y);
            BlockState clearance = column.getBlock(Math.min(y + 3, context.heightAccessor().getMaxBuildHeight() - 1));
            if (!floor.isAir() && floor.getFluidState().isEmpty() && clearance.isAir()) {
                return y;
            }
        }

        return context.chunkGenerator().getSeaLevel() - 13;
    }

    @Override
    public StructureType<?> type() {
        return CustomStructureTypes.PORTED_JIGSAW.get();
    }
}
