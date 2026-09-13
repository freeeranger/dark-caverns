package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/** Surface-stage blending: neighboring decoration never sees a temporary hard biome edge. */
public enum TransitionSurfaceRule implements SurfaceRules.RuleSource {
    INSTANCE;

    public static final MapCodec<TransitionSurfaceRule> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return KeyDispatchDataCodec.of(CODEC);
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        var blend = new BiomeTransition(context.biomeGetter);
        var patches =
                NormalNoise.create(
                        context.randomState
                                .getOrCreateRandomFactory(DarkCaverns.id("transition_patches"))
                                .fromHashOf(DarkCaverns.id("surface")),
                        new NormalNoise.NoiseParameters(-3, 1.0, 0.4));
        var forest =
                SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                SurfaceRules.state(
                                        CustomBlocks.GLIMMERGRASS_BLOCK.get().defaultBlockState()))
                        .apply(context);
        var moltenState =
                SurfaceRules.state(CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState());
        var molten =
                SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, moltenState),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, moltenState))
                        .apply(context);
        return new SurfaceRules.SurfaceRule() {
            private int lastX = Integer.MIN_VALUE;
            private int lastZ = Integer.MIN_VALUE;
            private int material;

            @Override
            public BlockState tryApply(int x, int y, int z) {
                if (x != lastX || z != lastZ) {
                    lastX = x;
                    lastZ = z;
                    double patch = Math.clamp(0.5 + patches.getValue(x, 0, z) * 0.85, 0.001, 0.999);
                    material = blend.weights(x, z).material(patch);
                }
                return switch (material) {
                    case BiomeTransition.FOREST -> forest.tryApply(x, y, z);
                    case BiomeTransition.MOLTEN -> molten.tryApply(x, y, z);
                    default -> null;
                };
            }
        };
    }
}
