package com.freeranger.dark_caverns.mixin;

import com.freeranger.dark_caverns.core.LuminiteHelmetLighting;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IBlockReader.class)
public interface IBlockReaderMixin
{
    /**
     * @author Freeranger
     */
    @Overwrite
    default int getLightEmission(BlockPos pos) {
        if (LuminiteHelmetLighting.SOURCES.containsKey(pos) && LuminiteHelmetLighting.SOURCES.get(pos).shouldStay)
            return 15;
        return this.getBlockState(pos).getLightValue((IBlockReader) this, pos);
    }

    @Shadow
    BlockState getBlockState(BlockPos pos);
}

