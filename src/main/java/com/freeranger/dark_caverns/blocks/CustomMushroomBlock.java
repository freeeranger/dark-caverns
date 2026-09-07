package com.freeranger.dark_caverns.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.event.EventHooks;

/** A mushroom that preserves the legacy random choice between its two huge variants. */
public final class CustomMushroomBlock extends MushroomBlock {
    private final ResourceKey<ConfiguredFeature<?, ?>> primaryFeature;
    private final ResourceKey<ConfiguredFeature<?, ?>> alternateFeature;

    public CustomMushroomBlock(
            ResourceKey<ConfiguredFeature<?, ?>> feature,
            ResourceKey<ConfiguredFeature<?, ?>> alternateFeature,
            BlockBehaviour.Properties properties) {
        super(feature, properties);
        this.primaryFeature = feature;
        this.alternateFeature = alternateFeature;
    }

    @Override
    public MapCodec<MushroomBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public boolean growMushroom(
            ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        ResourceKey<ConfiguredFeature<?, ?>> selected =
                random.nextBoolean() ? primaryFeature : alternateFeature;
        Holder<ConfiguredFeature<?, ?>> feature =
                level.registryAccess()
                        .registryOrThrow(Registries.CONFIGURED_FEATURE)
                        .getHolder(selected)
                        .orElse(null);
        var event = EventHooks.fireBlockGrowFeature(level, random, pos, feature);
        if (event.isCanceled() || event.getFeature() == null) {
            return false;
        }

        level.removeBlock(pos, false);
        if (event.getFeature()
                .value()
                .place(level, level.getChunkSource().getGenerator(), random, pos)) {
            return true;
        }

        level.setBlock(pos, state, 3);
        return false;
    }
}
