package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.blocks.LuminiteWallTorchBlock;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomItems;
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

    // TODO remove this check from minecraft dev plugin and then remove this constructor
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

    Boolean hasHit = false;

    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 1f);
        this.remove();
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {
        Direction direction = result.getDirection();
        BlockPos placeAt = result.getBlockPos().offset(direction.getNormal());
        if (!hasHit && direction == Direction.UP) {
            level.setBlock(placeAt, CustomBlocks.LUMINITE_TORCH.get().defaultBlockState(), 3);
            hasHit = true;
        } else if (!hasHit && direction == Direction.NORTH) {
            level.setBlock(placeAt, CustomBlocks.LUMINITE_WALL_TORCH.get().defaultBlockState().setValue(LuminiteWallTorchBlock.FACING, Direction.NORTH), 3);
            hasHit = true;
        } else if (!hasHit && direction == Direction.SOUTH) {
            level.setBlock(placeAt, CustomBlocks.LUMINITE_WALL_TORCH.get().defaultBlockState().setValue(LuminiteWallTorchBlock.FACING, Direction.SOUTH), 3);
            hasHit = true;
        } else if (!hasHit && direction == Direction.WEST) {
            level.setBlock(placeAt, CustomBlocks.LUMINITE_WALL_TORCH.get().defaultBlockState().setValue(LuminiteWallTorchBlock.FACING, Direction.WEST), 3);
            hasHit = true;
        } else if (!hasHit && direction == Direction.EAST) {
            level.setBlock(placeAt, CustomBlocks.LUMINITE_WALL_TORCH.get().defaultBlockState().setValue(LuminiteWallTorchBlock.FACING, Direction.EAST), 3);
            hasHit = true;
        }
    }
}