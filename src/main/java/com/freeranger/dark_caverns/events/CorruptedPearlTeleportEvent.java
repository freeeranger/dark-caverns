package com.freeranger.dark_caverns.events;

import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

/** Fired before a player-owned corrupted pearl teleports its selected victim. */
public final class CorruptedPearlTeleportEvent extends EntityTeleportEvent {
    private final ServerPlayer player;
    private final CorruptedPearlEntity pearlEntity;
    private float attackDamage;

    public CorruptedPearlTeleportEvent(
            ServerPlayer player,
            double targetX,
            double targetY,
            double targetZ,
            CorruptedPearlEntity pearlEntity,
            float attackDamage
    ) {
        super(player, targetX, targetY, targetZ);
        this.player = player;
        this.pearlEntity = pearlEntity;
        this.attackDamage = attackDamage;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public CorruptedPearlEntity getPearlEntity() {
        return pearlEntity;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(float attackDamage) {
        this.attackDamage = attackDamage;
    }
}
