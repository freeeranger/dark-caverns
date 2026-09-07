package com.freeranger.dark_caverns.core;

import net.minecraft.world.phys.Vec3;

/** Tracks a player's current uninterrupted period of standing still. */
public final class ScorchsteelStealthState {
    private Vec3 lastPosition;
    private int stationaryTicks;

    public int update(Vec3 position) {
        stationaryTicks = position.equals(lastPosition) ? stationaryTicks + 1 : 0;
        lastPosition = position;
        return stationaryTicks;
    }
}
