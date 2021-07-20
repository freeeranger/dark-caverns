package com.freeranger.dark_caverns.blocks;

import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class GlimmershroomBlock extends HugeMushroomBlock {
    public GlimmershroomBlock(Properties properties) {
        super(properties);
    }

    public void fallOn(World world, BlockPos pos, Entity entity, float val) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(world, pos, entity, val);
        } else {
            entity.causeFallDamage(val, 0.0F);
        }

    }

    public void updateEntityAfterFallOn(IBlockReader reader, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(reader, entity);
        } else {
            this.bounceUp(entity);
        }

    }

    private void bounceUp(Entity entity) {
        Vector3d vector3d = entity.getDeltaMovement();
        if (vector3d.y < 0.0D) {
            double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(vector3d.x, -vector3d.y * d0, vector3d.z);
        }

    }

    public void stepOn(World world, BlockPos pos, Entity entity) {
        double d0 = Math.abs(entity.getDeltaMovement().y);
        if (d0 < 0.1D && !entity.isSteppingCarefully()) {
            double d1 = 0.4D + d0 * 0.2D;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(d1, 1.0D, d1));
        }

        super.stepOn(world, pos, entity);
    }
}
