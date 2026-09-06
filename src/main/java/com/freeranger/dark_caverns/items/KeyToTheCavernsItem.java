package com.freeranger.dark_caverns.items;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public final class KeyToTheCavernsItem extends Item {
    public KeyToTheCavernsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        if (!context.getLevel().getBlockState(pos).is(CustomBlocks.CRACKED_BEDROCK.get())) {
            return InteractionResult.FAIL;
        }

        if (context.getLevel() instanceof ServerLevel level) {
            level.setBlock(pos, CustomBlocks.GATEWAY_TO_THE_CAVERNS.get().defaultBlockState(), Block.UPDATE_ALL);
            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.sendParticles(
                    ParticleTypes.CLOUD,
                    pos.getX() + 0.5,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5,
                    10,
                    0.2,
                    0.2,
                    0.2,
                    0.05
            );
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }
}
