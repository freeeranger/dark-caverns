package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public final class ThrowableLuminiteTorchEntity extends DarkCavernsThrowableItemProjectile {
    public ThrowableLuminiteTorchEntity(EntityType<? extends ThrowableLuminiteTorchEntity> type, Level level) {
        super(type, level);
    }

    public ThrowableLuminiteTorchEntity(Level level, LivingEntity owner) {
        super(CustomEntityTypes.THROWABLE_LUMINITE_TORCH.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return CustomItems.THROWABLE_LUMINITE_TORCH.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(damageSources().thrown(this, getOwner()), 1.0F);
        finishImpact();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            Direction face = result.getDirection();
            BlockPos placeAt = result.getBlockPos().relative(face);
            BlockState existing = level().getBlockState(placeAt);
            BlockState placed = null;

            if (existing.canBeReplaced()) {
                if (face == Direction.UP) {
                    placed = CustomBlocks.LUMINITE_TORCH.get().defaultBlockState();
                } else if (face.getAxis().isHorizontal()) {
                    placed = CustomBlocks.LUMINITE_WALL_TORCH.get()
                            .defaultBlockState()
                            .setValue(WallTorchBlock.FACING, face);
                }
            }

            if (placed != null && placed.canSurvive(level(), placeAt)) {
                level().setBlock(placeAt, placed, 3);
            } else {
                spawnAtLocation(getItem());
            }
        }
        finishImpact();
    }
}
