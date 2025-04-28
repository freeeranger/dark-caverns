package com.freeranger.darkcaverns.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrassLikeBlock extends BushBlock {
    private static final VoxelShape SHAPE = Block.column(12f, 0f, 10f);

    private Block ground;

    public GrassLikeBlock(Properties properties, Block ground, boolean defaultProperties) {
        super(defaultProperties ? properties.instabreak().noOcclusion().sound(SoundType.GRASS).noCollission().pushReaction(PushReaction.DESTROY) : properties);
        
        this.ground = ground;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos) {
        return state.is(ground);
    }

    protected VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return true;
    }
}
