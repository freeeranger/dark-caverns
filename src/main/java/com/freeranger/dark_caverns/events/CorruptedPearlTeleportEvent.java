package com.freeranger.dark_caverns.events;

import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

/** Fired before a player-owned corrupted pearl teleports its selected victim. */
public final class CorruptedPearlTeleportEvent extends EntityTeleportEvent {
    private final ServerPlayer pearlOwner;
    private final CorruptedPearlEntity pearlEntity;

    public CorruptedPearlTeleportEvent(
            LivingEntity target,
            ServerPlayer pearlOwner,
            double targetX,
            double targetY,
            double targetZ,
            CorruptedPearlEntity pearlEntity) {
        super(target, targetX, targetY, targetZ);
        this.pearlOwner = pearlOwner;
        this.pearlEntity = pearlEntity;
    }

    public ServerPlayer getPearlOwner() {
        return pearlOwner;
    }

    public CorruptedPearlEntity getPearlEntity() {
        return pearlEntity;
    }
}
