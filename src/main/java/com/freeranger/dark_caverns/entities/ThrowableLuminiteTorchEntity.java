package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.blocks.LuminiteWallTorchBlock;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class ThrowableLuminiteTorchEntity extends ProjectileItemEntity {

    public ThrowableLuminiteTorchEntity(EntityType<? extends ThrowableLuminiteTorchEntity> entityType, World world) {
        super(entityType, world);
    }

    public ThrowableLuminiteTorchEntity(World world, LivingEntity entity) {
        super(CustomEntityTypes.THROWABLE_LUMINITE_TORCH.get(), entity, world);
    }

    public ThrowableLuminiteTorchEntity(World world, double x, double y, double z) {
        super(CustomEntityTypes.THROWABLE_LUMINITE_TORCH.get(), x, y, z, world);
    }

    public ThrowableLuminiteTorchEntity(World world) {
        super(CustomEntityTypes.THROWABLE_LUMINITE_TORCH.get(), 0, 0, 0, world);
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected Item getDefaultItem() {
        return CustomItems.THROWABLE_LUMINITE_TORCH.get();
    }

    @OnlyIn(Dist.CLIENT)
    private IParticleData getParticle() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleData(ParticleTypes.ITEM, itemstack);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 3) {
            IParticleData iparticledata = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(iparticledata, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 1f);
        this.remove();
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {
        super.onHitBlock(result);
        if (!this.level.isClientSide) {
            Direction direction = result.getDirection();
            BlockPos placeAt = result.getBlockPos().relative(direction);
            BlockState existingState = this.level.getBlockState(placeAt);

            if (existingState.getMaterial().isReplaceable()) {
                BlockState stateToPlace = null;
                if (direction == Direction.UP) {
                    stateToPlace = CustomBlocks.LUMINITE_TORCH.get().defaultBlockState();
                } else if (direction.getAxis().isHorizontal()) {
                    stateToPlace = CustomBlocks.LUMINITE_WALL_TORCH.get().defaultBlockState().setValue(LuminiteWallTorchBlock.FACING, direction);
                }

                if (stateToPlace != null && stateToPlace.canSurvive(this.level, placeAt)) {
                    this.level.setBlock(placeAt, stateToPlace, 3);
                } else {
                    this.spawnAtLocation(this.getItem());
                }
            } else {
                this.spawnAtLocation(this.getItem());
            }
        }
        this.remove();
    }
}