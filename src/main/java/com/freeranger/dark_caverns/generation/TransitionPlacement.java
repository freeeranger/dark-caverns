package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.registry.CustomWorldgen;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/** Small decorations spill into matching surface patches; large/hazardous features prefer cores. */
public final class TransitionPlacement extends PlacementFilter {
    public static final MapCodec<TransitionPlacement> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance ->
                            instance.group(
                                            Codec.intRange(0, 2)
                                                    .fieldOf("biome")
                                                    .forGetter(p -> p.biome),
                                            Codec.BOOL
                                                    .optionalFieldOf("core", false)
                                                    .forGetter(p -> p.core))
                                    .apply(instance, TransitionPlacement::new));
    private final int biome;
    private final boolean core;

    public TransitionPlacement(int biome, boolean core) {
        this.biome = biome;
        this.core = core;
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        double chance =
                new BiomeTransition(context.getLevel()::getBiome)
                        .weights(pos.getX(), pos.getZ())
                        .chance(biome, core);
        // Preserve the existing random stream and density in pure biome interiors.
        return chance >= 1 || (chance > 0 && random.nextFloat() < chance);
    }

    @Override
    public PlacementModifierType<?> type() {
        return CustomWorldgen.TRANSITION_PLACEMENT.get();
    }
}
