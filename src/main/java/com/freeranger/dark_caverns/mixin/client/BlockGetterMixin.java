package com.freeranger.dark_caverns.mixin.client;

import com.freeranger.dark_caverns.client.LuminiteHelmetLighting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {
    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    private void darkCaverns$dynamicHelmetLight(
            BlockPos pos, CallbackInfoReturnable<Integer> callback) {
        if (LuminiteHelmetLighting.isSource(pos)) {
            callback.setReturnValue(15);
        }
    }
}
