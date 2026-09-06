package com.freeranger.dark_caverns.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class GlimmershroomBlock extends HugeMushroomBlock {
    public GlimmershroomBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<HugeMushroomBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        } else {
            entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(level, entity);
            return;
        }

        Vec3 movement = entity.getDeltaMovement();
        if (movement.y < 0.0) {
            double bounce = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(movement.x, -movement.y * bounce, movement.z);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        double verticalSpeed = Math.abs(entity.getDeltaMovement().y);
        if (verticalSpeed < 0.1 && !entity.isSteppingCarefully()) {
            double horizontalMultiplier = 0.4 + verticalSpeed * 0.2;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(horizontalMultiplier, 1.0, horizontalMultiplier));
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 30;
    }
}
