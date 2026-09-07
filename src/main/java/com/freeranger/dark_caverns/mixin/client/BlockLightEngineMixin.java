package com.freeranger.dark_caverns.mixin.client;

import com.freeranger.dark_caverns.client.LuminiteHelmetLighting;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin {
    @Inject(method = "getEmission", at = @At("HEAD"), cancellable = true)
    private void darkCaverns$dynamicHelmetLight(
            long packedPos, BlockState state, CallbackInfoReturnable<Integer> callback) {
        if (LuminiteHelmetLighting.isSource(packedPos)) {
            callback.setReturnValue(LuminiteHelmetLighting.LIGHT_LEVEL);
        }
    }
}
