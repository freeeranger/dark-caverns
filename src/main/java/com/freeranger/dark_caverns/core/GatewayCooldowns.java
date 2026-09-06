package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/** Stores gateway cooldowns as an absolute server tick on the entity itself. */
public final class GatewayCooldowns {
    private static final String COOLDOWN_UNTIL = DarkCaverns.MOD_ID + ":gateway_cooldown_until";

    private GatewayCooldowns() {
    }

    public static boolean isReady(Entity entity, ServerLevel level) {
        return entity.getPersistentData().getLong(COOLDOWN_UNTIL) <= level.getGameTime();
    }

    public static void start(Entity entity, ServerLevel level) {
        long duration = DarkCavernsConfig.COMMON.gatewayCooldownTicks.get();
        entity.getPersistentData().putLong(COOLDOWN_UNTIL, level.getGameTime() + duration);
    }
}
