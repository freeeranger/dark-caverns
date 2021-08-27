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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class ShroombombEntity extends ProjectileItemEntity {
    Boolean hasHit = false;

    public ShroombombEntity(EntityType<? extends ShroombombEntity> entityType, World world) {
        super(entityType, world);
    }

    public ShroombombEntity(World world, LivingEntity entity) {
        super(CustomEntityTypes.SHROOMBOMB.get(), entity, world);
    }

    public ShroombombEntity(World world, double x, double y, double z) {
        super(CustomEntityTypes.SHROOMBOMB.get(), x, y, z, world);
    }

    public ShroombombEntity(World world) {
        super(CustomEntityTypes.SHROOMBOMB.get(), 0, 0, 0, world);
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected Item getDefaultItem() {
        return CustomItems.SHROOMBOMB.get();
    }

    @OnlyIn(Dist.CLIENT)
    private IParticleData getParticle() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleData(ParticleTypes.ITEM, itemstack);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 3){
            IParticleData iparticledata = this.getParticle();

            for(int i = 0; i < 8; ++i){
                this.level.addParticle(iparticledata, this.getX(), this.getY(), this.getZ(),
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        if(!level.isClientSide()){
            BlockPos pos = blockPosition();
            level.explode(this, pos.getX(), pos.getY(), pos.getZ(), 4, Explosion.Mode.BREAK);
        }
        this.remove();
    }
}