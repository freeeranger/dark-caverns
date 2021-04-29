package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class ScorchedBerryBushBlock extends CustomBushBlock implements IGrowable {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public ScorchedBerryBushBlock(Properties properties, Block baseBlock) {
        super(properties, baseBlock);
    }

    public ItemStack getCloneItemStack(IBlockReader reader, BlockPos pos, BlockState state) {
        return new ItemStack(CustomItems.SCORCHED_BERRIES.get());
    }

    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        if (state.getValue(AGE) == 0) {
            return SAPLING_SHAPE;
        } else {
            return state.getValue(AGE) < 3 ? MID_GROWTH_SHAPE : super.getShape(state, reader, pos, context);
        }
    }

    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3;
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        int i = state.getValue(AGE);
        if (i < 3 && world.getRawBrightness(pos.above(), 0) >= 9 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state,rand.nextInt(5) == 0)) {
            world.setBlock(pos, state.setValue(AGE, Integer.valueOf(i + 1)), 2);
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state);
        }

    }

    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE) {
            entity.makeStuckInBlock(state, new Vector3d((double)0.8F, 0.75D, (double)0.8F));
            if (!world.isClientSide && state.getValue(AGE) > 0 && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
                double d0 = Math.abs(entity.getX() - entity.xOld);
                double d1 = Math.abs(entity.getZ() - entity.zOld);
                if (d0 >= (double)0.003F || d1 >= (double)0.003F) {
                    entity.setSecondsOnFire(10);
                }
            }

        }
    }

    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        int i = state.getValue(AGE);
        boolean flag = i == 3;
        if (!flag && player.getItemInHand(hand).getItem() == Items.BONE_MEAL) {
            return ActionResultType.PASS;
        } else if (i > 1) {
            int j = 1 + world.random.nextInt(2);
            popResource(world, pos, new ItemStack(CustomItems.SCORCHED_BERRIES.get(), j + (flag ? 1 : 0)));
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlock(pos, state.setValue(AGE, Integer.valueOf(1)), 2);
            return ActionResultType.sidedSuccess(world.isClientSide);
        } else {
            return super.use(state, world, pos, player, hand, result);
        }
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateContainer) {
        stateContainer.add(AGE);
    }

    public boolean isValidBonemealTarget(IBlockReader reader, BlockPos pos, BlockState state, boolean bool) {
        return state.getValue(AGE) < 3;
    }

    public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        int i = Math.min(3, state.getValue(AGE) + 1);
        world.setBlock(pos, state.setValue(AGE, Integer.valueOf(i)), 2);
    }
}
