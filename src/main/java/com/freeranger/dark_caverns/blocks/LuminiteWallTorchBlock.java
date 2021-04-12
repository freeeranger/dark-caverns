package com.freeranger.dark_caverns.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class LuminiteWallTorchBlock extends WallTorchBlock {
    public LuminiteWallTorchBlock(Properties properties) {
        super(properties, ParticleTypes.FLAME);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        Direction direction = stateIn.getValue(FACING);
        double x = (double)pos.getX() + 0.5D;
        double y = (double)pos.getY() + 0.7D;
        double z = (double)pos.getZ() + 0.5D;
        Direction oppositeDirection = direction.getOpposite();
        worldIn.addParticle(ParticleTypes.SMOKE, x + 0.27D * (double)oppositeDirection.getStepX(), y + 0.22D, z + 0.27D * (double)oppositeDirection.getStepZ(), 0.0D, 0.0D, 0.0D);
        //worldIn.addParticle(CustomParticles.LUMINITE_PARTICLE.get(), x + 0.27D * (double)oppositeDirection.getStepX(), y + 0.22D, z + 0.27D * (double)oppositeDirection.getStepZ(), 0.0D, 0.0D, 0.0D);
    }
}
