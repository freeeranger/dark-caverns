package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.registry.CustomItems;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class ScorchedBerryBushBlock extends SweetBerryBushBlock {
    private final Supplier<? extends Block> baseBlock;

    public ScorchedBerryBushBlock(
            BlockBehaviour.Properties properties, Supplier<? extends Block> baseBlock) {
        super(properties);
        this.baseBlock = baseBlock;
    }

    @Override
    public MapCodec<SweetBerryBushBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(baseBlock.get());
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(CustomItems.SCORCHED_BERRIES.get());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity
                && entity.getType() != EntityType.FOX
                && entity.getType() != EntityType.BEE) {
            entity.makeStuckInBlock(state, new Vec3(0.8F, 0.75, 0.8F));
            if (!level.isClientSide && state.getValue(AGE) > 0 && hasMoved(entity)) {
                entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks(), 200));
            }
        }
    }

    private static boolean hasMoved(Entity entity) {
        return Math.abs(entity.getX() - entity.xOld) >= 0.003F
                || Math.abs(entity.getZ() - entity.zOld) >= 0.003F;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age <= 1) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }

        boolean fullyGrown = age == MAX_AGE;
        int count = 1 + level.random.nextInt(2) + (fullyGrown ? 1 : 0);
        popResource(level, pos, new ItemStack(CustomItems.SCORCHED_BERRIES.get(), count));
        level.setBlock(pos, state.setValue(AGE, 1), Block.UPDATE_CLIENTS);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
