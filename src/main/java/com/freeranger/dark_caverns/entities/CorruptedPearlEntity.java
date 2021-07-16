package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.events.CorruptedPearlTeleportEvent;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomEvents;
import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CorruptedPearlEntity extends ProjectileItemEntity {

    Boolean hasHit = false;

    public CorruptedPearlEntity(EntityType<? extends CorruptedPearlEntity> entityType, World world) {
        super(entityType, world);
    }

    public CorruptedPearlEntity(World world, LivingEntity entity) {
        super(CustomEntityTypes.CORRUPTED_PEARL.get(), entity, world);
    }

    public CorruptedPearlEntity(World world, double x, double y, double z) {
        super(CustomEntityTypes.CORRUPTED_PEARL.get(), x, y, z, world);
    }

    public CorruptedPearlEntity(World world) {
        super(CustomEntityTypes.CORRUPTED_PEARL.get(), 0, 0, 0, world);
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected Item getDefaultItem() {
        return CustomItems.CORRUPTED_PEARL.get();
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
        result.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0f);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        Entity entity = this.getOwner();

        for(int i = 0; i < 32; ++i) {
            this.level.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
        }

        if (!this.level.isClientSide && !this.removed) {
            AxisAlignedBB box = new AxisAlignedBB(
                    new BlockPos(
                            entity.blockPosition().getX() - 5,
                            entity.blockPosition().getY() - 5,
                            entity.blockPosition().getZ() - 5
                    ),
                    new BlockPos(
                            entity.blockPosition().getX() + 5,
                            entity.blockPosition().getY() + 5,
                            entity.blockPosition().getZ() + 5
                    )
            );
            List<Entity> entities = entity.level.getEntities(entity, box);
            LivingEntity victim = null;
            for (Entity i : entities){
                if(i instanceof LivingEntity && !(i instanceof PlayerEntity)){
                    if(victim == null || i.distanceTo(entity) < victim.distanceTo(entity)){
                        victim = (LivingEntity) i;
                    }
                }
            }

            if (entity instanceof ServerPlayerEntity && victim != null) {
                ServerPlayerEntity splayer = (ServerPlayerEntity)entity;

                if (splayer.connection.getConnection().isConnected() && splayer.level == this.level && !splayer.isSleeping()) {

                    CorruptedPearlTeleportEvent event;
                    event = CustomEvents.onCorruptedPearlLand(splayer, this.getX(), this.getY(), this.getZ(), this, 5.0F);
                    if (!event.isCanceled()) {
                        if (victim.isPassenger()) {
                            victim.stopRiding();
                        }

                        victim.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                        victim.fallDistance = 0.0F;
                    }
                }
            } else if (victim != null) {
                victim.teleportTo(this.getX(), this.getY(), this.getZ());
                victim.fallDistance = 0.0F;
            }
            this.remove();
        }

    }

    public void tick() {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntity && !entity.isAlive()) {
            this.remove();
        } else {
            super.tick();
        }

    }

    @Nullable
    public Entity changeDimension(ServerWorld p_241206_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
        Entity entity = this.getOwner();
        if (entity != null && entity.level.dimension() != p_241206_1_.dimension()) {
            this.setOwner(null);
        }

        return super.changeDimension(p_241206_1_, teleporter);
    }
}