package com.freeranger.dark_caverns.mixin.client;

import com.freeranger.dark_caverns.client.LuminiteHelmetLighting;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin {
    @ModifyReturnValue(method = "getEmission", at = @At("RETURN"))
    private int darkCaverns$dynamicHelmetLight(
            int originalLight, long packedPos, BlockState state) {
        return LuminiteHelmetLighting.isSource(packedPos)
                ? Math.max(originalLight, LuminiteHelmetLighting.LIGHT_LEVEL)
                : originalLight;
    }
}
