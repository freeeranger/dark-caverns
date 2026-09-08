package com.freeranger.dark_caverns.core;

import net.minecraft.world.phys.Vec3;

/** Tracks a player's current uninterrupted period of standing still. */
public final class ScorchsteelStealthState {
    private static final double MOVEMENT_EPSILON_SQUARED = 1.0E-4;

    private Vec3 lastPosition;
    private int stationaryTicks;
    private boolean active;

    public int update(Vec3 position) {
        stationaryTicks =
                lastPosition != null
                                && position.distanceToSqr(lastPosition) <= MOVEMENT_EPSILON_SQUARED
                        ? stationaryTicks + 1
                        : 0;
        lastPosition = position;
        return stationaryTicks;
    }

    public boolean activate() {
        if (active) {
            return false;
        }
        active = true;
        return true;
    }

    public boolean deactivate() {
        if (!active) {
            return false;
        }
        active = false;
        stationaryTicks = 0;
        return true;
    }

    public boolean isActive() {
        return active;
    }
}
