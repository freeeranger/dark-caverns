package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/** Stores gateway cooldowns as an absolute server tick on the entity itself. */
public final class GatewayCooldowns {
    private GatewayCooldowns() {}

    public static boolean isReady(Entity entity, ServerLevel level) {
        Long cooldownUntil = entity.getExistingDataOrNull(CustomAttachments.GATEWAY_COOLDOWN_UNTIL);
        if (cooldownUntil == null) {
            return true;
        }
        if (cooldownUntil <= level.getGameTime()) {
            entity.removeData(CustomAttachments.GATEWAY_COOLDOWN_UNTIL);
            return true;
        }
        return false;
    }

    public static void start(Entity entity, ServerLevel level) {
        entity.setData(
                CustomAttachments.GATEWAY_COOLDOWN_UNTIL,
                level.getGameTime() + ServerConfig.gatewayCooldownTicks());
    }
}
