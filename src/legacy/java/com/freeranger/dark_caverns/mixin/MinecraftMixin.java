package com.freeranger.dark_caverns.mixin;

import com.freeranger.dark_caverns.core.LuminiteHelmetLighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(at = @At("HEAD"), method = "clearLevel(Lnet/minecraft/client/gui/screen/Screen;)V")
    private void unloadWorld(Screen screenIn, CallbackInfo callback)
    {
        LuminiteHelmetLighting.cleanUp();
    }
}
