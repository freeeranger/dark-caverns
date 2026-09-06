package com.freeranger.dark_caverns.events;

import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class CorruptedPearlTeleportEvent extends EntityTeleportEvent
{
    private final ServerPlayerEntity player;
    private final CorruptedPearlEntity pearlEntity;
    private float attackDamage;

    public CorruptedPearlTeleportEvent(ServerPlayerEntity entity, double targetX, double targetY, double targetZ, CorruptedPearlEntity pearlEntity, float attackDamage)
    {
        super(entity, targetX, targetY, targetZ);
        this.pearlEntity = pearlEntity;
        this.player = entity;
        this.attackDamage = attackDamage;
    }

    public CorruptedPearlEntity getPearlEntity()
    {
        return pearlEntity;
    }

    public ServerPlayerEntity getPlayer()
    {
        return player;
    }

    public float getAttackDamage()
    {
        return attackDamage;
    }

    public void setAttackDamage(float attackDamage)
    {
        this.attackDamage = attackDamage;
    }
}